# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Structure

Three independent subprojects in one repo:

| Subproject | Role | Port |
|---|---|---|
| `coj-backend-master` | Spring Boot 2.7.2 REST API (monolith) | 8121 (dev), 8101 (prod) |
| `coj-code-sandbox-master` | Spring Boot 2.7.14 code execution service | 8090 |
| `coj-frontend-master` | Vue 3 SPA | 8080 |

Each subproject is independently built and run. There is no root build system.

---

## Backend (`coj-backend-master`)

### Commands
```bash
# Run (from subproject root)
mvn spring-boot:run

# Build JAR
mvn clean package -DskipTests

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName
```

### Architecture
- **Entry point**: `src/main/java/com/cheems/coj/MainApplication.java`
- **API context path**: `/api` (all endpoints prefixed)
- **API docs**: Knife4j at `/api/doc.html` (dev only)
- **Auth**: Session-based via Spring Session + Redis. `@AuthCheck` annotation enforces role.
- **Access roles**: `user` < `admin` (string constants in `UserConstant`)
- **Judge flow**: `QuestionController` → `QuestionSubmitService.doQuestionSubmit()` → async `JudgeService.doJudge()` → `JudgeManager` selects strategy → calls code sandbox via HTTP with HMAC auth
- **Code sandbox call**: `codesandbox/` package; factory pattern selects `example`/`remote`/`thirdParty` sandbox; `CodeSandboxProxy` wraps for request/response logging only
- **Judge strategies**: `strategy/DefaultJudgeStrategy` (all languages), `strategy/JavaLanguageJudgeStrategy` (Java adds 10 MB memory overhead to account for JVM baseline)
- **Database**: MySQL via MyBatis-Plus. Schema in `sql/create_table.sql`. **Note**: `map-underscore-to-camel-case` is `false` — column names must match field names exactly.
- **Redis**: Used for session storage only.
- **Elasticsearch**: Optional; `esdao/` package for post search. `job/cycle/IncSyncPostToEs` handles incremental ES sync.
- **Config**: `src/main/resources/application.yml` (dev), `application-prod.yml` (prod). Sandbox URL and HMAC keys configured here.

### Key packages
```
controller/     HTTP handlers
judge/          Judging orchestration + sandbox client
model/          entity/, dto/, vo/, enums/, codesandbox/
service/        Business logic interfaces + impls
mapper/         MyBatis-Plus mappers
aop/            AuthInterceptor (@AuthCheck enforcement), LogInterceptor (request logging)
job/            Scheduled jobs (ES sync)
```

### Known gaps
- `JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED` has an empty string value (not yet implemented)
- Memory usage is not tracked: `judgeInfo.setMemory()` is commented out in the sandbox template

---

## Code Sandbox (`coj-code-sandbox-master`)

### Commands
```bash
mvn spring-boot:run
mvn clean package -DskipTests
```

### Architecture
- **Entry point**: `src/main/java/com/coj/codesandbox/CojCodeSandboxApplication.java`
- **Active implementation**: `MainController` hardcodes `JavaNativeCodeSandbox` — the Docker implementation exists but is not wired in
- **Two execution modes**:
  - `JavaNativeCodeSandbox` — compiles and runs Java via `ProcessBuilder`; uses `security/` package (`DefaultSecurityManager`, `DenySecurityManager`, `MySecurityManager`) to restrict file/network access
  - `JavaDockerCodeSandbox` — runs code inside a Docker container (requires Docker daemon); not active by default
- **Template method pattern**: `JavaCodeSandboxTemplate` defines the pipeline (save file → compile → run → collect result); both implementations extend it
- **Auth**: `ApiSignatureFilter` validates every request using HMAC-SHA256. Requires `accessKey`, `timestamp`, `nonce`, and `sign` headers. Replay window is 300 seconds. `CachedBodyRequestWrapper` buffers the request body so the filter can read it for signature verification.
- **Temp files**: Written to `tmpCode/` at runtime; cleaned up after execution.
- **Security config**: `src/main/resources/security/` contains Java security policy files used by the native sandbox.

---

## Frontend (`coj-frontend-master`)

See also `coj-frontend-master/CLAUDE.md` for detailed frontend-specific guidance.

### Commands
```bash
npm run serve      # Dev server (port 8080)
npm run build      # Production build
npm run lint       # ESLint

# Regenerate API client (backend must be running on 8121)
npx openapi-typescript-codegen --input http://localhost:8121/api/v2/api-docs --output ./generated --type axios
```

### Architecture
- **Layout**: `BasicLayout` (GlobalHeader + content) for most routes; `UserLayout` (centered, no header) for `/user/*`
- **Access control**: Router guard in `src/access/index.ts`; roles `notLogin` < `user` < `admin`; `meta.access` on routes sets required level; `meta.hideInMenu: true` hides from the nav bar
- **State**: Vuex store `user` module — mutation is `user/updateUser`, action is `user/getLoginUser`
- **API calls**: Always use `generated/services/*` — never write raw axios calls
- **Base URL**: `src/plugins/axios.ts`
- **Design system**: Apple-inspired minimalism; tokens in `src/assets/global.css`; Inter font
- **Code templates**: Default starter code per language in `src/utils/codeTemplates.ts`

### Routes
| Path | Component | Access | In Nav |
|------|-----------|--------|--------|
| `/` | HomeView | notLogin | yes |
| `/questions` | QuestionsView | notLogin | yes |
| `/view/question/:id` | ViewQuestionView | user | no |
| `/question/submit` | QuestionSubmitView | user | no |
| `/question/add` | AddQuestionView | admin | no |
| `/posts` | PostListView | notLogin | yes |
| `/post/:id` | PostDetailView | notLogin | no |
| `/post/add` | PostAddView | user | no |
| `/manage/question/` | ManageQuestionView | admin | yes |
| `/manage/post` | ManagePostView | admin | yes |
| `/user/login` | UserLoginView | notLogin | — |
| `/user/register` | UserRegisterView | notLogin | — |
| `/user/profile` | UserProfileView | user | no |
| `/user/profile/edit` | UserProfileEdit | notLogin | — |

### Package notes
- `@arco-design/web-vue` is in `devDependencies` (not `dependencies`) — this is intentional for the build setup
- `ant-design-vue` is installed but not used; use Arco Design components exclusively

---

## Infrastructure

### Docker
- `coj-backend-master/Dockerfile` — backend JAR image
- `coj-code-sandbox-master/Dockerfile` — sandbox image

### Shared Secrets
The backend and sandbox share HMAC credentials. Keep them in sync:
- Backend: `application.yml` → `codesandbox.auth.*`
- Sandbox: `application.yml` → `api-auth.access-key` / `api-auth.secret-key`
