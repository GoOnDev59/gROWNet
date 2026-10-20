
import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth, HOME } from '../context/AuthContext.jsx';
import { api } from '../api/client.js';
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

  const [resetMode, setResetMode] = useState(false);
  const [otpSent, setOtpSent] = useState(false);
  const [mobileNumber, setMobileNumber] = useState('');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [resetMessage, setResetMessage] = useState('');
  const [resetError, setResetError] = useState('');

  const set = (k) => (e) => {
    setF((old) => ({ ...old, [k]: e.target.value }));
    setErrs((old) => ({ ...old, [k]: undefined }));
  };

  async function submit(e) {
    e.preventDefault();
    if (busy) return;

    const v = {};
    if (!/^\S+@\S+\.\S+$/.test(f.email)) {
      v.email = 'Enter a valid email address.';
    }
    if (!f.password) {
      v.password = 'Enter your password.';
    }

    setErrs(v);
    setFail('');
    if (Object.keys(v).length) return;

    setBusy(true);
    try {
      const u = await login({
        email: f.email.trim(),
        password: f.password,
      });

      nav(loc.state?.from?.pathname || HOME[u.role] || '/', {
        replace: true,
      });
    } catch (err) {
      setFail(
        err.status === 401
          ? 'Email or password is incorrect.'
          : err.status === 500
            ? "We couldn't log you in. Check your email and password. If they are correct, the server may be having trouble."
            : err.message
      );
    } finally {
      setBusy(false);
    }
  }

  async function requestOtp(e) {
    e.preventDefault();
    if (busy) return;

    setResetError('');
    setResetMessage('');

    if (!/^\+?[0-9\s()-]{7,20}$/.test(mobileNumber.trim())) {
      setResetError('Enter a valid mobile number.');
      return;
    }

    setBusy(true);
    try {
      const result = await api('/api/auth/forgot-password', {
        method: 'POST',
        auth: false,
        body: { mobileNumber: mobileNumber.trim() },
      });

      setOtpSent(true);
      setResetMessage(
        result?.message ||
          'If an account exists for this number, an OTP has been sent.'
      );
    } catch (err) {
      setResetError(
        err.message || 'Could not request an OTP. Please try again.'
      );
    } finally {
      setBusy(false);
    }
  }

  async function resetPassword(e) {
    e.preventDefault();
    if (busy) return;

    setResetError('');
    setResetMessage('');

    if (!/^\d{6}$/.test(otp)) {
      setResetError('Enter the six-digit OTP.');
      return;
    }

    if (newPassword.length < 8 || newPassword.length > 72) {
      setResetError('Your new password must be between 8 and 72 characters.');
      return;
    }

    setBusy(true);
    try {
      const result = await api('/api/auth/reset-password', {
        method: 'POST',
        auth: false,
        body: {
          mobileNumber: mobileNumber.trim(),
          otp,
          newPassword,
        },
      });

      setResetMessage(
        result?.message || 'Password reset successful. You can now log in.'
      );
      setResetMode(false);
      setOtpSent(false);
      setF((old) => ({ ...old, password: '' }));
      setOtp('');
      setNewPassword('');
    } catch (err) {
      setResetError(
        err.message || 'Password reset failed. Please try again.'
      );
    } finally {
      setBusy(false);
    }
  }

  function openReset() {
    setResetMode(true);
    setOtpSent(false);
    setMobileNumber('');
    setOtp('');
    setNewPassword('');
    setResetError('');
    setResetMessage('');
    setFail('');
  }

  function backToLogin() {
    setResetMode(false);
    setOtpSent(false);
    setResetError('');
    setResetMessage('');
  }

  return (
    <AuthLayout
      title={resetMode ? 'Reset your password' : 'Welcome back'}
      sub={
        resetMode
          ? 'Recover access to your GrowNet account.'
          : 'Log in to continue your search.'
      }
    >
      {resetMode ? (
        <form onSubmit={otpSent ? resetPassword : requestOtp} noValidate>
          {resetMessage && (
            <div className="ok-box" role="status">
              {resetMessage}
            </div>
          )}

          {resetError && (
            <div className="alert-box" role="alert">
              {resetError}
            </div>
          )}

          <Field id="reset-mobile" label="Registered mobile number">
            <input
              id="reset-mobile"
              type="tel"
              autoComplete="tel"
              value={mobileNumber}
              onChange={(e) => setMobileNumber(e.target.value)}
              placeholder="Enter your registered mobile number"
              required
            />
          </Field>

          {otpSent && (
            <>
              <Field id="reset-otp" label="Six-digit OTP">
                <input
                  id="reset-otp"
                  type="text"
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  maxLength={6}
                  value={otp}
                  onChange={(e) =>
                    setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))
                  }
                  placeholder="Enter OTP"
                  required
                />
              </Field>

              <Field id="new-password" label="New password">
                <PasswordInput
                  id="new-password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  autoComplete="new-password"
                />
              </Field>
            </>
          )}

          <button
            type="submit"
            className="btn-signal w-100 mt-2"
            disabled={busy}
          >
            {busy
              ? 'Please wait...'
              : otpSent
                ? 'Reset password'
                : 'Request OTP'}
          </button>

          {otpSent && (
            <button
              type="button"
              className="btn-signal w-100 mt-2"
              disabled={busy}
              onClick={requestOtp}
            >
              Request a new OTP
            </button>
          )}

          <p className="alt-link">
            <button
              type="button"
              className="btn-signal w-100 mt-2"
              onClick={backToLogin}
              disabled={busy}
            >
              Back to login
            </button>
          </p>
        </form>
      ) : (
        <>
          {resetMessage && (
            <div className="ok-box" role="status">
              {resetMessage}
            </div>
          )}

          <form onSubmit={submit} noValidate>
            {loc.state?.registered && !fail && (
              <div className="ok-box" role="status">
                Account created. Log in to continue.
              </div>
            )}

            {fail && (
              <div className="alert-box" role="alert">
                {fail}
              </div>
            )}

            <Field id="email" label="Email" error={errs.email}>
              <input
                id="email"
                type="email"
                autoComplete="email"
                value={f.email}
                onChange={set('email')}
                aria-invalid={!!errs.email}
                aria-describedby={errs.email ? 'email-err' : undefined}
              />
            </Field>

            <Field id="password" label="Password" error={errs.password}>
              <PasswordInput
                id="password"
                value={f.password}
                onChange={set('password')}
                error={errs.password}
                autoComplete="current-password"
              />
            </Field>

            <p className="alt-link">
              <button
                type="button"
                className="btn-signal w-100 mt-2"
                onClick={openReset}
              >
                Forgot password?
              </button>
            </p>

            <button
              type="submit"
              className="btn-signal w-100 mt-2"
              disabled={busy}
            >
              {busy ? 'Logging in...' : 'Log in'}
            </button>
          </form>

          <p className="alt-link">
            New to GrowNet? <Link to="/register">Create an account</Link>
          </p>
        </>
      )}
    </AuthLayout>
  );
}
