USE ecommerce_db;

-- Create reviews table
CREATE TABLE IF NOT EXISTS SELLER_REVIEW (
    review_id    INT AUTO_INCREMENT PRIMARY KEY,
    seller_id    INT NOT NULL,
    customer_name VARCHAR(60) NOT NULL,
    rating       INT NOT NULL,
    review_text  TEXT NOT NULL,
    created_at   DATETIME DEFAULT NOW()
);

-- TechZone Electronics (seller_id matches business_name)
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Electronics%' OR business_name LIKE '%Tech%' OR business_name LIKE '%Gadget%' LIMIT 1), 'Arjun Mehta', 5, 'Absolutely fantastic experience. Got my Sony headphones in 2 days, packaging was perfect and the product is 100% genuine. Will definitely order again.', '2024-02-15 10:00:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Electronics%' OR business_name LIKE '%Tech%' OR business_name LIKE '%Gadget%' LIMIT 1), 'Priya Sharma', 5, 'Best electronics seller on ShopEasy. Competitive prices and super fast delivery. The Samsung buds I ordered work flawlessly.', '2024-03-01 14:30:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Electronics%' OR business_name LIKE '%Tech%' OR business_name LIKE '%Gadget%' LIMIT 1), 'Rohan Kapoor', 4, 'Good seller, genuine products. Delivery took one extra day but overall happy with the purchase.', '2024-03-20 09:15:00');

-- StyleHub Fashion / Footwear
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Fashion%' OR business_name LIKE '%Style%' OR business_name LIKE '%Footwear%' LIMIT 1), 'Sneha Patel', 5, 'Ordered Nike Air Max and they arrived in perfect condition. Exactly as described, great quality. Very happy customer!', '2024-02-20 11:00:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Fashion%' OR business_name LIKE '%Style%' OR business_name LIKE '%Footwear%' LIMIT 1), 'Vikram Singh', 5, 'Best footwear store here. Got Adidas Ultraboost at a great price. Delivery was quick and the shoes are 100% authentic.', '2024-03-10 16:45:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Fashion%' OR business_name LIKE '%Style%' OR business_name LIKE '%Footwear%' LIMIT 1), 'Ananya Reddy', 4, 'Good collection and fast shipping. The Puma shoes fit perfectly. Would recommend to anyone looking for branded footwear.', '2024-04-05 13:20:00');

-- Kitchen
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Kitchen%' LIMIT 1), 'Meera Nair', 5, 'The Instant Pot I ordered is amazing. Seller packed it really well and it arrived without any damage. Great after-sales support too.', '2024-02-28 10:30:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Kitchen%' LIMIT 1), 'Suresh Kumar', 5, 'Ordered the Philips Air Fryer. Works perfectly, delivery was on time. This seller is very reliable.', '2024-03-15 15:00:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Kitchen%' LIMIT 1), 'Deepa Iyer', 4, 'Good products at fair prices. The induction cooktop works great. Minor delay in delivery but seller communicated well.', '2024-04-01 12:00:00');

-- Sports
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Sport%' LIMIT 1), 'Rahul Verma', 5, 'Got the Yonex racket and it is exactly what I needed. Genuine product, well packed. This seller knows their sports equipment.', '2024-03-05 09:00:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Sport%' LIMIT 1), 'Kavya Menon', 5, 'Ordered yoga mat and badminton set together. Both arrived in 3 days, great quality. Highly recommend this store.', '2024-03-22 14:00:00');

-- Books
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Book%' LIMIT 1), 'Aditya Joshi', 5, 'Ordered 3 books and all arrived the next day. Prices are the best I have found anywhere. This is my go-to book store.', '2024-02-10 08:00:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Book%' LIMIT 1), 'Pooja Gupta', 5, 'Atomic Habits arrived in perfect condition, well wrapped. Fast dispatch and great packaging. 5 stars without hesitation.', '2024-03-18 11:30:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Book%' LIMIT 1), 'Nikhil Bose', 4, 'Good book seller with a wide selection. Delivery was prompt. Will order again for sure.', '2024-04-10 16:00:00');

-- Home Decor
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Home%' OR business_name LIKE '%Decor%' LIMIT 1), 'Ritu Agarwal', 5, 'The photo frames I ordered look absolutely beautiful. Quality is premium and they arrived well packed. My living room looks amazing now.', '2024-03-08 10:00:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Home%' OR business_name LIKE '%Decor%' LIMIT 1), 'Sanjay Malhotra', 4, 'Ordered the scented candle set as a gift. Lovely packaging and great fragrance. Seller even added a small thank you note.', '2024-04-02 14:30:00');

-- Toys
INSERT INTO SELLER_REVIEW (seller_id, customer_name, rating, review_text, created_at) VALUES
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Toy%' LIMIT 1), 'Neha Sharma', 5, 'Ordered LEGO set for my son and he absolutely loves it. All pieces were present, packaging was intact. Great seller!', '2024-03-12 09:30:00'),
((SELECT seller_id FROM SELLER WHERE business_name LIKE '%Toy%' LIMIT 1), 'Amit Tiwari', 5, 'Hot Wheels pack arrived quickly and my kids are thrilled. Genuine products, great prices. Will order more toys from here.', '2024-04-08 15:00:00');
