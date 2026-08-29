-- Additional mock complaints for alice (customer_id=1) and bob (customer_id=2)
-- Run after seed_data.sql if you want more complaint examples

-- Alice's complaints (already have 3 from seed_data.sql, adding 2 more)
INSERT INTO COMPLAINT (customer_id, order_id, subject, description, status, admin_notes, image_url, created_at, resolved_at) VALUES

(1, 4, 'Item arrived damaged',
 'The packaging was completely crushed when it arrived. The product inside has a visible crack. I have attached a photo of the damage.',
 'in_progress',
 'We have raised a replacement request with the seller. Please allow 2-3 business days.',
 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80',
 '2024-04-10 11:20:00', NULL),

(1, 5, 'Wrong size delivered',
 'I ordered size 42 shoes but received size 40. The invoice also shows size 42 so this is a packing error on the seller side.',
 'open',
 NULL,
 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',
 '2024-04-20 15:45:00', NULL),

-- Bob's complaints
(2, 6, 'Product not as described',
 'The headphones I received do not match the product description. The noise cancellation feature does not work at all.',
 'resolved',
 'Refund of Rs.8999 has been processed. It will reflect in your account within 5-7 business days.',
 NULL,
 '2024-03-25 09:30:00', '2024-03-28 14:00:00'),

(2, 7, 'Delivery took too long',
 'My order was placed on March 1st and was supposed to arrive in 3 days. It arrived on March 12th, 9 days late. This caused significant inconvenience.',
 'resolved',
 'We apologize for the delay. A coupon code SORRY50 for Rs.50 off has been added to your account.',
 NULL,
 '2024-03-12 18:00:00', '2024-03-14 10:00:00');
