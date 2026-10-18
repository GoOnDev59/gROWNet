import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const NAV = {
  STUDENT: [['Dashboard', '/student/dashboard'], ['Saved', '/student/saved'], ['Compare', '/student/compare'], ['Readiness', '/student/readiness'], ['Assistant', '/student/career-assistant'], ['Profile', '/student/profile']],
  RECRUITER: [['Dashboard', '/recruiter/dashboard'], ['Post internship', '/recruiter/internships/new'], ['Profile', '/recruiter/profile']],
  ADMIN: [['Dashboard', '/admin/dashboard'], ['Recruiters', '/admin/recruiters'], ['Internships', '/admin/internships']],
};
export function Brand({ light }) {
  return (
    <Link to="/" className={'brand' + (light ? ' brand-light' : '')} aria-label="GrowNet home">
      <svg width="26" height="26" viewBox="0 0 26 26" aria-hidden="true"><rect width="26" height="26" rx="8" fill="currentColor" opacity=".12"/><path d="M7 18l4-5 3 3 5-7" fill="none" stroke="#1FBF75" strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round"/></svg>
      GrowNet
    </Link>
  );
}
export default function Shell({ children }) {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const nav = useNavigate();
  const links = [['Internships', '/internships'], ...(user ? NAV[user.role] || [] : [])];
  return (
    <>
      <a href="#main" className="skip">Skip to content</a>
      <header className="topbar">
        <div className="container d-flex align-items-center gap-4">
          <Brand />
          <button className="menu-btn" aria-expanded={open} aria-controls="nav" onClick={() => setOpen(!open)}>{open ? 'Close' : 'Menu'}</button>
          <nav id="nav" className={'nav-links' + (open ? ' open' : '')} onClick={() => setOpen(false)}>
            {links.map(([l, to]) => <NavLink key={to} to={to}>{l}</NavLink>)}
            <span className="grow" />
            {user ? (
              <>
                <span className="who">{user.fullName}</span>
                <button className="btn-quiet" onClick={() => { logout(); nav('/'); }}>Log out</button>
              </>
            ) : (
              <>
                <Link to="/login">Log in</Link>
                <Link to="/register" className="btn-ink">Get started</Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main id="main">{children}</main>
    </>
  );
}
export const Footer = () => (
  <footer className="foot"><div className="container d-flex flex-wrap justify-content-between gap-3">
    <Brand light /><span>Build skills. Find fit. Move forward.</span></div></footer>
);
