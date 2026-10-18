const L = { EXPIRED: ['Closed', 'bad'], CLOSING_SOON: ['Closing soon', 'warn'], UPCOMING: ['Open', 'ok'], NO_DEADLINE: ['No deadline', ''] };
export function deadlineInfo(i) {
  const [label, tone] = L[i.deadlineStatus] || ['', ''];
  let text = '', days = null;
  if (i.deadline) {
    const d = new Date(i.deadline + 'T00:00:00');
    days = Math.round((d - new Date().setHours(0, 0, 0, 0)) / 864e5);
    text = d.toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' }) + (days >= 0 ? ' (' + (days === 0 ? 'today' : days + (days === 1 ? ' day left' : ' days left')) + ')' : '');
  }
  return { label, tone, text, days };
}
export const initials = (s) => String(s || '?').split(/\s+/).filter(Boolean).slice(0, 2).map((x) => x[0].toUpperCase()).join('');
