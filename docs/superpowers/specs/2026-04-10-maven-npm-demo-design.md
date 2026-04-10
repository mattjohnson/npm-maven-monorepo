# Maven + NPM Demo Project Design

## Purpose

Create a demo Todo web application with both Maven (Java/Spring Boot backend) and NPM (React frontend) build systems in the same repository. All build files (`pom.xml`, `package.json`, `package-lock.json`) live in the repo root. Dependencies are intentionally 1-2 years old to enable testing dependency upgrades.

## Architecture

**Approach**: Flat root with both build systems. `pom.xml`, `package.json`, and `package-lock.json` all in the repository root. Java sources follow Maven convention (`src/main/java/`), React sources live under `frontend/`.

## Backend (Maven / Spring Boot)

- **Framework**: Spring Boot 2.7.18 (Java 17)
- **Dependencies**:
  - `spring-boot-starter-web` (2.7.18) — REST API with embedded Tomcat
  - `spring-boot-starter-data-jpa` (2.7.18) — JPA/Hibernate for data access
  - `h2` (2.1.214) — In-memory database, zero config
  - `lombok` (1.18.24) — Boilerplate reduction
- **REST Endpoints**: CRUD on `/api/todos` (GET list, GET by id, POST, PUT, DELETE)
- **Entity**: Todo with id, title, completed, createdAt fields
- **Config**: `application.properties` with H2 console enabled, Hibernate auto-DDL

### Backend files

- `src/main/java/com/example/todo/TodoApplication.java` — Spring Boot main class
- `src/main/java/com/example/todo/Todo.java` — JPA entity
- `src/main/java/com/example/todo/TodoRepository.java` — Spring Data JPA repository
- `src/main/java/com/example/todo/TodoController.java` — REST controller
- `src/main/resources/application.properties` — DB and server config

## Frontend (NPM / React)

- **Framework**: React 17.0.2 with Vite 3.2.7
- **Dependencies** (package.json):
  - `react` (17.0.2)
  - `react-dom` (17.0.2)
  - `axios` (0.27.2)
- **Dev Dependencies**:
  - `vite` (3.2.7)
  - `@vitejs/plugin-react` (2.2.0)
- **Vite proxy**: `/api` requests forwarded to Spring Boot on port 8080

### Frontend files

- `frontend/index.html` — HTML entry point
- `frontend/vite.config.js` — Vite config with API proxy
- `frontend/src/main.jsx` — React entry point (ReactDOM.render for React 17)
- `frontend/src/App.jsx` — Todo list UI with CRUD operations
- `frontend/src/App.css` — Basic styling

## Project Structure

```
/
├── pom.xml
├── package.json
├── package-lock.json
├── .gitignore
├── docs/superpowers/specs/
│   └── 2026-04-10-maven-npm-demo-design.md
├── src/main/java/com/example/todo/
│   ├── TodoApplication.java
│   ├── Todo.java
│   ├── TodoRepository.java
│   └── TodoController.java
├── src/main/resources/
│   └── application.properties
└── frontend/
    ├── index.html
    ├── vite.config.js
    └── src/
        ├── App.jsx
        ├── App.css
        └── main.jsx
```

## Running

- **Backend**: `mvn spring-boot:run` (starts API on port 8080)
- **Frontend**: `cd frontend && npm run dev` (starts Vite dev server, proxies API)
- **Install frontend deps**: `npm install` (from root, generates package-lock.json)

## Upgrade Paths Available

- Spring Boot 2.7.18 -> 3.x (major, requires Jakarta EE namespace migration)
- H2 2.1.214 -> 2.2.x+
- Lombok 1.18.24 -> 1.18.30+
- React 17.0.2 -> 18.x / 19.x (requires createRoot migration)
- Axios 0.27.2 -> 1.x (major)
- Vite 3.2.7 -> 5.x / 6.x (major)
