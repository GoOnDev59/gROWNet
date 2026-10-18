import { Link } from 'react-router-dom';
import { internships } from '../api/services.js';
import { useLoad } from '../hooks/useLoad.js';
import { useMatches } from '../hooks/useMatches.js';
import { useStudent } from '../context/StudentContext.jsx';
import { Badge, Chip, EmptyState, ErrorState, PageHeader } from '../components/ui.jsx';
import { MatchBadge } from '../components/InternshipCard.jsx';
import { deadlineInfo } from '../utils/format.js';
import { skillList } from '../utils/profile.js';

export default function Compare() {
  const { compareIds, toggleCompare, clearCompare } = useStudent();
  const key = compareIds.join(',');
  const { data, error, loading, reload } = useLoad(() => (compareIds.length >= 2 ? internships.compare(compareIds) : Promise.resolve([])), [key]);
  const matches = useMatches(compareIds, true);
  const list = data || [];
  const head = <PageHeader title="Compare internships" sub="Side by side, up to four." action={compareIds.length > 0 && <button className="btn-quiet" onClick={clearCompare}>Clear all</button>} />;
  if (compareIds.length < 2) return <div className="page container">{head}<EmptyState title="Pick at least two internships" text="Tick Compare on internship cards, then come back here." action={<Link to="/internships" className="btn-signal">Browse internships</Link>} /></div>;
  if (error) return <div className="page container">{head}<ErrorState message={error + ' You can clear your selection and try again.'} onRetry={reload} /></div>;
  if (loading && !data) return <div className="page container">{head}<div className="skeleton" style={{ height: 360 }} aria-label="Loading comparison" /></div>;
  const rows = [
    ['Company', (i) => i.company], ['Location', (i) => i.location || 'Not specified'], ['Stipend', (i) => i.stipend || 'Not specified'],
    ['Required skills', (i) => <div className="chips">{skillList(i.skillsRequired).map((s) => <Chip key={s}>{s}</Chip>)}</div>],
    ['Deadline', (i) => { const d = deadlineInfo(i); return <>{d.label && <Badge tone={d.tone}>{d.label}</Badge>}<small className="d-block">{d.text}</small></>; }],
    ['Your match', (i) => <MatchBadge m={matches[i.id]} />],
  ];
  return (
    <div className="page container">{head}
      <div className="table-scroll"><table className="cmp-table">
        <thead><tr><th scope="col"><span className="visually-hidden">Attribute</span></th>{list.map((i) => <th scope="col" key={i.id}><Link to={'/internships/' + i.id}>{i.title}</Link><button className="btn-quiet btn-sm d-block mt-2" onClick={() => toggleCompare(i.id)} aria-label={'Remove ' + i.title + ' from comparison'}>Remove</button></th>)}</tr></thead>
        <tbody>{rows.map(([label, fn]) => <tr key={label}><th scope="row">{label}</th>{list.map((i) => <td key={i.id}>{fn(i)}</td>)}</tr>)}</tbody>
      </table></div>
    </div>
  );
}
