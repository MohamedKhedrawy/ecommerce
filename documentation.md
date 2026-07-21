# E-Commerce Backend: Design and Implementation

This document outlines the core architecture and implementation decisions for the e-commerce backend service.

---

## 1. System Architecture

The application implements a strict layered architecture pattern to enforce separation of concerns:

*   **Models:** JPA Entities mapping directly to database tables (`User`, `Product`, `Order`, `OrderItem`, `Role`).
*   **Repositories:** Spring Data JPA interfaces for database interaction.
*   **Services:** Encapsulate all business logic and transaction boundaries.
*   **Controllers:** Expose REST API endpoints and manage HTTP request/response lifecycles.
*   **DTOs (Data Transfer Objects):** Define API contracts for requests and responses.

**Database Configuration:** MySQL is utilized as the primary relational data store due to its standard compatibility with Spring Data JPA and suitability for e-commerce transactional workloads.

---

## 2. Implementation Decisions

### A. Data Transfer Objects (DTOs)
*   **Implementation:** API boundaries strictly consume and return DTOs (e.g., `OrderRequest`, `UserResponse`).
*   **Rationale:** Prevents exposure of internal entity structures and sensitive data (e.g., password hashes). Enables API-level validation (using `jakarta.validation`) independent of database constraints.

### B. Authentication and Authorization
*   **Implementation:** Stateless JSON Web Tokens (JWT) combined with Spring Security and Role-Based Access Control (RBAC).
*   **Rationale:** Stateless architecture eliminates the need for server-side session replication, enabling horizontal scalability. RBAC ensures strict endpoint protection (e.g., `POST /api/products` is restricted to the `ADMIN` role).

### C. Transaction Management
*   **Implementation:** The `@Transactional` annotation is applied at the service layer, particularly on `OrderService.placeOrder()`.
*   **Rationale:** Order placement involves multiple discrete database operations (order creation, item assignment, stock deduction). Transactional boundaries ensure atomicity, preventing data inconsistencies (e.g., stock depletion without order creation) in the event of an application exception.

### D. Historical Pricing Integrity
*   **Implementation:** The `OrderItem` entity maintains a `priceAtPurchase` field.
*   **Rationale:** Decouples historical order data from current product pricing. Ensures that future modifications to a `Product`'s price do not retroactively alter the calculated totals of finalized orders.

### E. Global Exception Handling
*   **Implementation:** A centralized `@RestControllerAdvice` component intercepts application exceptions.
*   **Rationale:** Normalizes all error responses into a consistent JSON structure. Prevents the exposure of raw Java stack traces to the client layer, mitigating potential information disclosure vulnerabilities.

### F. Application Bootstrapping
*   **Implementation:** A `DataSeeder` component implementing `CommandLineRunner` injects a default `admin` user on application startup.
*   **Rationale:** Automates the creation of the initial high-privileged account without exposing insecure admin-creation endpoints on the public API.

### G. Automated Unit Testing
*   **Implementation:** Core business logic and API endpoints (Services, Security utilities, and REST Controllers) are verified via JUnit 5, Mockito, and Spring `MockMvc`.
*   **Rationale:** Unit tests isolate the service layer from external dependencies via mocking, enabling fast execution for business logic. Controller tests utilizing `@WebMvcTest` and `MockMvc` validate routing, HTTP status codes, DTO validation, and security constraints (RBAC) without bootstrapping the full application context.

### H. API Documentation
*   **Implementation:** OpenAPI 3.0 specification generated dynamically via `springdoc-openapi-starter-webmvc-ui`.
*   **Rationale:** Provides an interactive, self-updating Swagger UI. This eliminates the operational overhead of maintaining static API documentation and offers consumers a reliable contract for integration.
