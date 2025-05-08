# Exercise Description

Below is the original, unmodified challenge description. Usually this would be a part of issue tracking software such as
Atlassian Jira.

## Challenge

Build a REST API for a "Product Inventory" application with the following features:

### Framework Choice

Use either Spring Boot or Quarkus, and Maven as automation tool

### Core Endpoints

- `POST` `/products`: Create a new product.  
  Request body: Product details (name, description, price, quantity).
  Validation: Ensure required fields are present and data types are correct.
- `GET` `/products`: Retrieve a list of all products.
  Pagination: Implement pagination (e.g., 10 products per page).
- `GET` `/products/{id}`: Retrieve a specific product by ID.
  Error Handling: Return a 404 Not Found error if the product doesn't exist.
- `PUT` `/products/{id}`: Update an existing product.
  Optimistic Locking: Implement optimistic locking to prevent concurrent update issues.
- `DELETE` `/products/{id}`: Delete a product.
  Junit tests: Positive (2xx codes) and negative (errors) tests covering the API endpoints
  API documentation: According to OpenAPI specification.

### Advanced Features (Optional)

- Filtering: Allow filtering products (e.g., by name, price range).
- Sorting: Enable sorting products (e.g., by name, price).
- Relationships: Introduce a Category entity with a one-to-many relationship to Product.
- Protect the endpoints with JWT & roles
- Use Flyway for DB initialization/population scripts
- Any other feature you will find interesting

### Data Persistence

Use an embedded database (like H2).

### Testing

Write unit tests for your API endpoints.

### Documentation

Provide clear API documentation (using Swagger or a similar tool).

### Requirements

- Use Java (>= v17) for the backend.
- Choose either Spring Boot or Quarkus.
- Follow RESTful API design principles.
- Write clean, well-structured, and documented code.
- Provide a README.md with instructions about how to compile, test, and run the application.