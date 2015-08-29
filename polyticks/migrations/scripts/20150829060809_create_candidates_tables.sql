--// create candidates tables
-- Migration SQL that makes the change goes here.

CREATE TABLE candidates(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	survey_id UUID NOT NULL,
	full_name VARCHAR(256) NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (survey_id) REFERENCES surveys(id)
);

CREATE TABLE candidates_responses(
	id UUID NOT NULL DEFAULT uuid_generate_v4(),
	question_id UUID NOT NULL,
	answer_id UUID NOT NULL,
	candidate_id UUID NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY (question_id) REFERENCES questions(id),
	FOREIGN KEY (answer_id) REFERENCES answers(id),
	FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

--//@UNDO
-- SQL to undo the change goes here.

DROP TABLE candidates_responses;
DROP TABLE candidates;
