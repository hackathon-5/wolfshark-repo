--// create users tables1
-- Migration SQL that makes the change goes here.

CREATE TABLE users (
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	username VARCHAR(256) NOT NULL,
	full_name VARCHAR(256) NOT NULL,
	inserted_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE TABLE users_connections(
	user_id UUID NOT NULL,
	provider_id VARCHAR(256) NOT NULL,
	provider_user_id VARCHAR(256) NOT NULL,
	rank int not null,
	display_name VARCHAR(256),
	profile_url VARCHAR(512),
	image_url VARCHAR(255),
	access_token VARCHAR(255) NOT NULL,
	secret VARCHAR(255),
	refresh_token VARCHAR(255),
	expire_time BIGINT,
	PRIMARY KEY (user_id, provider_id, provider_user_id)
);


--//@UNDO
-- SQL to undo the change goes here.

DROP TABLE users_connections;
DROP TABLE users;
