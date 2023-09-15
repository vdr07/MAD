CREATE TABLE comments (
  id BIGINT,
  commentText VARCHAR(24),
  originalDateTime BIGINT,
  modifiedDateTime BIGINT,
  userId BIGINT,
  postId BIGINT,
  deleted INT,
  pCommentId BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE children_comment (
  pId BIGINT,
  cId BIGINT,
  PRIMARY KEY (pId, cId)
);

CREATE TABLE comment_rating (
  id BIGINT,
  userId BIGINT,
  rate INT,
  commentId BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE posts (
  id BIGINT,
  title VARCHAR(24),
  shortTextPart VARCHAR(24),
  fullPostText VARCHAR(24),
  originalDateTime BIGINT,
  hide INT,
  PRIMARY KEY (id)
);

CREATE TABLE post_tag (
  postId BIGINT,
  tagId BIGINT,
  PRIMARY KEY (postId, tagId)
);

CREATE TABLE post_rating (
  id BIGINT,
  userId BIGINT,
  rate INT,
  postId BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE roles (
  id BIGINT,
  rname VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE tags (
  id BIGINT,
  tname VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id BIGINT,
  username VARCHAR(24),
  email VARCHAR(24),
  password VARCHAR(24),
  enabled INT,
  registrationDate BIGINT,
  aboutText VARCHAR(24),
  websiteLink VARCHAR(24),
  smallAvatarLink VARCHAR(24),
  bigAvatarLink VARCHAR(24),
  PRIMARY KEY (id)
);

CREATE TABLE user_role (
  userId BIGINT,
  roleId BIGINT,
  PRIMARY KEY (userId, roleId)
);

ALTER TABLE comments  ADD CONSTRAINT fkey_comments_1 FOREIGN KEY(userId) REFERENCES USER(id);
ALTER TABLE comments  ADD CONSTRAINT fkey_comments_2 FOREIGN KEY(postId) REFERENCES POST(id);
ALTER TABLE children_comment  ADD CONSTRAINT fkey_children_comment_1 FOREIGN KEY(pId) REFERENCES COMMENTS(pCommentId);
ALTER TABLE children_comment  ADD CONSTRAINT fkey_children_comment_2 FOREIGN KEY(cId) REFERENCES COMMENTS(id);
ALTER TABLE comment_rating  ADD CONSTRAINT fkey_comment_rating_1 FOREIGN KEY(userId) REFERENCES USERS(id);
ALTER TABLE comment_rating  ADD CONSTRAINT fkey_comment_rating_2 FOREIGN KEY(commentId) REFERENCES COMMENTS(id);
ALTER TABLE post_tag  ADD CONSTRAINT fkey_post_tag_1 FOREIGN KEY(postId) REFERENCES POSTS(id);
ALTER TABLE post_tag  ADD CONSTRAINT fkey_post_tag_2 FOREIGN KEY(tagId) REFERENCES TAGS(id);
ALTER TABLE post_rating  ADD CONSTRAINT fkey_post_rating_1 FOREIGN KEY(userId) REFERENCES USERS(id);
ALTER TABLE post_rating  ADD CONSTRAINT fkey_post_rating_2 FOREIGN KEY(postId) REFERENCES POSTS(id);
ALTER TABLE user_role  ADD CONSTRAINT fkey_user_role_1 FOREIGN KEY(userId) REFERENCES USERS(id);
ALTER TABLE user_role  ADD CONSTRAINT fkey_user_role_2 FOREIGN KEY(roleId) REFERENCES ROLES(id);
