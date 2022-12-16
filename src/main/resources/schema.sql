DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS discount_cards;

DROP SEQUENCE IF EXISTS product_seq CASCADE;
DROP SEQUENCE IF EXISTS discount_card_seq CASCADE;
DROP SEQUENCE IF EXISTS order_seq CASCADE;

CREATE SEQUENCE discount_card_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE  discount_cards (
  id BIGINT NOT NULL,
  discount_size DECIMAL NOT NULL,
  status INTEGER NOT NULL,
  CONSTRAINT pk_discount_cards PRIMARY KEY (id)
);

CREATE SEQUENCE  IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE products (
  id BIGINT NOT NULL,
     description VARCHAR(16) NOT NULL,
     price DECIMAL NOT NULL,
     sale_status INTEGER,
     status INTEGER NOT NULL,
     CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE SEQUENCE order_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE  orders (
  id BIGINT NOT NULL,
 discount_card_id BIGINT,
 CONSTRAINT pk_orders PRIMARY KEY (id)
);
ALTER TABLE orders DROP CONSTRAINT IF EXISTS FK_ORDERS_ON_DISCOUNTCARD;
ALTER TABLE orders ADD CONSTRAINT FK_ORDERS_ON_DISCOUNTCARD FOREIGN KEY (discount_card_id) REFERENCES discount_cards (id);

CREATE TABLE  order_items (
  quantity INTEGER NOT NULL,
 order_id BIGINT NOT NULL,
 product_id BIGINT NOT NULL,
 CONSTRAINT pk_order_items PRIMARY KEY (order_id, product_id)
);
ALTER TABLE order_items DROP CONSTRAINT IF EXISTS FK_ORDER_ITEMS_ON_ORDER;
ALTER TABLE order_items ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);
ALTER TABLE order_items DROP CONSTRAINT IF EXISTS FK_ORDER_ITEMS_ON_PRODUCT;
ALTER TABLE order_items ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);



