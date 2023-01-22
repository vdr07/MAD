DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

CREATE TABLE testks.TOPICS {
	topic_name		text,
	course_name 			text,
	course_type 			text,
	PRIMARY KEY (topic_name)
}

CREATE TABLE testks.COURSES {
	course_name		text,
	course_type 			text,
	PRIMARY KEY (course_name)
}

