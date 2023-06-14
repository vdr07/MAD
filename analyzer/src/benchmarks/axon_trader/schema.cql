DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

CREATE TABLE testks.user_view (
  identifier VARCHAR(24),
  uname VARCHAR(24),
  username VARCHAR(24),
  upassword VARCHAR(24),
  PRIMARY KEY (identifier)
);

CREATE TABLE testks.company_view (
  identifier VARCHAR(24),
  cname VARCHAR(24),
  cvalue BIGINT,
  amountOfShares BIGINT,
  tradeStarted INT,
  PRIMARY KEY (identifier)
);

CREATE TABLE testks.order_book_view (
  identifier VARCHAR(24),
  companyIdentifier VARCHAR(24),
  companyName VARCHAR(24),
  PRIMARY KEY (identifier)
);

CREATE TABLE testks.order_view (
  jpaId BIGINT,
  identifier VARCHAR(24),
  tradeCount BIGINT,
  itemPrice BIGINT,
  userId VARCHAR(24),
  itemsRemaining BIGINT,
  otype VARCHAR(24),
  PRIMARY KEY (jpaId)
);

CREATE TABLE testks.orderentry_sell (
  orderbook_id VARCHAR(24),
  order_id BIGINT,
  PRIMARY KEY (orderbook_id, order_id)
);

CREATE TABLE testks.orderentry_buy (
  orderbook_id VARCHAR(24),
  order_id BIGINT,
  PRIMARY KEY (orderbook_id, order_id)
);

CREATE TABLE testks.item_entry (
  generatedId BIGINT,
  identifier VARCHAR(24),
  companyIdentifier VARCHAR(24),
  companyName VARCHAR(24),
  amount BIGINT,
  PRIMARY KEY (generatedId)
);

CREATE TABLE testks.portfolio_view (
  identifier VARCHAR(24),
  userIdentifier VARCHAR(24),
  userName VARCHAR(24),
  amountOfMoney BIGINT,
  reservedAmountOfMoney BIGINT,
  PRIMARY KEY (identifier)
);

CREATE TABLE testks.portfolio_item_possession (
  portfolioId VARCHAR(24),
  orderBookId VARCHAR(24),
  itemId BIGINT,
  PRIMARY KEY (portfolioId, orderBookId, itemId)
);

CREATE TABLE testks.portfolio_item_reserved (
  portfolioId VARCHAR(24),
  orderBookId VARCHAR(24),
  itemId BIGINT,
  PRIMARY KEY (portfolioId, orderBookId, itemId)
);

CREATE TABLE testks.trade_executed_view (
  generatedId BIGINT,
  tradeCount BIGINT,
  tradePrice BIGINT,
  companyName VARCHAR(24),
  orderBookId VARCHAR(24),
  PRIMARY KEY (generatedId)
);

CREATE TABLE testks.transaction_view (
  identifier VARCHAR(24),
  orderBookId VARCHAR(24),
  portfolioId VARCHAR(24),
  companyName VARCHAR(24),
  amountOfItems BIGINT,
  amountOfExecutedItems BIGINT,
  pricePerItem BIGINT,
  TransactionState INT,
  TransactionType INT,
  PRIMARY KEY (identifier)
);

ALTER TABLE orderentry_sell  ADD CONSTRAINT fkey_orderentry_sell_1 FOREIGN KEY(orderbook_id) REFERENCES order_book_view(identifier);
ALTER TABLE orderentry_sell  ADD CONSTRAINT fkey_orderentry_sell_2 FOREIGN KEY(order_id) REFERENCES order_view(jpaId);
ALTER TABLE orderentry_buy  ADD CONSTRAINT fkey_orderentry_buy_1 FOREIGN KEY(orderbook_id) REFERENCES order_book_view(identifier);
ALTER TABLE orderentry_buy  ADD CONSTRAINT fkey_orderentry_buy_2 FOREIGN KEY(order_id) REFERENCES order_view(jpaId);
ALTER TABLE portfolio_item_possession  ADD CONSTRAINT fkey_portfolio_item_possession_1 FOREIGN KEY(portfolioId) REFERENCES portfolio_view(identifier);
ALTER TABLE portfolio_item_possession  ADD CONSTRAINT fkey_portfolio_item_possession_2 FOREIGN KEY(itemId) REFERENCES item_entry(generatedId);
ALTER TABLE portfolio_item_reserved  ADD CONSTRAINT fkey_portfolio_item_reserved_1 FOREIGN KEY(portfolioId) REFERENCES portfolio_view(identifier);
ALTER TABLE portfolio_item_reserved  ADD CONSTRAINT fkey_portfolio_item_reserved_2 FOREIGN KEY(itemId) REFERENCES item_entry(generatedId);
