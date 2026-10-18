import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { internships } from '../api/services.js';
import { useLoad } from '../hooks/useLoad.js';
import { useMatches } from '../hooks/useMatches.js';
import { useStudent, MAX_COMPARE } from '../context/StudentContext.jsx';
import InternshipCard from '../components/InternshipCard.jsx';
import { PageHeader, EmptyState, ErrorState, ListSkeleton } from '../components/ui.jsx';

const EMPTY = { keyword: '', location: '', skill: '' };
export default function Internships() {
  const { isStudent, compareIds, clearCompare } = useStudent();
  const [f, setF] = useState(EMPTY), [q, setQ] = useState(EMPTY), [sort, setSort] = useState('default'), [note, setNote] = useState('');
  useEffect(() => { const t = setTimeout(() => setQ(f), 350); return () => clearTimeout(t); }, [f]);
  const { data, error, loading, reload } = useLoad(() => internships.list(q), [q]);
  const list = data || [];
  const matches = useMatches(list.slice(0, 40).map((i) => i.id), isStudent);
  const rank = (i) => matches[i.id]?.matchPercentage ?? -1;
  const dl = (i) => (i.deadline && i.deadlineStatus !== 'EXPIRED' ? i.deadline : '9999');
  const sorted = useMemo(() => {
    const a = [...list];
    if (sort === 'deadline') a.sort((x, y) => dl(x).localeCompare(dl(y)));
    if (sort === 'match') a.sort((x, y) => rank(y) - rank(x));
    if (sort === 'title') a.sort((x, y) => x.title.localeCompare(y.title));
    return a;
  }, [list, sort, matches]); // eslint-disable-line
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const filtered = f.keyword || f.location || f.skill;
  return (
    <div className="page container">
      <PageHeader title="Internships" sub="Approved internships from recruiters on GrowNet." />
      <form className="panel filters" role="search" onSubmit={(e) => e.preventDefault()}>
        <div><label htmlFor="kw">Search</label><input id="kw" placeholder="Title, company or keyword" value={f.keyword} onChange={set('keyword')} /></div>
        <div><label htmlFor="loc">Location</label><input id="loc" placeholder="e.g. Mumbai" value={f.location} onChange={set('location')} /></div>
        <div><label htmlFor="sk">Skill</label><input id="sk" placeholder="e.g. React" value={f.skill} onChange={set('skill')} /></div>
        <div><label htmlFor="so">Sort by</label>
          <select id="so" value={sort} onChange={(e) => setSort(e.target.value)}>
            <option value="default">Default</option><option value="deadline">Deadline, soonest</option><option value="title">Title, A to Z</option>
            {isStudent && <option value="match">Best match</option>}
          </select></div>
        {filtered && <button type="button" className="btn-quiet" onClick={() => setF(EMPTY)}>Clear filters</button>}
      </form>
      {note && <div className="alert-box" role="alert">{note}</div>}
      <p className="count" aria-live="polite">{loading ? 'Loading…' : error ? '' : list.length + (list.length === 1 ? ' internship' : ' internships')}</p>
      {error ? <ErrorState message={error} onRetry={reload} />
        : loading && !data ? <ListSkeleton />
        : !list.length ? <EmptyState title={filtered ? 'No internships match these filters' : 'No internships yet'} text={filtered ? 'Try fewer words or clear the filters.' : 'Approved internships will appear here.'} action={filtered && <button className="btn-quiet" onClick={() => setF(EMPTY)}>Clear filters</button>} />
        : <div className={'grid-2' + (loading ? ' fade' : '')}>{sorted.map((i) => <InternshipCard key={i.id} i={i} match={matches[i.id]} onError={setNote} />)}</div>}
      {isStudent && compareIds.length > 0 && (
        <div className="tray" role="region" aria-label="Comparison selection">
          <span>{compareIds.length} of {MAX_COMPARE} selected</span>
          <button className="btn-quiet btn-sm" onClick={clearCompare}>Clear</button>
          {compareIds.length >= 2 ? <Link to="/student/compare" className="btn-signal btn-sm">Compare</Link> : <span className="muted">Select at least 2</span>}
        </div>)}
    </div>
  );
}
