import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Shell from './components/Shell.jsx';
import { useAuth } from './context/AuthContext.jsx';
import Landing from './pages/Landing.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import Profile from './pages/Profile.jsx';
import StudentDashboard from './pages/StudentDashboard.jsx';
import Internships from './pages/Internships.jsx';
import InternshipDetail from './pages/InternshipDetail.jsx';
import Saved from './pages/Saved.jsx';
import Compare from './pages/Compare.jsx';
import Readiness from './pages/Readiness.jsx';
import Assistant from './pages/Assistant.jsx';
import RecruiterDashboard from './pages/RecruiterDashboard.jsx';
import InternshipForm from './pages/InternshipForm.jsx';
import { AdminDashboard, AdminRecruiters, AdminInternships } from './pages/Admin.jsx';
import { NotFound, Unauthorized } from './pages/Misc.jsx';

// UX guard only. The backend remains the source of truth for authorization.
function Guard({ role, children }) {
  const { user, ready } = useAuth();
  const loc = useLocation();
  if (!ready) return <div className="page container"><div className="skeleton" style={{ height: 200 }} /></div>;
  if (!user) return <Navigate to="/login" state={{ from: loc }} replace />;
  if (role && user.role !== role) return <Navigate to="/unauthorized" replace />;
  return children;
}
const G = (role, el) => <Guard role={role}>{el}</Guard>;
export default function App() {
  return (
    <Shell>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/internships" element={<Internships />} />
        <Route path="/internships/:id" element={<InternshipDetail />} />
        <Route path="/student/dashboard" element={G('STUDENT', <StudentDashboard />)} />
        <Route path="/student/profile" element={G('STUDENT', <Profile />)} />
        <Route path="/student/saved" element={G('STUDENT', <Saved />)} />
        <Route path="/student/compare" element={G('STUDENT', <Compare />)} />
        <Route path="/student/readiness" element={G('STUDENT', <Readiness />)} />
        <Route path="/student/career-assistant" element={G('STUDENT', <Assistant />)} />
        <Route path="/recruiter/dashboard" element={G('RECRUITER', <RecruiterDashboard />)} />
        <Route path="/recruiter/profile" element={G('RECRUITER', <Profile />)} />
        <Route path="/recruiter/internships/new" element={G('RECRUITER', <InternshipForm />)} />
        <Route path="/admin/dashboard" element={G('ADMIN', <AdminDashboard />)} />
        <Route path="/admin/recruiters" element={G('ADMIN', <AdminRecruiters />)} />
        <Route path="/admin/internships" element={G('ADMIN', <AdminInternships />)} />
        <Route path="/unauthorized" element={<Unauthorized />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Shell>
  );
}
