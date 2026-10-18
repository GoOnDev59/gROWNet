const P = { check: 'M5 12l4 4 10-10', match: 'M4 12h4l3-6 3 12 3-6h3', gauge: 'M4 18a8 8 0 1116 0M12 18l4-5', calendar: 'M5 7h14v12H5zM5 11h14M9 4v4M15 4v4', chat: 'M5 6h14v9H10l-4 3v-3H5z', dot: 'M12 12m-3 0a3 3 0 106 0a3 3 0 10-6 0' };
export const Icon = ({ name, size = 20 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true"><path d={P[name]} /></svg>
);
export function Ring({ value, size = 96, label = 'Progress' }) {
  const r = (size - 12) / 2, c = 2 * Math.PI * r;
  return (
    <div className="ring" style={{ width: size, height: size }} role="img" aria-label={label + ' ' + value + ' percent'}>
      <svg width={size} height={size} viewBox={'0 0 ' + size + ' ' + size}>
        <circle cx={size / 2} cy={size / 2} r={r} className="ring-bg" />
        <circle cx={size / 2} cy={size / 2} r={r} className="ring-fg" strokeDasharray={c} strokeDashoffset={c * (1 - value / 100)} transform={'rotate(-90 ' + size / 2 + ' ' + size / 2 + ')'} />
      </svg>
      <span style={{ fontSize: size / 4.2 }}>{value}%</span>
    </div>
  );
}
export const Chip = ({ children, tone }) => <span className={'chip' + (tone ? ' ' + tone : '')}>{tone === 'ok' && <Icon name="check" size={14} />}{children}</span>;
export const PageHeader = ({ title, sub, action }) => (
  <div className="page-head"><div><h1>{title}</h1>{sub && <p className="muted">{sub}</p>}</div>{action}</div>
);
export const Pending = ({ icon, title, text }) => (
  <div className="pending"><span className="pending-ic"><Icon name={icon} /></span>
    <div><h3>{title}</h3><p>{text}</p><span className="tag">Not connected yet</span></div></div>
);
export const ErrorState = ({ message, onRetry }) => (
  <div className="alert-box" role="alert">{message} {onRetry && <button className="btn-quiet btn-sm" onClick={onRetry}>Try again</button>}</div>
);
export const EmptyState = ({ title, text, action }) => (
  <div className="empty"><h3>{title}</h3>{text && <p>{text}</p>}{action}</div>
);
export const Badge = ({ tone, children }) => <span className={'badge-s ' + (tone || '')}>{children}</span>;
export const ListSkeleton = ({ n = 4, h = 230 }) => (
  <div className="grid-2" aria-busy="true" aria-label="Loading">{Array.from({ length: n }, (_, k) => <div key={k} className="skeleton" style={{ height: h }} />)}</div>
);
