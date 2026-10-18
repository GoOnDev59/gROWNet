import { createContext, useContext, useEffect, useState } from 'react';
import { useAuth } from './AuthContext.jsx';
import { saved as savedApi } from '../api/services.js';

const Ctx = createContext(null);
const NONE = { isStudent: false, savedIds: new Set(), compareIds: [], toggleSave: async () => {}, toggleCompare: () => {}, clearCompare: () => {} };
export const useStudent = () => useContext(Ctx) || NONE;
export const MAX_COMPARE = 4; // backend accepts 2 to 4

export function StudentProvider({ children }) {
  const { user, ready } = useAuth();
  const isStudent = user?.role === 'STUDENT';
  const [savedIds, setSavedIds] = useState(new Set());
  const [compareIds, setCompare] = useState(() => { try { return JSON.parse(sessionStorage.getItem('grownet.compare') || '[]'); } catch { return []; } });
  useEffect(() => { sessionStorage.setItem('grownet.compare', JSON.stringify(compareIds)); }, [compareIds]);
  useEffect(() => {
    if (!ready) return; // do not wipe the saved comparison while the session is still being restored
    if (!isStudent) { setSavedIds(new Set()); setCompare([]); return; }
    savedApi.list().then((l) => setSavedIds(new Set(l.map((i) => i.id)))).catch(() => {});
  }, [ready, isStudent, user?.id]);
  // Throws if the backend call fails, so callers can show the error. State only changes on success.
  const toggleSave = async (id) => {
    const was = savedIds.has(id);
    if (was) await savedApi.remove(id); else await savedApi.save(id);
    setSavedIds((s) => { const n = new Set(s); if (was) n.delete(id); else n.add(id); return n; });
    return !was;
  };
  const toggleCompare = (id) => setCompare((c) => (c.includes(id) ? c.filter((x) => x !== id) : c.length >= MAX_COMPARE ? c : [...c, id]));
  return <Ctx.Provider value={{ isStudent, savedIds, compareIds, toggleSave, toggleCompare, clearCompare: () => setCompare([]) }}>{children}</Ctx.Provider>;
}
