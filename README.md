# Recruitment Platform

**Backend Development Internship Project**

A Spring Boot REST API for managing a recruitment pipeline: user accounts with role-based
access, candidate records (including CV upload and parsing), and job-application tracking
through hiring stages.

## Tech stack

| Area | Choice |
|------|--------|
| Language | Java 21 |
| Framework | Spring Boot 4.1 (Web MVC, Data JPA, Security, Validation) |
| Database | PostgreSQL |
| Auth | JWT (jjwt) + embedded LDAP (UnboundID) for password verification |
| CV parsing | Apache PDFBox 3 |
| API docs | springdoc OpenAPI / Swagger UI |
| Build | Maven (wrapper included) |

## Project layout

```
src/main/java/com/recruitment
├── RecruitmentPlatformApplication.java   # entry point
├── auth/          # users, registration, login, JWT
├── candidate/     # candidate CRUD, tags, CV upload/parse
├── tracking/      # job applications, stages, feedback
└── security/      # JWT filter + Spring Security configuration
postman/           # Postman collections & test assets (see postman/README.md)
uploads/           # stored CV files (created at runtime)
```

## Prerequisites

- JDK 21
- PostgreSQL running locally on port `5432` with a database named `recruitment_db`
- (No separate LDAP server needed — an embedded LDAP server starts with the app on port `8389`)

## Configuration

Secrets are read from environment variables — nothing sensitive is committed. Set these
before running:

| Variable | Purpose |
|----------|---------|
| `DB_PASSWORD` | PostgreSQL password (required) |
| `DB_USERNAME` | PostgreSQL user (optional, defaults to `postgres`) |
| `JWT_SECRET` | Secret key used to sign JWTs — must be at least 32 characters (required) |

Example (PowerShell):

```powershell
$env:DB_PASSWORD = "your-db-password"
$env:JWT_SECRET  = "your-long-random-secret-at-least-32-chars"
```

The remaining settings live in
[`src/main/resources/application.properties`](src/main/resources/application.properties).
Schema is auto-managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## Running

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Authentication & roles

Auth is stateless via a **JWT Bearer token**. Log in to obtain a token, then send it as
`Authorization: Bearer <token>` on every other request.

Login verifies the password against the **embedded LDAP** directory, but the user must
**also exist in the `users` table** — login looks the account up in the database first and
returns `401` if it is missing.

**Demo / test credentials — for local development only.** These are dummy accounts seeded
into the embedded LDAP directory (see [`src/main/resources/users`](src/main/resources/users)),
all with password `123456`. They are not real credentials.

| Email | Role |
|-------|------|
| `admin@test.com` | ADMIN |
| `hr@test.com` | HR |
| `interviewer@test.com` | INTERVIEWER |

### First-time setup: seed the database users

On a new database, run this once against `recruitment_db` (after starting the app at
least once, so the tables are created) to add the login accounts:

```sql
INSERT INTO users (name, email, role) VALUES
  ('System Admin',     'admin@test.com',       'ADMIN'),
  ('Test HR',          'hr@test.com',          'HR'),
  ('Test Interviewer', 'interviewer@test.com', 'INTERVIEWER');
```

Then log in with any of these accounts using password `123456`.

Role rules (from [`SecurityConfiguration`](src/main/java/com/recruitment/security/SecurityConfiguration.java)):

- `POST /api/auth/login` — public
- `POST /api/auth/register` — **ADMIN** only
- `GET /api/candidates/**`, `GET /api/applications/**` — HR, INTERVIEWER, ADMIN
- Other `/api/candidates/**` and `/api/applications/**` writes — HR, ADMIN
- `PUT /api/applications/{id}/feedback` — INTERVIEWER, ADMIN

## API overview

Base URL: `http://localhost:8080`

### Auth — `/api/auth`
| Method | Path | Body / Params | Notes |
|--------|------|---------------|-------|
| POST | `/register` | `{ name, email, password, role }` | ADMIN only |
| POST | `/login` | `{ email, password }` | Returns JWT |

### Candidates — `/api/candidates`
| Method | Path | Body / Params |
|--------|------|---------------|
| POST | `/` | `{ firstName, lastName, email, phone }` |
| GET | `/` | — |
| GET | `/{id}` | — |
| PUT | `/{id}` | `{ firstName, lastName, email, phone }` |
| DELETE | `/{id}` | — |
| GET | `/search` | `?name=` |
| POST | `/{id}/tags` | `?tag=` |
| GET | `/search/tag` | `?tag=` |
| POST | `/{id}/cv` | multipart `file` (PDF) |
| GET | `/{id}/cv/text` | — (extracts text from stored PDF) |
| POST | `/{id}/cv/parse` | — (auto-tags detected skills) |
| POST | `/cv/bulk` | multipart `candidateIds[]` + `files[]` |

### Applications — `/api/applications`
| Method | Path | Body / Params |
|--------|------|---------------|
| POST | `/` | `{ candidateId, jobTitle }` |
| GET | `/` | — |
| GET | `/{id}` | — |
| DELETE | `/{id}` | — |
| PUT | `/{id}/stage` | `?stage=` (APPLIED, SCREENING, INTERVIEW, OFFER, HIRED, REJECTED) |
| GET | `/{id}/history` | — (stage change history) |
| PUT | `/{id}/recruiter` | `?recruiterId=` (must be an HR user) |
| PUT | `/{id}/interviewer` | `?interviewerId=` (must be an INTERVIEWER user) |
| PUT | `/{id}/feedback` | `?feedback=&score=` (INTERVIEWER/ADMIN) |

## Testing the API

A ready-to-run Postman collection and an automated end-to-end suite are provided in
[`postman/`](postman/). See [`postman/README.md`](postman/README.md) for how to import,
run in the Postman app, or run headlessly from the command line with Newman.
