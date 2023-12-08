create table supplier (
    suppid INT,
    name VARCHAR(80),
    status VARCHAR(2),
    addr1 VARCHAR(80),
    addr2 VARCHAR(80),
    city VARCHAR(80),
    state VARCHAR(80),
    zip VARCHAR(5),
    phone VARCHAR(80),
    PRIMARY KEY (suppid)
);

create table signon (
    username VARCHAR(25),
    password VARCHAR(25) ,
    PRIMARY KEY (username)
);

create table account (
    userid VARCHAR(80),
    email VARCHAR(80),
    firstname VARCHAR(80),
    lastname VARCHAR(80),
    status VARCHAR(2),
    addr1 VARCHAR(80),
    addr2 VARCHAR(40),
    city VARCHAR(80),
    state VARCHAR(80),
    zip VARCHAR(20),
    country VARCHAR(20),
    phone VARCHAR(80),
    PRIMARY KEY (userid)
);

create table profile (
    userid VARCHAR(80),
    langpref VARCHAR(80),
    favcategory VARCHAR(30),
    mylistopt INT,
    banneropt INT,
    PRIMARY KEY (userid)
);

create table bannerdata (
    favcategory VARCHAR(80),
    bannername VARCHAR(255) ,
    PRIMARY KEY (favcategory)
);

create table orders (
      orderid INT,
      userid VARCHAR(80),
      orderdate VARCHAR(20),
      shipaddr1 VARCHAR(80),
      shipaddr2 VARCHAR(80),
      shipcity VARCHAR(80),
      shipstate VARCHAR(80),
      shipzip VARCHAR(20),
      shipcountry VARCHAR(20),
      billaddr1 VARCHAR(80),
      billaddr2 VARCHAR(80) ,
      billcity VARCHAR(80),
      billstate VARCHAR(80),
      billzip VARCHAR(20),
      billcountry VARCHAR(20),
      courier VARCHAR(80),
      totalprice INT,
      billtofirstname VARCHAR(80),
      billtolastname VARCHAR(80),
      shiptofirstname VARCHAR(80),
      shiptolastname VARCHAR(80),
      creditcard VARCHAR(80),
      exprdate VARCHAR(7),
      cardtype VARCHAR(80),
      locale VARCHAR(80),
      PRIMARY KEY (orderid)
);

create table orderstatus (
      orderid INT,
      linenum INT,
      timestamp VARCHAR(20),
      status VARCHAR(2),
      PRIMARY KEY (orderid, linenum)
);

create table lineitem (
      orderid INT,
      linenum INT,
      itemid VARCHAR(10),
      quantity INT,
      unitprice INT,
      PRIMARY KEY (orderid, linenum)
);

create table category (
	catid VARCHAR(10),
	name VARCHAR(80),
	descn VARCHAR(255),
	PRIMARY KEY (catid)
);

create table product (
    productid VARCHAR(10),
    category VARCHAR(10),
    name VARCHAR(80),
    descn VARCHAR(255),
    PRIMARY KEY (productid)
);

create table item (
    itemid VARCHAR(10),
    productid VARCHAR(10),
    listprice INT,
    unitcost INT,
    supplier INT,
    status VARCHAR(2),
    attr1 VARCHAR(80),
    attr2 VARCHAR(80),
    attr3 VARCHAR(80),
    attr4 VARCHAR(80),
    attr5 VARCHAR(80),
    PRIMARY KEY (itemid)
);

create table inventory (
    itemid VARCHAR(10),
    qty INT,
    PRIMARY KEY (itemid)
);

CREATE TABLE sequence
(
    name               VARCHAR(30) ,
    nextid             INT         ,
    PRIMARY KEY (name)
);


ALTER TABLE product  ADD CONSTRAINT fkey_product_1 FOREIGN KEY(category) REFERENCES CATEGORY(catid);
ALTER TABLE item  ADD CONSTRAINT fkey_item_1 FOREIGN KEY(productid) REFERENCES PRODUCT(productid);
ALTER TABLE item  ADD CONSTRAINT fkey_item_2 FOREIGN KEY(supplier) REFERENCES SUPPLIER(suppid);
