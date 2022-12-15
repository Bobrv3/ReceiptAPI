INSERT
INTO
  discount_cards
  (id, discount_size)
VALUES
  (nextval('discount_card_seq'), 5),
  (nextval('discount_card_seq'), 3),
  (nextval('discount_card_seq'), 6),
  (nextval('discount_card_seq'), 2);

INSERT
INTO
  products
  (id, description, price, status)
VALUES
  (nextval('product_seq'), 'Milk', 1.6, null),
  (nextval('product_seq'), 'Bread', 1.45, null),
  (nextval('product_seq'), 'Meat', 7.5, null),
  (nextval('product_seq'), 'Apple', 2, 0),
  (nextval('product_seq'), 'Banana', 2.45, 0);

INSERT
INTO
  orders
  (id, discount_card_id)
VALUES
  (nextval('order_seq'), 1),
  (nextval('order_seq'), 2),
  (nextval('order_seq'), 3),
  (nextval('order_seq'), 4),
  (nextval('order_seq'), null);

  INSERT
INTO
  order_items
  (quantity, order_id, product_id)
VALUES
  (1, 1, 1),
  (6, 1, 4),
  (1, 2, 2),
  (1, 2, 3),
  (7, 3, 5),
  (1, 4, 1),
  (1, 4, 2),
  (1, 4, 5),
  (10, 5, 4);