import { Link } from 'react-router-dom';
import { internships, readiness, saved } from '../api/services.js';
import { useAuth } from '../context/AuthContext.jsx';
import { useLoad } from '../hooks/useLoad.js';
import { useMatches } from '../hooks/useMatches.js';
import { skillList } from '../utils/profile.js';
import { deadlineInfo } from '../utils/format.js';
import { Badge, Chip, EmptyState, ErrorState, PageHeader, Ring } from '../components/ui.jsx';
import { MatchBadge } from '../components/InternshipCard.jsx';

const greet = () => { const h = new Date().getHours(); return h < 12 ? 'Good morning' : h < 18 ? 'Good afternoon' : 'Good evening'; };
const Stat = ({ label, l, children }) => <div className="stat"><small>{label}</small>{l.loading ? <b className="dim">…</b> : l.error ? <><b className="dim">–</b><em>Unavailable</em></> : children}</div>;
export default function StudentDashboard() {
  const { user } = useAuth();
  const rd = useLoad(() => readiness.get(), []), sv = useLoad(() => saved.list(), []), all = useLoad(() => internships.list({}), []);
  const skills = skillList(user.skills), pool = (all.data || []).slice(0, 12);
  const matches = useMatches(pool.map((i) => i.id), true);
  const top = pool.filter((i) => matches[i.id]?.matchPercentage != null && (matches[i.id].matchedSkills.length + matches[i.id].missingSkills.length) > 0)
    .sort((a, b) => matches[b.id].matchPercentage - matches[a.id].matchPercentage).slice(0, 3);
  const upcoming = (sv.data || []).filter((i) => i.deadline && i.deadlineStatus !== 'EXPIRED').sort((a, b) => a.deadline.localeCompare(b.deadline));
  const next = upcoming[0] && deadlineInfo(upcoming[0]);
  return (
    <div className="page container">
      <PageHeader title={greet() + ', ' + user.fullName.split(' ')[0]} sub="Your search at a glance." action={<Link to="/internships" className="btn-signal">Browse internships</Link>} />
      <div className="grid-4">
        <Stat label="Readiness score" l={rd}><b>{rd.data?.readinessScore}%</b></Stat>
        <Stat label="Skills listed" l={{}}><b>{skills.length}</b></Stat>
        <Stat label="Saved internships" l={sv}><b>{sv.data?.length}</b></Stat>
        <Stat label="Next saved deadline" l={sv}>{next ? <><b>{next.days === 0 ? 'Today' : next.days + 'd'}</b><em>{upcoming[0].title}</em></> : <><b className="dim">–</b><em>None upcoming</em></>}</Stat>
      </div>
      <div className="dash">
        <section className="panel wide">
          <div className="d-flex justify-content-between align-items-center mb-3"><h2 className="h5 m-0">Top matches from current listings</h2><Link to="/internships">See all</Link></div>
          {all.error ? <ErrorState message={all.error} onRetry={all.reload} />
            : all.loading ? <div className="skeleton" style={{ height: 120 }} />
            : !pool.length ? <EmptyState title="No internships yet" text="Approved internships will appear here." />
            : !skills.length ? <EmptyState title="Add your skills to see matches" text="Matches compare your skills with what each internship requires." action={<Link to="/student/profile" className="btn-signal">Add skills</Link>} />
            : !top.length ? <p className="muted">Matches are loading or unavailable right now.</p>
            : <ul className="mini-list">{top.map((i) => <li key={i.id}><div><Link to={'/internships/' + i.id}><b>{i.title}</b></Link><small className="d-block">{i.company}</small></div><MatchBadge m={matches[i.id]} /></li>)}</ul>}
        </section>
        <section className="panel">
          <h2 className="h5">Readiness</h2>
          {rd.error ? <ErrorState message={rd.error} onRetry={rd.reload} /> : rd.loading ? <div className="skeleton" style={{ height: 130 }} /> : (
            <div className="prof-row"><Ring value={rd.data.readinessScore} label="Readiness" />
              <ul className="checks">{rd.data.checklist.map((c) => <li key={c.name} className={c.completed ? 'done' : ''}><span className="tick" />{c.name}</li>)}</ul></div>)}
          <Link to="/student/readiness" className="btn-quiet mt-3">View details</Link>
        </section>
        <section className="panel">
          <h2 className="h5">Upcoming deadlines</h2>
          {sv.error ? <ErrorState message={sv.error} onRetry={sv.reload} /> : sv.loading ? <div className="skeleton" style={{ height: 130 }} />
            : !upcoming.length ? <p className="muted">Save internships with deadlines and they will be listed here.</p>
            : <ul className="mini-list">{upcoming.slice(0, 4).map((i) => { const d = deadlineInfo(i); return <li key={i.id}><div><Link to={'/internships/' + i.id}>{i.title}</Link><small className="d-block">{d.text}</small></div><Badge tone={d.tone}>{d.label}</Badge></li>; })}</ul>}
        </section>
        <section className="panel wide">
          <h2 className="h5">Your skills</h2>
          {skills.length ? <div className="chips">{skills.map((s) => <Chip key={s}>{s}</Chip>)}</div> : <p className="muted">No skills yet.</p>}
          <Link to="/student/profile" className="btn-quiet mt-3">Edit profile</Link>
        </section>
      </div>
    </div>
  );
}
