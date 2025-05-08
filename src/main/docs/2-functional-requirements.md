# Functional Requirements

<!-- TOC -->
* [Functional Requirements](#functional-requirements)
  * [1. Introduction](#1-introduction)
    * [1.1 Purpose](#11-purpose)
    * [1.2 Scope](#12-scope)
  * [2. Overall Description](#2-overall-description)
    * [2.1 Product Features](#21-product-features)
    * [2.2 User Roles](#22-user-roles)
    * [2.4 Operating Environment](#24-operating-environment)
  * [3. Specific Requirements](#3-specific-requirements)
    * [3.1 Functional Requirements](#31-functional-requirements)
      * [3.1.1 Product Management](#311-product-management)
      * [3.1.2 Category Management](#312-category-management)
      * [3.1.3 User Authentication and Authorization](#313-user-authentication-and-authorization)
      * [3.1.4 Documentation](#314-documentation)
      * [3.1.5 Data Persistence and Schema Management](#315-data-persistence-and-schema-management)
    * [3.2 Non-Functional Requirements](#32-non-functional-requirements)
<!-- TOC -->

## 1. Introduction

### 1.1 Purpose
This document outlines the functional and non-functional requirements for the Product Inventory API application. The application provides a RESTful API for managing products and categories in an inventory system.

### 1.2 Scope
The Product Inventory API will allow users to perform Create, Read, Update, and Delete (CRUD) operations on products and categories. It will also include features for user authentication via JSON Web Tokens (JWT), and role-based authorization to protect its endpoints. The data will be stored in an embedded database that will be migrated by using Flyway. The API will be documented by following the OpenAPI specification.

## 2. Overall Description

### 2.1 Product Features
- Product Management (CRUD operations)
- Category Management (CRUD operations)
- User Authentication via JWT
- Role-Based Access Control
- API Documentation via OpenAPI/Swagger
- Data Persistence using an embedded database (H2)
- Database Schema Management with Flyway

### 2.2 User Roles
- **Full**: Users with full access to all API functionalities. Only those can Create, Update and Delete data.
- **Reader**: Users who can only view the data. They are also able to sort and paginate the results. Meant only to consume the API.

### 2.4 Operating Environment
- The application will run in a Java Virtual Machine (JVM) environment (Java 21 or newer) and be based on Spring Boot framework.
- It relies on a relational database (H2 embedded database for development and testing).

## 3. Specific Requirements

### 3.1 Functional Requirements

#### 3.1.1 Product Management

**FR-1: Create Products**  
The system should allow an authenticated user with appropriate permissions to create a new product.
- **Fields and Validation:** 
  - name (string, required, max length)
  - description (string, optional, max length)
  - price (decimal, required, > 0)
  - quantity (integer, required, >= 0).
  - category ID (long, required, exists).
- **Processing:**
  - Validate input data types and constraints.
  - Assign a unique ID and initial version for optimistic locking.
  - Store the product in the database.
- **Outputs:**
  - ID (long)
  - name (string)
  - description (string)
  - Price (integer)
  - quantity (integer)
  - version (long)

**FR-2: Retrieve a List of Products**  
The system should allow an authenticated user to retrieve a paginated list of all products.
- **Fields and Validation:** 
  - page number (integer, optional (default 1), > 0)
  - page size (integer, optional (default 10), > 0)
  - sort field (string, optional, correct field name)
  - sort direction (string, optional, 'asc' or 'desc')
- **Processing:**
  - Validate input data types and constraints.
  - Fetch products from the database (sorted)
  - Apply pagination.
- **Outputs:**
  - Current page number (integer)
  - Total number of pages (integer)
  - Page size (integer)
  - Array of results

**FR-3: Retrieve a Specific Product by ID**
The system should allow an authenticated user to retrieve details of a specific product using its unique ID.
- **Fields and Validation:**
  - Product ID (long, required)
- **Processing:**
  - Validate input data types and constraints.
  - Fetch the product from the database by its ID.
- **Outputs:**
  - ID (long)
  - name (string)
  - description (string)
  - Price (integer)
  - quantity (integer)
  - version (long)

**FR-4: Update an Existing Product**  
The system should allow an authenticated user with appropriate permissions to update an existing product.
- **Fields and Validation:**
  - ID (long, required)
  - name (string, required, max length)
  - description (string, optional, max length)
  - price (decimal, required, > 0)
  - quantity (integer, required, >= 0).
  - version (for optimistic locking) (long, required)
- **Processing:**
  - Validate input data types and constraints.
  - Retrieve the product by ID.
  - Check the provided version against the current version in the database. If they don't match, reject the update.
  - Update product details and increment the version.
- **Outputs:**
  - ID (long)
  - name (string)
  - description (string)
  - Price (integer)
  - quantity (integer)
  - version (long)

**FR-5: Delete a Product**  
The system should allow an authenticated user with appropriate permissions to delete a product by its ID.
- **Fields and Validation:**
  - ID (long, required)
  - version (for optimistic locking) (long, required)
- **Processing:**
  - Validate input data types and constraints.
  - Check the provided version against the current version in the database. If they don't match, reject the delete.
  - Remove the product from the database.

#### 3.1.2 Category Management

**FR-6: Create Categories**  
The system should allow an authenticated user with appropriate permissions to create a new category.
- **Fields and Validation:**
  - name (string, required, max length)
- **Processing:**
  - Validate input data types and constraints.
  - Assign a unique ID and initial version for optimistic locking.
  - Store the category in the database.
- **Outputs:**
  - ID (long)
  - name (string)
  - version (long)

**FR-7: Retrieve a List of Categories**  
The system should allow an authenticated user to retrieve a paginated list of all categories.
- **Fields and Validation:**
  - page number (integer, optional (default 1), > 0)
  - page size (integer, optional (default 10), > 0)
  - sort field (string, optional, correct field name)
  - sort direction (string, optional, 'asc' or 'desc')
- **Processing:**
  - Validate input data types and constraints.
  - Fetch categories from the database (sorted)
  - Apply pagination.
- **Outputs:**
  - Current page number (integer)
  - Total number of pages (integer)
  - Page size (integer)
  - Array of results

**FR-8: Retrieve a Specific Category by ID**
The system should allow an authenticated user to retrieve details of a specific category using its unique ID.
- **Fields and Validation:**
  - ID (long, required)
- **Processing:**
  - Validate input data types and constraints.
  - Fetch the category from the database by its ID.
- **Outputs:**
  - ID (long)
  - name (string)
  - version (long)

**FR-9: Update an Existing Category**  
The system should allow an authenticated user with appropriate permissions to update an existing category.
- **Fields and Validation:**
  - ID (long, required)
  - name (string, required, max length)
  - version (for optimistic locking) (long, required)
- **Processing:**
  - Validate input data types and constraints.
  - Retrieve the category by ID.
  - Check the provided version against the current version in the database. If they don't match, reject the update.
  - Update category details and increment the version.
- **Outputs:**
  - ID (long)
  - name (string)
  - version (long)

**FR-10: Delete a Category**  
The system should allow an authenticated user with appropriate permissions to delete a category by its ID.
- **Fields and Validation:**
  - ID (long, required)
  - version (for optimistic locking) (long, required)
- **Processing:**
  - Validate input data types and constraints.
  - Check the provided version against the current version in the database. If they don't match, reject the delete.
  - Check if the category contains any products. If it does then reject the delete.
  - Remove the category from the database.

#### 3.1.3 User Authentication and Authorization

**FR-11: User Management**
For simplicity there are no API endpoints for user registration. Username, password and role will be managed by the Administrator and provided to users on request. The Users will be created via CLI.
- **Fields and Validation:**
  - Username (string, required, unique)
  - Password (string, required)
  - Role (string, required, correct role)
- **Processing:**
  - Validate input data types and constraints.
  - Check for uniqueness of username.
  - Securely hash the password.
  - Store user details and assigned role in the database

**FR-12: User Login (Authentication)**
The system should allow registered users to log in.
- **Fields and Validation:**
  - Username (string, required)
  - Password (string, required, compared to stored hash).
- **Processing:**
  - Validate input data types and constraints.
  - If valid, generate a JWT.
- **Outputs:**
  - JWT token

**FR-13: Secure Endpoints (Authorization)**
The system should protect API endpoints based on user roles and authentication status.
- **Processing:**
  - All product endpoints must require a valid JWT.
  - Product creation, update, and deletion (`POST`, `PUT`, `DELETE`) will require `Full` role.
  - Product retrieval (one or many) will require `Reader` or `Full` roles.

#### 3.1.4 Documentation

**FR-14: OpenAPI Specification**  
The system should provide API documentation compliant with the OpenAPI specification. Ideally there should be Swagger UI endpoint that is describing the API.

**FR-15: Installation Guide**  
Instructions about how to compile, test, and run the application should be provided in the README.md file.

#### 3.1.5 Data Persistence and Schema Management

**FR-16: Embedded Database Usage**  
The system should use an embedded database (e.g., H2) for data persistence during development and testing.

**FR-17: Database Schema Management**
The system should use Flyway for managing database schema initialization and migrations. There should be few initial users and roles for development testing purposes (not in production environment).

### 3.2 Non-Functional Requirements

**NFR-1: Security**  
All sensitive operations must be protected. Passwords must be securely hashed in the database. Communication should ideally be over HTTPS and JWTs should have an expiration time.

**NFR-2: API Design**  
The API should follow RESTful design principles, using standard HTTP methods, status codes, and JSON for request/response bodies. The status codes should be at least:
- Invalid input data (400)
- Unauthorized access (401)
- Forbidden access (403)
- Resource not found (404)
- Conflict (in case of Optimistic lock) (409)
- Server errors (500)

**NFR-3: Testing**  
The system should have unit and functional tests covering API endpoints, including positive and negative test cases.

**NFR-4: Build and Dependency Management**  
Maven should be used as the build automation and dependency management tool.

**NFR-5: Environment and Framework**  
The application should be built using Java 21 and the Spring Boot framework.

**NFR-6: Maintainability**  
The code should be well-structured and follow the common clean code principles making it easy to maintain by other developers.

**NFR-7: Version Control**  
The codebase should be versioned by using Git. All commits should have clear messages and contain only changes related to their scope (in case of bigger changes there should be multiple, more granular commits).