import { useEffect, useState } from 'react';
import { matching } from '../api/services.js';
// The backend only offers one match per request (GET /api/matching/{id}), so this fetches each id once.
export function useMatches(ids, enabled) {
  const [map, setMap] = useState({});
  const key = ids.join(',');
  useEffect(() => {
    if (!enabled) return;
    const todo = ids.filter((id) => !(id in map));
    if (!todo.length) return;
    let dead = false;
    Promise.all(todo.map((id) => matching.get(id).then((r) => [id, r]).catch((e) => [id, { error: e.message }])))
      .then((rs) => { if (!dead) setMap((m) => ({ ...m, ...Object.fromEntries(rs) })); });
    return () => { dead = true; };
  }, [key, enabled]); // eslint-disable-line react-hooks/exhaustive-deps
  return map;
}
