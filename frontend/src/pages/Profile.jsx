import { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../api/authService.js';
import { useAuth } from '../context/AuthContext.jsx';
import { Field } from '../components/Field.jsx';
import { Ring, PageHeader, ErrorState, Icon } from '../components/ui.jsx';
import { completeness } from '../utils/profile.js';

export default function Profile() {
  const { setUser } = useAuth();
  const [f, setF] = useState(null);
  const [skillsWasArray, setArr] = useState(false);
  const [state, setState] = useState({ load: true, err: '', ok: '', busy: false, errs: {} });
  const load = () => {
    setState((s) => ({ ...s, load: true, err: '' }));
    getProfile().then((u) => {
      setArr(Array.isArray(u.skills));
      setF({ fullName: u.fullName || '', email: u.email || '', college: u.college || '', branch: u.branch || '',
        skills: Array.isArray(u.skills) ? u.skills.join(', ') : u.skills || '', bio: u.bio || '' });
      setState((s) => ({ ...s, load: false }));
    }).catch((e) => setState((s) => ({ ...s, load: false, err: e.message })));
  };
  useEffect(load, []);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  async function save(e) {
    e.preventDefault();
    if (state.busy) return;
    const errs = {};
    if (f.fullName.trim().length < 2) errs.fullName = 'Enter your full name.';
    setState((s) => ({ ...s, errs, ok: '', err: '' }));
    if (Object.keys(errs).length) return;
    setState((s) => ({ ...s, busy: true }));
    const skills = skillsWasArray ? f.skills.split(',').map((s) => s.trim()).filter(Boolean) : f.skills;
    try {
      const u = await updateProfile({ fullName: f.fullName.trim(), college: f.college, branch: f.branch, skills, bio: f.bio });
      if (u && u.role) setUser(u);
      setState((s) => ({ ...s, busy: false, ok: 'Profile saved.' }));
    } catch (er) { setState((s) => ({ ...s, busy: false, err: er.message })); }
  }
  if (state.load) return <div className="page container"><div className="skeleton" style={{ height: 380 }} aria-label="Loading profile" /></div>;
  if (!f) return <div className="page container"><ErrorState message={state.err} onRetry={load} /></div>;
  const c = completeness(f);
  const initials = f.fullName.split(' ').filter(Boolean).slice(0, 2).map((x) => x[0].toUpperCase()).join('');
  return (
    <div className="page container">
      <PageHeader title="Your profile" sub="Complete profiles give more accurate matches." />
      {state.err && <div className="alert-box" role="alert">{state.err}</div>}
      {state.ok && <div className="ok-box" role="status">{state.ok}</div>}
      <div className="split">
        <form className="panel" onSubmit={save} noValidate>
          <Field id="fullName" label="Full name" error={state.errs.fullName}><input id="fullName" value={f.fullName} onChange={set('fullName')} aria-invalid={!!state.errs.fullName} /></Field>
          <Field id="email" label="Email" hint="Email cannot be changed here."><input id="email" value={f.email} readOnly /></Field>
          <div className="row g-3"><div className="col-md-6"><Field id="college" label="College"><input id="college" value={f.college} onChange={set('college')} /></Field></div>
          <div className="col-md-6"><Field id="branch" label="Branch"><input id="branch" value={f.branch} onChange={set('branch')} /></Field></div></div>
          <Field id="skills" label="Skills" hint="Separate skills with commas."><input id="skills" value={f.skills} onChange={set('skills')} /></Field>
          <Field id="bio" label="Bio"><textarea id="bio" rows="4" value={f.bio} onChange={set('bio')} /></Field>
          <button className="btn-signal" disabled={state.busy}>{state.busy ? 'Saving…' : 'Save changes'}</button>
        </form>
        <aside className="panel side">
          <span className="avatar lg" aria-hidden="true">{initials || '?'}</span>
          <b>{f.fullName || 'Your name'}</b><small className="muted">{f.email}</small>
          <Ring value={c.pct} label="Profile completeness" />
          <ul className="checks">{c.items.map((i) => <li key={i.key} className={i.done ? 'done' : ''}><span className="tick">{i.done && <Icon name="check" size={14} />}</span>{i.label}</li>)}</ul>
        </aside>
      </div>
    </div>
  );
}
