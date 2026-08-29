-- Add description column to SELLER table
ALTER TABLE SELLER ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE SELLER ADD COLUMN IF NOT EXISTS location VARCHAR(100);
ALTER TABLE SELLER ADD COLUMN IF NOT EXISTS speciality VARCHAR(100);

-- Update each seller with real details
UPDATE SELLER SET 
    description = 'Premium electronics retailer specializing in audio equipment, monitors, and accessories. Authorized reseller for Samsung, Sony, and LG products. All products come with manufacturer warranty.',
    location = 'Mumbai, Maharashtra',
    speciality = 'Audio & Electronics'
WHERE seller_id = 1;

UPDATE SELLER SET 
    description = 'Your one-stop destination for branded footwear and fashion accessories. We stock the latest collections from Nike, Adidas, Puma, and Skechers. Genuine products with 30-day return policy.',
    location = 'Delhi, NCR',
    speciality = 'Footwear & Fashion'
WHERE seller_id = 2;

UPDATE SELLER SET 
    description = 'Trusted kitchen appliance and cookware supplier since 2020. We carry Prestige, Philips, Instant Pot, and Milton products. Fast delivery and excellent after-sales support.',
    location = 'Bangalore, Karnataka',
    speciality = 'Kitchen & Appliances'
WHERE seller_id = 3;

UPDATE SELLER SET 
    description = 'Sports equipment and fitness gear for every level of athlete. From beginner badminton sets to professional Yonex rackets. We also stock yoga mats, footballs, and gym accessories.',
    location = 'Chennai, Tamil Nadu',
    speciality = 'Sports & Fitness'
WHERE seller_id = 4;

UPDATE SELLER SET 
    description = 'India largest online bookstore with over 10,000 titles. We specialize in bestsellers, self-help, business, and academic books. Same-day dispatch on all orders placed before 2 PM.',
    location = 'Pune, Maharashtra',
    speciality = 'Books & Stationery'
WHERE seller_id = 5;

UPDATE SELLER SET 
    description = 'Curated home decor and lifestyle products to make your space beautiful. We source unique pieces including wall art, photo frames, candles, and decorative items from artisans across India.',
    location = 'Jaipur, Rajasthan',
    speciality = 'Home Decor & Lifestyle'
WHERE seller_id = 6;

UPDATE SELLER SET 
    description = 'Premium pet care products and toys for your furry friends. We stock LEGO, Hot Wheels, board games, and educational toys. All toys are safety certified and age-appropriate.',
    location = 'Hyderabad, Telangana',
    speciality = 'Toys & Games'
WHERE seller_id = 7;
