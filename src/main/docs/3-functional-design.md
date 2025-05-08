# Functional Design

<!-- TOC -->
* [Functional Design](#functional-design)
  * [1. System Overview](#1-system-overview)
  * [2. Architecture](#2-architecture)
    * [2.1 High-Level Architecture](#21-high-level-architecture)
    * [2.2 Security Architecture](#22-security-architecture)
    * [2.3 Technologies Used](#23-technologies-used)
  * [3. Data Model](#3-data-model)
    * [3.1 Entity Relationship Diagram](#31-entity-relationship-diagram)
    * [3.2 Entity Descriptions](#32-entity-descriptions)
      * [3.2.1 Product](#321-product)
      * [3.2.2 Category](#322-category)
  * [4. API Design](#4-api-design)
    * [4.1 RESTful Principles](#41-restful-principles)
    * [4.2 Authentication API](#42-authentication-api)
      * [4.2.1 Login](#421-login)
    * [4.3 Product API](#43-product-api)
      * [4.3.1 Get All Products](#431-get-all-products)
      * [4.3.2 Get Product by ID](#432-get-product-by-id)
      * [4.3.3 Create Product](#433-create-product)
      * [4.3.4 Update Product](#434-update-product)
      * [4.3.5 Delete Product](#435-delete-product)
    * [4.4 Category API](#44-category-api)
      * [4.4.1 Get All Categories](#441-get-all-categories)
      * [4.4.2 Get Category by ID](#442-get-category-by-id)
      * [4.4.3 Create Category](#443-create-category)
      * [4.4.4 Update Category](#444-update-category)
      * [4.4.5 Delete Category](#445-delete-category)
  * [5. Error Handling](#5-error-handling)
    * [5.1 Error Response Format](#51-error-response-format)
    * [5.2 Error Types](#52-error-types)
  * [6. Security Implementation](#6-security-implementation)
    * [6.1 Authentication Flow](#61-authentication-flow)
    * [6.2 Authorization Rules](#62-authorization-rules)
  * [7. Database Management](#7-database-management)
    * [7.1 Database Initialization](#71-database-initialization)
    * [7.2 Database Configuration](#72-database-configuration)
  * [8. API Documentation](#8-api-documentation)
<!-- TOC -->

Initially based on the design document the project structure will be created by using Spring Initializr with the [following configuration](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.4.5&packaging=jar&jvmVersion=21&groupId=com.exercise&artifactId=exercise-java&name=exercise-java&description=Java%20exercise&packageName=com.exercise&dependencies=lombok,web,security,flyway,h2,data-jpa).

## 1. System Overview

The Product Inventory API is a RESTful web service built with Spring Boot that provides endpoints for managing products and categories in an inventory system. The system supports authentication, authorization, data validation, and comprehensive error handling.

## 2. Architecture

### 2.1 High-Level Architecture

The application follows a layered architecture, common in Spring Boot applications:

```
┌─────────────────┐
│  API Layer      │ Controllers, Request/Response DTOs
├─────────────────┤
│  Service Layer  │ Business Logic, Validation
├─────────────────┤
│  Data Layer     │ Repositories, Entity Models
├─────────────────┤
│  Database       │ H2 In-Memory Database
└─────────────────┘
```

### 2.2 Security Architecture

The application implements JWT-based authentication and role-based access control:

```
┌─────────────────┐
│  Client         │ Sends JWT token in Authorization header
├─────────────────┤
│  JWT Filter     │ Validates token and sets authentication context
├─────────────────┤
│  Security       │ Enforces role-based access control
│  Configuration  │
├─────────────────┤
│  API Endpoints  │ Protected based on user roles
└─────────────────┘
```

### 2.3 Technologies Used
- **Backend Framework:** Spring Boot 3.x
- **Language:** Java 21
- **Build Tool:** Apache Maven
- **Data Persistence:** Spring Data JPA, Hibernate
- **Database:** H2 (Embedded)
- **Database Migration:** Flyway
- **Security:** Spring Security, JSON Web Tokens
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Utilities:** Lombok (for reducing boilerplate code)
- **Testing:** JUnit 5, Mockito, Spring Test

## 3. Data Model

### 3.1 Entity Relationship Diagram

```
┌───────────────┐       ┌─────────────────┐
│   Category    │       │     Product     │
├───────────────┤       ├─────────────────┤
│ id (PK)       │       │ id (PK)         │
│ name          │       │ name            │
│ version       │       │ description     │
│               │◄──────┤ price           │
│               │       │ quantity        │
│               │       │ version         │
│               │       │ category_id (FK)│
└───────────────┘       └─────────────────┘
```

### 3.2 Entity Descriptions

#### 3.2.1 Product
- **id**: Long - Primary key
- **name**: String - Product name
- **description**: String - Product description
- **price**: BigDecimal - Product price
- **quantity**: Integer - Available quantity
- **version**: Long - Used for optimistic locking
- **category**: Category - Associated category

#### 3.2.2 Category
- **id**: Long - Primary key
- **name**: String - Category name
- **version**: Long - Used for optimistic locking

## 4. API Design

### 4.1 RESTful Principles
- Use of standard HTTP methods (GET, POST, PUT, DELETE).
- Stateless communication (managed by JWTs).
- Resource-based URLs (e.g., `/products`, `/products/{id}`).
- Use of standard HTTP status codes for responses.
- JSON as the primary data format for requests and responses.

### 4.2 Authentication API

#### 4.2.1 Login
- **Endpoint**: POST `/auth/login`
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "token": "string"
  }
  ```

### 4.3 Product API

#### 4.3.1 Get All Products
- **Endpoint**: GET `/products`
- **Query Parameters**:
  - `page`: int (default: 0)
  - `pageSize`: int (default: 10)
  - `sortBy`: string (default: "id")
  - `sortDir`: string (default: "ASC")
- **Response**: Paginated list of products
  ```json
  {
    "count": "int",
    "currentPage": "int",
    "totalPages": "int",
    "pageSize": "int",
    "results": [
      "array"
    ]
  }
  ```
#### 4.3.2 Get Product by ID
- **Endpoint**: GET `/products/{id}`
- **Response**: Product details with version

#### 4.3.3 Create Product
- **Endpoint**: POST `/products`
- **Request Body**: Product data with version
- **Response**: Created product
  ```json
  {
    "id": "long",
    "name": "string",
    "version": "long"
  }
  ```

#### 4.3.4 Update Product
- **Endpoint**: PUT `/products/{id}`
- **Request Body**: Product data with version
- **Response**: Updated product with new version

#### 4.3.5 Delete Product
- **Endpoint**: DELETE `/products/{id}`
- **Response**: No content

### 4.4 Category API

#### 4.4.1 Get All Categories
- **Endpoint**: GET `/categories`
- **Query Parameters**:
    - `page`: int (default: 0)
    - `pageSize`: int (default: 10)
    - `sortBy`: string (default: "id")
    - `sortDir`: string (default: "ASC")
- **Response**: Paginated list of categories
  ```json
  {
    "count": "int",
    "currentPage": "int",
    "totalPages": "int",
    "pageSize": "int",
    "results": [
      "array"
    ]
  }
  ```

#### 4.4.2 Get Category by ID
- **Endpoint**: GET `/categories/{id}`
- **Response**: Category details

#### 4.4.3 Create Category
- **Endpoint**: POST `/categories`
- **Request Body**: Category data
- **Response**: Created category
  ```json
  {
    "id": "long",
    "name": "string",
    "version": "long"
  }
  ```

#### 4.4.4 Update Category
- **Endpoint**: PUT `/categories/{id}`
- **Request Body**: Category data
- **Response**: Updated category

#### 4.4.5 Delete Category
- **Endpoint**: DELETE `/categories/{id}`
- **Response**: No content

## 5. Error Handling

### 5.1 Error Response Format
```json
{
  "timestamp": "2025-05-08T12:34:56.789Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 123"
}
```

### 5.2 Error Types
- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource does not exist
- **409 Conflict**: Optimistic locking conflict
- **500 Internal Server Error**: Unexpected server error

## 6. Security Implementation

### 6.1 Authentication Flow
1. Client sends credentials to `/auth/login`
2. Server validates credentials and generates JWT token
3. Server returns JWT token to client
4. Client includes token in Authorization header for subsequent requests
5. JwtAuthenticationFilter validates token and sets security context

### 6.2 Authorization Rules
- Public endpoints: `/auth/login`, `/swagger-ui/*`
- GET endpoints: Accessible to authenticated users (FULL, READER)
- POST, PUT, DELETE endpoints: Accessible only to FULL users

## 7. Database Management

### 7.1 Database Initialization
- **Flyway:** Used for version-controlled database migrations.
- Initial schema creation script
- Sample data insertion script

### 7.2 Database Configuration
- H2 in-memory database for development and testing
- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa`
- Password: `password`

## 8. API Documentation

- OpenAPI/Swagger documentation available at /swagger-ui.html
