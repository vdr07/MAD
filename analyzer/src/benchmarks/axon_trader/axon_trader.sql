CREATE TABLE user_view (
  identifier VARCHAR(24),
  uname VARCHAR(24),
  username VARCHAR(24),
  upassword VARCHAR(24),
  PRIMARY KEY (identifier)
);

CREATE TABLE company_view (
  identifier VARCHAR(24),
  cname VARCHAR(24),
  cvalue BIGINT,
  amountOfShares BIGINT,
  tradeStarted INT,
  PRIMARY KEY (identifier)
);

CREATE TABLE order_book_view (
  identifier VARCHAR(24),
  companyIdentifier VARCHAR(24),
  companyName VARCHAR(24),
  PRIMARY KEY (identifier)
);

CREATE TABLE order_view (
  jpaId BIGINT,
  identifier VARCHAR(24),
  tradeCount BIGINT,
  itemPrice BIGINT,
  userId VARCHAR(24),
  itemsRemaining BIGINT,
  otype VARCHAR(24),
  PRIMARY KEY (jpaId)
);

CREATE TABLE orderentry_sell (
  orderBookId VARCHAR(24),
  orderId BIGINT,
  PRIMARY KEY (orderBookId, orderId)
);

CREATE TABLE orderentry_buy (
  orderBookId VARCHAR(24),
  orderId BIGINT,
  PRIMARY KEY (orderBookId, orderId)
);

CREATE TABLE item_entry (
  generatedId BIGINT,
  identifier VARCHAR(24),
  companyIdentifier VARCHAR(24),
  companyName VARCHAR(24),
  amount BIGINT,
  PRIMARY KEY (generatedId)
);

CREATE TABLE portfolio_view (
  identifier VARCHAR(24),
  userIdentifier VARCHAR(24),
  userName VARCHAR(24),
  amountOfMoney BIGINT,
  reservedAmountOfMoney BIGINT,
  PRIMARY KEY (identifier)
);

CREATE TABLE portfolio_item_possession (
  portfolioId VARCHAR(24),
  itemIdentifier VARCHAR(24),
  PRIMARY KEY (portfolioId, itemIdentifier)
);

CREATE TABLE portfolio_item_reserved (
  portfolioId VARCHAR(24),
  itemIdentifier VARCHAR(24),
  PRIMARY KEY (portfolioId, itemIdentifier)
);

CREATE TABLE trade_executed_view (
  generatedId BIGINT,
  tradeCount BIGINT,
  tradePrice BIGINT,
  companyName VARCHAR(24),
  orderBookId VARCHAR(24),
  PRIMARY KEY (generatedId)
);

CREATE TABLE transaction_view (
  identifier VARCHAR(24),
  orderBookId VARCHAR(24),
  portfolioId VARCHAR(24),
  companyName VARCHAR(24),
  amountOfItems BIGINT,
  amountOfExecutedItems BIGINT,
  pricePerItem BIGINT,
  transactionState VARCHAR(24),
  transactionType VARCHAR(24),
  PRIMARY KEY (identifier)
);

ALTER TABLE orderentry_sell  ADD CONSTRAINT fkey_orderentry_sell_1 FOREIGN KEY(orderBookId) REFERENCES order_book_view(identifier);
ALTER TABLE orderentry_sell  ADD CONSTRAINT fkey_orderentry_sell_2 FOREIGN KEY(orderId) REFERENCES order_view(jpaId);
ALTER TABLE orderentry_buy  ADD CONSTRAINT fkey_orderentry_buy_1 FOREIGN KEY(orderBookId) REFERENCES order_book_view(identifier);
ALTER TABLE orderentry_buy  ADD CONSTRAINT fkey_orderentry_buy_2 FOREIGN KEY(orderId) REFERENCES order_view(jpaId);
ALTER TABLE portfolio_item_possession  ADD CONSTRAINT fkey_portfolio_item_possession_1 FOREIGN KEY(portfolioId) REFERENCES portfolio_view(identifier);
ALTER TABLE portfolio_item_possession  ADD CONSTRAINT fkey_portfolio_item_possession_2 FOREIGN KEY(itemIdentifier) REFERENCES item_entry(identifier);
ALTER TABLE portfolio_item_reserved  ADD CONSTRAINT fkey_portfolio_item_reserved_1 FOREIGN KEY(portfolioId) REFERENCES portfolio_view(identifier);
ALTER TABLE portfolio_item_reserved  ADD CONSTRAINT fkey_portfolio_item_reserved_2 FOREIGN KEY(itemIdentifier) REFERENCES item_entry(identifier);
