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
- Centralized API error handling
- PostgreSQL persistence with Flyway database migrations

## Tech Stack

> Java 21 · Spring Boot · Spring Security · Spring Data JPA · PostgreSQL · Flyway · Docker Compose · Maven · JWT

## API

### Authentication
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Authenticate and receive a JWT

### Users
- `GET /api/users/me` — Get the authenticated user's profile

### Workspaces
- `POST /api/workspaces` — Create a workspace and assign the creator as `OWNER`
- `GET /api/workspaces` — Get workspaces belonging to the authenticated user

Protected endpoints require a valid JWT in the `Authorization: Bearer <token>` header.

## Planned Features

- Workspace member management and role-based access control
- Project and task management
- Kanban boards and task statuses
- Task assignments and priorities
- Comments and activity history
- Notifications
- Automated testing and frontend integration

> Glintwein is an ongoing project. New features and improvements will be added as development continues.