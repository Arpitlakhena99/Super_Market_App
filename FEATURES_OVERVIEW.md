# 🛒 Supermarket Management Application - Complete Feature Overview

## 📋 Project Overview

**Supermarket Management System** is a **Spring Boot REST API** that provides a complete backend for managing a supermarket's operations including products, categories, customers, and orders.

---

## ✨ Core Features

### 1. 🔐 **Authentication & Authorization** ✅ NEW
- **User Registration**: Create new user accounts
- **User Login**: Authenticate with JWT tokens
- **Role-Based Access Control**: USER and ADMIN roles
- **Token Validation**: Automatic token verification on protected endpoints
- **Secure Password Storage**: BCrypt hashing
- **Stateless Authentication**: JWT-based, no server sessions

**Endpoints**:
```
POST   /api/auth/register        Register new user
POST   /api/auth/login           Login and get JWT token
```

---

### 2. 📦 **Product Management**

#### Features:
- ✅ Create new products (ADMIN only)
- ✅ Read/View all products
- ✅ Get specific product by ID
- ✅ Update product details (ADMIN only)
- ✅ Delete products (ADMIN only)
- ✅ List products sorted by name (ascending)

#### Product Attributes:
- Product ID (auto-generated)
- Name (unique, required)
- SKU (Stock Keeping Unit, unique identifier)
- Price (decimal, required)
- Stock quantity (inventory count)

**Endpoints**:
```
GET    /api/products              Get all products (sorted by name)
GET    /api/products/{id}         Get product by ID
POST   /api/products              Create product (ADMIN only)
PUT    /api/products/{id}         Update product (ADMIN only)
DELETE /api/products/{id}         Delete product (ADMIN only)
```

**Example Product**:
```json
{
  "id": 1,
  "name": "Fresh Milk",
  "sku": "MILK001",
  "price": 50.00,
  "stock": 100
}
```

---

### 3. 🏷️ **Category Management**

#### Features:
- ✅ Create product categories
- ✅ View all categories
- ✅ Get specific category
- ✅ Update category information
- ✅ Delete categories
- ✅ Sort categories alphabetically

#### Category Attributes:
- Category ID (auto-generated)
- Name (unique, required)
- Description (optional)

**Endpoints**:
```
GET    /api/categories            Get all categories (sorted)
GET    /api/categories/{id}       Get category by ID
POST   /api/categories            Create category (ADMIN only)
PUT    /api/categories/{id}       Update category (ADMIN only)
DELETE /api/categories/{id}       Delete category (ADMIN only)
```

**Example Category**:
```json
{
  "id": 1,
  "name": "Dairy Products",
  "description": "Milk, cheese, and dairy items"
}
```

---

### 4. 👥 **Customer Management**

#### Features:
- ✅ Register/Create customers
- ✅ View all customers
- ✅ Get specific customer details
- ✅ Update customer information
- ✅ Delete customer records
- ✅ Sort customers alphabetically

#### Customer Attributes:
- Customer ID (auto-generated)
- Name (required)
- Email (unique, required)
- Phone number (required)
- Address (optional)

**Endpoints**:
```
GET    /api/customers             Get all customers (sorted)
GET    /api/customers/{id}        Get customer by ID
POST   /api/customers             Create customer
PUT    /api/customers/{id}        Update customer
DELETE /api/customers/{id}        Delete customer
```

**Example Customer**:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "address": "123 Main Street"
}
```

---

### 5. 🛍️ **Order Management**

#### Features:
- ✅ Create orders with multiple items
- ✅ View all orders
- ✅ Get specific order details with items
- ✅ Update order information
- ✅ Delete orders
- ✅ Calculate total order price
- ✅ Track order items quantity and price

#### Order Attributes:
- Order ID (auto-generated)
- Customer (reference to customer)
- Order Date (timestamp)
- Total Price (calculated)
- Order Items (list of products ordered)

#### Order Item Attributes:
- Item ID (auto-generated)
- Product (reference to product)
- Quantity (how many units)
- Unit Price (price per unit)
- Subtotal (quantity × unit price)

**Endpoints**:
```
GET    /api/orders                Get all orders
GET    /api/orders/{id}           Get order by ID (with items)
POST   /api/orders                Create new order
PUT    /api/orders/{id}           Update order
DELETE /api/orders/{id}           Delete order
```

**Example Order**:
```json
{
  "id": 1,
  "customer": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "orderDate": "2026-03-11T10:30:00",
  "totalPrice": 500.00,
  "items": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "Fresh Milk",
        "sku": "MILK001"
      },
      "quantity": 5,
      "unitPrice": 50.00,
      "subtotal": 250.00
    },
    {
      "id": 2,
      "product": {
        "id": 2,
        "name": "Bread",
        "sku": "BREAD001"
      },
      "quantity": 5,
      "unitPrice": 50.00,
      "subtotal": 250.00
    }
  ]
}
```

---

## 🔒 Access Control Matrix

| Feature | Public | USER | ADMIN |
|---------|--------|------|-------|
| Register/Login | ✅ | - | - |
| View Products | ✅ | ✅ | ✅ |
| Create Product | ❌ | ❌ | ✅ |
| Update Product | ❌ | ❌ | ✅ |
| Delete Product | ❌ | ❌ | ✅ |
| View Categories | ✅ | ✅ | ✅ |
| Create Category | ❌ | ❌ | ✅ |
| Update Category | ❌ | ❌ | ✅ |
| Delete Category | ❌ | ❌ | ✅ |
| View Customers | ✅ | ✅ | ✅ |
| Create Customer | ✅ | ✅ | ✅ |
| Update Customer | ✅ | ✅ | ✅ |
| Delete Customer | ❌ | ❌ | ✅ |
| View Orders | ✅ | ✅ | ✅ |
| Create Order | ✅ | ✅ | ✅ |
| Update Order | ✅ | ✅ | ✅ |
| Delete Order | ❌ | ❌ | ✅ |

---

## 📊 Technical Capabilities

### Data Management
- ✅ **CRUD Operations**: Create, Read, Update, Delete for all entities
- ✅ **Data Validation**: Input validation on all endpoints
- ✅ **Sorting**: Default sorting by name (alphabetically)
- ✅ **Pagination Ready**: Architecture supports pagination
- ✅ **Error Handling**: Global exception handling with meaningful messages

### Database Support
- ✅ **H2**: In-memory database (development/testing)
- ✅ **PostgreSQL**: Production database support
- ✅ **JPA/Hibernate**: ORM with Hibernate
- ✅ **Automatic Schema**: SQL schema initialization

### API Features
- ✅ **REST API**: RESTful endpoint design
- ✅ **JSON**: Request/Response in JSON format
- ✅ **HTTP Status Codes**: Proper 200, 201, 204, 400, 401, 403, 404, 500
- ✅ **Error Responses**: Structured error messages
- ✅ **DTOs**: Data Transfer Objects for clean API contracts

### Security Features
- ✅ **Authentication**: User registration and login
- ✅ **Authorization**: Role-based access control
- ✅ **Password Hashing**: BCrypt password encoding
- ✅ **JWT Tokens**: Stateless token-based auth
- ✅ **Token Validation**: Automatic on protected endpoints

### Architecture Patterns
- ✅ **Layered Architecture**: Controller → Service → Repository
- ✅ **Dependency Injection**: Spring's DI container
- ✅ **Service Layer**: Business logic separation
- ✅ **Repository Pattern**: Data access abstraction
- ✅ **DTO Pattern**: Clean API contracts
- ✅ **Singleton Pattern**: Logger manager singleton

### Code Quality
- ✅ **Unit Tests**: Test cases for services and controllers
- ✅ **Logging**: SLF4J logging with custom logger
- ✅ **Modular Design**: Separated concerns
- ✅ **Documentation**: Code comments and README
- ✅ **Exception Handling**: Global exception handler

---

## 🚀 API Usage Examples

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
# Returns: { "token": "eyJhbGc...", "username": "john", "roles": ["USER"] }
```

### 3. Create Product (ADMIN only)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Fresh Milk",
    "sku": "MILK001",
    "price": 50.00,
    "stock": 100
  }'
```

### 4. Get All Products
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>"
```

### 5. Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 5,
        "unitPrice": 50.00
      }
    ]
  }'
```

### 6. Get Order Details
```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 💾 Database Schema

### Tables
1. **USERS** - User accounts
2. **USER_ROLES** - User role assignments
3. **CATEGORIES** - Product categories
4. **PRODUCTS** - Product inventory
5. **CUSTOMERS** - Customer records
6. **ORDERS** - Order records
7. **ORDER_ITEMS** - Individual items in orders

### Relationships
```
Users ←→ User_Roles (Many-to-Many)
Products ←→ Categories (Many-to-One)
Orders ←→ Customers (Many-to-One)
Orders ←→ Order_Items (One-to-Many)
Order_Items ←→ Products (Many-to-One)
```

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.1.4 |
| Data Access | Spring Data JPA | - |
| ORM | Hibernate | - |
| Database | H2 / PostgreSQL | - |
| Security | Spring Security + JWT | 0.12.3 |
| JSON | Jackson | - |
| Validation | Jakarta Validation | - |
| Utilities | Lombok | 1.18.28 |
| Mapping | ModelMapper | 3.1.0 |
| Testing | JUnit 5 + Mockito | - |
| Build | Maven | 3.10.1 |

---

## 📈 Scalability & Performance

- ✅ **Stateless Design**: No server-side sessions (scales horizontally)
- ✅ **Database Optimization**: Indexes on primary/foreign keys
- ✅ **Lazy Loading**: JPA lazy loading to reduce memory
- ✅ **Connection Pooling**: Hikari connection pool
- ✅ **Caching**: Spring Cache ready (not enabled by default)

---

## 🔄 Workflow Example: Complete Order

```
1. User registers
   POST /api/auth/register → Gets account

2. User logs in
   POST /api/auth/login → Gets JWT token

3. User creates customer profile
   POST /api/customers → Customer record created

4. User views products
   GET /api/products → Lists all available products

5. User creates order
   POST /api/orders → Order created with items

6. Admin updates product stock
   PUT /api/products/{id} → Stock adjusted

7. User views their order
   GET /api/orders/{id} → Order details with items
```

---

## 📝 Input Validation

All endpoints validate inputs:
- ✅ Required fields must be present
- ✅ Email format validation
- ✅ Numeric fields range validation
- ✅ String length constraints
- ✅ Unique constraints (username, email, SKU)

**Error Response**:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "name: must not be blank, price: must be greater than 0",
  "timestamp": "2026-03-11T10:30:00",
  "path": "/api/products"
}
```

---

## 🧪 Testing

The application includes:
- ✅ Unit tests for services
- ✅ Integration tests for controllers
- ✅ Mock objects with Mockito
- ✅ JUnit 5 test framework

**Run Tests**:
```bash
mvn test
```

---

## 🚀 Deployment

**Build Application**:
```bash
mvn clean install
```

**Run Application**:
```bash
mvn spring-boot:run
```

**Access Application**:
- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

---

## 📚 Documentation Included

1. **QUICK_START.md** - Quick start guide for JWT auth
2. **AUTHENTICATION_GUIDE.md** - Detailed authentication documentation
3. **IMPLEMENTATION_SUMMARY.md** - Complete implementation details
4. **TEACHING_GUIDE.md** - Student learning guide
5. **README.md** - Original project documentation

---

## ✅ What You Can Do Right Now

1. ✅ **Register and manage users** with secure JWT authentication
2. ✅ **Manage products** with create, update, delete operations
3. ✅ **Organize products** into categories
4. ✅ **Manage customer data** with full CRUD operations
5. ✅ **Create and track orders** with detailed order items
6. ✅ **Control access** with role-based permissions
7. ✅ **Get detailed error messages** for debugging
8. ✅ **Use in production** with PostgreSQL database
9. ✅ **Test with multiple endpoints** via REST API
10. ✅ **Scale horizontally** with stateless architecture

---

## 🎯 Future Enhancements

**Optional additions**:
- 🔄 Refresh token mechanism
- 🚀 Pagination and filtering
- 📊 Analytics and reports
- 💳 Payment integration
- 📧 Email notifications
- 📱 Mobile app support
- 🔍 Search functionality
- 🏪 Multi-store support
- 📦 Inventory management
- 👨‍💼 Staff management

---

## 📞 Quick Reference

**Base URL**: `http://localhost:8080`

**Key Endpoints**:
- Auth: `/api/auth/**`
- Products: `/api/products/**`
- Categories: `/api/categories/**`
- Customers: `/api/customers/**`
- Orders: `/api/orders/**`

**Default Port**: `8080`
**Database**: H2 (in-memory) / PostgreSQL
**Authentication**: JWT Bearer tokens

---

## ✨ Summary

This is a **complete, production-ready supermarket management system** with:

✅ Comprehensive REST API
✅ Secure authentication and authorization
✅ Full CRUD operations for all entities
✅ Role-based access control
✅ Data validation and error handling
✅ Logging and monitoring ready
✅ Test coverage
✅ Clean architecture
✅ Scalable design
✅ Multiple database support

**Ready to use! 🎉**


