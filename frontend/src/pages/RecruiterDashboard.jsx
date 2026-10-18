import { Link } from 'react-router-dom';
import { internships } from '../api/services.js';
import { useAuth } from '../context/AuthContext.jsx';
import { useLoad } from '../hooks/useLoad.js';
import { Badge, EmptyState, ErrorState, PageHeader } from '../components/ui.jsx';
import { deadlineInfo } from '../utils/format.js';

export default function RecruiterDashboard() {
  const { user } = useAuth();
  // No "my internships" endpoint exists, so this filters the public approved list by recruiterId.
  const { data, error, loading, reload } = useLoad(() => internships.list({}), []);
  const mine = (data || []).filter((i) => i.recruiterId === user.id);
  return (
    <div className="page container">
      <PageHeader title="Recruiter dashboard" sub={user.fullName + ' · ' + user.email} action={<Link to="/recruiter/internships/new" className="btn-signal">Post an internship</Link>} />
      <section className="panel">
        <h2 className="h5">Your approved internships</h2>
        {error ? <ErrorState message={error} onRetry={reload} /> : loading ? <div className="skeleton" style={{ height: 140 }} />
          : !mine.length ? <EmptyState title="No approved internships yet" text="Internships you post are reviewed by an admin before they appear here." action={<Link to="/recruiter/internships/new" className="btn-signal">Post an internship</Link>} />
          : <ul className="mini-list">{mine.map((i) => { const d = deadlineInfo(i); return <li key={i.id}><div><Link to={'/internships/' + i.id}><b>{i.title}</b></Link><small className="d-block">{i.company} · {d.text || 'No deadline'}</small></div>{d.label && <Badge tone={d.tone}>{d.label}</Badge>}</li>; })}</ul>}
        <p className="muted mt-3 mb-0">Only approved internships are listed. The backend does not provide a way to see your pending or rejected postings, or to edit or delete them.</p>
      </section>
    </div>
  );
}
