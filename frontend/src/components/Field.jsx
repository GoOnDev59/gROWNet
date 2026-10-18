import { useState } from 'react';
export function Field({ id, label, error, hint, children }) {
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      {children}
      {hint && !error && <div className="hint">{hint}</div>}
      {error && <div id={id + '-err'} className="err" role="alert">{error}</div>}
    </div>
  );
}
export function PasswordInput({ id, value, onChange, error, autoComplete, maxLength }) {
  const [show, setShow] = useState(false);
  return (
    <div className="pw">
      <input id={id} type={show ? 'text' : 'password'} value={value} onChange={onChange} autoComplete={autoComplete} maxLength={maxLength}
        aria-invalid={!!error} aria-describedby={error ? id + '-err' : undefined} />
      <button type="button" onClick={() => setShow(!show)} aria-pressed={show}>{show ? 'Hide' : 'Show'}</button>
    </div>
  );
}
