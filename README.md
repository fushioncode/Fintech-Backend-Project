# Fintech-Backend-Project - README

## Project Overview
This project is a comprehensive fintech application designed to manage user accounts, authenticate users, process loans, handle transactions, and more. It follows a modular architecture, ensuring scalability, maintainability, and robust performance.

## Services and Endpoints

### 1. User Service
Handles user-related operations, including registration, profile management, and retrieval of user details.

- **POST /users/register**
  - Registers a new user.
  - Request body: User details (e.g., name, email, password, etc.).
  
- **GET /users/{userId}**
  - Retrieves details of a specific user by ID.

- **PUT /users/{userId}**
  - Updates user profile information.

- **DELETE /users/{userId}**
  - Deletes a user account.

---

### 2. Auth Service
Manages authentication and authorization for the application.

- **POST /auth/login**
  - Authenticates a user and generates a JWT token.
  - Request body: Username and password.

- **POST /auth/logout**
  - Logs out a user and invalidates the JWT token.

- **POST /auth/refresh-token**
  - Generates a new token using the refresh token.

---

### 3. Loan Service
Manages loan-related functionalities.

- **POST /loans/apply**
  - Allows users to apply for a loan.
  - Request body: Loan details (amount, tenure, purpose, etc.).

- **GET /loans/{loanId}**
  - Fetches details of a specific loan.

- **GET /loans/user/{userId}**
  - Retrieves all loans associated with a user.

- **PUT /loans/{loanId}/status**
  - Updates the status of a loan (approved, rejected, etc.).

---

### 4. Transaction Service
Handles financial transactions, including deposits, withdrawals, and transfers.

- **POST /transactions/deposit**
  - Deposits money into a user account.
  - Request body: Transaction details (amount, account number, etc.).

- **POST /transactions/withdraw**
  - Withdraws money from a user account.

- **POST /transactions/transfer**
  - Transfers money between accounts.
  - Request body: Sender account, receiver account, and amount.

- **GET /transactions/{transactionId}**
  - Retrieves details of a specific transaction.

- **GET /transactions/user/{userId}**
  - Fetches all transactions associated with a user.

---

### 5. Admin Service
Admin-specific functionalities for managing and monitoring the application.

- **GET /admin/users**
  - Retrieves all users.

- **GET /admin/loans**
  - Retrieves all loans.

- **GET /admin/transactions**
  - Retrieves all transactions.

- **PUT /admin/users/{userId}/status**
  - Updates the status of a user account (active, inactive, etc.).

---

## Running the Project
1. Clone the repository.
2. Set up the database and update the `application.properties` file with your database credentials.
3. Build and run the application using:
   ```bash
   mvn spring-boot:run
   ```
4. Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

---

## Testing
Ensure comprehensive unit and integration tests are available for all service layers. Run the tests using:
```bash
mvn test
```

---

## Future Enhancements
- Implementing advanced analytics for user transactions.
- Adding support for multi-currency operations.
- Integrating with third-party APIs for credit score checks.
- Enhancing security with multi-factor authentication.

---

## Contributors
- [Your Name] - Lead Developer
- [Contributor Name] - Backend Specialist
- [Contributor Name] - Frontend Developer


