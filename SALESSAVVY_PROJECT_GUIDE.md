# 🛍️ SalesSavvy: Comprehensive Interview & Architecture Study Guide

Welcome to the **SalesSavvy** Study Guide! This document translates the complex, professional-grade architecture of your **Sales Management & E-Commerce System** into clear, plain English. It is custom-tailored to help you impress interviewers by explaining technical details, design patterns, and engineering choices confidently.

---

## 🏛️ The Big Picture: Architectural Flow (The Restaurant Analogy)

To explain this project to an interviewer, use **The Restaurant Analogy**. It makes the technical stack instantly relatable and showcases your communication skills.

```
[FRONTEND (Customer)] ──(Request + JWT Bearer)──> [BOUNCER (Security Filter)]
                                                            │ (Permitted)
                                                            ▼
[DATABASE (Pantry)] <──(ACID Transactions)─── [SOUS-CHEF (PaymentService)]
```

*   **Frontend (The Customer)**: Built with dynamic, responsive JavaScript and HTML (`customer.js`, `view-cart.html`). The customer makes choices, adds items to their cart, and interacts with the **Razorpay Checkout Overlay** to pay.
*   **JWT Token (The VIP Wristband)**: A stateless token issued upon login. The frontend stores this in `localStorage` and attaches it in the `Authorization: Bearer <token>` header of every request.
*   **Security Filter & Config (The Restaurant Bouncer)**: Intercepts every single incoming request (`JwtAuthenticationFilter.java` and `SecurityConfig.java`). It checks if the VIP wristband (JWT) is authentic. If valid, it tells the kitchen who the user is and what role they have (`ROLE_USER` or `ROLE_ADMIN`).
*   **Controllers (The Head Chef)**: REST controllers (`PaymentController.java`, `CartController.java`) receive requests, validate request bodies, and route them to specialized services.
*   **Services (The Specialized Sous-Chefs)**: The logic makers. `PaymentService.java` manages inventory calculations, order creations, and integrates with the external payment processor.
*   **Razorpay SDK (The External Cashier)**: An external payment system. We talk to it via basic auth to create orders, charge the customer, and send back secure payment receipts.
*   **JPA & Hibernate (The Pantry Catalog)**: An Object-Relational Mapping (ORM) layer that automatically translates Java objects (entities like `Product` or `CartItem`) into SQL queries.
*   **MySQL Database (The Cold Storage Pantry)**: Persistently stores user accounts, products, carts, purchase orders, order items, and payment transactions.

---

## 🔒 Deep Dive 1: Stateless Security & JWT Authentication

### Why stateless JWT is superior to session-based cookies:
In a traditional session-based app, the server stores user session data in its RAM memory. If the server crashes, all users are logged out. Furthermore, if you scale horizontally (adding more servers to handle heavy traffic), Server A won't know about sessions on Server B.
**JWT solves this:** The server does not store session memory. It generates a signed token containing the user's details and cryptographically signs it using a secret key. Every incoming request carries the token. The server simply decodes the token and verifies the signature locally. It is 100% stateless and infinitely scalable.

### 📂 File Breakdown:
1.  **[User.java](file:///C:/Users/Juned%20Bankapur/git/repository3/SalesSavvy/src/main/java/com/example/jwtDemo/entity/User.java)**:
    *   MySQL Entity representing the user credentials, password (stored securely as a BCrypt hash), and user role (`USER` or `ADMIN`).
2.  **[JwtService.java](file:///C:/Users/Juned%20Bankapur/git/repository3/SalesSavvy/src/main/java/com/example/jwtDemo/service/JwtService.java)**:
    *   Generates base64 keys, signs JWTs using **HS256 (HMAC SHA-256)**, sets expiration boundaries (3600 seconds = 1 hour), and extracts claims like username to check validity.
3.  **[JwtAuthenticationFilter.java](file:///C:/Users/Juned%20Bankapur/git/repository3/SalesSavvy/src/main/java/com/example/jwtDemo/filter/JwtAuthenticationFilter.java)**:
    *   Extends `OncePerRequestFilter`. Intercepts incoming HTTP requests.
    *   Extracts the header: `Authorization: Bearer <token>`.
    *   Decrypts the token to find the username. If valid, loads `UserDetails` via JPA and sets the authentication object into Spring Security's `SecurityContextHolder`.
4.  **[SecurityConfig.java](file:///C:/Users/Juned%20Bankapur/git/repository3/SalesSavvy/src/main/java/com/example/jwtDemo/config/SecurityConfig.java)**:
    *   Defines HTTP firewall rules: disables CSRF (since JWTs are immune to CSRF cross-origin script-jacking), sets session policy to **Stateless**, permits public paths (like static HTML, CSS, `/auth/**`), and locks down `/customer/cart/**` and `/customer/payment/**` to `ROLE_USER`.

---

## 💳 Deep Dive 2: Razorpay Integration & Financial Precision

Handling money is the most critical part of an e-commerce platform. Here is how SalesSavvy does it safely and securely.

### 1. Decimal Safety (`BigDecimal` & Paise Math)
Floating-point data types (`float` and `double`) are represented in binary scientific notation (powers of 2). Because of this, they cannot precisely represent decimal numbers (powers of 10) like `0.1` or `0.7`. This leads to rounding errors (e.g., `10.00` becomes `9.9999999998`), which is completely unacceptable in banking.
*   **The Solution**: SalesSavvy uses **`BigDecimal`** for all price tracking in the database.
*   **Paise Conversion**: Payment gateways like Razorpay require amounts to be passed as integers in the lowest currency unit (e.g., paise for INR, cents for USD). We convert the `BigDecimal` Rupees value to an exact `long` in paise:
    ```java
    private long toPaise(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
    ```

### 2. Cryptographic Signature Verification (The Spoofing Shield)
When the user pays on the frontend via the Razorpay popup, Razorpay returns a `razorpay_payment_id`, `razorpay_order_id`, and `razorpay_signature`.
> [!WARNING]
> **Never trust the frontend alone.** A malicious user could intercept the browser's JavaScript, mock a "success" callback, and send a fake payment ID to our server.

*   **How we verify security**: The server computes a SHA-256 HMAC (Hash-based Message Authentication Code) using the secret key that only our server and Razorpay know.
*   **The Payload**: `serverOrderId + "|" + razorpayPaymentId`
*   **The Logic** in `RazorpayService.java`:
    ```java
    String payload = serverOrderId + "|" + razorpayPaymentId;
    Mac mac = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec = new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    mac.init(secretKeySpec);
    byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
    String generatedSignature = HexFormat.of().formatHex(digest);
    return generatedSignature.equals(razorpaySignature);
    ```
    If the `generatedSignature` matches the client-supplied signature, we guarantee the payment was genuine and successfully completed.

---

## 🛡️ Deep Dive 3: ACID Transactional Safety

In `PaymentService.java`, the checkout completion flow is annotated with **`@Transactional`**.

### What is `@Transactional`?
It is a Spring Boot annotation that wraps database operations in a single database transaction. This transaction conforms to the **ACID** properties:
1.  **Atomicity**: "All or nothing." Either every single database operation succeeds, or every single one is rolled back to its original state as if nothing happened.
2.  **Consistency**: Ensures the database moves from one valid state to another, maintaining all foreign key constraints and integrity checks.
3.  **Isolation**: Transactions execute independently without overlapping or dirty-reading half-updated data.
4.  **Durability**: Once committed, changes are permanently saved in persistent storage.

### Why is it critical in SalesSavvy?
Consider the operations inside `verifyPayment`:
1.  Retrieve the customer's cart.
2.  Create an `OrderItem` record for every cart product.
3.  **Decrement product stock** in the `products` table.
4.  Create a successful `PaymentTransaction` record.
5.  Set `PurchaseOrder` status to `PAID`.
6.  **Delete the customer's cart** (`cartItemRepository.deleteByUser`).

> [!CAUTION]
> If a network failure or database crash occurs at Step 5, and we did **not** use `@Transactional`:
> *   The customer's cart is NOT cleared, but the stock HAS been decremented.
> *   Or worse: the customer's money is taken, their cart is deleted, but the `OrderItem` creation failed. The database is now corrupted.

With `@Transactional`, if **any** step fails (even at the very end), the entire transaction rolls back. The product stock is restored, the cart remains intact, and the order is not completed. **No partial failures, zero database corruption.**

---

## 📊 Database Relationships: JPA Entity Mapping

Here is how the Hibernate relational schema is defined in your entities:

| Entity | Mapping Type | Target Entity | Foreign Key Col | Explanation |
| :--- | :--- | :--- | :--- | :--- |
| **`CartItem`** | `@ManyToOne` | `User` | `user_id` | A user can have multiple items in their cart. |
| **`CartItem`** | `@ManyToOne` | `Product` | `product_id` | Multiple users can have the same product in their carts. |
| **`PurchaseOrder`** | `@ManyToOne` | `User` | `user_id` | A user can place many separate purchase orders over time. |
| **`OrderItem`** | `@ManyToOne` | `PurchaseOrder` | `order_id` | An order consists of multiple order item rows. |
| **`OrderItem`** | `@ManyToOne` | `Product` | `product_id` | Links the purchased item to the static product details. |
| **`PaymentTransaction`** | `@OneToOne` | `PurchaseOrder` | `order_id` | An order has exactly one payment transaction record (Success/Failed). |

### 💡 Unique Constraints:
In `CartItem.java`, we define a unique constraint across two columns:
```java
@Table(
    name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
    }
)
```
This is a database-level safety net. It prevents a user from having two separate rows for the same product in their cart. If they add it again, the controller detects it and increments the existing item's quantity instead of creating a duplicate row.

---

## 💬 Interview Scripts: Pitching SalesSavvy Like a Pro

### Q1. "Tell me about your second full-stack project, SalesSavvy."
> **Script**:
> *"SalesSavvy is a complete Sales Management & E-Commerce application built with Spring Boot 3, Java 17, and MySQL. It features a lightweight, dynamic HTML5 and native JavaScript frontend. The system manages core operations including user authentication, real-time inventory tracking, shopping cart states, order history, and payment gateway integration.*
>
> *Architecturally, the application implements a fully stateless design. It secures customer and administrator endpoints using Spring Security and stateless JWT token authentication. On the database layer, it uses JPA and Hibernate. A key highlight of this project is its integration with the Razorpay Payment Gateway, featuring precise BigDecimal calculations to avoid floating-point errors, cryptographic HmacSHA256 signature verification to prevent spoofing, and strict ACID transactional controls using `@Transactional` to ensure data integrity during inventory stock deductions."*

### Q2. "How did you handle security and session management in this project?"
> **Script**:
> *"I implemented a highly scalable, stateless security model. Instead of traditional session cookies, which consume server memory and hinder horizontal scaling, I built a stateless JWT authentication filter. When a user registers or logs in, the backend encodes their credentials and role into a compact token, cryptographically signed with an HMAC-SHA256 secret key.*
>
> *On the frontend, this token is stored in localStorage. It is automatically sent with every API request inside the HTTP Authorization Bearer header. An incoming filter, the `JwtAuthenticationFilter`, intercepts the request, validates the signature, extracts the authorities, and injects them into Spring's `SecurityContextHolder`. This keeps our API secure, role-restricted, and completely stateless."*

### Q3. "Explain how you integrated the payment gateway and verified that payments were genuine."
> **Script**:
> *"I integrated the Razorpay REST API into our Spring Boot backend. When a customer initiates checkout, the backend retrieves their cart, performs a real-time stock check, and calculates the total amount using BigDecimal to ensure floating-point precision. We then convert the amount to paise—since Razorpay expects the lowest currency unit—and request an official Order ID from Razorpay using our API credentials.*
>
> *Once the customer pays through the frontend Checkout overlay, Razorpay returns a secure payment ID and a signature. To prevent malicious client spoofing, our backend performs cryptographic signature verification. We combine the order ID and payment ID and hash it using HmacSHA256 with our private API secret key. If our generated signature matches the one returned from the client, we know the transaction is genuine, and we proceed with stock deduction and order finalization."*

### Q4. "What is `@Transactional` and why was it necessary for your order processing?"
> **Script**:
> *"The `@Transactional` annotation in Spring Boot is critical for maintaining database consistency. When verifying a payment, we perform multiple related database operations: we save order details, reduce inventory stock levels, create a transaction record, and clear the user's shopping cart. If any database or network failure occurs halfway through—for example, if the cart deletion fails after stock deduction—the database would enter an inconsistent state.*
>
> *By using `@Transactional`, Spring Boot wraps all these queries inside a single database transaction. This guarantees the ACID properties: either every single query succeeds and commits, or, if a failure occurs at any point, the entire set of changes rolls back automatically. This protects our system from stock mismatches and corrupted order records."*

---

## 🛠️ Step-by-Step Installation & Run Process

To run **SalesSavvy** locally, make sure your MySQL database is active and follow these simple steps:

### 1. Database Creation
Create a new MySQL database scheme named `jwtdb` (matching `application.properties`):
```sql
CREATE DATABASE jwtdb;
```

### 2. Configure Credentials
Open `C:\Users\Juned Bankapur\git\repository3\SalesSavvy\src\main\resources\application.properties` and verify your credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jwtdb
spring.datasource.username=root
spring.datasource.password=juned09
```

### 3. Run the Backend Application
Since this is a standard Maven project, you can run it via the terminal using the Maven wrapper:
1.  Open PowerShell in the root directory:
    ```powershell
    cd C:\Users\Juned Bankapur\git\repository3\SalesSavvy
    ```
2.  Run the application:
    ```powershell
    ./mvnw spring-boot:run
    ```
3.  The server will spin up on **`http://localhost:8081`**.

### 4. Open the Interface
Because the project contains a static directory mapping inside Spring Boot, you do not need a separate frontend server!
1.  Open your browser and navigate to:
    ```
    http://localhost:8081/index.html
    ```
2.  Sign up as a customer, log in, add products to your cart, and test the checkout flow!

*Tip: The backend seeds a default administrator account upon startup via `AdminSeeder.java`. You can log in as admin at `/admin-login.html` using username `admin` and password `admin123` to add new products and manage inventory!*

---
*Good luck with your interview! Study this guide alongside your AI Placement Coach project, and you will do fantastic!*
