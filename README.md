# ShopEasy — E-Commerce Inventory Management System

A full-stack e-commerce platform built with **Spring Boot**, **MySQL**, and **vanilla HTML/CSS/JS**. ShopEasy supports three user roles — Customer, Seller, and Admin — each with a dedicated portal and workflow.
<img width="1897" height="912" alt="image" src="https://github.com/user-attachments/assets/c651a96b-03b9-41a4-b936-5eb95febe7af" /> 
<img width="1917" height="902" alt="image" src="https://github.com/user-attachments/assets/118f5329-dd3d-4fc2-9c19-73a2dfd43a4e" /> 
<img width="1916" height="908" alt="image" src="https://github.com/user-attachments/assets/04f40272-7100-4e74-b7de-6430e10b835b" />
<img width="1901" height="912" alt="image" src="https://github.com/user-attachments/assets/44b107b3-eacd-4f07-b724-5df224e2f4bd" />
Customer: 
<img width="1892" height="906" alt="image" src="https://github.com/user-attachments/assets/6a9818ca-402e-41a0-9ca6-453552ea4e4a" />
<img width="1897" height="903" alt="image" src="https://github.com/user-attachments/assets/08e02f95-c025-48e9-8a44-39765ab423c9" />
Admin: 
<img width="1917" height="902" alt="image" src="https://github.com/user-attachments/assets/9f9dd9b9-89b8-4a84-99b5-a2b8e941fd34" />
<img width="1892" height="901" alt="image" src="https://github.com/user-attachments/assets/8412b633-f5a5-4a8e-88e3-4f8ce8c135bf" />
Seller: 
<img width="1912" height="902" alt="image" src="https://github.com/user-attachments/assets/9ed4a887-fa14-4d12-8729-d9080584f9d9" />
<img width="1906" height="881" alt="image" src="https://github.com/user-attachments/assets/0fcff2e1-d0f9-41f1-8a04-3022c05489d5" />
Email: 
<img width="718" height="1382" alt="image" src="https://github.com/user-attachments/assets/b4f95f4e-706f-4eb2-83bd-9c94a929d4ca" />

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.5, Spring Data JPA, Spring Mail |
| Database | MySQL 8 |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Email | Gmail SMTP via JavaMailSender |
| Build | Maven (mvnw wrapper) |

---

## Getting Started

### Prerequisites
- Java 17
- MySQL 8 running on `localhost:3306`
- Maven (or use the included `mvnw.cmd`)

### Database Setup

1. Create the database in MySQL Workbench:
```sql
CREATE DATABASE ecommerce_db;
```

2. Run the SQL files in order:
```sql
source src/main/resources/db/schema.sql
source src/main/resources/db/seed_data.sql
source src/main/resources/db/cart_table.sql
source src/main/resources/db/notification_table.sql
```

3. Run the fix scripts:
```sql
source src/main/resources/db/fix_complaints.sql
source src/main/resources/db/seller_descriptions.sql
source src/main/resources/db/seller_reviews.sql
```

4. Add the inventory image column:
```sql
ALTER TABLE INVENTORY ADD COLUMN image_url MEDIUMTEXT;
```

5. Drop the email uniqueness constraint (allows multiple sellers per email for testing):
```sql
ALTER TABLE USER DROP INDEX email;
```

### Configuration

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=JDBC:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.mail.username=YOUR_GMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
shopeasy.mail.from=YOUR_GMAIL@gmail.com
```

To get a Gmail App Password: Google Account → Security → 2-Step Verification → App Passwords.

### Run

```bash
mvnw.cmd spring-boot:run
```

Open `http://localhost:8080` — redirects to the landing page.

---

| Role | Email / Username | Password |
|---|---|---|
| Customer | alice@example.com | alice123 |
| Admin | admin1234@gmail.com | admin123 |
| Seller | carol_white | demo123 |


---

## Project Structure

```
src/
├── main/
│   ├── java/com/ecommerce/inventory_system/
│   │   ├── controller/          # REST API controllers
│   │   ├── entity/              # JPA entities
│   │   ├── repository/          # Spring Data repositories
│   │   ├── service/             # Business logic
│   │   └── dto/                 # Data transfer objects
│   └── resources/
│       ├── static/              # Frontend HTML/CSS/JS
│       └── db/                  # SQL scripts
```

---

## Pages

| Page | URL | Access |
|---|---|---|
| Landing | `/landing.html` | Public |
| Login / Register | `/login.html` | Public |
| Shop (Home) | `/index.html` | Public / Customer |
| Product Detail | `/product.html?id=X` | Public |
| Seller Store | `/store.html?id=X` | Public |
| Cart | `/cart.html` | Customer |
| Orders | `/orders.html` | Customer |
| Profile | `/profile.html` | Customer |
| Seller Dashboard | `/seller.html` | Approved Seller |
| Admin Portal | `/admin.html` | Admin only |

---

## User Workflows

### Customer Workflow

1. **Landing Page** → Browse features, click "Start Shopping"
2. **Register** → Create account with email, username, password
3. **Login** → Redirected to shop homepage
4. **Browse** → Filter by category, search by name, sort by price
5. **Product Detail** → View product, compare seller prices, add to cart or buy now
6. **Cart** → Adjust quantities, place order (cart persists in DB across sessions)
7. **Orders** → View order history with product images, seller names, status; cancel active orders
8. **Profile** → View account stats, order history, file complaints with image attachments, track complaint status
9. **Seller Store** → Click any seller name to view their storefront, products, reviews, and about page

### Seller Workflow

1. **Register** → Fill business name, category, description, location, GST, bank details
2. **Pending** → Cannot log in until admin approves; receives email notification on approval
3. **Login** → Enter username + password → redirected to Seller Dashboard
4. **Overview** → See total products, orders, estimated revenue, store rating, recent orders, open complaints
5. **Inventory** → View all listed products with images, search/filter/sort; see stock levels and units sold
6. **Orders** → View all orders for their store, filter by status, sort by date/amount/product; click Details for full order modal with product image
7. **Complaints** → See all complaints linked to their orders; rating auto-recalculates (deducts 0.2 per unresolved complaint)
8. **Price History** → Search by product name; see full price change history with images
9. **Update Stock** → Select product from dropdown, update quantity and price
10. **List New Product** → Dropdown filtered to seller's category; set price, quantity, upload product image
11. **My Profile** → Edit business name, description, location, speciality; preview store card; link to public store
12. **Notifications** → Bell icon shows unread count; approval/rejection messages from admin

### Admin Workflow

1. **Login** → `admin1234@gmail.com` → redirected to Admin Portal (server-verified, non-admins see Access Denied)
2. **Dashboard** → Stats (products, sellers, orders, pending approvals), pending seller list, recent orders
3. **Sellers** → Search, filter by approved/pending, approve sellers (triggers email + in-app notification), view store
4. **Users** → Split view: Customers on left, Sellers & Admins on right
5. **Orders** → Search by order ID/customer/product, filter by status, sort by date/amount; click Details for modal with product image and full breakdown
6. **Products** → Search, filter by category, sort by price/name; product images in table; add new products
7. **Complaints** → All complaints with images, inline status update and admin response form
8. **Price History** → Search by product name and seller business name; results show product images

---

## Database Schema

### Tables

| Table | Description |
|---|---|
| `USER` | All users (customers, sellers, admins) |
| `SELLER` | Seller profiles linked to users |
| `PRODUCT` | Product catalogue |
| `INVENTORY` | Seller-product listings with price, stock, image |
| `ORDER` | Customer orders |
| `COMPLAINT` | Customer complaints with image support |
| `CART_ITEM` | Persistent cart (per user, synced across devices) |
| `NOTIFICATION` | In-app notifications for sellers |
| `SELLER_REVIEW` | Customer reviews per seller |
| `PRICE_HISTORY` | Price change log per product-seller pair |

---

## API Endpoints

### Users
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users/register` | Register customer or seller |
| POST | `/api/users/login` | Login (email or username) |

### Products
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | All products |
| GET | `/api/products/{id}` | Single product |
| POST | `/api/products/add` | Add product (admin) |

### Orders
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders/place` | Place order |
| GET | `/api/orders/customer/{id}` | Customer orders |
| GET | `/api/orders/customer/{id}/detailed` | Enriched orders with product/seller names |
| GET | `/api/orders/all` | All orders (admin) |
| POST | `/api/orders/{id}/cancel` | Cancel order |

### Inventory
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/inventory/available` | Available sellers for a product |
| PUT | `/api/inventory/update` | Update stock and price |
| POST | `/api/inventory/list` | List new product |

### Cart
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart/{userId}` | Get cart |
| POST | `/api/cart/add` | Add item |
| PUT | `/api/cart/update` | Update quantity |
| DELETE | `/api/cart/{userId}/{productId}` | Remove item |
| DELETE | `/api/cart/{userId}` | Clear cart |
| POST | `/api/cart/sync` | Sync localStorage cart on login |

### Complaints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/complaints/raise` | File complaint (with image) |
| GET | `/api/complaints/customer/{id}` | Customer's complaints |
| GET | `/api/complaints/all` | All complaints (admin) |
| PUT | `/api/complaints/{id}/status` | Update status + admin notes |

### Sellers (Public)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/sellers` | All approved sellers |
| GET | `/api/sellers/{id}` | Seller profile + products + reviews |

### Seller Dashboard
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/seller-dashboard/profile/by-user/{userId}` | Get seller by user ID |
| PUT | `/api/seller-dashboard/profile/{sellerId}` | Update seller profile |
| GET | `/api/seller-dashboard/inventory/{sellerId}` | Enriched inventory |
| GET | `/api/seller-dashboard/orders/{sellerId}` | Seller's orders |
| GET | `/api/seller-dashboard/complaints/{sellerId}` | Complaints for seller |
| GET | `/api/seller-dashboard/price-history/{sellerId}` | Price history by product name |
| POST | `/api/seller-dashboard/recalculate-rating/{sellerId}` | Recalculate rating |
| POST | `/api/seller-dashboard/list-product/{sellerId}` | List new product with image |

### Admin
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/verify` | Verify admin credentials |
| GET | `/api/admin/sellers` | All sellers (with user info) |
| POST | `/api/admin/sellers/{id}/approve` | Approve seller (sends email + notification) |
| GET | `/api/admin/users` | All users |
| GET | `/api/admin/price-history` | Price history by product/seller ID |

### Notifications
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notifications/{userId}` | Get notifications |
| GET | `/api/notifications/{userId}/unread-count` | Unread count |
| PUT | `/api/notifications/{userId}/mark-read` | Mark all read |

---

## Key Features

### Smart Cart Persistence
Cart items are stored in the `CART_ITEM` database table for logged-in users. When a guest adds items and then logs in, the localStorage cart is synced to the database automatically.

### Seller Approval + Email Notification
When a seller registers, their account is `pending`. The admin approves them from the admin portal. On approval, Spring Boot asynchronously sends a branded HTML email to the seller's registered address and creates an in-app notification visible in the seller dashboard bell icon.

### Rating System
Seller ratings are recalculated automatically when the seller views their complaints tab. The formula deducts 0.2 from a base of 5.0 for each unresolved complaint, with a minimum of 1.0.

### Price History Tracking
Every time a seller updates their price, a record is written to `PRICE_HISTORY`. Both the admin and seller dashboards can search this history by product name.

### Role-Based Access
- Customers see no admin or seller links
- Sellers are blocked from logging in until approved
- Admin portal verifies the user's role server-side on every page load; non-admins see an Access Denied screen
- All admin API endpoints require an `X-User-Id` header and return 403 if the user is not an admin

### Category-Filtered Product Listing
When a seller lists a new product, the product dropdown is automatically filtered to match their store's speciality category (set during registration or profile edit).
