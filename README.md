# Library Management System (LMS)

A RESTful API for managing books, patrons, and borrowing records in a library. Built with Spring Boot, PostgreSQL, and Redis.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Postman Collection](#postman-collection)
- [Running Tests](#running-tests)

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL
- Redis

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/neodoomer/libms
   cd libms
   ```
2. **Run Docker compose**
   ```bash
    docker-compose up -d
   ```
3. **Build the project**
   ```bash
   mvn clean install
   ```
4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
## API Documentation
You can find the postman collection in the root of the project:

### Postman Collection:
```declarative
libms.postman_collection.json
```
**Base URL**
```declarative
http://localhost:8080/api
```
## API Endpoints

### Authentication
| Method | Endpoint       | Description          |
|--------|----------------|----------------------|
| POST   | `/auth/register` | Register new user    |
| POST   | `/auth/login`    | Login with credentials|
| POST   | `/auth/refresh`  | Refresh access token  |

### Books
| Method | Endpoint       | Description          |
|--------|----------------|----------------------|
| GET    | `/books`       | Get all books        |
| GET    | `/books/{id}`  | Get a book by ID     |
| POST   | `/books`       | Create a new book    |
| PUT    | `/books/{id}`  | Update a book by ID  |
| DELETE | `/books/{id}`  | Delete a book by ID  |

### Patrons
| Method | Endpoint         | Description           |
|--------|------------------|-----------------------|
| GET    | `/patrons`       | Get all patrons       |
| GET    | `/patrons/{id}`  | Get a patron by ID    |
| POST   | `/patrons`       | Create a new patron   |
| PUT    | `/patrons/{id}`  | Update a patron by ID |
| DELETE | `/patrons/{id}`  | Delete a patron by ID |


### Borrowing
| Method | Endpoint                          | Description                |
|--------|-----------------------------------|----------------------------|
| POST   | `/borrow/{bookId}/patron/{patronId}` | Borrow a book              |
| PUT    | `/return/{bookId}/patron/{patronId}` | Return a borrowed book     |

# Authentication Guide

This guide explains how to register, log in, and use the access token to interact with non-authentication API endpoints.

---

## 1. Register a New User

To register a new user, send a `POST` request to the `/auth/register` endpoint with the required credentials.

### Request
```bash
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "password": "securepassword"
}'
```
### Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 2. Log in

To login , send a `POST` request to the `/auth/login` endpoint with the required credentials.
### Request
```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "password": "securepassword"
}'
```
### Response
```json
{
   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
## 3. Use the Access Token in the rest api's

To access non-authentication endpoints (e.g., books, patrons, borrowing),
include the `accessToken` in the `Authorization` header of your requests.

### Example: Get All Books 
```bash
curl -X GET http://localhost:8080/api/books \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Running Tests
To run all tests you can run:
```bash
mvn test
```

