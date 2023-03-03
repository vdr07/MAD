DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1 } ;

CREATE TABLE testks.X (
    id 	         BIGINT,
    value        BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE testks.Y (
    id 	         BIGINT,
    value        BIGINT,
    PRIMARY KEY (id)
);


