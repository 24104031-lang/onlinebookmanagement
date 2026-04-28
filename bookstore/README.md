# 📚 BookVault — Online Book Management System

A full-stack Spring Boot web application for managing an online bookstore, built with:
- **Spring Boot 3.2** — REST + MVC backend
- **Spring Security** — Login, roles (Admin / Customer)
- **SQLite + JDBC Template** — Lightweight embedded database (no setup needed)
- **Thymeleaf** — Server-side HTML templates
- **Dark theme UI** — HTML5 / CSS3 / Vanilla JS

---

## 🚀 How to Run

### Prerequisites
- **Java 17+** — `java -version`
- **Maven 3.8+** — `mvn -version`

### Steps

```bash
# 1. Clone / open the project
cd online-book-management

# 2. Run with Maven wrapper
./mvnw spring-boot:run

# On Windows:
mvnw.cmd spring-boot:run

# Or with system Maven:
mvn spring-boot:run
```

### 3. Open in browser
```
http://localhost:8081
```

---

## 🔑 Demo Credentials

| Role     | User ID | Password |
|----------|---------|----------|
| Admin    | admin   | admin    |
| Customer | cust1   | pass     |

Register your own customer account at `/register`.

---

## ✨ Features

### Customer
| Feature | Description |
|---|---|
| Browse Books | Filter by category (Fiction, Tech, Academic, etc.) |
| Search | Search by title or author |
| Book Detail | View cover, rating, stock, reviews |
| Cart | Add/remove/update quantities, session-based |
| Checkout | Credit Card / UPI / Cash on Delivery |
| Order History | View all orders with live tracking bar |
| Wishlist | Save books, get stock alerts |
| Reviews | Star rating (1–5) + comment per book |

### Admin
| Feature | Description |
|---|---|
| Dashboard | Stats: books, orders, users, categories |
| Book Management | Add / Edit / Delete / Update stock |
| Order Management | View all orders, update status |
| Review Management | View all submitted reviews |
| User Management | View all registered users |

---

## 🗂 Project Structure

```
src/main/java/com/bookstore/
├── BookManagementApplication.java   # Entry point
├── config/
│   ├── DatabaseInitializer.java     # SQLite schema + seed data
│   └── SecurityConfig.java          # Spring Security setup
├── controller/
│   ├── AuthController.java          # Login / Register / Redirect
│   ├── AdminController.java         # Admin pages
│   └── CustomerController.java      # Customer pages + cart/checkout
├── model/
│   ├── Book.java
│   ├── User.java
│   ├── Order.java / OrderItem.java
│   ├── CartItem.java
│   └── Review.java
├── repository/                      # JDBC Template DAOs
│   ├── BookRepository.java
│   ├── UserRepository.java
│   ├── OrderRepository.java
│   ├── ReviewRepository.java
│   └── WishlistRepository.java
└── service/                         # Business logic
    ├── BookService.java
    ├── UserService.java
    ├── OrderService.java
    ├── ReviewService.java
    ├── WishlistService.java
    └── CustomUserDetailsService.java

src/main/resources/
├── templates/
│   ├── login.html / register.html
│   ├── layout.html                  # Shared navbar fragment
│   ├── admin/                       # Admin pages
│   └── customer/                    # Customer pages
└── static/
    ├── css/main.css                 # Dark theme CSS
    └── js/main.js                   # Interactions
```

---

## 🗄 Database

SQLite file `bookstore.db` is auto-created in the project root on first run.

Tables: `books`, `users`, `orders`, `order_items`, `reviews`, `wishlist`

To reset: delete `bookstore.db` and restart.

---

## 🛠 Configuration

`src/main/resources/application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:sqlite:bookstore.db
```

Change `8081` to any port you prefer.
