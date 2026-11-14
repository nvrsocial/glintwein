# Glintwein

Glintwein is a Java Spring Boot backend application for managing team workspaces, projects, and tasks. The project is currently under active development.

## Current Features

- User registration and login with input validation
- JWT-based authentication with Spring Security
- Secure password hashing with BCrypt
- Duplicate email and username checks
- Authenticated user profile endpoint
- Workspace creation and retrieval
- Automatic workspace ownership assignment (`OWNER` role)
- Workspace membership model with `OWNER`, `ADMIN`, and `MEMBER` roles
- Workspace member retrieval with access validation
- Role-based permissions for workspace member management
- Member creation by email (service layer)
- Duplicate workspace membership prevention
- Workspace retrieval by ID with membership validation
- Partial workspace updates for `OWNER` and `ADMIN`
- Workspace deletion restricted to `OWNER`
- REST endpoints for adding and removing workspace members
- Workspace member role updates with owner-only permissions
- Project creation, retrieval, updates, and deletion within workspaces
- Role-based access validation for project management
- Task creation, retrieval, updates, and deletion within projects
- Kanban board with `TODO`, `IN_PROGRESS`, and `DONE` columns
- Moving tasks between columns and reordering them within a column
- Consistent task ordering under concurrent board updates
- Centralized API error handling
- PostgreSQL persistence with Flyway database migrations

## Tech Stack

> Java 21 · Spring Boot · Spring Security · Spring Data JPA · PostgreSQL · Flyway · Docker Compose · Maven · JWT

## Getting Started

Requirements: Java 21, Docker and Git. Maven is not required — the project uses the Maven Wrapper (`./mvnw`).

Clone the repository:

```bash
git clone https://github.com/nvrsocial/glintwein.git
cd glintwein
```

Create a `.env` file from the template:

```bash
cp .env.example .env
```

Open `.env` and set `DB_PASSWORD` to any password, and `JWT_SECRET` to a key generated with:

```bash
openssl rand -base64 32
```

Load the variables into your terminal (repeat this in every new terminal window):

```bash
set -a; source .env; set +a
```

Make sure Docker is running, then start PostgreSQL using Docker Compose:

```bash
docker compose up -d
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

The first run downloads Maven and all dependencies, so it may take a few minutes. Flyway creates the database tables automatically on startup.

The API will be available at `http://localhost:8080`.

To stop PostgreSQL:

```bash
docker compose down
```

PostgreSQL runs on port `5433` and uses a persistent Docker volume. To delete all data and start with an empty database, run `docker compose down -v`.

## API

### Authentication

- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Authenticate and receive a JWT

### Users

- `GET /api/users/me` — Get the authenticated user's profile

### Workspaces

- `POST /api/workspaces` — Create a workspace and assign the creator as `OWNER`
- `GET /api/workspaces` — Get workspaces belonging to the authenticated user
- `GET /api/workspaces/{workspaceId}` — Get workspace details with membership validation
- `PATCH /api/workspaces/{workspaceId}` — Update workspace name and description (`OWNER`, `ADMIN`)
- `DELETE /api/workspaces/{workspaceId}` — Delete a workspace (`OWNER` only)

### Workspace Members

- `GET /api/workspaces/{workspaceId}/members` — Retrieve workspace members with access validation
- `POST /api/workspaces/{workspaceId}/members` — Add an existing user to a workspace by email (`OWNER`, `ADMIN`)
- `PATCH /api/workspaces/{workspaceId}/members/{userId}/role` — Update a member's role (`OWNER` only)
- `DELETE /api/workspaces/{workspaceId}/members/{userId}` — Remove a workspace member with role-based permissions

### Projects

- `POST /api/workspaces/{workspaceId}/projects` — Create a project within a workspace (`OWNER`, `ADMIN`)
- `GET /api/workspaces/{workspaceId}/projects` — Get all projects of a workspace (any member)
- `GET /api/workspaces/{workspaceId}/projects/{projectId}` — Get project details (any member)
- `PATCH /api/workspaces/{workspaceId}/projects/{projectId}` — Update project name and description (`OWNER`, `ADMIN`)
- `DELETE /api/workspaces/{workspaceId}/projects/{projectId}` — Delete a project and all its tasks (`OWNER`, `ADMIN`)

### Tasks

- `POST /api/workspaces/{workspaceId}/projects/{projectId}/tasks` — Create a task at the end of its column, `TODO` by default (any member)
- `GET /api/workspaces/{workspaceId}/projects/{projectId}/tasks` — Get all tasks of a project, optionally filtered with `?status=` (any member)
- `GET /api/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}` — Get task details (any member)
- `PATCH /api/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}` — Update task title and description (any member)
- `PATCH /api/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/move` — Move a task to a column and position (any member)
- `DELETE /api/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}` — Delete a task (`OWNER`, `ADMIN`, or the task's creator)

### Board

- `GET /api/workspaces/{workspaceId}/projects/{projectId}/board` — Get the project's Kanban board: `TODO`, `IN_PROGRESS` and `DONE` columns with tasks in order (any member)

Each task has a `status` (its column) and a `position` (its zero-based index within the column). The move endpoint accepts `{ "status": "IN_PROGRESS", "position": 0 }`. If `position` is omitted or larger than the column, the task is placed last. The positions of the other tasks are shifted automatically.

Protected endpoints require a valid JWT in the `Authorization: Bearer <token>` header.

## Planned Features

- Task assignments and priorities
- Comments and activity history
- Notifications
- Automated testing and frontend integration

> Glintwein is an ongoing project. New features and improvements will be added as development continues.
