-- Make order_id nullable and add image_url if not exists
ALTER TABLE COMPLAINT MODIFY COLUMN order_id INT NULL;
ALTER TABLE COMPLAINT ADD COLUMN IF NOT EXISTS image_url TEXT;

-- Clear old complaints and re-insert with proper data
DELETE FROM COMPLAINT;

INSERT INTO COMPLAINT (customer_id, order_id, subject, description, status, admin_notes, image_url, created_at, resolved_at) VALUES
(1, 1, 'Wrong item delivered', 'I ordered Samsung Galaxy Buds Pro but received a different model. The packaging was also damaged when it arrived.', 'resolved', 'We apologize for the inconvenience. A replacement has been dispatched and Order #1 has been refunded to your account.', NULL, '2024-03-10 14:30:00', '2024-03-12 10:00:00'),
(1, 2, 'Delayed delivery', 'My order was supposed to arrive within 3 days but it has been 7 days and I have not received it yet. Please check the status urgently.', 'in_progress', 'Escalated to logistics team. Tracking shows item is in transit and should arrive within 2 days.', NULL, '2024-03-18 09:15:00', NULL),
(1, 3, 'Product quality issue', 'The headphones I received have a crackling sound in the left ear. This seems to be a manufacturing defect. Very disappointed.', 'open', NULL, NULL, '2024-04-01 16:45:00', NULL),
(1, 4, 'Item arrived damaged', 'The packaging was completely crushed when it arrived. The product inside has a visible crack on the casing.', 'in_progress', 'We have raised a replacement request with the seller. Please allow 2-3 business days for resolution.', NULL, '2024-04-10 11:20:00', NULL),
(1, 5, 'Wrong size delivered', 'I ordered size 42 shoes but received size 40. The invoice also shows size 42 so this is a packing error on the seller side.', 'open', NULL, NULL, '2024-04-20 15:45:00', NULL),
(2, 1, 'Product not as described', 'The headphones I received do not match the product description. The noise cancellation feature does not work at all.', 'resolved', 'Refund of Rs.8999 has been processed. It will reflect in your account within 5-7 business days.', NULL, '2024-03-25 09:30:00', '2024-03-28 14:00:00'),
(2, 2, 'Delivery took too long', 'My order was placed on March 1st and was supposed to arrive in 3 days. It arrived on March 12th, 9 days late.', 'resolved', 'We apologize for the delay. A coupon code SORRY50 for Rs.50 off has been added to your account.', NULL, '2024-03-12 18:00:00', '2024-03-14 10:00:00');
