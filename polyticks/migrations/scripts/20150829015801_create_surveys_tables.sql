--// create surveys tables
-- Migration SQL that makes the change goes here.

CREATE TABLE surveys(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	name VARCHAR(128) NOT NULL,
	description TEXT NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE questions(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	survey_id UUID NOT NULL,
	text TEXT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (survey_id) REFERENCES surveys(id)
);

CREATE TABLE answers(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	question_id UUID NOT NULL,
	text TEXT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (question_id) REFERENCES questions(id)
);

CREATE TABLE responses(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	answer_id UUID NOT NULL,
	user_id UUID NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (answer_id) REFERENCES answers(id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE surveys_taken(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	survey_id UUID NOT NULL,
	user_id UUID NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (survey_id) REFERENCES surveys(id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	UNIQUE (survey_id, user_id)
);

CREATE TABLE surveys_owned(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	survey_id UUID NOT NULL,
	user_id UUID NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (survey_id) REFERENCES surveys(id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	UNIQUE (survey_id, user_id)
);

--//@UNDO
-- SQL to undo the change goes here.

DROP TABLE surveys_owned;
DROP TABLE surveys_taken;
DROP TABLE responses;
DROP TABLE answers;
DROP TABLE questions;
DROP TABLE surveys;
