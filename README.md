# Matchday Companion — Crowd Status API (V1)

Backend service powering the **Crowd Meter** feature: users can report venue busyness (**QUIET / OK / PACKED**) with a **server-enforced cooldown**.

---

## Features

* Get crowd status per venue
* List crowd statuses
* Update crowd status (1-tap)
* **Cooldown enforced server-side**: 1 update per venue per device per 30 minutes (**429 + Retry-After**)
* Audit trail of updates (`venue_status_update`)
* Swagger/OpenAPI docs
* Dockerized (API + Postgres)
* Request tracing: **X-Request-Id** returned in responses and included in logs (MDC)

---

## Tech

* Spring Boot 4 (Java 17)
* PostgreSQL + Flyway migrations
* Spring Data JPA
* springdoc-openapi (Swagger UI)
* Testcontainers integration tests

---

## Run locally (Docker)

### 1) Create env file

```bash
cp .env.example .env
```

### 2) Start services

```bash
docker compose up --build
```

### Useful URLs

* API: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* OpenAPI JSON: `http://localhost:8080/v3/api-docs`
* Health: `http://localhost:8080/actuator/health`

> CORS is configured via `APP_CORS_ALLOWED_ORIGINS` (see `.env.example`).

---

## API quickstart

### List venue statuses

```bash
curl -s http://localhost:8080/venue_status
```

### Get one venue status

```bash
curl -s http://localhost:8080/venue_status/A1
```

### Update venue status

`X-Device-Id` is required.

```bash
curl -i -X PUT "http://localhost:8080/venue_status/A1" \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: dev-123" \
  -d '{"status":"PACKED"}'
```

### Cooldown response (429)

If cooldown is active, the API returns:

* HTTP `429`
* `Retry-After: <seconds>` header
* error body containing `retryAfterSeconds`

Example:

```bash
curl -i -X PUT "http://localhost:8080/venue_status/A1" \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: dev-123" \
  -d '{"status":"OK"}'
```

---

## Request tracing

* Send `X-Request-Id` to propagate an existing request id
* If missing, the server generates one
* The response always includes `X-Request-Id`
* Logs include the `requestId` via MDC

Example:

```bash
curl -i http://localhost:8080/venue_status/A1 \
  -H "X-Request-Id: demo-req-001"
```

---

## Run tests

```bash
./mvnw clean test
```

---

## Configuration

Common env vars (see `.env.example`):

* `SPRING_PROFILES_ACTIVE` (dev/prod)
* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`
* `APP_CORS_ALLOWED_ORIGINS`

---

## Data model (V1)

* `venue_status` stores the latest status per venue
* `venue_status_update` stores the audit trail of updates

---

## Roadmap

* Pagination / filtering for list endpoints
* Auth (optional)
* Production deployment manifest (Render/Fly.io/K8s)
