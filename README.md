# Fintech-Backend-Project - README

## Project Overview
This project is a comprehensive fintech application designed to manage user accounts, authenticate users, process loans, handle transactions, and more. It follows a modular architecture, ensuring scalability, maintainability, and robust performance.

## Services and Endpoints

### 1. User Service
Handles user-related operations, including registration, profile management, and retrieval of user details.
  
- **GET /api/users/{userId}**
  - Retrieves details of a specific user by ID.
  - 
- **POST /api/users/activate/{id}**
  - Active user account by ID.
    
- **PUT /api/users/update/{id}**
  - Active user account by ID.
  - Request body: User details (fullName, email, password, phoneNumber).

- **DELETE /users/{userId}**
  - Deletes a user account.

- **GET /api/user/all-users**
  - Get all users
  
---

### 2. Auth Service
Manages authentication and authorization for the application.

- **POST /api/auth/login**
  - Authenticates a user and generates a JWT token.
  - Request body: Username and password.

- **POST /api/auth/users/register**
  - Registers a new user.
  - Request body: User details (fullName, email, password, phoneNumber).

- **POST /api/auth/admin/register**
  - Registers a new user.
  - Request body: User details (fullName, email, password, phoneNumber).
---

### 3. Loan Service
Manages loan-related functionalities.

- **POST /api/loans/apply**
  - Allows users to apply for a loan.
    - Request body: Loan details (userId, amount, tenure).

- **GET /api/loans/user/{userId}**
  - Retrieves all loans associated with a user.

- **PUT /api/loans/{loanId}/status/update**
  - Updates the status of a loan (approved, rejected, etc.).
    - @RequestParam String status(APPROVE, REJECTED)

---

### 4. Transaction Service
Handles financial transactions, including deposits, withdrawals, and transfers.

- **POST /api/transactions/record**
  - Loan transaction management (DISBURSEMENT, REPAYMNET).
  - Request body: Transaction details (userId, amount, type(DISBURSEMENT, REPAYMENT)).

- **GET /api/transactions/loan/{loanId}**
  - Get Loan transaction history.

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
- Admin-specific functionalities for managing and monitoring the application.

---

## Contributors
- [Your Name] - Lead Developer
- [Contributor Name] - Backend Specialist
- [Contributor Name] - Frontend Developer


