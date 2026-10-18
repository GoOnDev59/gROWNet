
import { useEffect, useRef, useState } from 'react';
import ReactMarkdown from 'react-markdown';
import { ai } from '../api/services.js';
import { PageHeader } from '../components/ui.jsx';

const STARTERS = [
  'How do I tailor my resume for a software internship?',
  'Which skills should I learn for a data analyst role?',
  'How can I prepare for my first technical interview?'
];

const FALLBACKS = [
  'AI Career Assistant is not configured yet',
  'AI Career Assistant is temporarily unavailable'
];

function FormattedAnswer({ text }) {
  return (
    <div className="markdown-answer">
      <ReactMarkdown
        components={{
          a: ({ children, ...props }) => (
            <a
              {...props}
              target="_blank"
              rel="noopener noreferrer"
            >
              {children}
            </a>
          ),
          pre: ({ children }) => (
            <pre className="markdown-code-block">{children}</pre>
          ),
          code: ({ children, className, ...props }) => (
            <code className={className || 'markdown-inline-code'} {...props}>
              {children}
            </code>
          )
        }}
      >
        {text || ''}
      </ReactMarkdown>
    </div>
  );
}

export default function Assistant() {
  const [msgs, setMsgs] = useState([]);
  const [text, setText] = useState('');
  const [busy, setBusy] = useState(false);
  const end = useRef(null);

  useEffect(() => {
    end.current?.scrollIntoView?.({ block: 'end', behavior: 'smooth' });
  }, [msgs, busy]);

  async function send(q) {
    q = q.trim();

    if (!q || busy) return;

    setText('');
    setBusy(true);

    setMsgs((m) => [
      ...m.filter((x) => !x.error),
      { role: 'me', text: q }
    ]);

    try {
      const r = await ai.ask(q);
      const answer = String(r?.answer ?? 'Sorry, no answer was returned.');
      const failed = FALLBACKS.some((f) => answer.includes(f));

      setMsgs((m) => [
        ...m,
        failed
          ? { role: 'ai', error: true, q, text: answer }
          : { role: 'ai', text: answer }
      ]);
    } catch (e) {
      setMsgs((m) => [
        ...m,
        {
          role: 'ai',
          error: true,
          q,
          text: e.message || 'Something went wrong. Please try again.'
        }
      ]);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="page container narrow-lg">
      <PageHeader
        title="Career assistant"
        sub="Ask about internships, skills, resumes and interviews."
        action={
          msgs.length > 0 && (
            <button className="btn-quiet" onClick={() => setMsgs([])}>
              Clear conversation
            </button>
          )
        }
      />

      <section className="chat-box" aria-label="Conversation">
        <div className="chat-log" role="log" aria-live="polite">
          {!msgs.length && (
            <div className="starters">
              <p className="muted">Try one of these:</p>
              {STARTERS.map((s) => (
                <button
                  key={s}
                  className="btn-quiet"
                  onClick={() => send(s)}
                >
                  {s}
                </button>
              ))}
            </div>
          )}

          {msgs.map((m, k) => (
            <div
              key={k}
              className={
                'msg ' + m.role + (m.error ? ' err-msg' : '')
              }
            >
              {m.role === 'ai' ? (
                <FormattedAnswer text={m.text} />
              ) : (
                m.text
              )}

              {m.error && (
                <button
                  className="btn-quiet btn-sm d-block mt-2"
                  onClick={() => send(m.q)}
                >
                  Try again
                </button>
              )}
            </div>
          ))}

          {busy && (
            <div
              className="msg ai typing"
              aria-label="Assistant is typing"
            >
              <i />
              <i />
              <i />
            </div>
          )}

          <div ref={end} />
        </div>

        <form
          className="composer"
          onSubmit={(e) => {
            e.preventDefault();
            send(text);
          }}
        >
          <label htmlFor="q" className="visually-hidden">
            Your question
          </label>

          <textarea
            id="q"
            rows="2"
            placeholder="Ask a career question"
            value={text}
            onChange={(e) => setText(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                send(text);
              }
            }}
          />

          <button
            className="btn-signal"
            disabled={busy || !text.trim()}
          >
            {busy ? 'Sending…' : 'Send'}
          </button>
        </form>
      </section>

      <small className="muted">
        Each question is answered on its own. The assistant does not
        remember earlier messages, and answers may be imperfect.
      </small>
    </div>
  );
}
