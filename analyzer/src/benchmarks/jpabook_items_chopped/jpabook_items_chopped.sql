CREATE TABLE items (
  id BIGINT,
  name VARCHAR(24),
  price INT,
  stockQuantity INT,
  PRIMARY KEY (id)
);

CREATE TABLE delivery (
  id BIGINT,
  orderId BIGINT,
  city VARCHAR(24),
  street VARCHAR(24),
  zipcode VARCHAR(24),
  status VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE member (
  id BIGINT,
  name VARCHAR(24),
  city VARCHAR(24),
  street VARCHAR(24),
  zipcode VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE orders (
  id BIGINT,
  memberId BIGINT,
  deliveryId BIGINT,
  orderDate VARCHAR(24),
  status VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE order_item (
  id BIGINT,
  itemId BIGINT,
  orderId BIGINT,
  deliveryId BIGINT,
  price INT,
  count INT,
  PRIMARY KEY (id)
);

ALTER TABLE delivery  ADD CONSTRAINT fkey_delivery_1 FOREIGN KEY(orderId) REFERENCES ORDERS(id);
ALTER TABLE orders  ADD CONSTRAINT fkey_order_1 FOREIGN KEY(memberId) REFERENCES MEMBER(id);
ALTER TABLE orders  ADD CONSTRAINT fkey_order_2 FOREIGN KEY(deliveryId) REFERENCES DELIVERY(id);
ALTER TABLE order_item  ADD CONSTRAINT fkey_order_item_1 FOREIGN KEY(itemId) REFERENCES ITEMS(id);
ALTER TABLE order_item  ADD CONSTRAINT fkey_order_item_2 FOREIGN KEY(orderId) REFERENCES ORDERS(id);
ALTER TABLE order_item  ADD CONSTRAINT fkey_order_item_3 FOREIGN KEY(deliveryId) REFERENCES DELIVERY(id);

