// Every call below maps to an endpoint verified in the backend source. See docs/INTEGRATION.md.
import { api } from './client.js';
const qs = (o = {}) => {
  const p = new URLSearchParams();
  Object.entries(o).forEach(([k, v]) => [].concat(v ?? []).forEach((x) => { if (String(x).trim() !== '') p.append(k, String(x).trim()); }));
  const s = p.toString();
  return s ? '?' + s : '';
};
export const internships = {
  list: (f) => api('/api/internships' + qs(f), { auth: false }),          // keyword, location, skill
  get: (id) => api('/api/internships/' + encodeURIComponent(id), { auth: false }),
  compare: (ids) => api('/api/internships/compare' + qs({ ids }), { auth: false }), // 2 to 4 ids
  create: (body) => api('/api/internships', { method: 'POST', body }),     // RECRUITER only
};
export const matching = { get: (id) => api('/api/matching/' + id) };        // STUDENT only
export const saved = {
  list: () => api('/api/saved-internships'),
  save: (id) => api('/api/saved-internships/' + id, { method: 'POST' }),
  remove: (id) => api('/api/saved-internships/' + id, { method: 'DELETE' }),
};
export const readiness = { get: () => api('/api/readiness') };
export const ai = { ask: (question) => api('/api/ai/ask', { method: 'POST', body: { question } }) };
export const admin = {
  pendingRecruiters: () => api('/api/admin/pending-recruiters'),
  approveRecruiter: (id) => api('/api/admin/recruiters/' + id + '/approve', { method: 'PUT' }),
  pendingInternships: () => api('/api/admin/pending-internships'),
  approveInternship: (id) => api('/api/admin/internships/' + id + '/approve', { method: 'PUT' }),
  rejectInternship: (id) => api('/api/admin/internships/' + id + '/reject', { method: 'PUT' }),
};

export const applications = {
  apply: (internshipId) => api('/api/applications/' + encodeURIComponent(internshipId), { method: 'POST' }),
  my: () => api('/api/applications/my'),
};