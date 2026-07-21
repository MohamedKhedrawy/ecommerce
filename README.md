# E-Commerce Backend System

This is a robust, clean-architecture Spring Boot backend application built for a modern e-commerce platform. It provides RESTful APIs to manage Products, Users, and Orders, protected by role-based access control and stateless JSON Web Tokens (JWT).

## 🚀 Key Features

*   **Clean Layered Architecture:** Follows strict Model-Repository-Service-Controller segregation.
*   **Security (JWT & RBAC):** Stateless authentication. `USER` can browse products and place orders, while `ADMIN` can manage products and view all system orders.
*   **Data Integrity:** Implements snapshots for historical pricing in `OrderItem` and fully `@Transactional` methods for order placement to prevent overselling stock.
*   **Validation & Error Handling:** Fully utilizes Java Bean Validation on all Request DTOs and provides clean, JSON-formatted error messages globally via `@RestControllerAdvice`.
*   **Secure Models:** Entity models are heavily encapsulated. Only Data Transfer Objects (DTOs) are passed over the network, ensuring zero exposure of sensitive data like password hashes.

## 📖 Documentation & Architecture

For a complete breakdown of the system design, architectural decisions, and the reasoning behind specific implementations (like DTOs and `@Transactional`), please read the **[Design & Implementation Document](documentation.md)**.

## 🗄️ Database Configuration

By default, the application is configured to connect to a local database.

You must configure your database credentials before running the application. The configuration file is located at:
**`src/main/resources/application.properties`**

Inside this file, locate the `DataSource` section and update the `URL`, `username`, and `password` to match your local database environment:

```properties
# ─────────────────────────────────────────────────────────────
# DataSource Configuration
# ─────────────────────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_database_password_here
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
*(Note: If the `ecommerce_db` database does not exist, the driver will automatically attempt to create it. If you prefer to use Oracle as per the spec alternative, simply replace the MySQL driver and URL accordingly).*

The database schema, including tables and relationships, is automatically generated and managed by **Hibernate (Spring Data JPA)** upon application startup. Additionally, `data.sql` will automatically seed default roles (`USER`, `ADMIN`) upon startup.

## 🏃 How to Run

1.  Ensure you have **Java 25** and a compatible database running.
2.  Update your database credentials in `src/main/resources/application.properties`.
3.  Open a terminal in the root directory and run the application using the Maven wrapper:

```bash
# Compile and start the application
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

## 🧪 Testing and Documentation

**1. Swagger API Documentation (Live)**
When the application is running, you can view the fully interactive, auto-generated OpenAPI documentation by visiting:
👉 **`http://localhost:8080/swagger-ui.html`**

**2. Automated Unit Tests**
Core business logic (like stock validation in `OrderService`) is verified via JUnit 5 and Mockito. To run the test suite:
```bash
./mvnw test
```

**3. Postman Collection**
A complete **Postman Collection** is provided (`Postman_Collection.json`). 
1. Import the collection into Postman.
2. Hit **Login Admin** or **Login User**. The collection contains an automated script that instantly saves your JWT token.
3. You can now seamlessly hit any protected endpoint without manually copying tokens!

## 📁 Project Structure

*   `model/` — JPA Entities (`User`, `Role`, `Product`, `Order`, `OrderItem`).
*   `repository/` — Spring Data JPA interface definitions.
*   `dto/` — Request and Response objects utilized by the API.
*   `service/` — Core business logic, transaction management, and DTO mappings.
*   `controller/` — REST API endpoints.
*   `security/` — JWT utilities, Security filters, and Spring Security configurations.
*   `exception/` — Custom exceptions and the Global Exception Handler (`@RestControllerAdvice`).
