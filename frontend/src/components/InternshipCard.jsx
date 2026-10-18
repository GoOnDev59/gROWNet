import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useStudent } from '../context/StudentContext.jsx';
import { Badge, Chip } from './ui.jsx';
import { deadlineInfo, initials } from '../utils/format.js';
import { skillList } from '../utils/profile.js';

export function MatchBadge({ m }) {
  if (!m) return null;
  if (m.error) return <small className="muted">Match unavailable</small>;
  if (!m.matchedSkills.length && !m.missingSkills.length) return <small className="muted">No skills listed</small>;
  return <span className={'match ' + (m.matchPercentage >= 70 ? 'hi' : m.matchPercentage >= 40 ? 'mid' : 'lo')}><b>{m.matchPercentage}%</b> match</span>;
}
export default function InternshipCard({ i, match, onError }) {
  const { isStudent, savedIds, toggleSave, compareIds, toggleCompare } = useStudent();
  const [busy, setBusy] = useState(false);
  const saved = savedIds.has(i.id), cmp = compareIds.includes(i.id), full = compareIds.length >= 4 && !cmp;
  const d = deadlineInfo(i), skills = skillList(i.skillsRequired);
  const save = async () => { setBusy(true); try { await toggleSave(i.id); } catch (e) { onError?.(e.message); } finally { setBusy(false); } };
  return (
    <article className="icard">
      <header>
        <span className="avatar" aria-hidden="true">{initials(i.company)}</span>
        <div className="grow-1"><h3><Link to={'/internships/' + i.id}>{i.title}</Link></h3><small>{i.company}</small></div>
        {isStudent && <MatchBadge m={match} />}
      </header>
      <dl className="meta">
        <div><dt>Location</dt><dd>{i.location || 'Not specified'}</dd></div>
        <div><dt>Stipend</dt><dd>{i.stipend || 'Not specified'}</dd></div>
      </dl>
      {skills.length > 0 && <div className="chips">{skills.slice(0, 6).map((s) => <Chip key={s}>{s}</Chip>)}{skills.length > 6 && <Chip>+{skills.length - 6}</Chip>}</div>}
      <footer>
        <div>{d.label && <Badge tone={d.tone}>{d.label}</Badge>}{d.text && <small className="d-block mt-1">{d.text}</small>}</div>
        <div className="acts">
          {isStudent && <>
            <label className={'cmp' + (full ? ' dis' : '')}><input type="checkbox" checked={cmp} disabled={full} onChange={() => toggleCompare(i.id)} /> Compare</label>
            <button className="btn-quiet btn-sm" aria-pressed={saved} onClick={save} disabled={busy}>{busy ? '…' : saved ? 'Saved' : 'Save'}</button>
          </>}
          <Link className="btn-ink btn-sm" to={'/internships/' + i.id}>View details</Link>
        </div>
      </footer>
    </article>
  );
}
