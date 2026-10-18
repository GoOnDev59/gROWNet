import { Link } from 'react-router-dom';
export const NotFound = () => (<div className="page container narrow center"><h1>Page not found</h1><p className="muted">That address does not exist.</p><Link to="/" className="btn-ink">Back to home</Link></div>);
export const Unauthorized = () => (<div className="page container narrow center"><h1>No access</h1><p className="muted">Your account type cannot open this page.</p><Link to="/" className="btn-ink">Back to home</Link></div>);
