# Exercise: Product Inventory

This project is meant to showcase my skills in Java development and the way I approach given tasks.  
More detailed documentation can be found at [3-functional-design.md](src/main/docs/3-functional-design.md).

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Apache Maven 3.6.3 or higher

## Compiling the Application

To compile the project, navigate to the root directory of the project in your terminal and run the following Maven command:

```bash
./mvnw clean compile
```

## Running Tests

To run the tests for the application:

```bash
./mvnw test
```

## Running the Application

To start the application:

```bash
./mvnw spring-boot:run
```

The application will start on port `8080` by default.

## API Access

Once the application is running, you can access:
- API: http://localhost:8080/
- JDBC URL: jdbc:h2:mem:productdb
- Username: sa
- Password: password

## API Documentation (Swagger UI)

Once the application is running, you can access the interactive API documentation via Swagger UI at:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

You can use the Swagger UI to view all available endpoints, their request/response formats, and test them directly from your browser. For secured endpoints, you can use the "Authorize" button to authenticate using a JWT token obtained from the `/auth/login` endpoint.

## API Endpoints

### Authentication
- `POST /auth/login` - Authenticate user and get JWT token

### Categories
- `GET /categories` - Get all categories (paginated)
- `GET /categories/{id}` - Get category by ID
- `POST /categories` - Create a new category (requires FULL role)
- `PUT /categories/{id}` - Update a category (requires FULL role)
- `DELETE /categories/{id}` - Delete a category (requires FULL role)

### Products
- `GET /products` - Get all products (paginated)
- `GET /products/{id}` - Get product by ID
- `POST /products` - Create a new product (requires FULL role)
- `PUT /products/{id}` - Update a product (requires FULL role)
- `DELETE /products/{id}` - Delete a product (requires FULL role)

## Authentication

The API uses JWT authentication. To access protected endpoints:

1. Login to get a token:
   ```
   POST /auth/login
   {
     "username": "admin",
     "password": "password"
   }
   ```
   
   For testing purposes there are built-in users:
   - username: `admin`  
     password: `password`   
     role: `FULL`
   - username: `user`  
     password: `password`   
     role: `READER`

2. Use the returned token in the Authorization header:
   ```
   Authorization: Bearer your_token_here
   ```

## Building for Production

To build a production-ready JAR file:

```bash
./mvnw clean package
```

The JAR file will be created in the `target` directory and can be run with:

```bash
java -jar target/product-inventory-0.0.1-SNAPSHOT.jar
```

## Additional features and improvements to consider

Below is a list of features that I did not manage to implement in given time.

- Extend User API to allow users to register, change password
- Allow Admin to manage the users and data via CLI (safely, not exposed)
- Add [Spring Boot Caching](https://docs.spring.io/spring-boot/reference/io/caching.html) as per RESTful principles
- Use more HTTP error codes in cases that don't have them
- Add some functional tests without mocking and integration tests
- use [MapStruct](https://mapstruct.org/)
- consider using [TestContainers](https://testcontainers.com/) (might be unnecessary for this simple project)