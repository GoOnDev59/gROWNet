import { useEffect, useRef } from 'react';
export default function ConfirmDialog({ open, title, text, confirmLabel, danger, busy, onConfirm, onCancel }) {
  const r = useRef();
  useEffect(() => {
    const d = r.current; if (!d) return;
    if (open && !d.open) { d.showModal ? d.showModal() : d.setAttribute('open', ''); }
    if (!open && d.open) { d.close ? d.close() : d.removeAttribute('open'); }
  }, [open]);
  return (
    <dialog ref={r} className="dlg" aria-labelledby="dlg-t" onCancel={(e) => { e.preventDefault(); if (!busy) onCancel(); }}>
      <h2 id="dlg-t" className="h5">{title}</h2><p className="muted">{text}</p>
      <div className="d-flex gap-2 justify-content-end">
        <button type="button" className="btn-quiet" onClick={onCancel} disabled={busy}>Cancel</button>
        <button type="button" className={danger ? 'btn-danger' : 'btn-ink'} onClick={onConfirm} disabled={busy}>{busy ? 'Working…' : confirmLabel}</button>
      </div>
    </dialog>
  );
}
