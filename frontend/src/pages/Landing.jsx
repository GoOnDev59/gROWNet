import { Link } from 'react-router-dom';
import { Footer } from '../components/Shell.jsx';
import { Icon, Ring, Chip } from '../components/ui.jsx';

export default function Landing() {
  return (
    <>
      <section className="hero"><div className="container hero-grid">
        <div className="hero-copy">
          <h1>Find the internship that fits. Then close the gap.</h1>
          <p className="lead-text">GrowNet compares your skills with each role, shows what is missing, and keeps your deadlines in view, so you know what to do next.</p>
          <div className="d-flex flex-wrap gap-3">
            <Link to="/register" className="btn-signal btn-lg">Get started</Link>
            <Link to="/login" className="btn-outline-ink btn-lg">Log in</Link>
          </div>
          <p className="hero-foot">Free for students. Recruiters can post roles too.</p>
        </div>
        <div className="stage" role="img" aria-label="Illustration of a match view with a fit score, matched and missing skills, and an upcoming deadline">
          <div className="mock main">
            <div className="mock-top"><span className="avatar">FI</span><div><b>Frontend Intern</b><small>Example company · Remote</small></div></div>
            <div className="mock-score"><Ring value={78} size={104} label="Example fit" /><div><b>Strong fit</b><small>3 of 4 required skills</small></div></div>
            <div className="chips"><Chip tone="ok">React</Chip><Chip tone="ok">SQL</Chip><Chip tone="ok">REST APIs</Chip><Chip tone="gap">Docker</Chip></div>
          </div>
          <div className="mock float"><Icon name="calendar" /><div><b>Applications close in 6 days</b><small>Example deadline</small></div></div>
          <p className="stage-note">Illustration only</p>
        </div>
      </div></section>

      <section className="band"><div className="container">
        <div className="sec-head"><h2>Everything between “I need an internship” and “I applied”</h2>
        <p className="muted">Four tools, one profile. The cards below are examples of what you will see.</p></div>
        <div className="bento">
          <article className="tile t-wide"><span className="ic"><Icon name="match" /></span><h3>See why a role fits</h3>
            <p>Every internship shows the skills you already have and the ones you are missing, so you can decide quickly.</p>
            <div className="chips"><Chip tone="ok">Python</Chip><Chip tone="ok">Git</Chip><Chip tone="gap">Kubernetes</Chip><Chip tone="gap">Testing</Chip></div></article>
          <article className="tile"><span className="ic"><Icon name="gauge" /></span><h3>Check your readiness</h3>
            <p>Work through a checklist of what employers look for.</p>
            <div className="mini-prog"><i style={{ width: '60%' }} /></div><small className="muted">3 of 5 done (example)</small></article>
          <article className="tile"><span className="ic"><Icon name="calendar" /></span><h3>Never miss a deadline</h3>
            <p>Saved roles and their closing dates live in one list.</p></article>
          <article className="tile t-wide chat"><span className="ic"><Icon name="chat" /></span><h3>Ask the career assistant</h3>
            <p>Get help with resumes, skills to learn and interview prep.</p>
            <div className="bubbles"><span className="b me">What should I learn for a data internship?</span><span className="b ai">Start with SQL and one visualisation tool…</span></div></article>
        </div>
      </div></section>

      <section className="band alt"><div className="container">
        <div className="sec-head"><h2>How it works</h2></div>
        <ol className="steps">
          <li><h3>Add your skills</h3><p>Fill in your college, branch and skills once.</p></li>
          <li><h3>See where you fit</h3><p>Compare your profile with each internship.</p></li>
          <li><h3>Act before it closes</h3><p>Save roles, compare them and track what is due.</p></li>
        </ol>
      </div></section>

      <section className="cta-wrap"><div className="container"><div className="cta-card">
        <div><h2>Start with your profile.</h2><p>Everything else builds on it.</p></div>
        <Link to="/register" className="btn-signal btn-lg">Create your account</Link>
      </div></div></section>
      <Footer />
    </>
  );
}
