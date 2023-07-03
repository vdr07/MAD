CREATE TABLE base_entity_impl (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  PRIMARY KEY (id)
);

CREATE TABLE sys_modify_logs (
  id VARCHAR(32),
  creator BIGINT,
  createDate VARCHAR(32),
  modifier BIGINT,
  modifiedDate VARCHAR(32),
  PRIMARY KEY (id)
);

CREATE TABLE sys_authorities (
  id BIGINT,
  authority VARCHAR(32),
  PRIMARY KEY (id)
);

CREATE TABLE sys_users (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  name VARCHAR(32),
  username VARCHAR(32),
  password VARCHAR(32),
  money INT,
  PRIMARY KEY (id)
); 

CREATE TABLE sys_user_authority (
  sysUserId BIGINT,
  sysAuthorityId BIGINT,
  PRIMARY KEY (sysUserId, sysAuthorityId)
);

CREATE TABLE buy_logs (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  resource BIGINT,
  user BIGINT,
  spending INT,
  PRIMARY KEY (id)
);

CREATE TABLE choices (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  content VARCHAR(32),
  answer BIGINT,
  question BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE choose_logs (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  exam BIGINT,
  question BIGINT,
  correct INT,
  user BIGINT,
  display INT,
  PRIMARY KEY (id)
);

CREATE TABLE choose_log_choice (
  chooseLogId BIGINT,
  choiceId BIGINT,
  PRIMARY KEY (chooseLogId, choiceId)
);

CREATE TABLE exams (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  name VARCHAR(32),
  description VARCHAR(32),
  time INT,
  exampaper BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE exampapers (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  name VARCHAR(32),
  description VARCHAR(32),
  PRIMARY KEY (id)
);

CREATE TABLE exampaper_question (
  exampaperId BIGINT,
  questionId BIGINT,
  PRIMARY KEY (exampaperId, questionId)
);

CREATE TABLE examresults (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  exam BIGINT,
  user BIGINT,
  allCount INT,
  wrongCount INT,
  grade INT,
  rank INT,
  PRIMARY KEY (id)
);

CREATE TABLE questions (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  type VARCHAR(32),
  content VARCHAR(32),
  PRIMARY KEY (id)
);

CREATE TABLE question_comments (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  user BIGINT,
  content VARCHAR(32),
  good INT,
  question BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE resources (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  name VARCHAR(32),
  description VARCHAR(32),
  fileName VARCHAR(32),
  fileSize BIGINT,
  filePath VARCHAR(32),
  price INT,
  downloadTimes INT,
  PRIMARY KEY (id)
);

CREATE TABLE resource_comments (
  id BIGINT,
  sysModifyLog VARCHAR(32),
  user BIGINT,
  content VARCHAR(32),
  good INT,
  resource BIGINT,
  PRIMARY KEY (id)
);

ALTER TABLE base_entity_impl  ADD CONSTRAINT fkey_base_entity_impl_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE sys_modify_logs  ADD CONSTRAINT fkey_sys_modify_logs_1 FOREIGN KEY(creator) REFERENCES sys_users(id);
ALTER TABLE sys_modify_logs  ADD CONSTRAINT fkey_sys_modify_logs_2 FOREIGN KEY(modifier) REFERENCES sys_users(id);
ALTER TABLE sys_users  ADD CONSTRAINT fkey_sys_users_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE sys_user_authority  ADD CONSTRAINT fkey_sys_user_authority_1 FOREIGN KEY(sysUserId) REFERENCES sys_users(id);
ALTER TABLE sys_user_authority  ADD CONSTRAINT fkey_sys_user_authority_2 FOREIGN KEY(sysAuthorityId) REFERENCES sys_authorities(id);
ALTER TABLE buy_logs  ADD CONSTRAINT fkey_buy_logs_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE buy_logs  ADD CONSTRAINT fkey_buy_logs_2 FOREIGN KEY(resource) REFERENCES resources(id);
ALTER TABLE buy_logs  ADD CONSTRAINT fkey_buy_logs_3 FOREIGN KEY(user) REFERENCES sys_users(id);
ALTER TABLE choices  ADD CONSTRAINT fkey_choices_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE choices  ADD CONSTRAINT fkey_choices_2 FOREIGN KEY(question) REFERENCES questions(id);
ALTER TABLE choose_logs  ADD CONSTRAINT fkey_choose_logs_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE choose_logs  ADD CONSTRAINT fkey_choose_logs_2 FOREIGN KEY(exam) REFERENCES exams(id);
ALTER TABLE choose_logs  ADD CONSTRAINT fkey_choose_logs_3 FOREIGN KEY(question) REFERENCES questions(id);
ALTER TABLE choose_logs  ADD CONSTRAINT fkey_choose_logs_4 FOREIGN KEY(user) REFERENCES sys_users(id);
ALTER TABLE choose_log_choice  ADD CONSTRAINT fkey_choose_log_choice_1 FOREIGN KEY(chooseLogId) REFERENCES choose_logs(id);
ALTER TABLE choose_log_choice  ADD CONSTRAINT fkey_choose_log_choice_2 FOREIGN KEY(choiceId) REFERENCES choices(id);
ALTER TABLE exams  ADD CONSTRAINT fkey_exams_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE exams  ADD CONSTRAINT fkey_exams_2 FOREIGN KEY(exampaper) REFERENCES exampapers(id);
ALTER TABLE exampapers  ADD CONSTRAINT fkey_exampapers_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE exampaper_question  ADD CONSTRAINT fkey_exampaper_question_1 FOREIGN KEY(exampaperId) REFERENCES exampapers(id);
ALTER TABLE exampaper_question  ADD CONSTRAINT fkey_exampaper_question_2 FOREIGN KEY(questionId) REFERENCES questions(id);
ALTER TABLE examresults  ADD CONSTRAINT fkey_examresults_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE examresults  ADD CONSTRAINT fkey_examresults_2 FOREIGN KEY(exam) REFERENCES exams(id);
ALTER TABLE examresults  ADD CONSTRAINT fkey_examresults_3 FOREIGN KEY(user) REFERENCES sys_users(id);
ALTER TABLE questions  ADD CONSTRAINT fkey_questions_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE question_comments  ADD CONSTRAINT fkey_question_comments_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE question_comments  ADD CONSTRAINT fkey_question_comments_2 FOREIGN KEY(user) REFERENCES sys_users(id);
ALTER TABLE question_comments  ADD CONSTRAINT fkey_question_comments_3 FOREIGN KEY(question) REFERENCES questions(id);
ALTER TABLE resources  ADD CONSTRAINT fkey_resources_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE resource_comments  ADD CONSTRAINT fkey_resource_comments_1 FOREIGN KEY(sysModifyLog) REFERENCES sys_modify_logs(id);
ALTER TABLE resource_comments  ADD CONSTRAINT fkey_resource_comments_2 FOREIGN KEY(user) REFERENCES sys_users(id);
ALTER TABLE resource_comments  ADD CONSTRAINT fkey_resource_comments_3 FOREIGN KEY(resource) REFERENCES resources(id);
