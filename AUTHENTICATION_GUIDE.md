# Spring Security Authentication & Authorization Implementation Guide

## Overview
This document describes the complete JWT (JSON Web Token) based authentication and authorization system implemented in the Supermarket Application.

---

## Architecture Components

### 1. **JWT Token Provider** (`JwtTokenProvider.java`)
Handles all JWT token operations:
- **Generate Token**: Creates JWT tokens with expiration time
- **Validate Token**: Verifies token signature and expiration
- **Extract Username**: Retrieves username from token claims

**Key Methods:**
```java
String generateToken(Authentication authentication)
String generateTokenFromUsername(String username)
String getUsernameFromToken(String token)
boolean validateToken(String token)
```

### 2. **JWT Authentication Filter** (`JwtAuthenticationFilter.java`)
- Intercepts every HTTP request
- Extracts JWT token from Authorization header (Bearer token format)
- Validates token and sets authentication context
- Runs before `UsernamePasswordAuthenticationFilter`

**Bearer Token Format:**
```
Authorization: Bearer <JWT_TOKEN>
```

### 3. **JWT Authentication Entry Point** (`JwtAuthenticationEntryPoint.java`)
- Handles authentication errors
- Returns JSON error response instead of default HTML
- Status: 401 Unauthorized

### 4. **Security Configuration** (`SecurityConfig.java`)
Core security setup:
- **Session Management**: Stateless (no server-side sessions)
- **CSRF Protection**: Disabled for REST APIs
- **HTTP Security**:
  - Public endpoints: `/api/auth/**` (login, register)
  - Protected endpoints: All others require authentication
  - ADMIN-only endpoints: Controlled via `@PreAuthorize`
- **Filter Chain**: JWT filter added before authentication

### 5. **User Details Service** (`CustomUserDetailsService.java`)
- Loads user from database by username
- Throws `UsernameNotFoundException` if not found
- Builds Spring Security UserDetails object with roles

### 6. **Authentication Controller** (`AuthController.java`)
REST endpoints for authentication:

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}

Response (201 Created):
{
  "username": "john",
  "roles": ["USER"],
  "message": "User registered successfully"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john",
  "roles": ["USER"],
  "message": "User logged in successfully"
}
```

---

## Security Features

### 1. **Authentication**
Users authenticate using username/password:
1. Call `/api/auth/register` to create account
2. Call `/api/auth/login` with credentials
3. Receive JWT token in response
4. Include token in Authorization header for protected endpoints

### 2. **Authorization**
Role-based access control using `@PreAuthorize` annotation:
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDto> create(...) { }

@PreAuthorize("hasRole('USER')")
public ResponseEntity<OrderDto> createOrder(...) { }

// No annotation = requires authentication
public ResponseEntity<List<ProductDto>> getAll() { }
```

### 3. **Token Validation**
Each request goes through:
1. Extract token from Authorization header
2. Validate token signature (not tampered)
3. Verify expiration time
4. Extract username and load user details
5. Set authentication in SecurityContext
6. Process request if authenticated

### 4. **Password Security**
- Passwords encoded using BCrypt
- Configurable strength (default: strength 10)
- Raw passwords never stored in database

---

## Configuration Properties

In `application.yml`:
```yaml
jwt:
  secret: your-secret-key-change-this-in-production-make-it-very-long-and-secure
  expiration: 86400000  # 24 hours in milliseconds
```

**Important**: Change the secret in production to a long, random, secure string!

---

## Token Flow Diagram

```
1. User Registration/Login
   ↓
2. AuthController validates credentials
   ↓
3. AuthenticationManager authenticates user
   ↓
4. JwtTokenProvider generates JWT token
   ↓
5. Token returned to client
   ↓
6. Client includes token in Authorization header
   ↓
7. JwtAuthenticationFilter validates token
   ↓
8. SecurityContext authenticated
   ↓
9. Request processed (authorized endpoints only)
```

---

## Access Control Examples

### Public Endpoints (No Token Required)
```
GET /api/auth/register
POST /api/auth/login
```

### Authenticated Endpoints (Token Required)
```
GET /api/products       (Requires authentication)
GET /api/orders         (Requires authentication)
GET /api/customers      (Requires authentication)
```

### Admin-Only Endpoints (Token + ADMIN Role Required)
```
POST /api/products      (Create product - ADMIN only)
PUT /api/products/{id}  (Update product - ADMIN only)
DELETE /api/products/{id} (Delete product - ADMIN only)
```

### User Endpoints
```
GET /api/orders/{id}    (User's own orders)
POST /api/orders        (Create order)
```

---

## Error Responses

### 401 Unauthorized (Missing/Invalid Token)
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "timestamp": "2026-03-11T20:30:00",
  "path": "/api/products"
}
```

### 403 Forbidden (Insufficient Permissions)
```json
{
  "status": 403,
  "error": "Access Denied",
  "message": "Access is denied",
  "timestamp": "2026-03-11T20:30:00",
  "path": "/api/products"
}
```

### 400 Bad Request (Invalid Credentials)
```json
{
  "token": null,
  "username": null,
  "roles": null,
  "message": "Invalid username or password"
}
```

---

## Testing the API

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

### 3. Use Token (Replace TOKEN with actual token)
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer TOKEN"
```

### 4. Admin Endpoint (Admin token required)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product","sku":"SKU","price":10.0,"stock":100}'
```

---

## Dependencies Added

```xml
<!-- JWT for token-based authentication -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

---

## Files Modified/Created

### New Files
- `JwtTokenProvider.java` - JWT operations
- `JwtAuthenticationFilter.java` - Token validation filter
- `JwtAuthenticationEntryPoint.java` - Exception handling
- `LoginRequest.java` - DTO for login
- `AuthResponse.java` - DTO for auth response

### Modified Files
- `SecurityConfig.java` - JWT configuration
- `AuthController.java` - JWT login endpoint
- `application.yml` - JWT properties
- `pom.xml` - JWT dependencies

---

## Production Checklist

- [ ] Change JWT secret to a strong, random value
- [ ] Update JWT expiration based on security needs
- [ ] Enable HTTPS for all endpoints
- [ ] Add rate limiting to login endpoint
- [ ] Implement refresh token mechanism
- [ ] Add audit logging for authentication events
- [ ] Configure CORS if frontend is separate
- [ ] Use environment variables for sensitive config
- [ ] Implement token blacklist for logout
- [ ] Add multi-factor authentication (MFA)

---

## Common Issues & Solutions

### Issue: Token not recognized
**Solution**: Ensure "Bearer " prefix is included in Authorization header

### Issue: Token expired error
**Solution**: Login again to get new token, or implement refresh token mechanism

### Issue: User not found after login
**Solution**: Verify user exists in database via `/api/auth/login`

### Issue: CORS errors in frontend
**Solution**: Add CORS configuration in SecurityConfig if frontend is separate domain

---

## Next Steps

1. **Test all endpoints** using provided curl commands
2. **Implement refresh tokens** for better UX
3. **Add rate limiting** to prevent brute force
4. **Setup email verification** for new registrations
5. **Implement logout/token blacklist** mechanism
6. **Add OAuth2 integration** for social login
7. **Setup audit logging** for security events

---

## References
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8949)


