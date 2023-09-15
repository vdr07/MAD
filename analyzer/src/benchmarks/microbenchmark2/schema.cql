DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1 } ;

CREATE TABLE testks.Member (
    id          INT ,
    status        INT ,
    money        INT ,
    PRIMARY KEY (id)
);

CREATE TABLE testks.Item (
    id          INT ,
    price        INT ,
    stock        INT ,
    PRIMARY KEY (id)
);


