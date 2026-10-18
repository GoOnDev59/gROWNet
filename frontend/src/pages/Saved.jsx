import { useState } from 'react';
import { Link } from 'react-router-dom';
import { saved } from '../api/services.js';
import { useLoad } from '../hooks/useLoad.js';
import { useMatches } from '../hooks/useMatches.js';
import { useStudent } from '../context/StudentContext.jsx';
import InternshipCard from '../components/InternshipCard.jsx';
import { PageHeader, EmptyState, ErrorState, ListSkeleton } from '../components/ui.jsx';

export default function Saved() {
  const { savedIds } = useStudent();
  const { data, error, loading, reload } = useLoad(() => saved.list(), [savedIds.size]);
  const [note, setNote] = useState('');
  const list = data || [];
  const matches = useMatches(list.map((i) => i.id), true);
  return (
    <div className="page container">
      <PageHeader title="Saved internships" sub="Roles you want to come back to." />
      {note && <div className="alert-box" role="alert">{note}</div>}
      {error ? <ErrorState message={error} onRetry={reload} />
        : loading && !data ? <ListSkeleton n={2} />
        : !list.length ? <EmptyState title="Nothing saved yet" text="Save internships while browsing and they will be listed here." action={<Link to="/internships" className="btn-signal">Browse internships</Link>} />
        : <div className="grid-2">{list.map((i) => <InternshipCard key={i.id} i={i} match={matches[i.id]} onError={setNote} />)}</div>}
    </div>
  );
}
