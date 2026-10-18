import { useCallback, useEffect, useRef, useState } from 'react';
// Loads data and ignores stale responses when inputs change quickly.
export function useLoad(fn, deps = []) {
  const [s, setS] = useState({ data: null, error: '', loading: true, status: 0 });
  const n = useRef(0);
  const run = useCallback(() => {
    const id = ++n.current;
    setS((x) => ({ ...x, loading: true, error: '' }));
    fn().then((d) => { if (id === n.current) setS({ data: d, error: '', loading: false, status: 200 }); })
      .catch((e) => { if (id === n.current) setS({ data: null, error: e.message, loading: false, status: e.status }); });
  }, deps); // eslint-disable-line react-hooks/exhaustive-deps
  useEffect(() => { run(); return () => { n.current++; }; }, [run]);
  return { ...s, reload: run };
}
