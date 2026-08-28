# Postman API Tests

API tests for the Recruitment Platform. Two collections are provided:

| File | Purpose |
|------|---------|
| `recruitment-platform.postman_collection.json` | **Manual/exploratory** collection — one folder per controller, nice for clicking through endpoints by hand in the Postman app. |
| `recruitment-platform.postman_environment.json` | Environment (`baseUrl`, `token`, `candidateId`, `applicationId`) for the manual collection. |
| `recruitment-platform.e2e.postman_collection.json` | **Automated end-to-end** collection — 26 requests ordered to run top-to-bottom, each with a status assertion. Self-contained (defines its own variables); ideal for Newman. |
| `sample_cv.pdf`, `sample_cv2.pdf` | Small valid PDFs used by the CV-upload requests. |

Both collections target `http://localhost:8080`.

## Prerequisites

1. **The app must be running** (`./mvnw spring-boot:run`) with PostgreSQL up.
2. An admin account (`admin@test.com` / `123456`) must exist in the `users` table — login
   checks the database first, then verifies the password against embedded LDAP.

---

## Option 1 — Run in the Postman app (manual)

1. **Import** both `recruitment-platform.postman_collection.json` and
   `recruitment-platform.postman_environment.json`.
2. Select the **Recruitment Platform - Local** environment (top-right dropdown).
3. Run **Auth → Login** first. A test script saves the returned JWT into the `token`
   variable, and collection-level Bearer auth then applies it to every other request
   automatically.
4. Run any other request. Helpful auto-captured variables:
   - `candidateId` — set by *Create Candidate*
   - `applicationId` — set by *Create Application*
5. For CV uploads, click the request's **Body → form-data** `file` row and pick
   `sample_cv.pdf` (Postman does not store file paths in the exported collection).

---

## Option 2 — Run everything at once with Newman (automated)

[Newman](https://github.com/postmanlabs/newman) is Postman's official command-line runner.
The `*.e2e.*` collection is built for it: it logs in first, registers valid HR and
INTERVIEWER users so the recruiter/interviewer assignment endpoints get role-checked IDs,
uploads real PDFs, and deletes the test data at the end.

From the `postman/` directory:

```bash
npx newman run recruitment-platform.e2e.postman_collection.json --working-dir .
```

The `--working-dir .` flag lets Newman find `sample_cv.pdf` / `sample_cv2.pdf` for the
multipart upload requests.

Prefer a global install:

```bash
npm install -g newman
newman run recruitment-platform.e2e.postman_collection.json --working-dir .
```

### Expected result

All 26 requests pass (26/26 assertions), exercising all 23 API endpoints:

```
┌─────────────────────────┬──────────┬────────┐
│                         │ executed │ failed │
├─────────────────────────┼──────────┼────────┤
│              requests   │       26 │      0 │
│            assertions   │       26 │      0 │
└─────────────────────────┴──────────┴────────┘
```

### What the E2E run covers

| Step | Endpoint |
|------|----------|
| 01 | `POST /api/auth/login` |
| 02–03 | `POST /api/auth/register` (HR, then INTERVIEWER) |
| 04–05 | `POST /api/candidates` (×2) |
| 06 | `GET /api/candidates` |
| 07 | `GET /api/candidates/{id}` |
| 08 | `PUT /api/candidates/{id}` |
| 09 | `GET /api/candidates/search?name=` |
| 10 | `POST /api/candidates/{id}/tags` |
| 11 | `GET /api/candidates/search/tag?tag=` |
| 12 | `POST /api/candidates/{id}/cv` |
| 13 | `GET /api/candidates/{id}/cv/text` |
| 14 | `POST /api/candidates/{id}/cv/parse` |
| 15 | `POST /api/candidates/cv/bulk` |
| 16 | `POST /api/applications` |
| 17 | `GET /api/applications` |
| 18 | `GET /api/applications/{id}` |
| 19 | `PUT /api/applications/{id}/stage` |
| 20 | `GET /api/applications/{id}/history` |
| 21 | `PUT /api/applications/{id}/recruiter` |
| 22 | `PUT /api/applications/{id}/interviewer` |
| 23 | `PUT /api/applications/{id}/feedback` |
| 24 | `DELETE /api/applications/{id}` |
| 25–26 | `DELETE /api/candidates/{id}` (×2) |

---

## Notes & gotchas

- **CV endpoints need a real PDF.** `GET /cv/text` and `POST /cv/parse` use Apache PDFBox,
  which will fail on non-PDF uploads. Use the provided `sample_cv.pdf` (it contains the words
  *Java, Spring, SQL, Python, React, JavaScript*, so the parse step auto-tags those skills).
- **Assignment endpoints validate roles.** `recruiterId` must reference an HR user and
  `interviewerId` an INTERVIEWER user, or the API returns `400`. The E2E collection creates
  these users itself; the manual collection expects you to supply valid IDs.
- **The E2E run leaves two users behind.** It registers `hr_<timestamp>@test.com` and
  `iv_<timestamp>@test.com` and cannot remove them — there is no delete-user endpoint. This
  is harmless but visible in the `users` table. Emails are timestamped so repeat runs never
  collide.
- **Login 401?** The account exists in LDAP but not in the `users` table. Seed the database
  user (or register it once via an existing admin) so login can find it.
