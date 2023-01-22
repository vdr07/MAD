DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

CREATE TABLE testks.VALUES {
	var_name		text,
	value 			BIGINT,
	PRIMARY KEY (text)
}

