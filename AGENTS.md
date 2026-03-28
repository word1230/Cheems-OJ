# Repository Guidelines

## Project Structure & Module Organization
This repository is a monorepo with three services and root-level orchestration:

- `coj-frontend-master/`: Vue 3 + TypeScript client. Main app code lives in `src/`, static files in `public/`, and generated API clients in `generated/`.
- `coj-backend-master/`: Spring Boot backend. Java sources are under `src/main/java/com/cheems/coj`, MyBatis XML mappings under `src/main/resources/mapper`, and SQL/bootstrap files under `sql/`.
- `coj-code-sandbox-master/`: isolated code execution service. Main code is under `src/main/java/com/coj/codesandbox`.
- `docker-compose.yml`: local stack for MySQL, Redis, Elasticsearch, sandbox, backend, and frontend.

Do not hand-edit `coj-frontend-master/generated/` unless you are regenerating the client.

## Build, Test, and Development Commands
- `docker compose up --build`: start the full local stack from the repo root.
- `npm install && npm run serve` in `coj-frontend-master/`: run the frontend dev server.
- `npm run build` in `coj-frontend-master/`: build the frontend for production.
- `npm run lint` in `coj-frontend-master/`: run ESLint + Prettier-backed checks.
- `./mvnw spring-boot:run` in `coj-backend-master/`: run the backend locally on port `8121`.
- `./mvnw test` in `coj-backend-master/`: run backend tests.
- `./mvnw spring-boot:run` in `coj-code-sandbox-master/`: run the sandbox locally on port `8090`.
- `./mvnw test` in `coj-code-sandbox-master/`: run sandbox tests.

## Coding Style & Naming Conventions
Follow the style already present in each module: 2-space indentation in frontend config/TS files, 4-space indentation in Java. Use `PascalCase` for Vue components (`UserLoginView.vue`) and Java classes (`QuestionController`), `camelCase` for variables/functions, and package names by feature. Backend DTO/VO/controller/service suffixes are established patterns. Frontend linting is defined by `coj-frontend-master/.eslintrc.js` and Prettier by `.prettierrc`.

## Testing Guidelines
Backend and sandbox tests use Spring Boot’s JUnit setup under `src/test/java`. Name tests `*Test` or `*Tests` to match the current suite. Frontend has no committed unit-test harness yet, so every frontend change should pass `npm run lint` and include manual verification for the affected route or component.

## Commit & Pull Request Guidelines
Recent history uses scoped conventional commits such as `feat(docker): ...` and `fix(auth): ...`. Keep the format `type(scope): summary`, with concise scopes like `frontend`, `sandbox`, `docker`, or `post`. PRs should state which modules changed, list required config or SQL updates, link related issues, and include screenshots for UI work. If API contracts change, mention whether `generated/` was regenerated.

## Security & Configuration Tips
`application.yml` and `docker-compose.yml` contain local example credentials and sandbox keys. Treat them as development defaults only: override secrets locally, avoid committing real credentials, and keep backend `codesandbox.auth` values synchronized with sandbox `api-auth`.
