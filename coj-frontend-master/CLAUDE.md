# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Development server
npm run serve

# Production build
npm run build

# Lint
npm run lint

# Regenerate API client from OpenAPI spec (run from project root)
npx openapi-typescript-codegen --input http://localhost:8080/api/v3/api-docs --output ./generated --type axios
```

## Architecture

### Tech Stack
- **Vue 3** with `<script setup>` (Composition API)
- **Arco Design Vue** (`@arco-design/web-vue`) — primary UI component library
- **Vuex 4** — global state management
- **Vue Router 4** — routing with route-level access control
- **Monaco Editor** — in-browser code editor
- **ByteMD** — Markdown editor/viewer
- **Generated API client** — auto-generated TypeScript client under `generated/` from backend OpenAPI spec

### Layout System
`App.vue` splits routing into two layout modes:
- `/user/*` routes → `UserLayout.vue` (centered, full-screen auth pages)
- All other routes → `BasicLayout.vue` (header + content + footer with `GlobalHeader`)

### Access Control
- `src/access/accessEnum.ts` — defines three roles: `notLogin`, `user`, `admin`
- `src/access/checkAccess.ts` — role comparison utility
- `src/access/index.ts` — global `router.beforeEach` guard; auto-fetches login user on first navigation, redirects unauthenticated/unauthorized users
- Route `meta.access` sets required permission; `meta.hideInMenu: true` hides from nav

### State Management
`src/store/user.ts` (namespaced as `user`) holds `loginUser`. The `getLoginUser` action calls the backend and updates state; called automatically by the nav guard on every cold navigation.

### API Client
All backend calls go through `generated/services/*`. Never write raw `axios` calls — import from `generated`. The base URL is configured in `src/plugins/axios.ts`.

### Key Views
| Path | View | Notes |
|------|------|-------|
| `/` | `HomeView` | Welcome + user stats |
| `/questions` | `QuestionsView` | Paginated question list with search |
| `/view/question/:id` | `ViewQuestionView` | Split: question detail (left) + Monaco code editor (right) |
| `/question/add` | `AddQuestionView` | Admin: create question |
| `/manage/question` | `ManageQuestionView` | Admin: manage questions |
| `/user/login` | `UserLoginView` | Auth |
| `/user/register` | `UserRegisterView` | Auth |
| `/posts` | `PostListView` | Community posts |

### Environment Files
- `.env.development` — development API base URL
- `.env.production` — production API base URL
