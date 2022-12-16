INSERT
INTO
  discount_cards
  (id, discount_size, status)
VALUES
  (nextval('discount_card_seq'), 1, 0);

INSERT
INTO
  products
  (id, description, price, sale_status, status)
VALUES
  (nextval('product_seq'), 'Milk', 1.6, 1, 0),
  (nextval('product_seq'), 'Bread', 2, 0, 0);

INSERT
INTO
  orders
  (id, discount_card_id, status)
VALUES
  (nextval('order_seq'), 1, 0),
  (nextval('order_seq'), null, 0);

  INSERT
INTO
  order_items
  (quantity, order_id, product_id)
VALUES
  (6, 1, 1),
  (2, 2, 2);