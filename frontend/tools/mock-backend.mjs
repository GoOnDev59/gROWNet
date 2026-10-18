// TEST DOUBLE ONLY. Mirrors the response shapes and error quirks read from the backend source
// (bare 403 for bad tokens, 500 for bad credentials). It is NOT the real backend. Run: node tools/mock-backend.mjs
import http from 'node:http';
const users = {
  'student@test.com': { id: 1, fullName: 'Asha Rao', email: 'student@test.com', password: 'Passw0rd!', role: 'STUDENT', skills: 'React, SQL', college: 'VJTI', branch: 'CS', bio: 'Final year student' },
  'recruiter@test.com': { id: 9, fullName: 'Rhea Recruiter', email: 'recruiter@test.com', password: 'Passw0rd!', role: 'RECRUITER', skills: '', college: '', branch: '', bio: '' },
  'admin@test.com': { id: 2, fullName: 'Ann Admin', email: 'admin@test.com', password: 'Passw0rd!', role: 'ADMIN', skills: '', college: '', branch: '', bio: '' },
};
const day = (n) => new Date(Date.now() + n * 864e5).toISOString().slice(0, 10);
let internships = [
  { id: 1, title: 'Frontend Developer Intern', company: 'Test Co A', location: 'Mumbai', stipend: '15000 per month', description: 'Build UI.', skillsRequired: 'React, CSS, Docker', deadline: day(5), recruiterId: 9, verified: true, deadlineStatus: 'CLOSING_SOON' },
  { id: 2, title: 'Data Analyst Intern', company: 'Test Co B', location: 'Remote', stipend: '', description: 'Analyse data.', skillsRequired: 'SQL, Python', deadline: day(40), recruiterId: 9, verified: true, deadlineStatus: 'UPCOMING' },
  { id: 3, title: 'Backend Intern With A Very Long Title That Should Wrap Nicely On Small Screens', company: 'Test Co C', location: 'Pune', stipend: '10000', description: 'APIs.', skillsRequired: 'Java, SQL, Spring, Docker, AWS, Git, Linux, Testing', deadline: null, recruiterId: 9, verified: true, deadlineStatus: 'NO_DEADLINE' },
  { id: 4, title: 'Pending Role', company: 'Test Co D', location: 'Delhi', stipend: '', description: '', skillsRequired: 'Go', deadline: null, recruiterId: 9, verified: false, deadlineStatus: 'NO_DEADLINE' },
];
let pendingRecruiters = [{ id: 20, fullName: 'Pat Pending', email: 'pat@x.com', college: '', branch: '', role: 'RECRUITER', approved: false }];
const saved = new Set();
const send = (res, status, body) => { res.writeHead(status, { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*', 'Access-Control-Allow-Headers': 'Authorization, Content-Type', 'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS' }); res.end(body === undefined ? '' : JSON.stringify(body)); };
const safe = ({ password, ...u }) => u;
http.createServer((req, res) => {
  if (req.method === 'OPTIONS') return send(res, 204);
  const url = new URL(req.url, 'http://x'), p = url.pathname;
  let raw = ''; req.on('data', (c) => (raw += c)); req.on('end', () => {
    const body = raw ? JSON.parse(raw) : {};
    const tok = (req.headers.authorization || '').replace('Bearer ', '');
    const me = Object.values(users).find((u) => 'tok-' + u.role === tok);
    const pub = req.method === 'GET' && (p === '/api/internships' || /^\/api\/internships\/(\d+|compare)$/.test(p));
    if (!pub && !p.startsWith('/api/auth') && !me) return send(res, 403, { status: 403, error: 'Forbidden' });
    const need = (role) => { if (me.role !== role) { send(res, 403, { success: false, message: 'Only ' + role.toLowerCase() + 's can do this.' }); return false; } return true; };
    if (p === '/api/auth/login') { const u = users[body.email]; return u && u.password === body.password ? send(res, 200, { token: 'tok-' + u.role, tokenType: 'Bearer', user: safe(u) }) : send(res, 500, { success: false, message: 'An unexpected error occurred. Please try again later.' }); }
    if (p === '/api/auth/register') return send(res, 201, { message: 'Registration successful. You can now log in.' });
    if (p === '/api/profile') { if (req.method === 'PUT') Object.assign(me, Object.fromEntries(Object.entries(body).filter(([, v]) => v != null))); return send(res, 200, safe(me)); }
    if (p === '/api/internships' && req.method === 'GET') {
      const k = (url.searchParams.get('keyword') || '').toLowerCase(), l = (url.searchParams.get('location') || '').toLowerCase(), s = (url.searchParams.get('skill') || '').toLowerCase();
      return send(res, 200, internships.filter((i) => i.verified && (!k || (i.title + i.company + i.description).toLowerCase().includes(k)) && (!l || i.location.toLowerCase().includes(l)) && (!s || i.skillsRequired.toLowerCase().includes(s))));
    }
    if (p === '/api/internships/compare') { const ids = url.searchParams.getAll('ids').map(Number); return ids.length < 2 || ids.length > 4 ? send(res, 400, { success: false, message: 'Compare between 2 and 4 internships.' }) : send(res, 200, ids.map((id) => internships.find((i) => i.id === id))); }
    let m;
    if ((m = p.match(/^\/api\/internships\/(\d+)$/))) { const i = internships.find((x) => x.id === +m[1] && x.verified); return i ? send(res, 200, i) : send(res, 404, { success: false, message: 'Internship not found.' }); }
    if (p === '/api/internships' && req.method === 'POST') { if (!need('RECRUITER')) return; const i = { id: 50, ...body, recruiterId: me.id, verified: false, deadlineStatus: 'NO_DEADLINE' }; internships.push(i); return send(res, 201, i); }
    if ((m = p.match(/^\/api\/matching\/(\d+)$/))) { if (!need('STUDENT')) return; const i = internships.find((x) => x.id === +m[1]); const req_ = i.skillsRequired.split(',').map((x) => x.trim()), mine = me.skills.split(',').map((x) => x.trim().toLowerCase()); const ok = req_.filter((x) => mine.includes(x.toLowerCase())), miss = req_.filter((x) => !ok.includes(x)); return send(res, 200, { internshipId: i.id, matchPercentage: Math.round(ok.length * 100 / req_.length), matchedSkills: ok, missingSkills: miss, explanation: 'Matched ' + ok.length + ' of ' + req_.length + ' required skills.' }); }
    if (p === '/api/saved-internships' && req.method === 'GET') return send(res, 200, internships.filter((i) => saved.has(i.id)));
    if ((m = p.match(/^\/api\/saved-internships\/(\d+)$/))) { req.method === 'POST' ? saved.add(+m[1]) : saved.delete(+m[1]); return send(res, req.method === 'POST' ? 201 : 200, { message: 'ok' }); }
    if (p === '/api/readiness') { const a = !!me.skills, b = !!(me.college && me.branch && me.bio); return send(res, 200, { readinessScore: (a + b) * 50, completedItems: a + b, totalItems: 2, explanation: 'Completed ' + (a + b) + ' of 2 applicable checklist items.', checklist: [{ name: 'Complete profile', completed: b, suggestion: b ? 'Profile details completed.' : 'Add your college, branch and bio.' }, { name: 'Add skills', completed: a, suggestion: a ? 'Skills added to your profile.' : 'Add your technical or professional skills.' }] }); }
    if (p === '/api/ai/ask') return setTimeout(() => send(res, 200, { success: true, answer: 'Start with one project and a clear resume.' }), 600);
    if (p === '/api/admin/pending-recruiters') return send(res, 200, pendingRecruiters);
    if (p === '/api/admin/pending-internships') return send(res, 200, internships.filter((i) => !i.verified).map((i) => ({ ...i, recruiterName: 'Rhea Recruiter' })));
    if ((m = p.match(/^\/api\/admin\/recruiters\/(\d+)\/approve$/))) { pendingRecruiters = pendingRecruiters.filter((r) => r.id !== +m[1]); return send(res, 200, { success: true, message: 'Recruiter approved successfully.' }); }
    if ((m = p.match(/^\/api\/admin\/internships\/(\d+)\/(approve|reject)$/))) { const i = internships.find((x) => x.id === +m[1]); i.verified = m[2] === 'approve'; if (m[2] === 'reject') internships = internships.filter((x) => x !== i); return send(res, 200, { success: true, message: 'Internship ' + m[2] + 'd successfully.' }); }
    send(res, 404, { success: false, message: 'nope' });
  });
}).listen(8080, () => console.log('mock backend on 8080'));
