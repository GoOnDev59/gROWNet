const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const KEY = 'grownet.token';
// sessionStorage limits exposure to the tab, but any XSS can still read it. Not fully secure.
export const tokenStore = {
  get: () => sessionStorage.getItem(KEY),
  set: (t) => sessionStorage.setItem(KEY, t),
  clear: () => sessionStorage.removeItem(KEY),
};
export class ApiError extends Error {
  constructor(status, message, data) { super(message); this.status = status; this.data = data; }
}
let onUnauthorized = () => {};
export const setUnauthorizedHandler = (fn) => { onUnauthorized = fn; };
const fallback = {
  400: 'Some details look wrong. Check the form and try again.',
  401: 'Your session has ended or the credentials are wrong.',
  403: 'Your account does not have access to this.',
  404: 'We could not find what you asked for.',
  500: 'The server hit a problem. Try again in a moment.',
};
export async function api(path, { method = 'GET', body, auth = true } = {}) {
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  const t = tokenStore.get();
  if (auth && t) headers.Authorization = 'Bearer ' + t;
  let res;
  try {
    res = await fetch(BASE + path, { method, headers, body: body !== undefined ? JSON.stringify(body) : undefined });
  } catch {
    throw new ApiError(0, 'Cannot reach the GrowNet server. Make sure the backend is running on ' + BASE + '.');
  }
  const text = await res.text();
  let data = null;
  if (text) { try { data = JSON.parse(text); } catch { data = text; } }
  if (!res.ok) {
    // Spring Security answers an expired/invalid token with a bare 403 (no `success` field). Re-check the session.
    if (res.status === 403 && auth && t && !(data && data.success === false)) {
      const alive = await fetch(BASE + '/api/profile', { headers: { Authorization: 'Bearer ' + t } }).then((r) => r.ok).catch(() => true);
      if (!alive) { tokenStore.clear(); onUnauthorized(); throw new ApiError(401, 'Your session has expired. Log in again.'); }
    }
    if (res.status === 401 && auth) { tokenStore.clear(); onUnauthorized(); }
    const msg = (data && (data.message || data.error)) || fallback[res.status] || 'Something went wrong.';
    throw new ApiError(res.status, msg, data);
  }
  return data;
}
