import { Link } from 'react-router-dom';
import { readiness } from '../api/services.js';
import { useLoad } from '../hooks/useLoad.js';
import { Icon, ErrorState, PageHeader, Ring } from '../components/ui.jsx';

export default function Readiness() {
  const { data: r, error, loading, reload } = useLoad(() => readiness.get(), []);
  return (
    <div className="page container narrow-lg">
      <PageHeader title="Placement readiness" sub="Calculated by GrowNet from your profile." />
      {error ? <ErrorState message={error} onRetry={reload} /> : loading ? <div className="skeleton" style={{ height: 300 }} aria-label="Loading readiness" /> : (<>
        <section className="panel d-flex gap-4 align-items-center flex-wrap">
          <Ring value={r.readinessScore} size={128} label="Readiness score" />
          <div><h2 className="h4">{r.completedItems} of {r.totalItems} checks complete</h2><p className="muted">{r.explanation}</p></div>
        </section>
        <ul className="rlist">{r.checklist.map((c) => (
          <li key={c.name} className={'panel ' + (c.completed ? 'done' : '')}>
            <span className="tick lg">{c.completed ? <Icon name="check" size={16} /> : null}</span>
            <div><h3>{c.name} <span className="visually-hidden">{c.completed ? '(complete)' : '(not complete)'}</span></h3><p className="muted">{c.suggestion}</p></div>
            {!c.completed && <Link to="/student/profile" className="btn-signal btn-sm">Update profile</Link>}
          </li>))}</ul>
        <p className="muted">The checklist updates automatically when your profile changes. It cannot be ticked off manually.</p>
      </>)}
    </div>
  );
}
