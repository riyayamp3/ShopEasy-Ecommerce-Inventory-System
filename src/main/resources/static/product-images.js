// Shared product image map — keyed by product_id from the database
// Each URL is a relevant Unsplash photo matching the actual product
// How it works: product IDs are assigned sequentially in the DB (1-31).
// Each ID maps to a hardcoded Unsplash CDN URL. The ?w= param controls
// the image width served — smaller = faster load. No API key needed.

const PRODUCT_IMAGES = {
    // ── Electronics (IDs 1–8) ──
    1:  'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&q=80',  // Samsung Galaxy Buds Pro — in-ear earbuds
    2:  'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80',  // Sony WH-1000XM5 — over-ear headphones
    3:  'https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=500&q=80',  // Apple AirPods Pro — airpods in charging case
    4:  'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=500&q=80',  // boAt Rockerz 450 — on-ear bluetooth headphones
    5:  'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&q=80',  // Realme Buds Wireless 2 — neckband earbuds
    6:  'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&q=80',  // LG 32-inch 4K Monitor — desktop monitor
    7:  'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500&q=80',  // Logitech MX Master 3S — wireless mouse
    8:  'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=500&q=80',  // Philips 65W Fast Charger — USB-C charger

    // ── Footwear (IDs 9–13) ──
    9:  'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80',     // Nike Air Max 270 — red Nike running shoe
    10: 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=500&q=80',  // Adidas Ultraboost 22 — Adidas running shoe
    11: 'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=500&q=80',  // Puma Softride Enzo — white sneaker side view
    12: 'https://images.unsplash.com/photo-1449505278894-297fdb3edbc1?w=500&q=80',  // Bata Comfit Formal — brown leather formal shoe
    13: 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=500&q=80',     // Skechers Go Walk 6 — casual walking shoe

    // ── Kitchen (IDs 14–18) ──
    14: 'https://images.unsplash.com/photo-1585515320310-259814833e62?w=500&q=80',  // Prestige Induction Cooktop — induction stove
    15: 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=500&q=80',     // Instant Pot Duo 7-in-1 — electric pressure cooker
    16: 'https://images.unsplash.com/photo-1648170645898-e4e5e5e5e5e5?w=500&q=80',  // Philips Air Fryer — air fryer appliance (fallback to kitchen)
    17: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&q=80',  // Milton Thermosteel Flask — steel water bottle
    18: 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=500&q=80',     // Borosil Glass Mixing Bowl Set — glass bowls

    // ── Sports (IDs 19–22) ──
    19: 'https://images.unsplash.com/photo-1617083934551-ac1f3b4b7b4e?w=500&q=80',  // Cosco Badminton Racket Set — badminton racket
    20: 'https://images.unsplash.com/photo-1617083934551-ac1f3b4b7b4e?w=500&q=80',  // Yonex Astrox 88S Pro — professional badminton racket
    21: 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=500&q=80',  // Nivia Football Size 5 — football/soccer ball
    22: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=500&q=80',     // Decathlon Yoga Mat 8mm — rolled yoga mat

    // ── Books (IDs 23–25) ──
    23: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500&q=80',  // Atomic Habits — stack of books
    24: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500&q=80',  // The Psychology of Money — open book on table
    25: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&q=80',     // Rich Dad Poor Dad — book on shelf

    // ── Home Decor (IDs 26–28) ──
    26: 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500&q=80',  // Wooden Photo Frame Set — picture frames on wall
    27: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&q=80',  // Scented Soy Candle Set — lit candles
    28: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=500&q=80',     // Bohemian Wall Tapestry — decorative wall hanging

    // ── Toys (IDs 29–31) ──
    29: 'https://images.unsplash.com/photo-1558060370-d644479cb6f7?w=500&q=80',     // LEGO Classic Creative Bricks — colourful LEGO pieces
    30: 'https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?w=500&q=80',  // Hot Wheels 20-Car Gift Pack — toy cars
    31: 'https://images.unsplash.com/photo-1611996575749-79a3a250f948?w=500&q=80',  // Funskool Monopoly Classic — board game on table
};

