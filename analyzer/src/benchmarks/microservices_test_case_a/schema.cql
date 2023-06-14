DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1 } ;

CREATE TABLE testks.PRECO (
    id          BIGINT ,
    valor     	 BIGINT ,
    PRIMARY KEY (id)
);

CREATE TABLE testks.PRECO (
    id          BIGINT ,
    popularidade     	 BIGINT ,
    PRIMARY KEY (id)
);

