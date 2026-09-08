# Student Management Portal

A full-stack student record management system built with **Java Servlets, Hibernate, MySQL, and React** — no Spring Boot, built from the ground up to demonstrate core backend fundamentals.

## Features

- Full CRUD operations (Create, Read, Update, Delete) for student records
- RESTful API built with raw Java Servlets (`GET`, `POST`, `PUT`, `DELETE`)
- Hibernate ORM for database persistence with MySQL
- React frontend with live search/filter, client + server-side validation
- Duplicate email detection with proper HTTP status codes (409 Conflict)
- CORS handling for cross-origin frontend-backend communication

## Tech Stack

**Backend**
- Java 21
- Jakarta Servlet API 6.0
- Hibernate ORM 6.6 (Jakarta Persistence)
- MySQL
- Maven
- Apache Tomcat 10

**Frontend**
- React
- Fetch API for HTTP requests
- Custom CSS (no UI framework)

## Running Locally

### Backend
1. Create the MySQL database: `CREATE DATABASE student_management;`
2. Update `backend/src/main/resources/hibernate.cfg.xml` with your MySQL credentials
3. Import the `backend/` folder into Eclipse as a Maven project
4. Run on Apache Tomcat (10.x)
5. Backend runs at `http://localhost:8081/StudentManagementPortal`

### Frontend
```bash
cd frontend
npm install
npm start
```
Runs at `http://localhost:3000`

## Author

Built as a hands-on learning project to understand Java web development fundamentals.
