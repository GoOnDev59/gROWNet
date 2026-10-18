import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth, HOME } from '../context/AuthContext.jsx';
import { Field, PasswordInput } from '../components/Field.jsx';
import AuthLayout from '../components/AuthLayout.jsx';

export default function Login() {
  const { login } = useAuth();
  const nav = useNavigate();
  const loc = useLocation();
  const [f, setF] = useState({ email: '', password: '' });
  const [errs, setErrs] = useState({});
  const [fail, setFail] = useState('');
  const [busy, setBusy] = useState(false);
  const set = (k) => (e) => { setF({ ...f, [k]: e.target.value }); setErrs((x) => ({ ...x, [k]: undefined })); };
  async function submit(e) {
    e.preventDefault();
    if (busy) return;
    const v = {};
    if (!/^\S+@\S+\.\S+$/.test(f.email)) v.email = 'Enter a valid email address.';
    if (!f.password) v.password = 'Enter your password.';
    setErrs(v); setFail('');
    if (Object.keys(v).length) return;
    setBusy(true);
    try {
      const u = await login({ email: f.email.trim(), password: f.password });
      nav(loc.state?.from?.pathname || HOME[u.role] || '/', { replace: true });
    } catch (err) {
      setFail(err.status === 401 ? 'Email or password is incorrect.' : err.status === 500 ? "We couldn't log you in. Check your email and password. If they are correct, the server may be having trouble." : err.message);
    } finally { setBusy(false); }
  }
  return (
    <AuthLayout title="Welcome back" sub="Log in to continue your search.">
      <form onSubmit={submit} noValidate>
        {loc.state?.registered && !fail && <div className="ok-box" role="status">Account created. Log in to continue.</div>}
        {fail && <div className="alert-box" role="alert">{fail}</div>}
        <Field id="email" label="Email" error={errs.email}>
          <input id="email" type="email" autoComplete="email" value={f.email} onChange={set('email')} aria-invalid={!!errs.email} aria-describedby={errs.email ? 'email-err' : undefined} />
        </Field>
        <Field id="password" label="Password" error={errs.password}>
          <PasswordInput id="password" value={f.password} onChange={set('password')} error={errs.password} autoComplete="current-password" />
        </Field>
        <button className="btn-signal w-100 mt-2" disabled={busy}>{busy ? 'Logging in…' : 'Log in'}</button>
      </form>
      <p className="alt-link">New to GrowNet? <Link to="/register">Create an account</Link></p>
    </AuthLayout>
  );
}
