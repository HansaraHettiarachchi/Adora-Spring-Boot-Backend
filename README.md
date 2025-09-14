
# API Documentation — Adora Spring Boot Backend

---

## Table of contents

- [Authentication](#authentication)
- [User management](#user-management)
- [Product management](#product-management)
- [Category management](#category-management)
- [Order processing](#order-processing)
- [Admin / Dashboard](#admin--dashboard)
- [Global error handling & validation](#global-error-handling--validation)
- [Authentication flow (JWT)](#authentication-flow-jwt)
- [How to use this documentation](#how-to-use-this-documentation)

---

## Authentication

### `POST /api/auth/register`
- **Description:** Register a new user.
- **Headers:** `Content-Type: application/json`
- **Request body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "P@ssw0rd",
  "role": "USER" // optional
}
```
- **Responses:**
  - `201 Created`
```json
{
  "id": 123,
  "name": "John Doe",
  "email": "john@example.com",
  "message": "User registered successfully"
}
```
  - `400 Bad Request` — validation failed (e.g. email exists, weak password)
  - `500 Internal Server Error`

---

### `POST /api/auth/login`
- **Description:** Authenticate user and return JWT token.
- **Headers:** `Content-Type: application/json`
- **Request body:**
```json
{
  "email": "john@example.com",
  "password": "P@ssw0rd"
}
```
- **Responses:**
  - `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 123,
    "name": "John Doe",
    "email": "john@example.com",
    "roles": ["USER"]
  }
}
```
  - `401 Unauthorized` — invalid credentials
  - `400 Bad Request` — missing fields

---

### `POST /api/auth/refresh` *(optional)*
- **Description:** Exchange a refresh token for a new access token (if implemented).
- **Headers:** `Content-Type: application/json`
- **Request body:**
```json
{
  "refreshToken": "refresh-token-string"
}
```
- **Responses:**
  - `200 OK`
```json
{
  "token": "new-access-token",
  "expiresIn": 3600
}
```
  - `401 Unauthorized` — invalid/expired refresh token

---

## User management

### `GET /api/users`
- **Description:** Get list of users (admin protected).
- **Headers:**
  - `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `200 OK`
```json
[
  {
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com",
    "roles": ["USER"]
  },
  {
    "id": 2,
    "name": "Bob",
    "email": "bob@example.com",
    "roles": ["ADMIN"]
  }
]
```
  - `403 Forbidden` — not an admin
  - `401 Unauthorized` — no token / invalid token

---

### `GET /api/users/{id}`
- **Description:** Get user details by id (self or admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `200 OK`
```json
{
  "id": 1,
  "name": "Alice",
  "email": "alice@example.com",
  "phone": "0771234567"
}
```
  - `404 Not Found` — user not found
  - `401 Unauthorized` / `403 Forbidden`

---

### `PUT /api/users/{id}`
- **Description:** Update user profile (self or admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`, `Content-Type: application/json`
- **Request body:**
```json
{
  "name": "Alice Smith",
  "phone": "0777654321"
}
```
- **Responses:**
  - `200 OK`
```json
{
  "id": 1,
  "name": "Alice Smith",
  "email": "alice@example.com",
  "phone": "0777654321",
  "message": "User updated"
}
```
  - `400 Bad Request` — validation error
  - `403 Forbidden` — cannot update other users
  - `404 Not Found`

---

### `DELETE /api/users/{id}`
- **Description:** Delete user (admin or self depending on policy).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `204 No Content`
  - `403 Forbidden`, `401 Unauthorized`, `404 Not Found`

---

## Product management

### `GET /api/products`
- **Description:** Retrieve paginated list of products (public).
- **Headers:** Optional `Authorization`.
- **Query params (common):** `page`, `size`, `sort`, `search`, `category`
- **Responses:**
  - `200 OK`
```json
{
  "content": [
    {
      "id": 10,
      "name": "Cool T-shirt",
      "price": 19.99,
      "categoryId": 2,
      "stock": 40,
      "imageUrl": "https://..."
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 120,
  "totalPages": 6
}
```

---

### `GET /api/products/{id}`
- **Description:** Get product details by id.
- **Responses:**
  - `200 OK`
```json
{
  "id": 10,
  "name": "Cool T-shirt",
  "description": "Soft cotton T-shirt",
  "price": 19.99,
  "stock": 40,
  "category": { "id": 2, "name": "Apparel" },
  "images": ["https://..."]
}
```
  - `404 Not Found`

---

### `POST /api/products`
- **Description:** Create a new product (admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`, `Content-Type: application/json`
- **Request body:**
```json
{
  "name": "New Shirt",
  "description": "Eco-friendly fabric",
  "price": 24.5,
  "stock": 100,
  "categoryId": 2,
  "imageUrls": ["https://..."]
}
```
- **Responses:**
  - `201 Created`
```json
{
  "id": 101,
  "message": "Product created"
}
```
  - `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`

---

### `PUT /api/products/{id}`
- **Description:** Update product (admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Request body:** (same as create; fields optional)
- **Responses:**
  - `200 OK` — updated product payload or confirmation
  - `404 Not Found`, `403 Forbidden`

---

### `DELETE /api/products/{id}`
- **Description:** Delete product (admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `204 No Content`
  - `404 Not Found`, `403 Forbidden`

---

## Category management

### `GET /api/categories`
- **Description:** List categories (public).
- **Responses:**
  - `200 OK`
```json
[
  { "id": 1, "name": "Apparel" },
  { "id": 2, "name": "Accessories" }
]
```

---

### `GET /api/categories/{id}`
- **Description:** Get category details including products.
- **Responses:**
  - `200 OK`
```json
{
  "id": 1,
  "name": "Apparel",
  "products": [ { "id": 10, "name": "Cool T-shirt" } ]
}
```

---

### `POST /api/categories`
- **Description:** Create category (admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`, `Content-Type: application/json`
- **Request body:**
```json
{ "name": "New Category" }
```
- **Responses:**
  - `201 Created`
  - `400 Bad Request`, `403 Forbidden`

---

### `PUT /api/categories/{id}` and `DELETE /api/categories/{id}`
- **Description:** Update or delete category (admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `200 OK` / `204 No Content`, or appropriate error codes.

---

## Order processing

### `POST /api/orders`
- **Description:** Place an order (authenticated users).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`, `Content-Type: application/json`
- **Request body:**
```json
{
  "shippingAddress": {
    "line1": "No 10, Street",
    "city": "Colombo",
    "postalCode": "10000",
    "country": "Sri Lanka"
  },
  "items": [
    { "productId": 10, "quantity": 2 },
    { "productId": 12, "quantity": 1 }
  ],
  "paymentMethod": "CARD"
}
```
- **Responses:**
  - `201 Created`
```json
{
  "orderId": 501,
  "status": "PENDING",
  "total": 59.98
}
```
  - `400 Bad Request`, `401 Unauthorized`

---

### `GET /api/orders` (user)
- **Description:** List orders for the authenticated user.
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `200 OK`
```json
[ { "orderId": 501, "status": "DELIVERED", "total": 59.98 } ]
```

---

### `GET /api/orders/{id}`
- **Description:** Get order details (user owns order or admin).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Responses:**
  - `200 OK` detailed order payload
  - `403 Forbidden`, `404 Not Found`

---

### `PUT /api/orders/{id}/status` (admin)
- **Description:** Update order status (e.g., PROCESSING → SHIPPED).
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Request body:**
```json
{ "status": "SHIPPED" }
```
- **Responses:**
  - `200 OK` — updated order
  - `403 Forbidden`, `404 Not Found`

---

## Admin / Dashboard

> If your project contains admin-specific controllers, list them here. Common endpoints:

- `GET /api/admin/statistics` — site-wide stats (orders, revenue, users).
- `GET /api/admin/users` — manage users.
- `GET /api/admin/orders` — all orders.

**Headers:** `Authorization: Bearer <JWT_TOKEN>` (ADMIN role required)

---

## Global error handling & validation

- Typical Spring Boot apps use:
  - `@ControllerAdvice` + `@ExceptionHandler` for centralized exception handling.
  - Custom exception classes like `ResourceNotFoundException`, `BadRequestException`.
  - Validation with `@Valid` and JSR-380 annotations on DTOs (`@NotNull`, `@Email`, `@Size`).
  - Responses often use a consistent error format:
```json
{
  "timestamp": "2025-09-14T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users"
}
```
- **Action:** Replace with the exact exception handlers and response DTOs from your `@ControllerAdvice` class.

---

## Authentication flow (JWT)

1. Client authenticates with `POST /api/auth/login` (email + password).
2. Server returns a JWT in the response:
```json
{ "token": "eyJ...", "tokenType": "Bearer", "expiresIn": 3600 }
```
3. Client stores the token (localStorage / secure cookie).
4. For protected endpoints, include header:
```
Authorization: Bearer <token>
```
5. Server validates the token on each request (filters such as `OncePerRequestFilter` are common).
6. Token expiry: refresh strategy (optional) via refresh tokens.

**Notes:**
- JWT secret and expiry durations are configured in `application.properties` / `application.yml`.
- Roles are usually embedded as claims (e.g., `roles: ["ADMIN","USER"]`) — used for `@PreAuthorize("hasRole('ADMIN')")` or similar.

---

## How to use this documentation

1. Clone your repository and open `src/main/java` to find controller classes (look for `@RestController`).
2. For each controller:
   - Replace the placeholder endpoint paths / request / response bodies above with actual method signatures, DTOs and sample JSON.
   - Add any missing domains (e.g., `Cart`, `Wishlist`, `Payments`, `File Uploads`) following the same format.
3. Paste the completed section into your project's `README.md` under a header named **API Documentation**.
4. If you want, re-run the helper to generate a fully concrete README: upload the code (controllers) here and I will produce the finished section.

---
