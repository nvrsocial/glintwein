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
- Project creation within workspaces for `OWNER` and `ADMIN`
- Role-based access validation for project creation
- Centralized API error handling
- PostgreSQL persistence with Flyway database migrations

## Tech Stack

> Java 21 · Spring Boot · Spring Security · Spring Data JPA · PostgreSQL · Flyway · Docker Compose · Maven · JWT

## Getting Started

Requirements: Java 21, Docker and Git.

Clone the repository:

```bash
git clone https://github.com/nvrsocial/glintwein.git
cd glintwein
```

Configure environment variables:

```bash
export DB_PASSWORD="your_database_password"
export JWT_SECRET="$(openssl rand -base64 32)"
```

Start PostgreSQL using Docker Compose:

```bash
docker compose up -d
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

To stop PostgreSQL:

```bash
docker compose down
```

PostgreSQL runs on port `5433` and uses a persistent Docker volume.

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

Protected endpoints require a valid JWT in the `Authorization: Bearer <token>` header.

## Planned Features

- Project retrieval, updates, and deletion
- Task management
- Kanban boards and task statuses
- Task assignments and priorities
- Comments and activity history
- Notifications
- Automated testing and frontend integration

> Glintwein is an ongoing project. New features and improvements will be added as development continues.
