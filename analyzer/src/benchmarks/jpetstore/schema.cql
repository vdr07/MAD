DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

CREATE TABLE testks.items (
  id BIGINT,
  name VARCHAR(24),
  price INT,
  stockQuantity INT,
  PRIMARY KEY (id)
);

CREATE TABLE testks.album (
  id BIGINT,
  artist VARCHAR(24),
  etc VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.book (
  id BIGINT,
  author VARCHAR(24),
  isbn VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.movie (
  id BIGINT,
  director VARCHAR(24),
  actor VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.category (
  id BIGINT,
  name VARCHAR(24),
  parentCategory BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE testks.category_item (
  categoryId BIGINT,
  itemId BIGINT,
  PRIMARY KEY (categoryId, itemId)
);

CREATE TABLE testks.category_children (
  pId BIGINT,
  childId BIGINT,
  PRIMARY KEY (pId, childId)
);

CREATE TABLE testks.delivery (
  id BIGINT,
  orderId BIGINT,
  city VARCHAR(24),
  street VARCHAR(24),
  zipcode VARCHAR(24),
  status VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.member (
  id BIGINT,
  name VARCHAR(24),
  city VARCHAR(24),
  street VARCHAR(24),
  zipcode VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.order (
  id BIGINT,
  memberId BIGINT,
  deliveryId BIGINT,
  orderDate VARCHAR(24),
  status VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.order_item (
  id BIGINT,
  itemId BIGINT,
  orderId BIGINT,
  deliveryId BIGINT,
  price INT,
  count INT,
  PRIMARY KEY (id)
);

ALTER TABLE album  ADD CONSTRAINT fkey_album_1 FOREIGN KEY(id) REFERENCES ITEM(id);
ALTER TABLE book  ADD CONSTRAINT fkey_book_1 FOREIGN KEY(id) REFERENCES ITEM(id);
ALTER TABLE movie  ADD CONSTRAINT fkey_movie_1 FOREIGN KEY(id) REFERENCES ITEM(id);
ALTER TABLE category_item  ADD CONSTRAINT fkey_category_item_1 FOREIGN KEY(categoryId) REFERENCES CATEGORY(id);
ALTER TABLE category_item  ADD CONSTRAINT fkey_category_item_2 FOREIGN KEY(itemId) REFERENCES ITEM(id);
ALTER TABLE category_children  ADD CONSTRAINT fkey_category_children_1 FOREIGN KEY(pId) REFERENCES CATEGORY(parentCategory);
ALTER TABLE category_children  ADD CONSTRAINT fkey_category_children_2 FOREIGN KEY(childId) REFERENCES CATEGORY(id);
ALTER TABLE delivery  ADD CONSTRAINT fkey_delivery_1 FOREIGN KEY(orderId) REFERENCES ORDER(id);
ALTER TABLE order  ADD CONSTRAINT fkey_order_1 FOREIGN KEY(memberId) REFERENCES MEMBER(id);
ALTER TABLE order  ADD CONSTRAINT fkey_order_2 FOREIGN KEY(deliveryId) REFERENCES DELIVERY(id);
ALTER TABLE order_item  ADD CONSTRAINT fkey_order_item_1 FOREIGN KEY(itemId) REFERENCES ITEM(id);
ALTER TABLE order_item  ADD CONSTRAINT fkey_order_item_2 FOREIGN KEY(orderId) REFERENCES ORDER(id);
ALTER TABLE order_item  ADD CONSTRAINT fkey_order_item_3 FOREIGN KEY(deliveryId) REFERENCES DELIVERY(id);
