DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

CREATE TABLE testks.answers (
  id BIGINT,
  comment VARCHAR(24),
  createdAt VARCHAR(24),
  updatedAt VARCHAR(24),
  userId BIGINT,
  questionId BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE testks.questions (
  id BIGINT,
  title VARCHAR(24),
  ago VARCHAR(24),
  comment VARCHAR(24),
  userId BIGINT,
  createdAt VARCHAR(24),
  updatedAt VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE testks.question_tag (
  questionId BIGINT,
  tagId BIGINT,
  PRIMARY KEY (questionId, tagId)
);

CREATE TABLE testks.tags (
  id BIGINT,
  name VARCHAR(24),
  description VARCHAR(24),
  popular INT,
  createdAt VARCHAR(24),
  userId BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE testks.users (
  id BIGINT,
  username VARCHAR(24),
  password VARCHAR(24),
  createdAt VARCHAR(24),
  status VARCHAR(24),
  popular INT,
  PRIMARY KEY (id)
);

CREATE TABLE testks.votes (
  id BIGINT,
  module VARCHAR(24),
  questionId BIGINT,
  answerId BIGINT,
  userId BIGINT,
  mark VARCHAR(24),
  createdAt VARCHAR(24),
  status VARCHAR(24),
  popular INT,
  PRIMARY KEY (id)
);

ALTER TABLE answers  ADD CONSTRAINT fkey_answers_1 FOREIGN KEY(userId) REFERENCES USERS(id);
ALTER TABLE answers  ADD CONSTRAINT fkey_answers_2 FOREIGN KEY(questionId) REFERENCES QUESTIONS(id);
ALTER TABLE questions  ADD CONSTRAINT fkey_questions_1 FOREIGN KEY(userId) REFERENCES USERS(id);
ALTER TABLE question_tag  ADD CONSTRAINT fkey_question_tag_1 FOREIGN KEY(questionId) REFERENCES QUESTIONS(id);
ALTER TABLE question_tag  ADD CONSTRAINT fkey_question_tag_2 FOREIGN KEY(tagId) REFERENCES TAGS(id);
ALTER TABLE tags  ADD CONSTRAINT fkey_tags_1 FOREIGN KEY(userId) REFERENCES USERS(id);
ALTER TABLE votes  ADD CONSTRAINT fkey_votes_1 FOREIGN KEY(questionId) REFERENCES QUESTIONS(id);
ALTER TABLE votes  ADD CONSTRAINT fkey_votes_2 FOREIGN KEY(answerId) REFERENCES ANSWERS(id);
ALTER TABLE votes  ADD CONSTRAINT fkey_votes_3 FOREIGN KEY(userId) REFERENCES USERS(id);
