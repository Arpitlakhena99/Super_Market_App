INSERT INTO categories (id, name, description) VALUES (1, 'Beverages', 'Drinks and beverages');
INSERT INTO categories (id, name, description) VALUES (2, 'Snacks', 'Chips and snacks');

INSERT INTO products (id, name, sku, description, price, stock, category_id) VALUES (1, 'Cola', 'COLA-001', 'Cold drink', 1.50, 100, 1);
INSERT INTO products (id, name, sku, description, price, stock, category_id) VALUES (2, 'Potato Chips', 'CHIP-001', 'Crispy chips', 2.00, 50, 2);

INSERT INTO customers (id, first_name, last_name, email, phone, address) VALUES (1, 'John', 'Doe', 'john.doe@example.com', '1234567890', '123 Main St');

-- Create an order
INSERT INTO orders (id, order_date, status, total_amount, customer_id) VALUES (1, CURRENT_TIMESTAMP(), 'CREATED', 3.50, 1);

-- Create order items
INSERT INTO order_items (id, quantity, unit_price, product_id, order_id) VALUES (1, 1, 1.50, 1, 1);
INSERT INTO order_items (id, quantity, unit_price, product_id, order_id) VALUES (2, 1, 2.00, 2, 1);

