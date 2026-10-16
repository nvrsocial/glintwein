# TaskFlow

TaskFlow is a backend application for managing team projects and tasks. The project is currently under development, and more features will be added over time.

## Current Features

- User registration with input validation
- Secure password hashing with BCrypt
- Duplicate email and username checks
- Centralized API error handling
- PostgreSQL persistence with Flyway migrations

## Tech Stack

>Java 21 · Spring Boot · Spring Security · Spring Data JPA · PostgreSQL · Flyway · Docker Compose · Maven

## API

`POST /api/auth/register` — register a new user.

## Planned Features

Login and JWT authentication, workspaces, project and task management, team roles, comments, and notifications.

> TaskFlow is an ongoing project. New features and improvements will be added as development continues.
