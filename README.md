# Matchday Companion — Crowd Status API (V1)

Backend service that powers the **Crowd Meter** feature:
users can report venue busyness (**QUIET / OK / PACKED**) with server-side cooldown enforcement.

## Features
- Read crowd status per venue
- Update crowd status (1-tap)
- **Server-side rate limit**: 1 update per venue per device per 30 minutes
- Audit trail of updates
- Swagger/OpenAPI docs
- Dockerized (API + Postgres)

## Tech
- Spring Boot 3, Java 17
- PostgreSQL
- Flyway migrations
- Spring Data JPA
- springdoc-openapi

## Run locally (Docker)
```bash
docker compose up --build
