-- Benchmark Database Schema for PostgreSQL
-- Compatible with PostgreSQL 14+

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS category CASCADE;

-- Create category table
CREATE TABLE category (
   id BIGSERIAL PRIMARY KEY,
   code VARCHAR(32) UNIQUE NOT NULL,
   name VARCHAR(128) NOT NULL,
   updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create item table
CREATE TABLE item (
   id BIGSERIAL PRIMARY KEY,
   sku VARCHAR(64) UNIQUE NOT NULL,
   name VARCHAR(128) NOT NULL,
   price NUMERIC(10,2) NOT NULL,
   stock INT NOT NULL,
   category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
   updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_item_category ON item(category_id);
CREATE INDEX idx_item_updated_at ON item(updated_at);
CREATE INDEX idx_category_code ON category(code);

-- Comments for documentation
COMMENT ON TABLE category IS 'Category entity: groups multiple items';
COMMENT ON TABLE item IS 'Item entity: products with price, stock, and category reference';
COMMENT ON COLUMN category.code IS 'Unique code identifier for the category';
COMMENT ON COLUMN item.sku IS 'Stock Keeping Unit - unique identifier for the item';
