#!/usr/bin/env python3
"""
Data Generation Script for Benchmark
Generates 2000 categories and 100,000 items for performance testing
"""

import psycopg2
import random
import string
from datetime import datetime, timedelta

# Database configuration
DB_CONFIG = {
    'host': 'localhost',
    'port': 5432,
    'database': 'benchmark_db',
    'user': 'postgres',
    'password': 'postgres'
}

def random_string(length):
    """Generate a random string of specified length"""
    letters = string.ascii_uppercase + string.digits
    return ''.join(random.choice(letters) for _ in range(length))

def generate_categories(cursor, count=2000):
    """Generate categories"""
    print(f"Generating {count} categories...")
    categories = []
    for i in range(1, count + 1):
        code = f"CAT-{i:07d}"
        name = f"Category {i}"
        updated_at = datetime.now() - timedelta(days=random.randint(0, 365))
        categories.append((code, name, updated_at))
    
    cursor.executemany(
        "INSERT INTO category (code, name, updated_at) VALUES (%s, %s, %s)",
        categories
    )
    print(f"✓ Generated {count} categories")

def generate_items(cursor, count=100000):
    """Generate items"""
    print(f"Generating {count} items...")
    
    # Get category IDs
    cursor.execute("SELECT id FROM category")
    category_ids = [row[0] for row in cursor.fetchall()]
    
    items = []
    batch_size = 1000
    
    for i in range(1, count + 1):
        sku = f"SKU-{i:09d}"
        name = f"Item {i}"
        price = round(random.uniform(10.0, 1000.0), 2)
        stock = random.randint(0, 1000)
        category_id = random.choice(category_ids)
        updated_at = datetime.now() - timedelta(days=random.randint(0, 365))
        
        items.append((sku, name, price, stock, category_id, updated_at))
        
        # Insert in batches for performance
        if len(items) >= batch_size:
            cursor.executemany(
                """INSERT INTO item (sku, name, price, stock, category_id, updated_at) 
                   VALUES (%s, %s, %s, %s, %s, %s)""",
                items
            )
            items = []
            print(f"  Progress: {i}/{count} items")
    
    # Insert remaining items
    if items:
        cursor.executemany(
            """INSERT INTO item (sku, name, price, stock, category_id, updated_at) 
               VALUES (%s, %s, %s, %s, %s, %s)""",
            items
        )
    
    print(f"✓ Generated {count} items")

def main():
    """Main function"""
    print("=" * 60)
    print("Benchmark Data Generator")
    print("=" * 60)
    
    try:
        # Connect to database
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # Clear existing data
        print("\nClearing existing data...")
        cursor.execute("TRUNCATE TABLE item CASCADE")
        cursor.execute("TRUNCATE TABLE category CASCADE")
        print("✓ Data cleared")
        
        # Generate categories
        generate_categories(cursor, 2000)
        conn.commit()
        
        # Generate items
        generate_items(cursor, 100000)
        conn.commit()
        
        # Update statistics
        print("\nUpdating statistics...")
        cursor.execute("ANALYZE category")
        cursor.execute("ANALYZE item")
        
        # Display summary
        cursor.execute("SELECT COUNT(*) FROM category")
        category_count = cursor.fetchone()[0]
        cursor.execute("SELECT COUNT(*) FROM item")
        item_count = cursor.fetchone()[0]
        avg_items = item_count / category_count if category_count > 0 else 0
        
        print("\n" + "=" * 60)
        print("Generation Summary")
        print("=" * 60)
        print(f"Total Categories: {category_count:,}")
        print(f"Total Items: {item_count:,}")
        print(f"Average Items per Category: {avg_items:.2f}")
        print("=" * 60)
        
        cursor.close()
        conn.close()
        print("\n✓ Data generation completed successfully!")
        
    except Exception as e:
        print(f"\n✗ Error: {e}")
        raise

if __name__ == "__main__":
    main()
