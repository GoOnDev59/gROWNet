import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { internships, applications } from '../api/services.js';
import { useLoad } from '../hooks/useLoad.js';
import { useMatches } from '../hooks/useMatches.js';
import { useStudent } from '../context/StudentContext.jsx';
import { Badge, Chip, EmptyState, ErrorState, Ring } from '../components/ui.jsx';
import { deadlineInfo, initials } from '../utils/format.js';
import { skillList } from '../utils/profile.js';

export default function InternshipDetail() {
  const { id } = useParams();
  const { isStudent, savedIds, toggleSave, compareIds, toggleCompare } = useStudent();
  const { data: i, error, loading, status, reload } = useLoad(() => internships.get(id), [id]);
  const m = useMatches(i ? [i.id] : [], isStudent)[i?.id];

  const [busy, setBusy] = useState(false);
  const [applying, setApplying] = useState(false);
  const [applied, setApplied] = useState(false);
  const [msg, setMsg] = useState({ t: '', ok: true });

  if (loading) {
    return <div className="page container"><div className="skeleton" style={{ height: 420 }} aria-label="Loading internship" /></div>;
  }

  if (status === 404) {
    return <div className="page container"><EmptyState title="Internship not found" text="It may have been removed or is not approved yet." action={<Link to="/internships" className="btn-ink">Browse internships</Link>} /></div>;
  }

  if (error) {
    return <div className="page container"><ErrorState message={error} onRetry={reload} /></div>;
  }

  const d = deadlineInfo(i);
  const skills = skillList(i.skillsRequired);
  const saved = savedIds.has(i.id);
  const cmp = compareIds.includes(i.id);

  const save = async () => {
    setBusy(true);
    try {
      const now = await toggleSave(i.id);
      setMsg({ t: now ? 'Saved to your list.' : 'Removed from your saved list.', ok: true });
    } catch (e) {
      setMsg({ t: e.message, ok: false });
    } finally {
      setBusy(false);
    }
  };

  const apply = async () => {
    setApplying(true);
    setMsg({ t: '', ok: true });

    try {
      await applications.apply(i.id);
      setApplied(true);
      setMsg({ t: 'Application submitted successfully!', ok: true });
    } catch (e) {
      const message = e.message || 'Could not submit your application.';
      if (/already applied/i.test(message)) {
        setApplied(true);
        setMsg({ t: 'You have already applied for this internship.', ok: true });
      } else {
        setMsg({ t: message, ok: false });
      }
    } finally {
      setApplying(false);
    }
  };

  return (
    <div className="page container">
      <Link to="/internships" className="back">Back to internships</Link>

      <div className="split mt-3">
        <article className="panel">
          <div className="d-flex gap-3 align-items-center mb-3">
            <span className="avatar lg" aria-hidden="true">{initials(i.company)}</span>
            <div>
              <h1 className="h2">{i.title}</h1>
              <p className="muted">{i.company}</p>
            </div>
          </div>

          <dl className="meta big">
            <div><dt>Location</dt><dd>{i.location || 'Not specified'}</dd></div>
            <div><dt>Stipend</dt><dd>{i.stipend || 'Not specified'}</dd></div>
            <div><dt>Deadline</dt><dd>{d.label && <Badge tone={d.tone}>{d.label}</Badge>} {d.text}</dd></div>
          </dl>

          <h2 className="h5 mt-4">About this internship</h2>
          <p className="prose">{i.description || 'The recruiter has not added a description.'}</p>

          <h2 className="h5 mt-4">Required skills</h2>
          {skills.length
            ? <div className="chips">{skills.map((s) => <Chip key={s}>{s}</Chip>)}</div>
            : <p className="muted">No skills listed.</p>}
        </article>

        <aside className="panel side-stack">
          {isStudent ? (
            <>
              <h2 className="h5">Your match</h2>

              {!m ? <div className="skeleton" style={{ height: 120 }} />
                : m.error ? <p className="muted">{m.error}</p>
                : (
                  <>
                    <div className="d-flex gap-3 align-items-center">
                      <Ring value={m.matchPercentage} label="Match" />
                      <p>{m.explanation}</p>
                    </div>

                    {m.matchedSkills.length > 0 && (
                      <>
                        <h3 className="h6">You have</h3>
                        <div className="chips">{m.matchedSkills.map((s) => <Chip key={s} tone="ok">{s}</Chip>)}</div>
                      </>
                    )}

                    {m.missingSkills.length > 0 && (
                      <>
                        <h3 className="h6">To build</h3>
                        <div className="chips">{m.missingSkills.map((s) => <Chip key={s} tone="gap">{s}</Chip>)}</div>
                        <p className="muted">Add skills you already have in your <Link to="/student/profile">profile</Link> to improve this match.</p>
                      </>
                    )}
                  </>
                )}

              {msg.t && (
                <div className={msg.ok ? 'ok-box' : 'alert-box'} role={msg.ok ? 'status' : 'alert'}>
                  {msg.t}
                </div>
              )}

              <button
                className="btn-signal"
                onClick={apply}
                disabled={applying || applied}
              >
                {applying ? 'Submitting application…' : applied ? 'Application submitted' : 'Apply Now'}
              </button>

              <button className="btn-quiet" onClick={save} disabled={busy} aria-pressed={saved}>
                {busy ? 'Working…' : saved ? 'Remove from saved' : 'Save internship'}
              </button>

              <button
                className="btn-quiet"
                onClick={() => toggleCompare(i.id)}
                aria-pressed={cmp}
                disabled={!cmp && compareIds.length >= 4}
              >
                {cmp ? 'Remove from comparison' : 'Add to comparison'}
              </button>
            </>
          ) : (
            <p className="muted">Log in with a student account to apply, see your match, and save internships.</p>
          )}
        </aside>
      </div>
    </div>
  );
}