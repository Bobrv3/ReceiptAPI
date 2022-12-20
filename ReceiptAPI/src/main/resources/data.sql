INSERT
INTO
  discount_cards
  (id, discount_size, status)
VALUES
  (nextval('discount_card_seq'), 5, 0),
  (nextval('discount_card_seq'), 3, 0),
  (nextval('discount_card_seq'), 6, 0),
  (nextval('discount_card_seq'), 2, 0);

INSERT
INTO
    sales
    (id, discount_size, from_quantity, status)
VALUES
    (nextval('sale_seq'), 10, 5, 0);

INSERT
INTO
  products
  (id, description, price, sale_id, status)
VALUES
  (nextval('product_seq'), 'Milk', 1.6, null, 0),
  (nextval('product_seq'), 'Bread', 1.45, null, 0),
  (nextval('product_seq'), 'Meat', 7.5, null, 0),
  (nextval('product_seq'), 'Apple', 2, 1, 0),
  (nextval('product_seq'), 'Banana', 2.45, 1, 0);

INSERT
INTO
  orders
  (id, discount_card_id, status)
VALUES
  (nextval('order_seq'), 1, 0),
  (nextval('order_seq'), null, 0),
  (nextval('order_seq'), 3, 0),
  (nextval('order_seq'), 4, 0),
  (nextval('order_seq'), null, 0);

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