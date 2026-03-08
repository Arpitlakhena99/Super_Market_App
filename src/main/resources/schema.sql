CREATE TABLE categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1024)
);

CREATE TABLE customers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(50),
  address VARCHAR(1024)
);

CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  sku VARCHAR(255) NOT NULL,
  description VARCHAR(1024),
  price DECIMAL(19,2) NOT NULL,
  stock INT NOT NULL,
  category_id BIGINT,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_date TIMESTAMP,
  status VARCHAR(50),
  total_amount DECIMAL(19,2),
  customer_id BIGINT,
  CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  quantity INT NOT NULL,
  unit_price DECIMAL(19,2) NOT NULL,
  product_id BIGINT,
  order_id BIGINT,
  CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
