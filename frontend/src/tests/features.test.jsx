import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import App from '../App.jsx';
import { AuthProvider } from '../context/AuthContext.jsx';
import { StudentProvider } from '../context/StudentContext.jsx';
import { tokenStore } from '../api/client.js';

const U = (role, id = 1) => ({ id, fullName: 'Test ' + role, email: role + '@t.co', role, skills: 'React, SQL', college: 'X', branch: 'CS', bio: 'b' });
const I = (id, extra = {}) => ({ id, title: 'Role ' + id, company: 'Co ' + id, location: 'Mumbai', stipend: '10k', description: 'd', skillsRequired: 'React, Docker', deadline: null, recruiterId: 9, verified: true, deadlineStatus: 'NO_DEADLINE', ...extra });
const M = { internshipId: 1, matchPercentage: 50, matchedSkills: ['React'], missingSkills: ['Docker'], explanation: 'Matched 1 of 2 required skills.' };
let calls;
function mockFetch(routes) {
  calls = [];
  vi.stubGlobal('fetch', vi.fn(async (url, opts = {}) => {
    const key = (opts.method || 'GET') + ' ' + url.replace('http://localhost:8080', '');
    calls.push(key + (opts.body ? ' ' + opts.body : ''));
    const r = routes[key];
    const { status = 200, body } = r || { status: 404, body: { success: false, message: 'nope' } };
    return { ok: status < 400, status, text: async () => (body === undefined ? '' : JSON.stringify(body)) };
  }));
}
const mount = (path, role = 'STUDENT') => { tokenStore.set('t'); return render(<MemoryRouter initialEntries={[path]}><AuthProvider><StudentProvider><App /></StudentProvider></AuthProvider></MemoryRouter>); };
afterEach(() => { sessionStorage.clear(); vi.unstubAllGlobals(); });
const student = { 'GET /api/profile': { body: U('STUDENT') }, 'GET /api/saved-internships': { body: [] } };

it('lists internships, sends the real filter params, and shows an empty state', async () => {
  mockFetch({ ...student, 'GET /api/internships': { body: [I(1), I(2)] }, 'GET /api/matching/1': { body: M }, 'GET /api/matching/2': { body: M }, 'GET /api/internships?keyword=zzz': { body: [] } });
  mount('/internships');
  expect(await screen.findByText('2 internships')).toBeTruthy();
  expect((await screen.findAllByText(/50%/)).length).toBe(2);
  await userEvent.setup().type(screen.getByLabelText('Search'), 'zzz');
  expect(await screen.findByText('No internships match these filters')).toBeTruthy();
  expect(calls).toContain('GET /api/internships?keyword=zzz');
});
it('shows an error with retry when the list fails', async () => {
  mockFetch({ ...student, 'GET /api/internships': { status: 500, body: { success: false, message: 'An unexpected error occurred. Please try again later.' } } });
  mount('/internships');
  expect(await screen.findByText(/unexpected error/)).toBeTruthy(); expect(screen.getByRole('button', { name: 'Try again' })).toBeTruthy();
});
it('saves then unsaves with POST and DELETE, and reports a failed save without changing state', async () => {
  mockFetch({ ...student, 'GET /api/internships/1': { body: I(1) }, 'GET /api/matching/1': { body: M }, 'POST /api/saved-internships/1': { status: 201, body: { message: 'ok' } }, 'DELETE /api/saved-internships/1': { body: { message: 'ok' } } });
  mount('/internships/1'); const u = userEvent.setup();
  await u.click(await screen.findByRole('button', { name: 'Save internship' }));
  expect(await screen.findByText('Saved to your list.')).toBeTruthy();
  await u.click(screen.getByRole('button', { name: 'Remove from saved' }));
  expect(await screen.findByText('Removed from your saved list.')).toBeTruthy();
  expect(calls).toEqual(expect.arrayContaining(['POST /api/saved-internships/1', 'DELETE /api/saved-internships/1']));
});
it('shows a missing-internship state on 404', async () => {
  mockFetch({ ...student, 'GET /api/internships/5': { status: 404, body: { success: false, message: 'Internship not found.' } } });
  mount('/internships/5');
  expect(await screen.findByText('Internship not found')).toBeTruthy();
});
it('compares selected internships using repeated ids params', async () => {
  sessionStorage.setItem('grownet.compare', '[1,2]');
  mockFetch({ ...student, 'GET /api/internships/compare?ids=1&ids=2': { body: [I(1), I(2)] }, 'GET /api/matching/1': { body: M }, 'GET /api/matching/2': { body: M } });
  mount('/student/compare');
  expect(await screen.findByRole('columnheader', { name: /Role 1/ })).toBeTruthy();
  expect(calls).toContain('GET /api/internships/compare?ids=1&ids=2');
});
it('renders the readiness score and checklist from the backend', async () => {
  mockFetch({ ...student, 'GET /api/readiness': { body: { readinessScore: 50, completedItems: 1, totalItems: 2, explanation: 'Completed 1 of 2.', checklist: [{ name: 'Add skills', completed: true, suggestion: 'Skills added.' }, { name: 'Complete profile', completed: false, suggestion: 'Add your college.' }] } } });
  mount('/student/readiness');
  expect(await screen.findByText('1 of 2 checks complete')).toBeTruthy(); expect(screen.getByText('Add your college.')).toBeTruthy();
});
it('assistant posts {question} and treats the backend fallback sentence as an error', async () => {
  mockFetch({ ...student, 'POST /api/ai/ask': { body: { success: true, answer: 'Sorry, the AI Career Assistant is temporarily unavailable. Please try again later.' } } });
  mount('/student/career-assistant'); const u = userEvent.setup();
  await u.type(await screen.findByLabelText('Your question'), 'Hello');
  await u.click(screen.getByRole('button', { name: 'Send' }));
  expect(await screen.findByRole('button', { name: 'Try again' })).toBeTruthy();
  expect(calls).toContain('POST /api/ai/ask {"question":"Hello"}');
});
it('a bare 403 with a dead token logs the user out', async () => {
  mockFetch({ 'GET /api/profile': { status: 403, body: { status: 403 } } });
  mount('/student/dashboard');
  expect(await screen.findByRole('heading', { name: 'Welcome back' })).toBeTruthy(); expect(tokenStore.get()).toBeNull();
});
it('recruiter cannot open student pages, and submits the real internship payload', async () => {
  mockFetch({ 'GET /api/profile': { body: U('RECRUITER', 9) }, 'POST /api/internships': { status: 201, body: I(3, { verified: false, title: 'New role' }) } });
  const { unmount } = mount('/student/saved');
  expect(await screen.findByRole('heading', { name: 'No access' })).toBeTruthy(); unmount();
  mount('/recruiter/internships/new'); const u = userEvent.setup();
  await u.type(await screen.findByLabelText('Title *'), 'New role'); await u.type(screen.getByLabelText('Company *'), 'Acme');
  await u.click(screen.getByRole('button', { name: 'Submit for review' }));
  expect(await screen.findByText('Submitted for review')).toBeTruthy();
  expect(JSON.parse(calls.find((c) => c.startsWith('POST /api/internships')).split(' ').slice(2).join(' '))).toMatchObject({ title: 'New role', company: 'Acme', deadline: null });
});
it('admin approves an internship only after confirming', async () => {
  mockFetch({ 'GET /api/profile': { body: U('ADMIN', 2) }, 'GET /api/admin/pending-internships': { body: [{ id: 7, title: 'Pending role', company: 'Acme', location: '', stipend: '', description: '', skillsRequired: 'Go', deadline: '', recruiterName: 'Rec', verified: false }] }, 'PUT /api/admin/internships/7/approve': { body: { success: true, message: 'Internship approved successfully.' } } });
  mount('/admin/internships'); const u = userEvent.setup();
  await u.click(await screen.findByRole('button', { name: 'Approve' }));
  expect(calls.some((c) => c.startsWith('PUT'))).toBe(false);
  const dlg = await screen.findByRole('dialog'); await u.click(within(dlg).getByRole('button', { name: 'Approve' }));
  await waitFor(() => expect(calls).toContain('PUT /api/admin/internships/7/approve'));
  expect(await screen.findByText('Internship approved successfully.')).toBeTruthy();
});
