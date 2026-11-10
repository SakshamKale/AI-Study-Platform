# Phase 2: Authentication & Authorization

## 1. Overview

Phase 2 implements authentication and authorization in the AI Study Platform using Spring Security and JWT (JSON Web Tokens).

The main objectives are:

- Secure user registration and login.
- Store passwords securely using BCrypt.
- Authenticate users using JWT.
- Implement stateless authentication using Spring Security.
- Identify the currently logged-in user.
- Restrict users to accessing only their own topics.

## 2. Technology Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JJWT)
- BCrypt Password Encoder
- Maven
- Postman

## 3. Project Architecture

The authentication system consists of the following components:

| Component | Responsibility |
|---|---|
| AuthController | Handles registration and login requests |
| AuthService | Implements authentication business logic |
| AppUser | Represents registered users |
| AppUserRepository | Database operations for users |
| RegisterRequest | Registration request DTO |
| LoginRequest | Login request DTO |
| AuthResponse | Login response containing JWT and user information |
| PasswordEncoder | Hashes and verifies passwords |
| JwtService | Generates and validates JWTs |
| SecurityConfig | Configures Spring Security |
| JwtAuthenticationFilter | Processes JWTs on incoming requests |
| CustomUserDetailsService | Loads user details from the database |
| SecurityContextHolder | Provides access to the current authentication |
| TopicService | Implements topic ownership authorization |
| TopicRepository | Database operations for topics |

## 4. User Registration Flow

Registration creates a new user account.

### Flow

Frontend / Postman
↓
AuthController
↓
RegisterRequest
↓
AuthService
↓
Check whether email already exists
↓
PasswordEncoder.encode()
↓
Create AppUser object
↓
AppUserRepository.save()
↓
PostgreSQL

### Implementation details

1. The client sends the user's name, email, and password.
2. AuthController receives the registration request.
3. AuthService checks whether the email already exists.
4. PasswordEncoder hashes the password using BCrypt.
5. An AppUser entity is created.
6. AppUserRepository saves the user in PostgreSQL.

**Important:** The original password is never stored in the database.

## 5. User Login Flow

Login verifies the user's credentials and generates a JWT.

### Flow

Frontend / Postman
↓
AuthController
↓
LoginRequest
↓
AuthService
↓
Find user using email
↓
PasswordEncoder.matches()
↓
JwtService.generateToken()
↓
AuthResponse
↓
Frontend receives JWT

### Implementation details

1. The client sends an email and password.
2. AuthService retrieves the user using findByEmail().
3. PasswordEncoder.matches() compares the submitted password with the stored BCrypt hash.
4. If credentials are correct, JwtService generates a JWT.
5. AuthResponse returns the token, email, and name.

## 6. JWT Implementation

JWT stands for JSON Web Token.

It is used to identify authenticated users without maintaining a traditional server-side login session.

### JWT structure

A JWT consists of three parts:

- Header
- Payload
- Signature

The implementation uses the following claims:

| Claim | Purpose |
|---|---|
| sub | User email |
| iat | Token creation time |
| exp | Token expiration time |

### Token generation

JwtService generates the token using:

- User email as the subject.
- Current timestamp as issued time.
- Configured expiration time.
- HMAC signature using a secret key.

### Token validation

When a protected request arrives, JwtService verifies the signature and expiration before extracting the user's email.

### JWT expiration

The configured expiration is:

`86400000 milliseconds = 24 hours`

## 7. Spring Security Configuration

SecurityConfig defines the application's security rules.

The implementation includes:

- CSRF disabled for the stateless API configuration.
- Stateless session management.
- Public authentication endpoints using `/api/auth/**`.
- Authentication required for other endpoints.
- JwtAuthenticationFilter inserted before UsernamePasswordAuthenticationFilter.

### Why stateless authentication?

The backend does not maintain a traditional HTTP session for authenticated users.

Each protected request must provide its JWT.

## 8. JWT Authentication Filter

JwtAuthenticationFilter extends OncePerRequestFilter.

It processes incoming requests and performs the following operations:

1. Reads the Authorization header.
2. Checks whether it starts with `Bearer `.
3. Extracts the JWT.
4. Calls JwtService to validate the token and extract the email.
5. Loads the user through UserDetailsService.
6. Creates a UsernamePasswordAuthenticationToken.
7. Adds request details to the Authentication object.
8. Stores the Authentication object in SecurityContextHolder.

### Request header

`Authorization: Bearer <JWT_TOKEN>`

## 9. CustomUserDetailsService

CustomUserDetailsService implements Spring Security's UserDetailsService interface.

Its responsibility is to:

1. Receive the email as the username.
2. Find the user through AppUserRepository.
3. Load the stored password hash.
4. Return a Spring Security UserDetails object.
5. Assign the USER role.

## 10. SecurityContext and SecurityContextHolder

SecurityContext stores the Authentication object for the current security context.

SecurityContextHolder provides access to that context.

The Authentication object contains information such as:

- Authenticated principal.
- Authentication status.
- Granted authorities.
- Credentials, which are not retained after successful authentication in this implementation.

### Accessing the logged-in user

The TopicService retrieves the authenticated user's email using:

`SecurityContextHolder.getContext().getAuthentication().getName()`

This email is then used to identify the current user.

## 11. Topic Ownership Authorization

Authentication identifies the user. Authorization determines which topics that user can access.

### Database relationship

Topic and AppUser have a Many-to-One relationship.

One user can own multiple topics.

The Topic entity contains:

`@ManyToOne(fetch = FetchType.LAZY)`

`@JoinColumn(name = "user_id", nullable = false)`

This creates a foreign-key relationship between the topic table and the app_users table.

### Repository methods

Two custom repository methods are used:

- `findByUserEmail(String email)`
- `findByIdAndUserEmail(Long id, String email)`

These methods filter topics based on their owner's email.

### Topic creation

When a user creates a topic:

1. TopicService obtains the authenticated user's email.
2. AppUserRepository retrieves the corresponding AppUser.
3. The user is assigned to the Topic entity.
4. TopicRepository saves the topic.

### Topic retrieval

`findByUserEmail()` ensures that the user receives only their own topics.

### Individual topic operations

`findByIdAndUserEmail()` checks both the topic ID and owner email.

This method is used for:

- Retrieving a topic by ID.
- Updating a topic.
- Deleting a topic.

If a topic does not belong to the authenticated user, TopicNotFoundException is thrown.

## 12. Authentication vs Authorization

| Authentication | Authorization |
|---|---|
| Verifies user identity | Checks access permissions |
| Uses JWT | Uses ownership checks |
| Identifies the logged-in user | Determines accessible resources |
| Implemented through Spring Security | Implemented in TopicService |

## 13. Error Handling

The JWT filter handles:

- Invalid JWT.
- Expired JWT.
- Incorrect JWT signature.
- Invalid token input.
- User not found.

When these exceptions occur, the SecurityContext is cleared and the filter chain continues without authentication.

Spring Security then rejects requests to protected endpoints.

### Expected HTTP responses

| Scenario | Expected response |
|---|---|
| Valid JWT | 200 OK for successful GET |
| Invalid JWT | 401 Unauthorized |
| Expired JWT | 401 Unauthorized |
| Missing JWT on protected endpoint | 401 Unauthorized |
| Accessing another user's topic | 404 Not Found |
| Successful login | Successful response containing JWT |

## 14. Environment Configuration

Sensitive configuration is supplied using environment variables.

Example `application.properties`:

```properties
spring.application.name=Aistudyplatform

spring.datasource.url=jdbc:postgresql://localhost:5432/ai_study_platform
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

Required environment variables:

- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET

Never commit actual passwords, JWT secrets, or access tokens to GitHub.

## 15. Testing with Postman

The implementation was tested using two different user accounts.

### Test cases

| Test | Expected result |
|---|---|
| Register a new user | Successful registration |
| Login with correct credentials | JWT returned |
| Login with incorrect credentials | Authentication rejected |
| Access topics with valid JWT | 200 OK |
| Access topics with invalid JWT | 401 Unauthorized |
| Access topics without JWT | 401 Unauthorized |
| Access topics with expired JWT | 401 Unauthorized |
| User B retrieves User A's topics | User A's topics are not returned |
| User B accesses User A's topic ID | 404 Not Found |
| User B updates User A's topic | 404 Not Found |
| User B deletes User A's topic | 404 Not Found |
| User A accesses their own topic | 200 OK |

## 16. Phase 2 Outcome

Phase 2 establishes the authentication and authorization foundation of the AI Study Platform.

The completed implementation provides:

- Secure registration and login.
- Password hashing using BCrypt.
- JWT-based authentication.
- Stateless Spring Security configuration.
- Request authentication through JWT filters.
- SecurityContext-based user identification.
- User-specific topic access.
- Ownership checks for topic creation, retrieval, updates, and deletion.
- Handling of invalid and expired JWTs.

**Result:** Users can authenticate with JWT and access only their own topic resources through the protected API.
