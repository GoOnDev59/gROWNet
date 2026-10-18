import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { internships } from '../api/services.js';
import { Field } from '../components/Field.jsx';
import { PageHeader } from '../components/ui.jsx';

const BLANK = { title: '', company: '', location: '', stipend: '', skillsRequired: '', deadline: '', description: '' };
export default function InternshipForm() {
  const nav = useNavigate();
  const [f, setF] = useState(BLANK), [errs, setErrs] = useState({}), [fail, setFail] = useState(''), [busy, setBusy] = useState(false), [done, setDone] = useState(null);
  const set = (k) => (e) => { setF({ ...f, [k]: e.target.value }); setErrs((x) => ({ ...x, [k]: undefined })); };
  async function submit(e) {
    e.preventDefault(); if (busy) return;
    const v = {};
    if (!f.title.trim()) v.title = 'Enter a title.';
    if (!f.company.trim()) v.company = 'Enter the company name.';
    setErrs(v); setFail('');
    if (Object.keys(v).length) return;
    setBusy(true);
    try {
      const n = (s) => (s.trim() ? s.trim() : null);
      setDone(await internships.create({ title: f.title.trim(), company: f.company.trim(), location: n(f.location), stipend: n(f.stipend), description: n(f.description), skillsRequired: n(f.skillsRequired), deadline: f.deadline || null }));
    } catch (er) { setFail(er.status === 500 ? 'The server rejected this internship. Check the field lengths and try again.' : er.message); } finally { setBusy(false); }
  }
  if (done) return (
    <div className="page container narrow-lg"><div className="panel center-card">
      <h1 className="h3">Submitted for review</h1>
      <p className="muted">"{done.title}" will appear on GrowNet once an admin approves it.</p>
      <div className="d-flex gap-2 justify-content-center flex-wrap"><button className="btn-signal" onClick={() => { setF(BLANK); setDone(null); }}>Post another</button><Link to="/recruiter/dashboard" className="btn-quiet">Back to dashboard</Link></div>
    </div></div>);
  return (
    <div className="page container narrow-lg">
      <PageHeader title="Post an internship" sub="New postings are reviewed by an admin before students can see them." />
      {fail && <div className="alert-box" role="alert">{fail}</div>}
      <form className="panel" onSubmit={submit} noValidate>
        <div className="row g-3">
          <div className="col-md-6"><Field id="title" label="Title *" error={errs.title}><input id="title" maxLength={180} value={f.title} onChange={set('title')} aria-invalid={!!errs.title} aria-describedby={errs.title ? 'title-err' : undefined} /></Field></div>
          <div className="col-md-6"><Field id="company" label="Company *" error={errs.company}><input id="company" maxLength={180} value={f.company} onChange={set('company')} aria-invalid={!!errs.company} aria-describedby={errs.company ? 'company-err' : undefined} /></Field></div>
          <div className="col-md-4"><Field id="location" label="Location"><input id="location" maxLength={180} value={f.location} onChange={set('location')} /></Field></div>
          <div className="col-md-4"><Field id="stipend" label="Stipend"><input id="stipend" maxLength={100} placeholder="e.g. 15000 per month" value={f.stipend} onChange={set('stipend')} /></Field></div>
          <div className="col-md-4"><Field id="deadline" label="Application deadline"><input id="deadline" type="date" value={f.deadline} onChange={set('deadline')} /></Field></div>
        </div>
        <Field id="skillsRequired" label="Required skills" hint="Separate skills with commas. Students are matched on these."><input id="skillsRequired" maxLength={1500} value={f.skillsRequired} onChange={set('skillsRequired')} /></Field>
        <Field id="description" label="Description"><textarea id="description" rows="6" maxLength={5000} value={f.description} onChange={set('description')} /></Field>
        <div className="d-flex gap-2"><button className="btn-signal" disabled={busy}>{busy ? 'Submitting…' : 'Submit for review'}</button><button type="button" className="btn-quiet" onClick={() => nav('/recruiter/dashboard')}>Cancel</button></div>
      </form>
    </div>
  );
}
