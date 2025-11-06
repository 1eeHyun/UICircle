-- =============================================================
--  Category Seed Data (UIC Marketplace)
--  Flyway migration: V2__seed_categories.sql
--  Each INSERT is idempotent (safe to rerun)
-- =============================================================

-- =======================
--  PARENT CATEGORIES
-- =======================
INSERT INTO categories (name, parent_id)
SELECT 'Books', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Books' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Electronics & Tech', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Electronics & Tech' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Furniture & Home', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Furniture & Home' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Clothing & Fashion', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Clothing & Fashion' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Dorm & Essentials', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Dorm & Essentials' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Tickets & Passes', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Tickets & Passes' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Academic & Office', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Academic & Office' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Hobbies & Entertainment', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Hobbies & Entertainment' AND parent_id IS NULL);

INSERT INTO categories (name, parent_id)
SELECT 'Other / Miscellaneous', NULL WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name='Other / Miscellaneous' AND parent_id IS NULL);


-- =============================================================
--  CHILD CATEGORIES
-- =============================================================

-- ---------- Books ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Engineering' AS name UNION ALL
      SELECT 'Business & Economics' UNION ALL
      SELECT 'Natural Sciences' UNION ALL
      SELECT 'Social & Behavioral Sciences' UNION ALL
      SELECT 'Humanities & Communication' UNION ALL
      SELECT 'Health & Medicine' UNION ALL
      SELECT 'Education' UNION ALL
      SELECT 'Architecture / Design / Arts' UNION ALL
      SELECT 'Urban & Social Policy' UNION ALL
      SELECT 'Global / Cultural Studies' UNION ALL
      SELECT 'Math & Data' UNION ALL
      SELECT 'Languages' UNION ALL
      SELECT 'General Education' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Books' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Electronics & Tech ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Laptops / Tablets' AS name UNION ALL
      SELECT 'Monitors / Displays' UNION ALL
      SELECT 'Keyboards / Mice / Accessories' UNION ALL
      SELECT 'Headphones / Audio' UNION ALL
      SELECT 'Cameras / Photography' UNION ALL
      SELECT 'Game Consoles / VR' UNION ALL
      SELECT 'Cables / Chargers / Peripherals' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Electronics & Tech' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Furniture & Home ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Desks & Chairs' AS name UNION ALL
      SELECT 'Beds & Mattresses' UNION ALL
      SELECT 'Storage & Shelving' UNION ALL
      SELECT 'Sofas & Seating' UNION ALL
      SELECT 'Lamps & Lighting' UNION ALL
      SELECT 'Kitchen & Dining' UNION ALL
      SELECT 'Decor / Posters' UNION ALL
      SELECT 'Miscellaneous Furniture') AS sub
JOIN categories p ON p.name='Furniture & Home' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Clothing & Fashion ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Men' AS name UNION ALL
      SELECT 'Women' UNION ALL
      SELECT 'Shoes' UNION ALL
      SELECT 'Bags' UNION ALL
      SELECT 'Accessories' UNION ALL
      SELECT 'UIC Merchandise' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Clothing & Fashion' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Dorm & Essentials ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Bedding & Linens' AS name UNION ALL
      SELECT 'Cleaning Supplies' UNION ALL
      SELECT 'Kitchenware' UNION ALL
      SELECT 'Small Appliances' UNION ALL
      SELECT 'Toiletries / Personal Care' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Dorm & Essentials' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Tickets & Passes ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Concert' AS name UNION ALL
      SELECT 'Sports' UNION ALL
      SELECT 'Parking Passes' UNION ALL
      SELECT 'CTA / Metra Passes' UNION ALL
      SELECT 'Museum' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Tickets & Passes' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Academic & Office ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Calculators / Stationery' AS name UNION ALL
      SELECT 'Notebooks / Binders' UNION ALL
      SELECT 'Office Chairs / Desk Lamps' UNION ALL
      SELECT 'Art & Design Supplies' UNION ALL
      SELECT 'Lab Equipment / Tools' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Academic & Office' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Hobbies & Entertainment ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Musical Instruments' AS name UNION ALL
      SELECT 'Board Games / Cards' UNION ALL
      SELECT 'Books (Non-textbook)' UNION ALL
      SELECT 'Sports Equipment' UNION ALL
      SELECT 'Camping / Outdoors' UNION ALL
      SELECT 'Art Supplies / Crafts' UNION ALL
      SELECT 'Collectibles / Figures' UNION ALL
      SELECT 'Video Games / DVDs' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Hobbies & Entertainment' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);


-- ---------- Other / Miscellaneous ----------
INSERT INTO categories (name, parent_id)
SELECT sub.name, p.category_id
FROM (SELECT 'Lost & Found' AS name UNION ALL
      SELECT 'Free Items' UNION ALL
      SELECT 'Donations / Giveaways' UNION ALL
      SELECT 'Other') AS sub
JOIN categories p ON p.name='Other / Miscellaneous' AND p.parent_id IS NULL
WHERE NOT EXISTS (
    SELECT 1 FROM categories c WHERE c.name=sub.name AND c.parent_id=p.category_id);
