import { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { tokenStore, setUnauthorizedHandler } from '../api/client.js';
import * as auth from '../api/authService.js';

const Ctx = createContext(null);
export const useAuth = () => useContext(Ctx);
const norm = (u) => u && { ...u, role: String(u.role || '').replace(/^ROLE_/, '').toUpperCase() };
export const HOME = { STUDENT: '/student/dashboard', RECRUITER: '/recruiter/dashboard', ADMIN: '/admin/dashboard' };

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [ready, setReady] = useState(false);
  useEffect(() => {
    setUnauthorizedHandler(() => setUser(null));
    if (!tokenStore.get()) { setReady(true); return; }
    auth.getProfile().then((u) => setUser(norm(u))).catch(() => tokenStore.clear()).finally(() => setReady(true));
  }, []);
  const login = useCallback(async (creds) => {
    const res = await auth.login(creds);
    tokenStore.set(res.token);
    const u = norm(res.user);
    setUser(u);
    return u;
  }, []);
  const logout = useCallback(() => { tokenStore.clear(); setUser(null); }, []);
  return <Ctx.Provider value={{ user, setUser: (u) => setUser(norm(u)), ready, login, logout }}>{children}</Ctx.Provider>;
}
