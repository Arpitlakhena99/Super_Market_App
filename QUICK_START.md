# Spring Security JWT Authentication - Quick Start Guide

## ✅ Implementation Complete

Your Supermarket Application now has full **JWT Token-Based Authentication & Authorization** implemented!

---

## 🚀 What's Been Added

### 1. **JWT Token-Based Authentication**
- Users register and login with credentials
- JWT tokens issued on successful login
- Tokens validated on every request
- 24-hour token expiration (configurable)

### 2. **Authorization & Access Control**
- Role-based access control (RBAC)
- Public endpoints: `/api/auth/**` (login, register)
- Protected endpoints: Requires valid JWT token
- Admin-only endpoints: `/api/products` (POST, PUT, DELETE)

### 3. **Security Components**
- **JwtTokenProvider**: Token generation and validation
- **JwtAuthenticationFilter**: Validates tokens on each request
- **JwtAuthenticationEntryPoint**: Handles authentication errors
- **SecurityConfig**: Configures stateless JWT security

### 4. **New DTOs**
- **LoginRequest**: For login/register requests
- **AuthResponse**: Returns token on authentication success

### 5. **Enhanced AuthController**
- `/api/auth/register` - Register new users
- `/api/auth/login` - Authenticate and get JWT token

---

## 📋 Files Modified/Created

| File | Type | Purpose |
|------|------|---------|
| `JwtTokenProvider.java` | ✨ NEW | JWT token operations |
| `JwtAuthenticationFilter.java` | ✨ NEW | Request token validation |
| `JwtAuthenticationEntryPoint.java` | ✨ NEW | Error handling |
| `LoginRequest.java` | ✨ NEW | Login DTO |
| `AuthResponse.java` | ✨ NEW | Authentication response DTO |
| `SecurityConfig.java` | 🔄 MODIFIED | JWT configuration |
| `AuthController.java` | 🔄 MODIFIED | JWT login endpoint |
| `application.yml` | 🔄 MODIFIED | JWT properties |
| `pom.xml` | 🔄 MODIFIED | JWT dependencies |

---

## 🔑 Configuration (application.yml)

```yaml
jwt:
  secret: your-secret-key-change-this-in-production-make-it-very-long-and-secure
  expiration: 86400000  # 24 hours in milliseconds
```

**⚠️ IMPORTANT**: Change the secret to a strong, random string in production!

---

## 🧪 Testing the API

### 1. **Register a User**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Response (201 Created):**
```json
{
  "username": "john",
  "roles": ["USER"],
  "message": "User registered successfully"
}
```

### 2. **Login & Get Token**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNzE1NDI3MjAwLCJleHAiOjE3MTU1MTM2MDB9.xyz...",
  "username": "john",
  "roles": ["USER"],
  "message": "User logged in successfully"
}
```

### 3. **Access Protected Endpoint with Token**
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNzE1NDI3MjAwLCJleHAiOjE3MTU1MTM2MDB9.xyz..."
```

### 4. **Admin-Only Endpoint (Create Product)**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Milk",
    "sku": "MILK001",
    "price": 50.00,
    "stock": 100
  }'
```

> **Note**: This requires an admin user. Create admin user via database or custom endpoint.

---

## 📊 Access Control Matrix

| Endpoint | Method | Authentication | Authorization | Role Required |
|----------|--------|---|---|---|
| `/api/auth/register` | POST | ❌ No | ❌ No | - |
| `/api/auth/login` | POST | ❌ No | ❌ No | - |
| `/api/products` | GET | ✅ Yes | ✅ Yes | USER or ADMIN |
| `/api/products` | POST | ✅ Yes | ✅ Yes | ADMIN |
| `/api/products/{id}` | PUT | ✅ Yes | ✅ Yes | ADMIN |
| `/api/products/{id}` | DELETE | ✅ Yes | ✅ Yes | ADMIN |
| `/api/orders` | GET | ✅ Yes | ✅ Yes | USER or ADMIN |
| `/api/orders` | POST | ✅ Yes | ✅ Yes | USER or ADMIN |

---

## 🔐 Security Features

✅ **Password Hashing**: BCrypt with configurable strength
✅ **JWT Signing**: HMAC-SHA256 with secret key
✅ **Token Validation**: Signature and expiration verification
✅ **Stateless Authentication**: No server-side sessions
✅ **CSRF Protection**: Disabled for REST APIs (stateless)
✅ **Role-Based Access**: @PreAuthorize annotations
✅ **Error Handling**: JSON error responses with HTTP status
✅ **Bearer Token**: Standard Authorization header format

---

## 🧬 How It Works

```
1. User Registration
   ↓
   POST /api/auth/register { username, password }
   ↓
   Password encoded with BCrypt
   ↓
   User saved to database
   ↓
   201 Created response

2. User Login
   ↓
   POST /api/auth/login { username, password }
   ↓
   Credentials validated by AuthenticationManager
   ↓
   JwtTokenProvider generates JWT token
   ↓
   Token returned in response
   ↓
   200 OK with JWT token

3. Protected Request
   ↓
   GET /api/products with Authorization: Bearer <TOKEN>
   ↓
   JwtAuthenticationFilter intercepts request
   ↓
   Token extracted from Authorization header
   ↓
   Token signature and expiration verified
   ↓
   Username extracted from token
   ↓
   User details loaded from database
   ↓
   SecurityContext authenticated
   ↓
   Request processed
   ↓
   200 OK with response
```

---

## ⚙️ Building & Running

### Build
```bash
mvn clean install -DskipTests
```

### Run
```bash
mvn spring-boot:run
```

Application starts at `http://localhost:8080`

---

## 🐛 Error Handling

### 401 Unauthorized
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "timestamp": "2026-03-11T20:30:00",
  "path": "/api/products"
}
```

### 403 Forbidden (Invalid Credentials)
```json
{
  "token": null,
  "username": null,
  "roles": null,
  "message": "Invalid username or password"
}
```

### 400 Bad Request (Duplicate Username)
```json
{
  "username": null,
  "roles": null,
  "message": "Username already exists"
}
```

---

## 📚 Key Classes

### JwtTokenProvider
- Generates JWT tokens from authentication
- Validates tokens and extracts claims
- Handles token expiration

### JwtAuthenticationFilter
- Intercepts HTTP requests
- Extracts token from Authorization header
- Validates token and sets SecurityContext

### SecurityConfig
- Configures stateless session management
- Defines public/protected endpoints
- Registers JWT filter

### AuthController
- `/api/auth/register` - User registration
- `/api/auth/login` - User login with token generation

---

## 🎯 Next Steps

1. **Change JWT Secret** (Production)
   - Update `jwt.secret` in `application.yml`
   - Use strong, random value (minimum 32 characters)

2. **Adjust Token Expiration**
   - Modify `jwt.expiration` for different timeframes
   - Consider implementing refresh tokens

3. **Add More Endpoints**
   - Apply `@PreAuthorize` to other controllers
   - Implement fine-grained authorization

4. **Database Setup**
   - Create initial users with different roles
   - Add admin user for testing

5. **Logging & Monitoring**
   - Track authentication attempts
   - Monitor token generation and validation
   - Alert on suspicious activities

6. **Additional Security**
   - Implement rate limiting on login endpoint
   - Add token blacklist for logout
   - Implement refresh tokens
   - Add MFA (Multi-Factor Authentication)
   - Setup CORS if frontend is separate

---

## 📖 Documentation

Detailed documentation available in `AUTHENTICATION_GUIDE.md`

---

## ✨ Summary

Your application now has **production-ready JWT authentication** with:
- ✅ User registration & login
- ✅ Secure password storage
- ✅ Stateless token-based auth
- ✅ Role-based access control
- ✅ Comprehensive error handling
- ✅ Extensible security configuration

**Happy coding! 🎉**


