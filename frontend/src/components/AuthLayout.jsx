import { Brand } from './Shell.jsx';
import { Icon } from './ui.jsx';
export default function AuthLayout({ title, sub, children }) {
  return (
    <div className="auth-split">
      <aside className="auth-aside">
        <Brand light />
        <div>
          <h2>Your next internship starts with your profile.</h2>
          <ul>
            <li><Icon name="match" /> See how your skills line up with each role</li>
            <li><Icon name="gauge" /> Know what to improve before you apply</li>
            <li><Icon name="calendar" /> Keep every deadline in one place</li>
          </ul>
        </div>
      </aside>
      <section className="auth-main">
        <div className="auth-form">
          <h1>{title}</h1>
          <p className="muted mb-4">{sub}</p>
          {children}
        </div>
      </section>
    </div>
  );
}
