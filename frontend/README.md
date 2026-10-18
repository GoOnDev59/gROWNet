# GrowNet Frontend

React + Vite + JavaScript + Bootstrap 5 (with custom CSS). Requires Node.js 18+.

## Run
```
npm install
cp .env.example .env     # only needed if the backend is not on :8080
npm run dev              # http://localhost:5173
npm run build            # production build
```
Start the Spring Boot backend (http://localhost:8080) first. If the browser blocks requests, `SecurityConfig.java` needs to allow CORS from `http://localhost:5173`. Send that file and exact changes can be worked out.

## Auth flow
`POST /api/auth/login` returns `token` and `user`. The token is kept in `sessionStorage` and sent as a Bearer header. On load the app calls `GET /api/profile` to restore the session. A 401 clears the token and signs the user out. `sessionStorage` is readable by any XSS, so it is not fully secure. Credentials and tokens are never logged.

## Implemented
Landing, register, login, JWT auth with session restore and logout, role-based routes, student dashboard, internship browsing with search/filters/sort, details with skill match, save/unsave, saved list, comparison (2 to 4), readiness, AI career assistant, profile, recruiter dashboard and posting form, admin recruiter approval and internship review.

See `docs/INTEGRATION.md` for the endpoint map, backend limitations and suggested backend fixes (none applied).

## Test accounts
Registration only creates students. Recruiter and admin accounts must exist in the database already.

## Tests
`npm test` runs 23 automated tests against a mocked `fetch`. `tools/mock-backend.mjs` is a test double for browser checks only, not the real backend: `node tools/mock-backend.mjs`, then `npm run build && npx vite preview --port 4173`.
