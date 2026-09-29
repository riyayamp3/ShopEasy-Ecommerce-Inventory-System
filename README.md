# ShopEasy — E-Commerce Inventory Management System

A full-stack, multi-vendor e-commerce platform built with **Spring Boot**, **MySQL**, and **vanilla HTML/CSS/JS**. ShopEasy connects three kinds of users — **Customers**, **Sellers**, and **Admins** — each with a dedicated portal, and ties them together through a single REST backend covering catalogue, inventory, orders, complaints, reviews, notifications, and price tracking.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Tech Stack](#3-tech-stack)
4. [Applications](#4-applications)
5. [Core Workflow](#5-core-workflow)
6. [Features](#6-features)
7. [Database Schema](#7-database-schema)
8. [API Reference](#8-api-reference)
9. [Authentication & Access Control](#9-authentication--access-control)
10. [Getting Started](#10-getting-started)
11. [Project Structure](#11-project-structure)
12. [Known Limitations](#12-known-limitations)

---

## 1. Project Overview

ShopEasy is a marketplace where many sellers can list the same product at different prices, and customers pick the offer they want.

- **Customers** browse the catalogue, compare seller prices for a product, keep a persistent cart, place and cancel orders, leave seller reviews, and file complaints with image evidence.
- **Sellers** register a business, wait for admin approval, then manage inventory, pricing, orders, complaints, and a public storefront — with every price change logged automatically.
- **Admins** approve sellers (triggering an email and in-app notification), manage the product catalogue, monitor all orders, resolve complaints, and audit price history.

The central design idea is the split between **`PRODUCT`** (what an item *is*) and **`INVENTORY`** (who sells it, at what price, with how much stock). This is what makes multi-seller price comparison, per-seller price history, and seller-scoped dashboards possible.

---

## 2. System Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                         CLIENTS (static pages)                       │
│                                                                      │
│   Customer Pages          Seller Dashboard          Admin Portal     │
│   index / product /       seller.html               admin.html       │
│   cart / orders /                                                    │
│   profile / store                                                    │
│         │                       │                         │          │
└─────────┼───────────────────────┼─────────────────────────┼──────────┘
          │        fetch() — JSON over HTTP                 │
          ▼                       ▼                         ▼
┌──────────────────────────────────────────────────────────────────────┐
│                     Spring Boot 3.5 Application                      │
│                                                                      │
│   Controllers ──► Services ──► Spring Data JPA Repositories          │
│                                                                      │
│   /api/users   /api/products   /api/inventory   /api/orders          │
│   /api/cart    /api/complaints /api/sellers     /api/seller-dashboard│
│   /api/admin   /api/notifications                                    │
│                                                                      │
│   Async Mail Service (JavaMailSender) · Static file serving           │
└──────────────────┬───────────────────────────────┬───────────────────┘
                   │                               │
          ┌────────▼────────┐             ┌────────▼────────┐
          │    MySQL 8      │             │   Gmail SMTP    │
          │  ecommerce_db   │             │ (approval mail) │
          └─────────────────┘             └─────────────────┘
```

### Key Architectural Decisions

| Decision | Rationale |
|---|---|
| Single Spring Boot app serves API + frontend | Static pages live in `resources/static`, so one `spring-boot:run` starts everything |
| Layered design (Controller → Service → Repository) | Business rules (rating, approval, price logging) stay out of controllers |
| `PRODUCT` / `INVENTORY` separation | One product, many seller listings — enables price comparison and per-seller stock |
| DTOs for enriched responses | Endpoints like `/orders/customer/{id}/detailed` join product and seller names server-side so the frontend makes one call |
| Async email on seller approval | Admin's request returns immediately; SMTP latency doesn't block the UI |
| Cart stored in DB, synced from `localStorage` | Guests can shop without an account; cart follows the user across devices after login |
| Append-only `PRICE_HISTORY` | Every price update writes a row, giving a full audit trail instead of overwriting |
| Vanilla JS frontend | No build step; the focus of the project is the backend and data model |

---

## 3. Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Persistence | Spring Data JPA (Hibernate) |
| Database | MySQL 8 |
| Email | Spring Mail — `JavaMailSender` over Gmail SMTP |
| Frontend | HTML5, CSS3, Vanilla JavaScript (`fetch` API) |
| Client storage | `localStorage` (guest cart, session info) |
| Build | Maven (`mvnw` wrapper included) |

---

## 4. Applications

### 4.1 Customer Storefront

| Page | URL | Access | Purpose |
|---|---|---|---|
| Landing | `/landing.html` | Public | Feature overview, entry point |
| Login / Register | `/login.html` | Public | Customer & seller auth |
| Shop | `/index.html` | Public / Customer | Browse, filter, search, sort |
| Product Detail | `/product.html?id=X` | Public | Compare sellers, add to cart, buy now |
| Seller Store | `/store.html?id=X` | Public | Seller storefront, products, reviews, about |
| Cart | `/cart.html` | Customer | Adjust quantities, place order |
| Orders | `/orders.html` | Customer | Order history, cancel active orders |
| Profile | `/profile.html` | Customer | Account stats, complaints |

**Key capabilities**
- Filter by category, search by name, sort by price
- Side-by-side seller price comparison on each product
- Persistent cart that survives logout and syncs across devices
- Order history enriched with product images, seller names, and status
- Complaints with image attachments and live status tracking

### 4.2 Seller Dashboard (`/seller.html`)

Available only to **approved** sellers.

| Tab | What it does |
|---|---|
| Overview | Total products, orders, estimated revenue, store rating, recent orders, open complaints |
| Inventory | All listings with images; search / filter / sort; stock levels and units sold |
| Orders | Store orders filtered by status and sorted by date / amount / product; detail modal |
| Complaints | Complaints linked to the seller's orders; triggers rating recalculation |
| Price History | Full price-change log per product, searchable by name |
| Update Stock | Change quantity and price of an existing listing |
| List New Product | Category-filtered product dropdown, price, quantity, image upload |
| My Profile | Edit business name, description, location, speciality; preview store card |
| Notifications | Bell icon with unread count; approval / rejection messages |

### 4.3 Admin Portal (`/admin.html`)

Role is verified server-side on every page load; non-admins see an **Access Denied** screen.

| Tab | What it does |
|---|---|
| Dashboard | Products, sellers, orders, pending approvals; pending seller list; recent orders |
| Sellers | Search, filter approved / pending, approve, view store |
| Users | Split view — customers on the left, sellers & admins on the right |
| Orders | Search by order ID / customer / product; filter by status; sort; detail modal |
| Products | Search, category filter, sort; image table; add new products |
| Complaints | All complaints with images; inline status update and admin response |
| Price History | Search by product name and seller business name |

---

## 5. Core Workflow

```
1. Seller registers (business name, category, description, location, GST, bank details)
   → Seller status: PENDING — login is blocked
        │
        ▼
2. Admin approves seller from the Admin Portal
   → Seller status: APPROVED
   → Branded HTML email sent asynchronously
   → In-app notification created (seller's bell icon)
        │
        ▼
3. Seller lists products from their category
   → INVENTORY row created (price, stock, image)
   → Each later price change → new PRICE_HISTORY row
        │
        ▼
4. Customer browses → opens a product → compares seller offers
   → Adds to cart (localStorage as guest, CART_ITEM table once logged in)
   → Guest cart is synced to the DB on login
        │
        ▼
5. Customer places order
   → ORDER created against the chosen seller's inventory
   → Order appears in customer Orders, seller Orders, and admin Orders
        │
        ▼
6. (Optional) Customer cancels an active order, or files a complaint with an image
        │
        ▼
7. Admin reviews complaint → updates status + adds a response
   → Customer sees the update on their Profile page
   → Seller's rating recalculates (−0.2 per unresolved complaint, floor 1.0)
```

---

## 6. Features

### Multi-Seller Price Comparison
A single product can be stocked by many sellers. The product page lists every available seller with their price and stock (`GET /api/inventory/available`), so customers choose the offer rather than just the item.

### Smart Cart Persistence
- **Guests**: cart lives in `localStorage`
- **Logged-in users**: cart lives in the `CART_ITEM` table
- **On login**: `POST /api/cart/sync` merges the guest cart into the user's DB cart, so nothing is lost

### Seller Approval with Email + In-App Notification
New sellers start as pending and cannot log in. When an admin approves them, Spring sends a branded HTML email asynchronously and writes a row to `NOTIFICATION`, which drives the unread-count badge on the seller dashboard.

### Seller Rating System
Ratings are recalculated when a seller opens their Complaints tab (or via `POST /api/seller-dashboard/recalculate-rating/{sellerId}`):

```
rating = max(1.0, 5.0 − 0.2 × unresolved_complaints)
```

### Price History Tracking
Every stock/price update writes an entry to `PRICE_HISTORY`. Both seller and admin dashboards can search this log by product name (admins can also filter by seller), with product images shown in results.

### Complaints with Image Evidence
Customers attach images when filing a complaint. Admins see every complaint with its image, update status inline, and write a response the customer can read. Sellers see complaints tied to their own orders.

### Category-Filtered Product Listing
When listing a new product, a seller's dropdown only shows products matching their store's speciality category, keeping storefronts coherent.

### Public Seller Storefronts
Every approved seller gets a public store page with their products, customer reviews, and an about section, reachable by clicking a seller name anywhere on the site.

---

## 7. Database Schema

| Table | Description | Key relationships |
|---|---|---|
| `USER` | All accounts (customers, sellers, admins) | Role column determines portal |
| `SELLER` | Business profile, category, approval status | → `USER` |
| `PRODUCT` | Catalogue item (name, category, description) | — |
| `INVENTORY` | A seller's listing of a product: price, stock, image | → `SELLER`, → `PRODUCT` |
| `ORDER` | Customer order against a specific listing | → `USER`, → `PRODUCT`, → `SELLER` |
| `CART_ITEM` | Persistent cart line | → `USER`, → `PRODUCT` |
| `COMPLAINT` | Complaint with image, status, admin notes | → `ORDER`, → `USER` |
| `SELLER_REVIEW` | Customer review of a seller | → `SELLER`, → `USER` |
| `NOTIFICATION` | In-app message with read flag | → `USER` |
| `PRICE_HISTORY` | Append-only log of price changes | → `SELLER`, → `PRODUCT` |

```
USER ──1:1── SELLER ──1:N── INVENTORY ──N:1── PRODUCT
  │             │                                  │
  │             ├──1:N── SELLER_REVIEW             ├──1:N── PRICE_HISTORY
  │             └──1:N── NOTIFICATION (via USER)   │
  ├──1:N── ORDER ──1:N── COMPLAINT                 │
  └──1:N── CART_ITEM ──────────────────────────────┘
```

---

## 8. API Reference

All endpoints return JSON. Admin endpoints require an `X-User-Id` header and return **403** if the user is not an admin.

### Users
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users/register` | Register a customer or seller |
| POST | `/api/users/login` | Log in with email or username |

### Products
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | All products |
| GET | `/api/products/{id}` | Single product |
| POST | `/api/products/add` | Add product (admin) |

### Inventory
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/inventory/available` | Sellers offering a product |
| PUT | `/api/inventory/update` | Update stock and price (logs price history) |
| POST | `/api/inventory/list` | List a new product |

### Orders
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders/place` | Place an order |
| GET | `/api/orders/customer/{id}` | A customer's orders |
| GET | `/api/orders/customer/{id}/detailed` | Orders enriched with product and seller names |
| GET | `/api/orders/all` | All orders (admin) |
| POST | `/api/orders/{id}/cancel` | Cancel an order |

### Cart
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart/{userId}` | Get cart |
| POST | `/api/cart/add` | Add item |
| PUT | `/api/cart/update` | Update quantity |
| DELETE | `/api/cart/{userId}/{productId}` | Remove item |
| DELETE | `/api/cart/{userId}` | Clear cart |
| POST | `/api/cart/sync` | Merge `localStorage` cart on login |

### Complaints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/complaints/raise` | File a complaint (with image) |
| GET | `/api/complaints/customer/{id}` | A customer's complaints |
| GET | `/api/complaints/all` | All complaints (admin) |
| PUT | `/api/complaints/{id}/status` | Update status and admin notes |

### Sellers (Public)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/sellers` | All approved sellers |
| GET | `/api/sellers/{id}` | Seller profile, products, and reviews |

### Seller Dashboard
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/seller-dashboard/profile/by-user/{userId}` | Resolve seller from user ID |
| PUT | `/api/seller-dashboard/profile/{sellerId}` | Update seller profile |
| GET | `/api/seller-dashboard/inventory/{sellerId}` | Enriched inventory |
| GET | `/api/seller-dashboard/orders/{sellerId}` | Seller's orders |
| GET | `/api/seller-dashboard/complaints/{sellerId}` | Complaints on seller's orders |
| GET | `/api/seller-dashboard/price-history/{sellerId}` | Price history by product name |
| POST | `/api/seller-dashboard/recalculate-rating/{sellerId}` | Recalculate rating |
| POST | `/api/seller-dashboard/list-product/{sellerId}` | List new product with image |

### Admin
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/verify` | Verify admin credentials |
| GET | `/api/admin/sellers` | All sellers with user info |
| POST | `/api/admin/sellers/{id}/approve` | Approve seller (email + notification) |
| GET | `/api/admin/users` | All users |
| GET | `/api/admin/price-history` | Price history by product / seller |

### Notifications
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notifications/{userId}` | List notifications |
| GET | `/api/notifications/{userId}/unread-count` | Unread count |
| PUT | `/api/notifications/{userId}/mark-read` | Mark all as read |

### Common Responses
| Scenario | Status |
|---|---|
| Success | 200 |
| Non-admin calling an admin endpoint | 403 |
| Unapproved seller attempting login | Login rejected |

---

## 9. Authentication & Access Control

- Login accepts **email or username** plus password
- Role (`CUSTOMER`, `SELLER`, `ADMIN`) is returned on login and stored client-side to route users to the right portal
- **Customers** never see admin or seller navigation
- **Sellers** are blocked from logging in until an admin approves them
- **Admin portal** re-verifies the user's role with the server on every page load (`POST /api/admin/verify`) — non-admins get an Access Denied screen
- **Admin API** endpoints check the `X-User-Id` header against the user's role and return 403 for non-admins

---

## 10. Getting Started

### Prerequisites
- Java 17
- MySQL 8 running on `localhost:3306`
- Maven (or the included `mvnw` / `mvnw.cmd` wrapper)
- A Gmail account with an App Password (for approval emails)

### 1. Create the database
```sql
CREATE DATABASE ecommerce_db;
USE ecommerce_db;
```

### 2. Run the SQL scripts (in order)
```sql
-- Core schema and sample data
source src/main/resources/db/schema.sql
source src/main/resources/db/seed_data.sql
source src/main/resources/db/cart_table.sql
source src/main/resources/db/notification_table.sql

-- Fixes and extensions
source src/main/resources/db/fix_complaints.sql
source src/main/resources/db/seller_descriptions.sql
source src/main/resources/db/seller_reviews.sql

-- Product image column
ALTER TABLE INVENTORY ADD COLUMN image_url MEDIUMTEXT;

-- Dev only: allow several test sellers to share one email
ALTER TABLE USER DROP INDEX email;
```

### 3. Configure `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.mail.username=YOUR_GMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
shopeasy.mail.from=YOUR_GMAIL@gmail.com
```

> **Gmail App Password:** Google Account → Security → 2-Step Verification → App Passwords.
> Never commit real credentials — keep them in environment variables or an untracked local properties file.

### 4. Run
```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

Open **http://localhost:8080** — it redirects to the landing page.

### Demo Accounts (from `seed_data.sql`)

| Role | Login | Password |
|---|---|---|
| Customer | `alice@example.com` | `alice123` |
| Seller | `carol_white` | `demo123` |
| Admin | `admin1234@gmail.com` | `admin123` |

### Try the full flow
1. Register a new seller on `/login.html` → note that login is blocked
2. Log in as **admin** → Sellers → approve them → check the email and bell notification
3. Log in as the new **seller** → list a product with an image → change its price
4. Log in as **alice** → find the product, compare sellers, add to cart, place an order
5. As alice, file a complaint with an image from Profile
6. As **admin**, respond to the complaint → as the **seller**, open Complaints and watch the rating update

---

## 11. Project Structure

```
src/
└── main/
    ├── java/com/ecommerce/inventory_system/
    │   ├── controller/     # REST controllers — one per domain (orders, cart, admin, …)
    │   ├── service/        # Business logic: approval, rating, price logging, email
    │   ├── repository/     # Spring Data JPA repositories
    │   ├── entity/         # JPA entities mapped to the tables above
    │   └── dto/            # Request/response objects, enriched views
    └── resources/
        ├── static/         # Frontend: HTML pages, CSS, JS
        ├── db/             # Schema, seed data, and migration scripts
        └── application.properties
```

---

## 12. Known Limitations

This is a learning / portfolio project. Things a production version would change:

- **Auth is header-based** — admin endpoints trust the `X-User-Id` header. A real system would use Spring Security with sessions or JWT so identity can't be supplied by the client.
- **Manual SQL migrations** — scripts are run by hand in order; Flyway or Liquibase would version them automatically.
- **Images stored in the database** (`MEDIUMTEXT`) — object storage (S3, Cloudinary) with URLs in the DB would scale better.
- **Rating recalculates on view** rather than on complaint events.
- **Email uniqueness is disabled** for testing and should be restored.
