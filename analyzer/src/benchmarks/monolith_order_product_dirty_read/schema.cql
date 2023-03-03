DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1 } ;

CREATE TABLE testks.ORDERS (
    id 	         BIGINT,
    state        VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE testks.PRODUCTS (
    id 	         BIGINT,
    quantity        BIGINT,
    PRIMARY KEY (id)
);


