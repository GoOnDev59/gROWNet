import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../api/authService.js';
import { Field, PasswordInput } from '../components/Field.jsx';
import AuthLayout from '../components/AuthLayout.jsx';

// Client-side hint only. The backend decides what is actually accepted.
const strength = (p) => (p.length < 8 ? 0 : 1 + (/[A-Z]/.test(p) && /[a-z]/.test(p)) + (/\d/.test(p) && /[^A-Za-z0-9]/.test(p)));
const LABEL = ['Too short', 'Okay', 'Good', 'Strong'];
export default function Register() {
  const nav = useNavigate();
  const [f, setF] = useState({ fullName: '', email: '', password: '' });
  const [errs, setErrs] = useState({});
  const [fail, setFail] = useState('');
  const [busy, setBusy] = useState(false);
  const set = (k) => (e) => { setF({ ...f, [k]: e.target.value }); setErrs((x) => ({ ...x, [k]: undefined })); };
  const s = strength(f.password);
  async function submit(e) {
    e.preventDefault();
    if (busy) return;
    const v = {};
    if (f.fullName.trim().length < 2) v.fullName = 'Enter your full name.';
    if (!/^\S+@\S+\.\S+$/.test(f.email)) v.email = 'Enter a valid email address.';
    if (f.password.length < 8) v.password = 'Use 8 to 72 characters.';
    setErrs(v); setFail('');
    if (Object.keys(v).length) return;
    setBusy(true);
    try {
      const body = { fullName: f.fullName.trim(), email: f.email.trim(), password: f.password };
      await register(body);
      nav('/login', { replace: true, state: { registered: true } });
    } catch (err) { setFail(err.message); } finally { setBusy(false); }
  }
  return (
    <AuthLayout title="Create your account" sub="Student accounts only. It takes a minute, and you can finish your profile afterwards.">
      <form onSubmit={submit} noValidate>
        {fail && <div className="alert-box" role="alert">{fail}</div>}
        <Field id="fullName" label="Full name" error={errs.fullName}>
          <input id="fullName" autoComplete="name" maxLength={100} value={f.fullName} onChange={set('fullName')} aria-invalid={!!errs.fullName} aria-describedby={errs.fullName ? 'fullName-err' : undefined} />
        </Field>
        <Field id="email" label="Email" error={errs.email}>
          <input id="email" type="email" autoComplete="email" maxLength={254} value={f.email} onChange={set('email')} aria-invalid={!!errs.email} aria-describedby={errs.email ? 'email-err' : undefined} />
        </Field>
        <Field id="password" label="Password" error={errs.password}>
          <PasswordInput id="password" value={f.password} onChange={set('password')} error={errs.password} autoComplete="new-password" maxLength={72} />
          {f.password && <div className="meter" aria-live="polite"><div className="meter-bars">{[1, 2, 3].map((i) => <i key={i} className={i <= s ? 'on s' + s : ''} />)}</div><span>{LABEL[s]}</span></div>}
        </Field>
        <button className="btn-signal w-100 mt-2" disabled={busy}>{busy ? 'Creating account…' : 'Create account'}</button>
      </form>
      <p className="alt-link">Already registered? <Link to="/login">Log in</Link></p>
    </AuthLayout>
  );
}
