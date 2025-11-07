-- =============================================================
-- V2__seed_categories.sql
-- Category seed (idempotent) using UPSERT on category_slug
-- =============================================================

-- ---------- Parents ----------
INSERT INTO categories (name, parent_id, category_slug) VALUES
  ('Books', NULL, 'books'),
  ('Electronics & Tech', NULL, 'electronics-tech'),
  ('Furniture & Home', NULL, 'furniture-home'),
  ('Clothing & Fashion', NULL, 'clothing-fashion'),
  ('Dorm & Essentials', NULL, 'dorm-essentials'),
  ('Tickets & Passes', NULL, 'tickets-passes'),
  ('Academic & Office', NULL, 'academic-office'),
  ('Hobbies & Entertainment', NULL, 'hobbies-entertainment'),
  ('Other / Miscellaneous', NULL, 'other-miscellaneous')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

-- ---------- Books (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Engineering','books-engineering' UNION ALL
  SELECT 'Business & Economics','books-business-economics' UNION ALL
  SELECT 'Natural Sciences','books-natural-sciences' UNION ALL
  SELECT 'Social & Behavioral Sciences','books-social-behavioral-sciences' UNION ALL
  SELECT 'Humanities & Communication','books-humanities-communication' UNION ALL
  SELECT 'Health & Medicine','books-health-medicine' UNION ALL
  SELECT 'Education','books-education' UNION ALL
  SELECT 'Architecture / Design / Arts','books-architecture-design-arts' UNION ALL
  SELECT 'Urban & Social Policy','books-urban-social-policy' UNION ALL
  SELECT 'Global / Cultural Studies','books-global-cultural-studies' UNION ALL
  SELECT 'Math & Data','books-math-data' UNION ALL
  SELECT 'Languages','books-languages' UNION ALL
  SELECT 'General Education','books-general-education' UNION ALL
  SELECT 'Other','books-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'books'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Electronics & Tech (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Laptops / Tablets','electronics-tech-laptops-tablets' UNION ALL
  SELECT 'Monitors / Displays','electronics-tech-monitors-displays' UNION ALL
  SELECT 'Keyboards / Mice / Accessories','electronics-tech-keyboards-mice-accessories' UNION ALL
  SELECT 'Headphones / Audio','electronics-tech-headphones-audio' UNION ALL
  SELECT 'Cameras / Photography','electronics-tech-cameras-photography' UNION ALL
  SELECT 'Game Consoles / VR','electronics-tech-game-consoles-vr' UNION ALL
  SELECT 'Cables / Chargers / Peripherals','electronics-tech-cables-chargers-peripherals' UNION ALL
  SELECT 'Other','electronics-tech-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'electronics-tech'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Furniture & Home (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Desks & Chairs','furniture-home-desks-chairs' UNION ALL
  SELECT 'Beds & Mattresses','furniture-home-beds-mattresses' UNION ALL
  SELECT 'Storage & Shelving','furniture-home-storage-shelving' UNION ALL
  SELECT 'Sofas & Seating','furniture-home-sofas-seating' UNION ALL
  SELECT 'Lamps & Lighting','furniture-home-lamps-lighting' UNION ALL
  SELECT 'Kitchen & Dining','furniture-home-kitchen-dining' UNION ALL
  SELECT 'Decor / Posters','furniture-home-decor-posters' UNION ALL
  SELECT 'Miscellaneous Furniture','furniture-home-miscellaneous-furniture'
) v(name, slug)
JOIN categories p ON p.category_slug = 'furniture-home'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Clothing & Fashion (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Men','clothing-fashion-men' UNION ALL
  SELECT 'Women','clothing-fashion-women' UNION ALL
  SELECT 'Shoes','clothing-fashion-shoes' UNION ALL
  SELECT 'Bags','clothing-fashion-bags' UNION ALL
  SELECT 'Accessories','clothing-fashion-accessories' UNION ALL
  SELECT 'UIC Merchandise','clothing-fashion-uic-merchandise' UNION ALL
  SELECT 'Other','clothing-fashion-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'clothing-fashion'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Dorm & Essentials (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Bedding & Linens','dorm-essentials-bedding-linens' UNION ALL
  SELECT 'Cleaning Supplies','dorm-essentials-cleaning-supplies' UNION ALL
  SELECT 'Kitchenware','dorm-essentials-kitchenware' UNION ALL
  SELECT 'Small Appliances','dorm-essentials-small-appliances' UNION ALL
  SELECT 'Toiletries / Personal Care','dorm-essentials-toiletries-personal-care' UNION ALL
  SELECT 'Other','dorm-essentials-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'dorm-essentials'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Tickets & Passes (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Concert','tickets-passes-concert' UNION ALL
  SELECT 'Sports','tickets-passes-sports' UNION ALL
  SELECT 'Parking Passes','tickets-passes-parking-passes' UNION ALL
  SELECT 'CTA / Metra Passes','tickets-passes-cta-metra-passes' UNION ALL
  SELECT 'Museum','tickets-passes-museum' UNION ALL
  SELECT 'Other','tickets-passes-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'tickets-passes'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Academic & Office (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Calculators / Stationery','academic-office-calculators-stationery' UNION ALL
  SELECT 'Notebooks / Binders','academic-office-notebooks-binders' UNION ALL
  SELECT 'Office Chairs / Desk Lamps','academic-office-office-chairs-desk-lamps' UNION ALL
  SELECT 'Art & Design Supplies','academic-office-art-design-supplies' UNION ALL
  SELECT 'Lab Equipment / Tools','academic-office-lab-equipment-tools' UNION ALL
  SELECT 'Other','academic-office-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'academic-office'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Hobbies & Entertainment (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Musical Instruments','hobbies-entertainment-musical-instruments' UNION ALL
  SELECT 'Board Games / Cards','hobbies-entertainment-board-games-cards' UNION ALL
  SELECT 'Books (Non-textbook)','hobbies-entertainment-books-non-textbook' UNION ALL
  SELECT 'Sports Equipment','hobbies-entertainment-sports-equipment' UNION ALL
  SELECT 'Camping / Outdoors','hobbies-entertainment-camping-outdoors' UNION ALL
  SELECT 'Art Supplies / Crafts','hobbies-entertainment-art-supplies-crafts' UNION ALL
  SELECT 'Collectibles / Figures','hobbies-entertainment-collectibles-figures' UNION ALL
  SELECT 'Video Games / DVDs','hobbies-entertainment-video-games-dvds' UNION ALL
  SELECT 'Other','hobbies-entertainment-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'hobbies-entertainment'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;

-- ---------- Other / Miscellaneous (children) ----------
INSERT INTO categories (name, parent_id, category_slug)
SELECT v.name, p.category_id, v.slug
FROM (
  SELECT 'Lost & Found','other-miscellaneous-lost-found' UNION ALL
  SELECT 'Free Items','other-miscellaneous-free-items' UNION ALL
  SELECT 'Donations / Giveaways','other-miscellaneous-donations-giveaways' UNION ALL
  SELECT 'Other','other-miscellaneous-other'
) v(name, slug)
JOIN categories p ON p.category_slug = 'other-miscellaneous'
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = p.category_id;
