# Spring Security Implementation Summary

## Overview
Complete JWT-based authentication and authorization system implemented for the Supermarket Management Application.

---

## 📦 What Was Implemented

### 1. **JWT Token-Based Authentication**
- User registration endpoint
- User login endpoint with JWT generation
- Token validation on every request
- Stateless session management

### 2. **Authorization & Access Control**
- Role-based access control (RBAC)
- @PreAuthorize annotations for endpoint security
- Admin-only operations
- User-accessible operations

### 3. **Security Components**

#### JwtTokenProvider.java
```
Responsibilities:
- Generate JWT tokens from Authentication objects
- Generate JWT tokens from username strings
- Validate JWT token signatures and expiration
- Extract username from valid tokens
- Handle token parsing errors gracefully
```

#### JwtAuthenticationFilter.java
```
Responsibilities:
- Intercept HTTP requests
- Extract Bearer token from Authorization header
- Validate token using JwtTokenProvider
- Load user details using CustomUserDetailsService
- Set SecurityContext with authenticated user
- Chain to next filter if token is invalid/missing
```

#### JwtAuthenticationEntryPoint.java
```
Responsibilities:
- Handle authentication failures (401 Unauthorized)
- Return JSON error response instead of HTML
- Include timestamp, status, error message, path
```

#### SecurityConfig.java
```
Responsibilities:
- Configure stateless session management
- Define public endpoints (/api/auth/**)
- Define protected endpoints (all others)
- Register JWT filter before UsernamePasswordAuthenticationFilter
- Setup authentication manager
- Configure password encoder (BCrypt)
- Setup exception handling
```

#### AuthController.java
```
Endpoints:
- POST /api/auth/register - Register new user
- POST /api/auth/login - Login and get JWT token

Features:
- Input validation (username/password required)
- Duplicate username detection
- Password encoding before storage
- JWT token generation on login success
- Comprehensive error messages
```

---

## 🔧 Dependencies Added

```xml
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

**Library**: JJWT (JSON Web Token) 0.12.3
**Features**: Modern JJWT API with signature verification

---

## 📝 Configuration

### application.yml
```yaml
jwt:
  secret: your-secret-key-change-this-in-production-make-it-very-long-and-secure
  expiration: 86400000  # 24 hours in milliseconds
```

### Properties Explained
- `jwt.secret`: Used to sign JWT tokens (HMAC-SHA256)
- `jwt.expiration`: Token validity period in milliseconds

---

## 🔐 Authentication Flow

```
┌─────────────────────────────────────────────────────────┐
│ 1. USER REGISTRATION                                    │
└─────────────────────────────────────────────────────────┘
   POST /api/auth/register
   {username, password}
   ↓
   Check if user exists
   ↓
   Hash password with BCrypt
   ↓
   Save user to database
   ↓
   Return 201 Created

┌─────────────────────────────────────────────────────────┐
│ 2. USER LOGIN                                           │
└─────────────────────────────────────────────────────────┘
   POST /api/auth/login
   {username, password}
   ↓
   AuthenticationManager.authenticate(credentials)
   ↓
   CustomUserDetailsService.loadUserByUsername()
   ↓
   Verify password match with BCrypt
   ↓
   Create Authentication object
   ↓
   JwtTokenProvider.generateToken()
   ↓
   Return 200 OK with JWT token

┌─────────────────────────────────────────────────────────┐
│ 3. PROTECTED REQUEST                                    │
└─────────────────────────────────────────────────────────┘
   GET /api/products
   Authorization: Bearer <JWT_TOKEN>
   ↓
   JwtAuthenticationFilter.doFilterInternal()
   ↓
   Extract token from Authorization header
   ↓
   JwtTokenProvider.validateToken()
   ↓
   Verify signature: HMAC-SHA256
   ↓
   Check expiration timestamp
   ↓
   JwtTokenProvider.getUsernameFromToken()
   ↓
   CustomUserDetailsService.loadUserByUsername()
   ↓
   Create UsernamePasswordAuthenticationToken
   ↓
   Set in SecurityContext
   ↓
   Continue to endpoint
   ↓
   Return 200 OK with data
```

---

## 🛡️ Authorization Flow

```
Endpoint Request with JWT Token
   ↓
JwtAuthenticationFilter validates token
   ↓
User loaded with roles
   ↓
@PreAuthorize("hasRole('ADMIN')") evaluated
   ↓
✓ User has role → Allow access
✗ User lacks role → Return 403 Forbidden
```

---

## 📊 Role-Based Access Control

### Built-in Roles
- **USER**: Default role for registered users
- **ADMIN**: Admin role (must be assigned manually)

### Endpoint Access Matrix

| Endpoint | Method | Auth | Role | Endpoint Code |
|----------|--------|------|------|---|
| /api/auth/register | POST | ❌ | - | Public |
| /api/auth/login | POST | ❌ | - | Public |
| /api/products | GET | ✅ | USER,ADMIN | Protected |
| /api/products | POST | ✅ | ADMIN | @PreAuthorize("hasRole('ADMIN')") |
| /api/products/{id} | GET | ✅ | USER,ADMIN | Protected |
| /api/products/{id} | PUT | ✅ | ADMIN | @PreAuthorize("hasRole('ADMIN')") |
| /api/products/{id} | DELETE | ✅ | ADMIN | @PreAuthorize("hasRole('ADMIN')") |
| /api/orders | GET | ✅ | USER,ADMIN | Protected |
| /api/orders | POST | ✅ | USER,ADMIN | Protected |

---

## 🔌 Integration Points

### 1. With Spring Security
- Extends Spring Security's authentication mechanism
- Uses UserDetailsService for user loading
- Implements AuthenticationEntryPoint for error handling
- Filters chain integrated with SecurityFilterChain

### 2. With Database
- CustomUserDetailsService queries User entity
- UserRepository fetches users by username
- User entity stores username, password, roles

### 3. With Controllers
- @PreAuthorize on methods for access control
- SecurityContext available in controller methods
- Principal accessible via @AuthenticationPrincipal

---

## 📋 DTOs Used

### LoginRequest
```java
{
  "username": "john",
  "password": "password123"
}
```

### AuthResponse
```java
{
  "token": "eyJhbGc...",
  "username": "john",
  "roles": ["USER"],
  "message": "User logged in successfully"
}
```

---

## 🧪 Testing

### Register User Test
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

### Login Test
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'

# Response: { "token": "...", "username": "testuser", "roles": ["USER"] }
```

### Protected Endpoint Test
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer eyJhbGc..."
```

### Unauthorized Test (No Token)
```bash
curl -X GET http://localhost:8080/api/products
# Response: 401 Unauthorized
```

### Forbidden Test (Insufficient Permissions)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product","sku":"SKU","price":10,"stock":100}'
# Response: 403 Forbidden (USER role cannot create products)
```

---

## ⚙️ How to Add Admin User

### Via Database Query
```sql
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$...');
INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
```

### Via DataInitializer (Optional)
Add to `DataInitializer.java`:
```java
User admin = User.builder()
    .username("admin")
    .password(passwordEncoder.encode("admin123"))
    .roles(Sets.newHashSet("ADMIN", "USER"))
    .build();
userRepository.save(admin);
```

---

## 🔍 Error Scenarios

### Duplicate Username
```json
{
  "message": "Username already exists"
}
```

### Invalid Credentials
```json
{
  "message": "Invalid username or password"
}
```

### Missing Token
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

### Expired Token
- Token validated in JwtTokenProvider.validateToken()
- ExpiredJwtException caught
- Returns false, request rejected

### Invalid Signature
- Signature verified using secret key
- SecurityException caught
- Returns false, request rejected

---

## 🚀 Production Considerations

### 1. Secret Key Management
- Use environment variables or Spring Cloud Config
- Minimum 32 characters, random and secure
- Rotate periodically
- Different for each environment

### 2. Token Expiration
- 24 hours for short-lived tokens
- Consider 15 minutes for sensitive operations
- Implement refresh token mechanism

### 3. HTTPS
- All endpoints must use HTTPS in production
- Prevent token interception

### 4. Rate Limiting
- Limit login attempts per IP
- Prevent brute force attacks

### 5. Logging
- Log all authentication attempts
- Monitor failed login patterns
- Alert on suspicious activities

### 6. Token Storage (Client-Side)
- Store in secure, http-only cookies
- Don't store in localStorage (XSS vulnerable)
- Include CSRF protection

### 7. Additional Security
- Implement refresh tokens for better UX
- Add token blacklist for logout
- Implement MFA
- Use CORS properly if SPA frontend

---

## 📚 Key Files Reference

| File | Lines | Purpose |
|------|-------|---------|
| JwtTokenProvider.java | 104 | Token operations |
| JwtAuthenticationFilter.java | 64 | Token validation filter |
| JwtAuthenticationEntryPoint.java | 34 | Error handling |
| SecurityConfig.java | 64 | Security configuration |
| AuthController.java | 97 | Auth endpoints |
| LoginRequest.java | 13 | Login DTO |
| AuthResponse.java | 17 | Auth response DTO |

---

## 🎯 What You Can Do Next

1. ✅ Register users
2. ✅ Login and get JWT tokens
3. ✅ Access protected endpoints with tokens
4. ✅ Use role-based access control
5. ⏳ Implement refresh tokens
6. ⏳ Add token blacklist for logout
7. ⏳ Integrate with frontend (React, Angular, Vue)
8. ⏳ Add OAuth2/OpenID Connect
9. ⏳ Implement MFA
10. ⏳ Setup audit logging

---

## 📞 Support

For issues or questions:
1. Check application logs for error details
2. Verify JWT secret is configured
3. Ensure token format includes "Bearer " prefix
4. Check token expiration time
5. Verify user exists and has required roles

---

**Implementation Date**: March 11, 2026
**Status**: ✅ Complete and Ready to Use
**Version**: 1.0


