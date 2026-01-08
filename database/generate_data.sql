-- Data Generation Script for Benchmark
-- Generates 2000 categories and 100,000 items (approximately 50 items per category)

-- Function to generate random string
CREATE OR REPLACE FUNCTION random_string(length INTEGER) RETURNS TEXT AS $$
DECLARE
  chars TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  result TEXT := '';
  i INTEGER;
BEGIN
  FOR i IN 1..length LOOP
    result := result || substr(chars, floor(random() * length(chars) + 1)::INTEGER, 1);
  END LOOP;
  RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Generate 2000 categories
INSERT INTO category (code, name, updated_at)
SELECT 
    'CAT-' || LPAD((row_number() OVER ())::TEXT, 7, '0') AS code,
    'Category ' || (row_number() OVER ())::TEXT AS name,
    NOW() - (random() * INTERVAL '365 days') AS updated_at
FROM generate_series(1, 2000);

-- Generate 100,000 items (approximately 50 per category)
INSERT INTO item (sku, name, price, stock, category_id, updated_at)
SELECT 
    'SKU-' || LPAD((row_number() OVER ())::TEXT, 9, '0') AS sku,
    'Item ' || (row_number() OVER ())::TEXT AS name,
    ROUND((10 + random() * 990)::NUMERIC, 2) AS price,
    FLOOR(random() * 1000)::INTEGER AS stock,
    (FLOOR(random() * 2000)::BIGINT + 1) AS category_id,
    NOW() - (random() * INTERVAL '365 days') AS updated_at
FROM generate_series(1, 100000);

-- Update statistics for query planner
ANALYZE category;
ANALYZE item;

-- Display summary
SELECT 
    (SELECT COUNT(*) FROM category) AS total_categories,
    (SELECT COUNT(*) FROM item) AS total_items,
    (SELECT COUNT(*)::NUMERIC / NULLIF((SELECT COUNT(*) FROM category), 0) FROM item) AS avg_items_per_category;
