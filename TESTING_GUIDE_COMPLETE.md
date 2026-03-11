# 🧪 Complete Testing Guide - Authentication, Authorization & API

## Table of Contents
1. [Manual Testing with cURL](#manual-testing-with-curl)
2. [Testing with Postman](#testing-with-postman)
3. [Testing with REST Client (VS Code)](#testing-with-rest-client)
4. [Unit Testing](#unit-testing)
5. [Integration Testing](#integration-testing)
6. [Test Scenarios](#test-scenarios)
7. [Debugging Tips](#debugging-tips)

---

## 🚀 Part 1: Manual Testing with cURL

### Prerequisites
- Application running on `http://localhost:8080`
- cURL installed (comes with most systems)

### 1.1 Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Expected Response (201 Created)**:
```json
{
  "username": "john",
  "roles": ["USER"],
  "message": "User registered successfully"
}
```

**Test Cases**:

**Success Case**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"pass123"}'
# Expected: 201 Created
```

**Duplicate Username Case**:
```bash
# First registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"duplicate","password":"pass123"}'

# Try again with same username
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"duplicate","password":"pass456"}'
# Expected: 400 Bad Request - Username already exists
```

**Missing Fields Case**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"nopassword"}'
# Expected: 400 Bad Request - password required
```

---

### 1.2 Login User

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Expected Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNzE1NDI3MjAwLCJleHAiOjE3MTU1MTM2MDB9.xyz...",
  "username": "john",
  "roles": ["USER"],
  "message": "User logged in successfully"
}
```

**Test Cases**:

**Successful Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
# Expected: 200 OK with JWT token
```

**Invalid Password**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"wrongpassword"}'
# Expected: 401 Unauthorized
```

**Nonexistent User**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"nonexistent","password":"pass123"}'
# Expected: 401 Unauthorized
```

---

### 1.3 Access Protected Endpoint with Token

**SAVE TOKEN IN VARIABLE** (Linux/Mac):
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}' | jq -r '.token')

echo $TOKEN
```

**SAVE TOKEN IN VARIABLE** (Windows PowerShell):
```powershell
$response = curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"john","password":"password123"}'

$token = ($response | ConvertFrom-Json).token
echo $token
```

**Get All Products (With Token)**:
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "Bread",
    "sku": "BREAD001",
    "price": 40.00,
    "stock": 50
  },
  {
    "id": 2,
    "name": "Milk",
    "sku": "MILK001",
    "price": 50.00,
    "stock": 100
  }
]
```

---

### 1.4 Test Protected Endpoints

**Get Specific Product**:
```bash
curl -X GET http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with product details
```

**Get All Categories**:
```bash
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with categories list
```

**Get All Customers**:
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with customers list
```

**Get All Orders**:
```bash
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with orders list
```

---

### 1.5 Test Without Token (Should Fail)

```bash
curl -X GET http://localhost:8080/api/products
# Expected: 401 Unauthorized
```

**Response**:
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "timestamp": "2026-03-11T10:30:00",
  "path": "/api/products"
}
```

---

### 1.6 Test with Invalid Token

```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer invalid.token.here"
# Expected: 401 Unauthorized
```

---

### 1.7 Test Create Product (ADMIN Only)

**As Regular User (Should Fail)**:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "sku": "NEW001",
    "price": 100.00,
    "stock": 50
  }'
# Expected: 403 Forbidden (User doesn't have ADMIN role)
```

---

### 1.8 Create CRUD Test Script

**create_curl_tests.sh** (Linux/Mac):
```bash
#!/bin/bash

echo "=== REGISTER USER ==="
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
echo -e "\n"

echo "=== LOGIN USER ==="
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}')
TOKEN=$(echo $RESPONSE | jq -r '.token')
echo $RESPONSE
echo -e "\n"

echo "=== GET ALL PRODUCTS ==="
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "=== GET ALL CATEGORIES ==="
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "=== GET ALL CUSTOMERS ==="
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "=== GET ALL ORDERS ==="
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "=== CREATE CUSTOMER ==="
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "address": "123 Main Street"
  }'
echo -e "\n"

echo "=== TRY TO CREATE PRODUCT (Should Fail - No ADMIN) ==="
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "sku": "TEST001",
    "price": 100,
    "stock": 50
  }'
echo -e "\n"
```

**Make executable and run**:
```bash
chmod +x create_curl_tests.sh
./create_curl_tests.sh
```

---

## 📮 Part 2: Testing with Postman

### 2.1 Install Postman

1. Download from [postman.com](https://www.postman.com/downloads/)
2. Install and launch
3. Create free account

### 2.2 Create Collection

1. Click **"Collections"** → **"+"** 
2. Name it: `Supermarket API Tests`
3. Click **Create**

### 2.3 Add Requests to Collection

#### **Request 1: Register User**

**Details**:
- Name: `Register User`
- Method: `POST`
- URL: `http://localhost:8080/api/auth/register`

**Headers**:
```
Content-Type: application/json
```

**Body** (raw JSON):
```json
{
  "username": "postman_user",
  "password": "password123"
}
```

**Click Send** → Should see `201 Created` response

---

#### **Request 2: Login User**

**Details**:
- Name: `Login User`
- Method: `POST`
- URL: `http://localhost:8080/api/auth/login`

**Headers**:
```
Content-Type: application/json
```

**Body** (raw JSON):
```json
{
  "username": "postman_user",
  "password": "password123"
}
```

**Tests** (Click Tests tab):
```javascript
// Save token for later use
var jsonData = pm.response.json();
pm.environment.set("jwt_token", jsonData.token);
pm.environment.set("username", jsonData.username);

// Verify response
pm.test("Login successful", function () {
    pm.response.to.have.status(200);
});

pm.test("Token received", function () {
    pm.expect(jsonData.token).to.exist;
});

pm.test("Username matches", function () {
    pm.expect(jsonData.username).to.equal("postman_user");
});
```

**Click Send** → Token automatically saved

---

#### **Request 3: Get All Products**

**Details**:
- Name: `Get All Products`
- Method: `GET`
- URL: `http://localhost:8080/api/products`

**Headers**:
```
Authorization: Bearer {{jwt_token}}
```

**Tests**:
```javascript
pm.test("Get products successful", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});
```

**Click Send** → Should see products list

---

#### **Request 4: Get Product by ID**

**Details**:
- Name: `Get Product by ID`
- Method: `GET`
- URL: `http://localhost:8080/api/products/1`

**Headers**:
```
Authorization: Bearer {{jwt_token}}
```

**Tests**:
```javascript
pm.test("Get product successful", function () {
    pm.response.to.have.status(200);
});

var jsonData = pm.response.json();
pm.test("Product has required fields", function () {
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('name');
    pm.expect(jsonData).to.have.property('price');
});
```

---

#### **Request 5: Create Product (Admin Only)**

**Details**:
- Name: `Create Product - Should Fail`
- Method: `POST`
- URL: `http://localhost:8080/api/products`

**Headers**:
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

**Body**:
```json
{
  "name": "New Product",
  "sku": "NEW001",
  "price": 150.00,
  "stock": 50
}
```

**Tests**:
```javascript
pm.test("User forbidden from creating product", function () {
    pm.response.to.have.status(403);
});
```

---

#### **Request 6: Get Categories**

**Details**:
- Name: `Get All Categories`
- Method: `GET`
- URL: `http://localhost:8080/api/categories`

**Headers**:
```
Authorization: Bearer {{jwt_token}}
```

---

#### **Request 7: Create Customer**

**Details**:
- Name: `Create Customer`
- Method: `POST`
- URL: `http://localhost:8080/api/customers`

**Headers**:
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

**Body**:
```json
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "phone": "9876543210",
  "address": "456 Oak Street"
}
```

**Tests**:
```javascript
pm.test("Customer created", function () {
    pm.response.to.have.status(201);
});

var jsonData = pm.response.json();
pm.environment.set("customer_id", jsonData.id);
```

---

#### **Request 8: Create Order**

**Details**:
- Name: `Create Order`
- Method: `POST`
- URL: `http://localhost:8080/api/orders`

**Headers**:
```
Authorization: Bearer {{jwt_token}}
Content-Type: application/json
```

**Body**:
```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 3,
      "unitPrice": 50.00
    }
  ]
}
```

**Tests**:
```javascript
pm.test("Order created", function () {
    pm.response.to.have.status(201));
});

var jsonData = pm.response.json();
pm.test("Order has items", function () {
    pm.expect(jsonData.items).to.be.an('array');
});
```

---

#### **Request 9: No Token - Should Fail**

**Details**:
- Name: `Get Products - No Token`
- Method: `GET`
- URL: `http://localhost:8080/api/products`

**Headers**: (None)

**Tests**:
```javascript
pm.test("Unauthorized without token", function () {
    pm.response.to.have.status(401);
});
```

---

#### **Request 10: Invalid Token - Should Fail**

**Details**:
- Name: `Get Products - Invalid Token`
- Method: `GET`
- URL: `http://localhost:8080/api/products`

**Headers**:
```
Authorization: Bearer invalid.token.xyz
```

**Tests**:
```javascript
pm.test("Unauthorized with invalid token", function () {
    pm.response.to.have.status(401);
});
```

---

### 2.4 Run Collection Tests

1. Click **"..."** next to collection name
2. Select **"Run Collection"**
3. Configure:
   - Iterations: 1
   - Delay: 500ms
   - Save responses
4. Click **"Run Supermarket API Tests"**
5. View results with pass/fail status

---

## 🔗 Part 3: Testing with REST Client (VS Code Extension)

### 3.1 Install Extension

1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search for "REST Client" by Huachao Mao
4. Click Install

### 3.2 Create Test File

**File: `api-tests.http`**

```http
### Variables
@baseUrl = http://localhost:8080
@contentType = application/json

### Register User
POST {{baseUrl}}/api/auth/register
Content-Type: {{contentType}}

{
  "username": "restclient_user",
  "password": "password123"
}

### Login User
# @name Login
POST {{baseUrl}}/api/auth/login
Content-Type: {{contentType}}

{
  "username": "restclient_user",
  "password": "password123"
}

### Get All Products
@token = {{Login.response.body.token}}
GET {{baseUrl}}/api/products
Authorization: Bearer {{token}}

### Get Product by ID
GET {{baseUrl}}/api/products/1
Authorization: Bearer {{token}}

### Get All Categories
GET {{baseUrl}}/api/categories
Authorization: Bearer {{token}}

### Get All Customers
GET {{baseUrl}}/api/customers
Authorization: Bearer {{token}}

### Get All Orders
GET {{baseUrl}}/api/orders
Authorization: Bearer {{token}}

### Create Customer
POST {{baseUrl}}/api/customers
Authorization: Bearer {{token}}
Content-Type: {{contentType}}

{
  "name": "REST Client User",
  "email": "rest@example.com",
  "phone": "5555555555",
  "address": "789 Pine Street"
}

### Create Order
POST {{baseUrl}}/api/orders
Authorization: Bearer {{token}}
Content-Type: {{contentType}}

{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 50.00
    }
  ]
}

### Test - No Token (Should Fail)
GET {{baseUrl}}/api/products

### Test - Invalid Token (Should Fail)
GET {{baseUrl}}/api/products
Authorization: Bearer invalid.token

### Create Product as User (Should Fail)
POST {{baseUrl}}/api/products
Authorization: Bearer {{token}}
Content-Type: {{contentType}}

{
  "name": "Test Product",
  "sku": "TEST001",
  "price": 100.00,
  "stock": 50
}

### Update Customer
PUT {{baseUrl}}/api/customers/1
Authorization: Bearer {{token}}
Content-Type: {{contentType}}

{
  "name": "Updated Name",
  "email": "updated@example.com",
  "phone": "1111111111",
  "address": "Updated Address"
}

### Delete Order (Should Fail - Not Admin)
DELETE {{baseUrl}}/api/orders/1
Authorization: Bearer {{token}}
```

### 3.3 Run Tests

- Click **"Send Request"** above each request
- Response appears in separate panel
- Click **"Send All"** to run all requests

---

## 🧪 Part 4: Unit Testing

### 4.1 Service Layer Tests

**File: `src/test/java/com/example/supermarket/service/CustomUserDetailsServiceTest.java`** (Already provided)

**Run tests**:
```bash
mvn test -Dtest=CustomUserDetailsServiceTest
```

**Output**:
```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

---

### 4.2 JWT Provider Tests

**File: `src/test/java/com/example/supermarket/service/JwtTokenProviderTest.java`** (Already provided)

**Run tests**:
```bash
mvn test -Dtest=JwtTokenProviderTest
```

---

### 4.3 Controller Tests

**File: `src/test/java/com/example/supermarket/controller/AuthControllerTest.java`** (Already provided)

**Run tests**:
```bash
mvn test -Dtest=AuthControllerTest
```

---

### 4.4 Run All Tests

```bash
mvn test
```

**Output Example**:
```
[INFO] Running com.example.supermarket.controller.AuthControllerTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.example.supermarket.service.JwtTokenProviderTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

[INFO] Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
```

---

## 📊 Part 5: Integration Testing

### 5.1 Create New Integration Test

**File: `src/test/java/com/example/supermarket/IntegrationTests.java`**

```java
package com.example.supermarket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCompleteWorkflow() throws Exception {
        // 1. Register User
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"integration_user\",\"password\":\"pass123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration_user"));

        // 2. Login User
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"integration_user\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token
        String token = loginResponse.substring(
                loginResponse.indexOf("\"token\":\"") + 9,
                loginResponse.indexOf("\",\"username\"")
        );

        // 3. Get Products with Token
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));

        // 4. Create Customer
        mockMvc.perform(post("/api/customers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Customer\",\"email\":\"test@example.com\",\"phone\":\"1234567890\",\"address\":\"123 Test St\"}"))
                .andExpect(status().isCreated());

        // 5. Try to Create Product (Should Fail)
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"sku\":\"TST\",\"price\":100,\"stock\":10}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAuthorizationFailures() throws Exception {
        // Test without token
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());

        // Test with invalid token
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }
}
```

**Run integration tests**:
```bash
mvn test -Dtest=IntegrationTests
```

---

## 📋 Part 6: Complete Test Scenarios

### Scenario 1: Full Registration to Order Process

```
Step 1: Register User
  POST /api/auth/register
  Input: {username: "alice", password: "alice123"}
  Expected: 201 Created

Step 2: Login
  POST /api/auth/login
  Input: {username: "alice", password: "alice123"}
  Expected: 200 OK, get JWT token

Step 3: Create Customer
  POST /api/customers
  Input: {name: "Alice", email: "alice@example.com", phone: "1234567890", address: "123 Alice St"}
  Expected: 201 Created, get customer_id

Step 4: View Products
  GET /api/products
  Expected: 200 OK, list of products

Step 5: Create Order
  POST /api/orders
  Input: {customerId: <customer_id>, items: [{productId: 1, quantity: 2, unitPrice: 50}]}
  Expected: 201 Created, order with total price calculated

Step 6: View Orders
  GET /api/orders
  Expected: 200 OK, list of orders including created order

Step 7: Get Order Details
  GET /api/orders/<order_id>
  Expected: 200 OK, order with all items
```

### Scenario 2: Authorization Testing

```
Step 1: Register Regular User
  Expected: Can create customer, create order, view data
  
Step 2: Try to Create Product
  Expected: 403 Forbidden (USER role cannot create products)
  
Step 3: Try to Delete Product
  Expected: 403 Forbidden (USER role cannot delete)
  
Step 4: Try to Delete Customer
  Expected: 403 Forbidden (USER role cannot delete customer)
```

### Scenario 3: Authentication Failure Cases

```
Case 1: Wrong Password
  POST /api/auth/login with wrong password
  Expected: 401 Unauthorized

Case 2: Nonexistent User
  POST /api/auth/login with unknown username
  Expected: 401 Unauthorized

Case 3: No Token
  GET /api/products without Authorization header
  Expected: 401 Unauthorized

Case 4: Invalid Token
  GET /api/products with malformed token
  Expected: 401 Unauthorized

Case 5: Expired Token
  (Wait for token to expire or manually set expiration)
  GET /api/products with expired token
  Expected: 401 Unauthorized
```

### Scenario 4: Data Validation

```
Case 1: Empty Username
  POST /api/auth/register with empty username
  Expected: 400 Bad Request

Case 2: Invalid Email
  POST /api/customers with invalid email format
  Expected: 400 Bad Request

Case 3: Negative Price
  POST /api/products with negative price
  Expected: 400 Bad Request

Case 4: Duplicate Email
  POST /api/customers with duplicate email
  Expected: 400 Bad Request
```

---

## 🔧 Part 7: Debugging Tips

### 7.1 Enable Debug Logging

**application.yml**:
```yaml
logging:
  level:
    root: INFO
    com.example.supermarket: DEBUG
    org.springframework.security: DEBUG
```

**Run application**:
```bash
mvn spring-boot:run
```

**View logs**:
```
DEBUG com.example.supermarket.config.JwtAuthenticationFilter - Set user authentication for username: john
DEBUG org.springframework.security.access.vote.AffirmativeBased - Voter: org.springframework.security.access.vote.RoleVoter@xyz, returned: 1
```

---

### 7.2 Use Postman Console

1. Open Postman
2. Click **Console** at bottom
3. All requests logged with details
4. Check headers, body, response

---

### 7.3 H2 Console for Database Inspection

1. Navigate to `http://localhost:8080/h2-console`
2. Click **Connect**
3. Run SQL queries:

```sql
-- View all users
SELECT * FROM users;

-- View user roles
SELECT * FROM user_roles;

-- View all products
SELECT * FROM products;

-- View all orders
SELECT * FROM orders;

-- View order items
SELECT * FROM order_items;
```

---

### 7.4 cURL with Verbose Output

```bash
curl -v -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
```

**Output shows**:
```
> GET /api/products HTTP/1.1
> Authorization: Bearer eyJhbGc...
< HTTP/1.1 200 OK
< Content-Type: application/json
[response body]
```

---

### 7.5 Check Token Contents

**Decode JWT at [jwt.io](https://jwt.io)**:

1. Copy token from login response
2. Paste into jwt.io
3. View payload:
```json
{
  "sub": "john",
  "iat": 1715427200,
  "exp": 1715513600
}
```

---

## ✅ Complete Testing Checklist

### Authentication Tests
- [ ] Register new user successfully
- [ ] Register with duplicate username fails
- [ ] Register with missing password fails
- [ ] Login with correct credentials succeeds
- [ ] Login with wrong password fails
- [ ] Login with nonexistent user fails

### Authorization Tests
- [ ] USER can view products
- [ ] USER cannot create products
- [ ] USER cannot delete products
- [ ] USER can create orders
- [ ] USER cannot delete orders
- [ ] ADMIN can create products
- [ ] ADMIN can delete products
- [ ] ADMIN can delete orders

### API Tests
- [ ] GET /api/products returns list
- [ ] GET /api/products/{id} returns single product
- [ ] GET /api/categories returns list
- [ ] GET /api/customers returns list
- [ ] GET /api/orders returns list
- [ ] POST /api/customers creates customer
- [ ] POST /api/orders creates order
- [ ] PUT /api/customers updates customer

### Token Tests
- [ ] Request without token returns 401
- [ ] Request with invalid token returns 401
- [ ] Request with valid token succeeds
- [ ] Token format validation works

### Validation Tests
- [ ] Empty fields return 400
- [ ] Invalid email returns 400
- [ ] Duplicate email returns 400
- [ ] Negative price returns 400

### Error Handling Tests
- [ ] 404 returned for nonexistent resource
- [ ] 403 returned for insufficient permissions
- [ ] 401 returned for auth failures
- [ ] 400 returned for validation failures
- [ ] 500 handled gracefully

---

## 🎯 Summary

You now have complete testing coverage using:

1. **cURL** - Command line testing
2. **Postman** - GUI testing with collections
3. **REST Client** - VS Code extension
4. **Unit Tests** - Service/component testing
5. **Integration Tests** - Full workflow testing

**Run all tests**:
```bash
mvn clean test
```

**Expected Result**: All tests pass ✅


