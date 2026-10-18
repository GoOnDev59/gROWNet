export const skillList = (s) => (Array.isArray(s) ? s : String(s || '').split(',')).map((x) => String(x).trim()).filter(Boolean);
export const FIELDS = [['fullName', 'Full name'], ['college', 'College'], ['branch', 'Branch'], ['skills', 'Skills'], ['bio', 'Short bio']];
// Frontend-calculated from profile fields. Not a backend metric.
export function completeness(p) {
  const done = FIELDS.map(([k, label]) => ({ key: k, label, done: k === 'skills' ? skillList(p?.[k]).length > 0 : !!String(p?.[k] || '').trim() }));
  return { items: done, pct: Math.round((done.filter((d) => d.done).length / FIELDS.length) * 100) };
}
