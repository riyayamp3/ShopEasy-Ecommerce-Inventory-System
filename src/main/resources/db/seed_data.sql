-- ============================================================
-- ShopEasy Seed Data
-- Run this ONCE after schema is set up
-- ============================================================

-- USERS (customers + admin + sellers)
INSERT INTO USER (username, email, password_hash, role, created_at) VALUES
('alice',   'alice@example.com',  'hashed_pass', 'customer', '2024-01-15 10:00:00'),
('bob',     'bob@example.com',    'hashed_pass', 'customer', '2024-02-20 11:30:00'),
('carol',   'carol@example.com',  'hashed_pass', 'seller',   '2024-01-10 09:00:00'),
('dave',    'dave@example.com',   'hashed_pass', 'seller',   '2024-01-12 09:00:00'),
('eve',     'eve@example.com',    'hashed_pass', 'seller',   '2024-01-14 09:00:00'),
('frank',   'frank@example.com',  'hashed_pass', 'seller',   '2024-02-01 09:00:00'),
('grace',   'grace@example.com',  'hashed_pass', 'seller',   '2024-02-05 09:00:00'),
('henry',   'henry@example.com',  'hashed_pass', 'seller',   '2024-02-10 09:00:00'),
('irene',   'irene@example.com',  'hashed_pass', 'seller',   '2024-03-01 09:00:00'),
('jack',    'jack@example.com',   'hashed_pass', 'admin',    '2024-01-01 08:00:00');

-- SELLERS (linked to seller users above)
INSERT INTO SELLER (user_id, business_name, rating, approved_status, approved_by, approved_at) VALUES
(3,  'TechZone Electronics',    4.80, 'approved', 10, '2024-01-11 10:00:00'),
(4,  'StyleHub Fashion',        4.60, 'approved', 10, '2024-01-13 10:00:00'),
(5,  'KitchenKing Supplies',    4.70, 'approved', 10, '2024-01-15 10:00:00'),
(6,  'SportsPro India',         4.50, 'approved', 10, '2024-02-02 10:00:00'),
(7,  'BookWorld Store',         4.90, 'approved', 10, '2024-02-06 10:00:00'),
(8,  'HomeDecor Paradise',      4.40, 'approved', 10, '2024-02-11 10:00:00'),
(9,  'PetCare Essentials',      4.75, 'approved', 10, '2024-03-02 10:00:00');

-- PRODUCTS (30 products across categories with varied price ranges)
INSERT INTO PRODUCT (product_name, description, category, base_price, created_at, updated_at) VALUES
-- Electronics (budget to premium)
('Samsung Galaxy Buds Pro',       'Active noise cancellation, 28hr battery, IPX7 waterproof',                    'Electronics',  8999.00,  NOW(), NOW()),
('Sony WH-1000XM5 Headphones',    'Industry-leading noise cancellation, 30hr battery, multipoint connection',    'Electronics',  29999.00, NOW(), NOW()),
('Apple AirPods Pro 2nd Gen',     'Adaptive transparency, H2 chip, MagSafe charging case',                      'Electronics',  24900.00, NOW(), NOW()),
('boAt Rockerz 450 Bluetooth',    '40hr playback, foldable design, voice assistant support',                     'Electronics',  1299.00,  NOW(), NOW()),
('Realme Buds Wireless 2',        'Magnetic charging, 17hr battery, bass boost mode',                           'Electronics',  1499.00,  NOW(), NOW()),
('LG 32-inch 4K Monitor',         'IPS panel, HDR10, USB-C connectivity, 60Hz refresh rate',                    'Electronics',  22999.00, NOW(), NOW()),
('Logitech MX Master 3S Mouse',   'Electromagnetic scroll wheel, 8K DPI, Bluetooth multi-device',               'Electronics',  9999.00,  NOW(), NOW()),
('Philips 65W Fast Charger',      'GaN technology, multi-port, compatible with all USB-C devices',              'Electronics',  2499.00,  NOW(), NOW()),

-- Footwear (budget to premium)
('Nike Air Max 270',              'Max Air unit heel, breathable mesh upper, rubber outsole',                    'Footwear',     8995.00,  NOW(), NOW()),
('Adidas Ultraboost 22',          'Boost midsole, Primeknit upper, Continental rubber outsole',                 'Footwear',     14999.00, NOW(), NOW()),
('Puma Softride Enzo',            'SoftFoam+ sockliner, lightweight mesh, everyday comfort',                    'Footwear',     3499.00,  NOW(), NOW()),
('Bata Comfit Formal Shoes',      'Genuine leather upper, cushioned insole, slip-resistant sole',               'Footwear',     2299.00,  NOW(), NOW()),
('Skechers Go Walk 6',            'Air-cooled memory foam, machine washable, lightweight',                      'Footwear',     5499.00,  NOW(), NOW()),

-- Kitchen (budget to premium)
('Prestige Induction Cooktop',    '2000W, auto-off, 7 preset menus, feather touch controls',                    'Kitchen',      3299.00,  NOW(), NOW()),
('Instant Pot Duo 7-in-1',        'Pressure cooker, slow cooker, rice cooker, steamer, saute, yogurt maker',    'Kitchen',      8999.00,  NOW(), NOW()),
('Philips Air Fryer HD9200',      '4.1L capacity, rapid air technology, 90% less fat',                         'Kitchen',      6499.00,  NOW(), NOW()),
('Milton Thermosteel Flask 1L',   'Double wall insulation, 24hr hot/cold, leak-proof lid',                      'Kitchen',      899.00,   NOW(), NOW()),
('Borosil Glass Mixing Bowl Set', 'Microwave safe, dishwasher safe, set of 3 sizes',                           'Kitchen',      1299.00,  NOW(), NOW()),

-- Sports (budget to premium)
('Cosco Badminton Racket Set',    'Aluminium frame, 2 rackets + shuttlecocks + carry bag',                      'Sports',       799.00,   NOW(), NOW()),
('Yonex Astrox 88S Pro',          'Graphite shaft, isometric head, for advanced players',                       'Sports',       12999.00, NOW(), NOW()),
('Nivia Football Size 5',         'PU material, 32-panel design, machine stitched',                             'Sports',       699.00,   NOW(), NOW()),
('Decathlon Yoga Mat 8mm',        'Non-slip surface, carry strap included, eco-friendly TPE',                   'Sports',       1299.00,  NOW(), NOW()),

-- Books (budget to premium)
('Atomic Habits by James Clear',  'Proven framework for building good habits and breaking bad ones',             'Books',        399.00,   NOW(), NOW()),
('The Psychology of Money',       'Timeless lessons on wealth, greed, and happiness by Morgan Housel',          'Books',        349.00,   NOW(), NOW()),
('Rich Dad Poor Dad',             'What the rich teach their kids about money that the poor do not',            'Books',        299.00,   NOW(), NOW()),

-- Home Decor (budget to premium)
('Wooden Photo Frame Set of 6',   'Collage wall frames, natural wood finish, multiple sizes',                   'Home Decor',   1499.00,  NOW(), NOW()),
('Scented Soy Candle Set',        'Hand-poured, 40hr burn time, lavender and vanilla scents',                   'Home Decor',   799.00,   NOW(), NOW()),
('Bohemian Wall Tapestry',        'Cotton fabric, mandala print, 150x130cm, machine washable',                  'Home Decor',   599.00,   NOW(), NOW()),

-- Toys (budget to premium)
('LEGO Classic Creative Bricks',  '900 pieces, 10 classic colors, compatible with all LEGO sets',               'Toys',         2499.00,  NOW(), NOW()),
('Hot Wheels 20-Car Gift Pack',   'Die-cast metal, 1:64 scale, assorted styles',                               'Toys',         999.00,   NOW(), NOW()),
('Funskool Monopoly Classic',     'Classic board game, 2-8 players, ages 8+',                                  'Toys',         799.00,   NOW(), NOW());

-- INVENTORY (multiple sellers per product, varied prices for sorting demo)
-- Electronics
INSERT INTO INVENTORY (product_id, seller_id, original_quantity, current_quantity, price, last_updated, version) VALUES
(1,  1, 100, 87,  8499.00,  NOW(), 1),
(1,  2, 50,  42,  8799.00,  NOW(), 1),
(2,  1, 60,  55,  28999.00, NOW(), 1),
(2,  3, 30,  28,  29499.00, NOW(), 1),
(3,  1, 80,  71,  24500.00, NOW(), 1),
(4,  1, 200, 180, 1199.00,  NOW(), 1),
(4,  4, 150, 140, 1299.00,  NOW(), 1),
(5,  2, 120, 110, 1399.00,  NOW(), 1),
(6,  1, 40,  35,  21999.00, NOW(), 1),
(7,  1, 75,  68,  9499.00,  NOW(), 1),
(8,  3, 200, 190, 2299.00,  NOW(), 1),
-- Footwear
(9,  2, 80,  72,  8495.00,  NOW(), 1),
(9,  4, 60,  55,  8995.00,  NOW(), 1),
(10, 2, 50,  44,  14499.00, NOW(), 1),
(11, 2, 100, 95,  3299.00,  NOW(), 1),
(11, 4, 80,  75,  3499.00,  NOW(), 1),
(12, 4, 150, 140, 2199.00,  NOW(), 1),
(13, 2, 90,  82,  5299.00,  NOW(), 1),
-- Kitchen
(14, 3, 60,  54,  3099.00,  NOW(), 1),
(14, 5, 40,  38,  3299.00,  NOW(), 1),
(15, 3, 45,  40,  8499.00,  NOW(), 1),
(16, 3, 70,  65,  6199.00,  NOW(), 1),
(17, 3, 200, 195, 849.00,   NOW(), 1),
(18, 5, 100, 98,  1199.00,  NOW(), 1),
-- Sports
(19, 4, 150, 145, 749.00,   NOW(), 1),
(20, 4, 30,  27,  12499.00, NOW(), 1),
(21, 4, 200, 195, 649.00,   NOW(), 1),
(22, 6, 100, 96,  1199.00,  NOW(), 1),
-- Books
(23, 5, 300, 285, 379.00,   NOW(), 1),
(24, 5, 250, 240, 329.00,   NOW(), 1),
(25, 5, 200, 195, 279.00,   NOW(), 1),
-- Home Decor
(26, 6, 80,  75,  1399.00,  NOW(), 1),
(27, 6, 120, 115, 749.00,   NOW(), 1),
(28, 6, 90,  88,  549.00,   NOW(), 1),
-- Toys
(29, 7, 60,  55,  2299.00,  NOW(), 1),
(30, 7, 100, 95,  949.00,   NOW(), 1),
(31, 7, 150, 145, 749.00,   NOW(), 1);

-- MOCK COMPLAINTS (for customer alice, userId=1)
INSERT INTO COMPLAINT (customer_id, order_id, subject, description, status, admin_notes, created_at, resolved_at) VALUES
(1, 1, 'Wrong item delivered',
 'I ordered Samsung Galaxy Buds Pro but received a different model. The packaging was also damaged.',
 'resolved',
 'We apologize for the inconvenience. A replacement has been dispatched. Order #1 has been refunded.',
 '2024-03-10 14:30:00', '2024-03-12 10:00:00'),

(1, 2, 'Delayed delivery',
 'My order was supposed to arrive within 3 days but it has been 7 days and I have not received it yet. Please check the status.',
 'in_progress',
 'Escalated to logistics team. Tracking shows item is in transit.',
 '2024-03-18 09:15:00', NULL),

(1, 3, 'Product quality issue',
 'The headphones I received have a crackling sound in the left ear. This seems to be a manufacturing defect.',
 'open',
 NULL,
 '2024-04-01 16:45:00', NULL);
