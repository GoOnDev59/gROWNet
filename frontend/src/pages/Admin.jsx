import { useState } from 'react';
import { Link } from 'react-router-dom';
import { admin } from '../api/services.js';
import { useLoad } from '../hooks/useLoad.js';
import ConfirmDialog from '../components/ConfirmDialog.jsx';
import { Chip, EmptyState, ErrorState, PageHeader } from '../components/ui.jsx';
import { skillList } from '../utils/profile.js';

export function AdminDashboard() {
  const r = useLoad(() => admin.pendingRecruiters(), []), n = useLoad(() => admin.pendingInternships(), []);
  const C = ({ l, to, title }) => <Link to={to} className="stat link"><small>{title}</small>{l.loading ? <b className="dim">…</b> : l.error ? <><b className="dim">–</b><em>Unavailable</em></> : <b>{l.data.length}</b>}</Link>;
  return (
    <div className="page container">
      <PageHeader title="Admin dashboard" sub="What needs your review." />
      <div className="grid-4"><C l={r} to="/admin/recruiters" title="Recruiters awaiting approval" /><C l={n} to="/admin/internships" title="Internships awaiting review" /></div>
    </div>
  );
}
function Queue({ title, sub, load, empty, render, actions }) {
  const [ask, setAsk] = useState(null), [busy, setBusy] = useState(false), [msg, setMsg] = useState(null);
  const { data, error, loading, reload } = load;
  async function go() {
    setBusy(true);
    try { const r = await ask.run(ask.item.id); setMsg({ ok: true, t: r?.message || 'Done.' }); reload(); } catch (e) { setMsg({ ok: false, t: e.message }); } finally { setBusy(false); setAsk(null); }
  }
  return (
    <div className="page container">
      <PageHeader title={title} sub={sub} />
      {msg && <div className={msg.ok ? 'ok-box' : 'alert-box'} role={msg.ok ? 'status' : 'alert'}>{msg.t}</div>}
      {error ? <ErrorState message={error} onRetry={reload} /> : loading && !data ? <div className="skeleton" style={{ height: 200 }} aria-label="Loading" />
        : !data.length ? <EmptyState title={empty} />
        : <ul className="queue">{data.map((it) => <li key={it.id} className="panel">{render(it)}<div className="acts">{actions(it, setAsk)}</div></li>)}</ul>}
      <ConfirmDialog open={!!ask} busy={busy} title={ask?.title} text={ask?.text} confirmLabel={ask?.label} danger={ask?.danger} onConfirm={go} onCancel={() => setAsk(null)} />
    </div>
  );
}
export function AdminRecruiters() {
  const load = useLoad(() => admin.pendingRecruiters(), []);
  return <Queue title="Recruiter approvals" sub="Recruiters who are waiting for approval." load={load} empty="No recruiters are waiting for approval."
    render={(r) => <div><h2 className="h5">{r.fullName}</h2><p className="muted m-0">{r.email}</p>{(r.college || r.branch) && <small>{[r.college, r.branch].filter(Boolean).join(' · ')}</small>}</div>}
    actions={(r, setAsk) => <button className="btn-signal btn-sm" onClick={() => setAsk({ item: r, run: admin.approveRecruiter, title: 'Approve ' + r.fullName + '?', text: 'This marks the recruiter as approved.', label: 'Approve' })}>Approve</button>} />;
}
export function AdminInternships() {
  const load = useLoad(() => admin.pendingInternships(), []);
  return <Queue title="Internship review" sub="New postings that students cannot see yet." load={load} empty="No internships are waiting for review."
    render={(i) => <div><h2 className="h5">{i.title}</h2><p className="muted m-0">{i.company} · {i.location || 'No location'} · {i.stipend || 'No stipend'} · posted by {i.recruiterName}</p>
      {i.deadline && <small>Deadline {i.deadline}</small>}{i.description && <p className="clamp mt-2">{i.description}</p>}<div className="chips mt-2">{skillList(i.skillsRequired).map((s) => <Chip key={s}>{s}</Chip>)}</div></div>}
    actions={(i, setAsk) => <>
      <button className="btn-quiet btn-sm" onClick={() => setAsk({ item: i, run: admin.rejectInternship, title: 'Reject "' + i.title + '"?', text: 'It will not be shown to students.', label: 'Reject', danger: true })}>Reject</button>
      <button className="btn-signal btn-sm" onClick={() => setAsk({ item: i, run: admin.approveInternship, title: 'Approve "' + i.title + '"?', text: 'Students will be able to see it immediately.', label: 'Approve' })}>Approve</button></>} />;
}
