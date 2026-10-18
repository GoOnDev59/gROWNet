import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import App from '../App.jsx';
import { AuthProvider } from '../context/AuthContext.jsx';
import { api, tokenStore, setUnauthorizedHandler } from '../api/client.js';

const STUDENT = { id: 1, fullName: 'Test Student', email: 'a@b.co', role: 'STUDENT', skills: 'React, SQL', college: 'X', branch: 'CS', bio: '' };
let calls;
function mockFetch(routes) {
  calls = [];
  vi.stubGlobal('fetch', vi.fn(async (url, opts = {}) => {
    const key = (opts.method || 'GET') + ' ' + url.replace('http://localhost:8080', '');
    calls.push({ key, opts });
    const r = routes[key];
    if (r instanceof Error) throw r;
    const { status = 200, body } = r || { status: 404, body: { message: 'nope' } };
    return { ok: status < 400, status, text: async () => (body === undefined ? '' : JSON.stringify(body)) };
  }));
}
const mount = (path) => render(<MemoryRouter initialEntries={[path]}><AuthProvider><App /></AuthProvider></MemoryRouter>);
afterEach(() => { sessionStorage.clear(); vi.unstubAllGlobals(); setUnauthorizedHandler(() => {}); });

describe('api client', () => {
  it('attaches Bearer token and handles empty bodies', async () => {
    tokenStore.set('abc'); mockFetch({ 'GET /x': { status: 204 } });
    expect(await api('/x')).toBeNull();
    expect(calls[0].opts.headers.Authorization).toBe('Bearer abc');
  });
  it('clears token and signals on 401', async () => {
    tokenStore.set('abc'); const h = vi.fn(); setUnauthorizedHandler(h);
    mockFetch({ 'GET /x': { status: 401, body: {} } });
    await expect(api('/x')).rejects.toMatchObject({ status: 401 });
    expect(tokenStore.get()).toBeNull(); expect(h).toHaveBeenCalled();
  });
  it('gives a friendly message when the backend is unreachable', async () => {
    mockFetch({ 'GET /x': new TypeError('fail') });
    await expect(api('/x')).rejects.toThrow(/Cannot reach the GrowNet server/);
  });
});

describe('routing and auth', () => {
  it('redirects unauthenticated users to login', async () => {
    mockFetch({}); mount('/student/dashboard');
    expect(await screen.findByRole('heading', { name: 'Welcome back' })).toBeTruthy();
  });
  it('registers with exactly fullName, email, password', async () => {
    mockFetch({ 'POST /api/auth/register': { status: 201, body: { message: 'ok' } } }); mount('/register');
    const u = userEvent.setup();
    await u.type(screen.getByLabelText('Full name'), 'Mukesh Patil');
    await u.type(screen.getByLabelText('Email'), 'm@example.com');
    await u.type(screen.getByLabelText('Password'), 'TestPass@123');
    await u.click(screen.getByRole('button', { name: 'Create account' }));
    await waitFor(() => expect(calls.length).toBe(1));
    expect(JSON.parse(calls[0].opts.body)).toEqual({ fullName: 'Mukesh Patil', email: 'm@example.com', password: 'TestPass@123' });
    expect(await screen.findByRole('heading', { name: 'Welcome back' })).toBeTruthy();
  });
  it('shows inline validation without calling the API', async () => {
    mockFetch({}); mount('/register');
    await userEvent.setup().click(screen.getByRole('button', { name: 'Create account' }));
    expect(await screen.findByText('Enter your full name.')).toBeTruthy(); expect(calls.length).toBe(0);
  });
  it('logs in, stores the token, lands on the role dashboard, then logs out', async () => {
    mockFetch({ 'POST /api/auth/login': { body: { token: 't1', tokenType: 'Bearer', user: STUDENT } }, 'GET /api/profile': { body: STUDENT } });
    mount('/login'); const u = userEvent.setup();
    await u.type(screen.getByLabelText('Email'), 'a@b.co'); await u.type(screen.getByLabelText('Password'), 'secret123');
    await u.click(screen.getByRole('button', { name: 'Log in' }));
    expect(await screen.findByRole('heading', { name: /Test/ })).toBeTruthy();
    expect(tokenStore.get()).toBe('t1');
    await u.click(screen.getByRole('button', { name: 'Log out' }));
    expect(tokenStore.get()).toBeNull();
    expect(await screen.findByRole('heading', { name: /Find the internship/ })).toBeTruthy();
  });
  it('shows a clear message for invalid credentials', async () => {
    mockFetch({ 'POST /api/auth/login': { status: 401, body: { message: 'Bad credentials' } } });
    mount('/login'); const u = userEvent.setup();
    await u.type(screen.getByLabelText('Email'), 'a@b.co'); await u.type(screen.getByLabelText('Password'), 'x');
    await u.click(screen.getByRole('button', { name: 'Log in' }));
    expect(await screen.findByText('Email or password is incorrect.')).toBeTruthy();
  });
  it('restores the session from a stored token', async () => {
    tokenStore.set('persisted'); mockFetch({ 'GET /api/profile': { body: STUDENT } });
    mount('/student/dashboard');
    expect(await screen.findByRole('heading', { name: /Test/ })).toBeTruthy();
    expect(calls[0].opts.headers.Authorization).toBe('Bearer persisted');
  });
  it('blocks a student from the admin area', async () => {
    tokenStore.set('t'); mockFetch({ 'GET /api/profile': { body: STUDENT } });
    mount('/admin/dashboard');
    expect(await screen.findByRole('heading', { name: 'No access' })).toBeTruthy();
  });
  it('drops an expired token and returns to login', async () => {
    tokenStore.set('old'); mockFetch({ 'GET /api/profile': { status: 401, body: {} } });
    mount('/student/profile');
    expect(await screen.findByRole('heading', { name: 'Welcome back' })).toBeTruthy(); expect(tokenStore.get()).toBeNull();
  });
});

describe('profile', () => {
  it('saves edits with PUT and confirms', async () => {
    tokenStore.set('t');
    mockFetch({ 'GET /api/profile': { body: STUDENT }, 'PUT /api/profile': { body: { ...STUDENT, college: 'MIT' } } });
    mount('/student/profile'); const u = userEvent.setup();
    const college = await screen.findByLabelText('College');
    await u.clear(college); await u.type(college, 'MIT');
    await u.click(screen.getByRole('button', { name: 'Save changes' }));
    expect(await screen.findByText('Profile saved.')).toBeTruthy();
    const put = calls.find((c) => c.key === 'PUT /api/profile');
    expect(JSON.parse(put.opts.body)).toMatchObject({ college: 'MIT', skills: 'React, SQL' });
  });
  it('shows a server error when saving fails', async () => {
    tokenStore.set('t');
    mockFetch({ 'GET /api/profile': { body: STUDENT }, 'PUT /api/profile': { status: 500, body: {} } });
    mount('/student/profile');
    await userEvent.setup().click(await screen.findByRole('button', { name: 'Save changes' }));
    expect(await screen.findByText(/server hit a problem/)).toBeTruthy();
  });
});
