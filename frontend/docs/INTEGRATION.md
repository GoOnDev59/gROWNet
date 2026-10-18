# GrowNet API integration map

Source of truth: the backend ZIP (controllers, `Dtos.java`, `SecurityConfig.java`, services). Every call lives in `src/api/`.

| Feature | Endpoint | Who | Status |
|---|---|---|---|
| Register | `POST /api/auth/register` `{fullName,email,password}` | public | Integrated. Always creates a STUDENT |
| Login | `POST /api/auth/login` `{email,password}` -> `{token,tokenType,user}` | public | Integrated |
| Profile view/update | `GET`/`PUT /api/profile` (`fullName,skills,college,branch,bio`; `skills` is one comma-separated string) | any logged-in user | Integrated |
| Internship list + filters | `GET /api/internships?keyword&location&skill` | public | Integrated (no pagination, sort or work-mode filter exist) |
| Internship details | `GET /api/internships/{id}` | public | Integrated |
| Compare | `GET /api/internships/compare?ids=1&ids=2` (2 to 4 ids) | public | Integrated |
| Create internship | `POST /api/internships` | RECRUITER | Integrated. Starts unverified |
| Match + missing skills | `GET /api/matching/{id}` | STUDENT | Integrated. One request per internship |
| Saved list / save / unsave | `GET /api/saved-internships`, `POST`/`DELETE /api/saved-internships/{id}` | STUDENT | Integrated |
| Readiness | `GET /api/readiness` | STUDENT | Integrated, read-only |
| Deadlines | No endpoint. `deadline` and `deadlineStatus` come on each internship | n/a | Integrated through internship data |
| AI assistant | `POST /api/ai/ask` `{question}` -> `{success,answer}` | any logged-in user (UI: students) | Integrated |
| Admin recruiters | `GET /api/admin/pending-recruiters`, `PUT /api/admin/recruiters/{id}/approve` | ADMIN | Integrated (approve only, see below) |
| Admin internships | `GET /api/admin/pending-internships`, `PUT /api/admin/internships/{id}/approve` and `/reject` | ADMIN | Integrated |
| `PUT /api/admin/recruiters/{id}/reject`, `GET /api/ai/health`, `/health` | | | Not used |

## Not supported by the backend (so not built)
- Recruiter or admin sign-up. Those accounts must be created directly in the database.
- A recruiter's own pending/rejected internships, or editing/deleting an internship. The recruiter dashboard shows only the recruiter's approved internships (public list filtered by `recruiterId`).
- Recruiter verification status. `SafeUser` has no `approved` field, and the backend does not enforce `approved` at login or when creating internships.
- Applying to an internship, work mode, eligibility, pagination, server-side sort, saving readiness checklist changes, AI conversation memory.
- Admin summary statistics. The dashboard shows the sizes of the two pending queues.

## Backend behaviours found in the source (nothing was changed)
1. **Wrong password returns HTTP 500.** `BadCredentialsException` falls into the catch-all handler in `GlobalExceptionHandler`. The same happens for `@Valid` failures, malformed JSON and bad query params. Suggested fix: add handlers returning 401 for `BadCredentialsException` and 400 for `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, `MissingServletRequestParameterException` and `MethodArgumentTypeMismatchException`. The frontend validates the same limits locally and shows a neutral message on a login 500.
2. **Expired or invalid JWT returns a bare 403, not 401.** `SecurityConfig` has no authentication entry point. The frontend re-checks `GET /api/profile` after such a 403 and logs the user out if that also fails. Suggested fix: `.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))`.
3. **AI failures return HTTP 200** with an apology sentence in `answer`. The frontend detects the two known sentences and shows an error with retry. This text match is fragile. Suggested fix: return 503 from `AiController` when `AiService` fails.
4. **Rejecting a recruiter sets `approved=false`, the same as pending**, so a rejected recruiter stays in the pending list. The UI offers Approve only.
5. **CORS** allows only `http://localhost:5173` and `http://127.0.0.1:5173`. Use `npm run dev`. `npm run preview` (port 4173) would be blocked. No change is needed for development.
6. `README-SETUP.md` mentions `database/setup.sql`, which is not in the ZIP. `JWT_SECRET` must be Base64 and at least 32 bytes decoded, and `.env` files are not loaded automatically.

## Frontend decisions that depend on these
- Match scores are fetched per internship (first 40 on the list page, first 12 on the dashboard). That is many small requests because no bulk endpoint exists.
- Sorting happens in the browser on the loaded results. "Best match" is available to students only.
- Saved items and the comparison selection: saved items live in the backend, the comparison selection lives in `sessionStorage` for the tab.
