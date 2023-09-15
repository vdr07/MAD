DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

CREATE TABLE testks.FRIENDS_BY_PROFILE (
	profile_id		BIGINT,
	friend_id 		BIGINT,
	PRIMARY KEY ((profile_id), friend_id))
	WITH CLUSTERING ORDER BY (friend_id ASC) ;

CREATE TABLE testks.POSTS_BY_PROFILE (
	profile_id 		BIGINT, 
	post_id 		BIGINT,
	PRIMARY KEY ((profile_id), post_id))
	WITH CLUSTERING ORDER BY (post_id ASC) ;

