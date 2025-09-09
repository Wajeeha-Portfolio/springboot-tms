# Translation Management Service

A Spring Boot application for managing translations. Supports bulk data operations, efficient JSON export, JWT authentication, and OpenAPI/Swagger documentation.

## Features
- **Translation CRUD**: Add, update, view, search, and export translations.
- **CSV Import/Export**: Generate sample CSVs and import translations in bulk.
- **JWT Authentication**: Secure endpoints for user registration and login.
- **Swagger/OpenAPI**: Interactive API documentation and testing.

## Technologies
- Java 17+, Spring Boot
- Spring Data JPA, Hibernate
- In-memory H2 (with `create-drop` setting for simplicity)
- Maven
- Docker
- Swagger/OpenAPI

## Getting Started

### Prerequisites
- Java 17+
- Maven
- Docker (optional, for containerized deployment)
- In-memory H2 (no setup required)

### Installation & Running Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Wajeeha-Portfolio/springboot-tms.git
   cd springboot-tms
   ```
2. **Configure the database:**
   - By default, the app uses H2 in-memory database with `create-drop` setting. All data will be cleared after the session ends.
   - You can edit `src/main/resources/application.properties` for custom DB settings.
3. **Build the project:**
   ```bash
   ./mvnw clean package
   ```
4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
5. **Or run with Docker:**
   ```bash
   docker build -t springboot-tms .
   docker run -p 8080:8080 springboot-tms
   ```

### How to Test Authentication & API Access
1. **Create a user:**
   - Send a POST request to `/auth/register` with your desired username and password.
2. **Login:**
   - Send a POST request to `/auth/login` with the same credentials to receive a JWT token.
3. **Access translation APIs:**
   - Use the JWT token in the `Authorization` header as `Bearer <token>` for all translation-related endpoints.
   - If you do not provide a valid token, you will receive a 401 Unauthorized error.

## Quick Start Testing

### Using the Sample CSV File
- The file `sample-translations.csv` is provided in the project root.
- To import translations:
  1. Start the application.
  2. Use Postman or Swagger UI to send a `POST /csv` request with `sample-translations.csv` as the file (form-data, key: `file`).
  3. You can then use translation endpoints to view/search/export the imported data.

### Using the Postman Collection
- The file `tms.postman_collection.json` is provided in the project root.
- To use:
  1. Open Postman.
  2. Import the collection (`File > Import > tms.postman_collection.json`).
  3. Use the pre-configured requests to test authentication, translation CRUD, CSV import/export, scalability, and JSON export endpoints.
  4. For translation endpoints, first register and login to get a JWT token, then set the `Authorization` header as `Bearer <token>`.

## API Endpoints

### Authentication
- `POST /auth/register` — Register a new user
- `POST /auth/login` — Login and receive JWT token

### Translation Management
- `POST /translation` — Add translation
- `PUT /translation/{id}` — Update translation
- `GET /translation/{id}` — View translation by ID
- `POST /translation/search` — Search translations
- `GET /translation/export` — Export all translations

### CSV Operations
- `GET /sample-csv?count=1000` — Generate sample CSV file
- `POST /csv` — Import translations from CSV file

## Testing
- **Run all tests:**
  ```bash
  ./mvnw test
  ```
- Unit and integration tests are provided for all major services

## API Documentation
- Swagger UI available at: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `/v3/api-docs`
