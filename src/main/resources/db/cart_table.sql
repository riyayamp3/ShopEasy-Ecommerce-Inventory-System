-- Run this to add the cart persistence table
CREATE TABLE IF NOT EXISTS CART_ITEM (
    cart_item_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    product_id     INT NOT NULL,
    product_name   VARCHAR(100) NOT NULL,
    category       VARCHAR(50),
    base_price     DECIMAL(10,2) NOT NULL,
    quantity       INT NOT NULL DEFAULT 1,
    image_url      VARCHAR(500),
    added_at       DATETIME,
    UNIQUE KEY uq_user_product (user_id, product_id)
);
