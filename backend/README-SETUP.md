# GrowNet backend starter (Om's assigned APIs)

This is a coherent starter implementation for registration/login, JWT-protected profiles, and recruiter-owned internship creation/listing. It has not been compiled against your local repository and is not guaranteed to integrate unchanged with your friend's AI files.

## Important before copying
- Back up your existing project first.
- Do not overwrite existing `pom.xml`, `SecurityConfig`, `ApiException`, `GlobalExceptionHandler`, AI controller/service, or other shared files without comparing them.
- This starter uses packages `com.grownet.entity`, `repository`, `dto`, `security`, `service`, and `controller`. Keep the existing package layout if your project differs, and update imports consistently.
- `SavedInternshipRepository` and the entity are included; save/unsave HTTP endpoints are intentionally not added because the initial agreed API list did not include them.
- Public registration always creates STUDENT users. Recruiter account provisioning must be decided by the team; do not allow clients to self-register as ADMIN.
- Public internship listings return verified internships only. New recruiter-created internships are unverified until the existing admin workflow verifies them.
- Skills are represented as plain strings in this baseline. Agree on a canonical format with the matching-feature teammate before finalizing integration.

## Setup
1. Ensure Java 21 and Maven are installed.
2. Create a MySQL database named `grownet` (see `database/setup.sql`).
3. Set environment variables in the terminal where you will run Maven:
   - `DB_URL` (e.g. `jdbc:mysql://localhost:3306/grownet?createDatabaseIfNotExist=true`)
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`: Base64-encoded random key decoding to at least 32 bytes
   - optional `JWT_EXPIRATION_MS` (default 86400000)
4. `.env.example` is documentation only; Spring Boot does not automatically load `.env`.
5. Run `mvn spring-boot:run` from the actual Maven project root.

## API contract
- `POST /api/auth/register` body: `{"fullName":"Example Student","email":"student@example.com","password":"ExamplePassword123"}`. Creates a STUDENT account; returns a message.
- `POST /api/auth/login` body: `{"email":"student@example.com","password":"ExamplePassword123"}`. Returns `token`, `tokenType`, and safe `user` object.
- `GET /api/profile` requires `Authorization: Bearer <token>`.
- `PUT /api/profile` requires a token; body may include `fullName`, `skills`, `college`, `branch`, `bio`.
- `GET /api/internships` and `GET /api/internships/{id}` return verified listings only.
- `POST /api/internships` requires a RECRUITER token; body includes `title`, `company`, `location`, `stipend`, `description`, `skillsRequired`, `deadline` (YYYY-MM-DD). New listings are unverified.
- `GET /health` is public and reports application status only, not database connectivity.

## Not yet verified
This archive was generated as source code, but Maven compilation, MySQL connectivity, and API tests have not been run in your local environment. Run those tests after carefully integrating the files.
