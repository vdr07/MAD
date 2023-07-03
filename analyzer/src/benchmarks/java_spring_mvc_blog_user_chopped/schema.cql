DROP KEYSPACE IF EXISTS testks ;

create KEYSPACE testks WITH replication = {'class' : 'NetworkTopologyStrategy', 'DC1':1, 'DC2':1};

-- Comment
CREATE TABLE testks.comment (
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

CREATE TABLE testks.children_comments (
  pId BIGINT,
  cId BIGINT,
  PRIMARY KEY (pId, cId)
);

-- CommentRating
CREATE TABLE testks.comment_rating (
  id BIGINT,
  userId BIGINT,
  rate INT,
  commentId BIGINT,
  PRIMARY KEY (id)
);

-- Post
CREATE TABLE testks.post (
  id BIGINT,
  title VARCHAR(24),
  shortTextPart VARCHAR(24),
  fullPostText VARCHAR(24),
  originalDateTime BIGINT,
  hide INT,
  PRIMARY KEY (id)
);

CREATE TABLE testks.posts_tags (
  postId BIGINT,
  tagId BIGINT,
  PRIMARY KEY (postId, tagId)
);

-- PostRating
CREATE TABLE testks.post_rating (
  id BIGINT,
  userId BIGINT,
  rate INT,
  postId BIGINT,
  PRIMARY KEY (id)
);

-- Role
CREATE TABLE testks.role (
  id BIGINT,
  rname VARCHAR(24),
  PRIMARY KEY (id)
);

-- Tag
CREATE TABLE testks.tag (
  id BIGINT,
  tname VARCHAR(24),
  PRIMARY KEY (id)
);

-- User
CREATE TABLE testks.user (
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

CREATE TABLE testks.users_roles (
  userId BIGINT,
  roleId BIGINT,
  PRIMARY KEY (userId, roleId)
);

ALTER TABLE comment  ADD CONSTRAINT fkey_comment_1 FOREIGN KEY(userId) REFERENCES USER(id);
ALTER TABLE comment  ADD CONSTRAINT fkey_comment_2 FOREIGN KEY(postId) REFERENCES POST(id);
ALTER TABLE children_comments  ADD CONSTRAINT fkey_children_comments_1 FOREIGN KEY(pId) REFERENCES COMMENT(pCommentId);
ALTER TABLE children_comments  ADD CONSTRAINT fkey_children_comments_2 FOREIGN KEY(cId) REFERENCES COMMENT(id);
ALTER TABLE comment_rating  ADD CONSTRAINT fkey_comment_rating_1 FOREIGN KEY(userId) REFERENCES USER(id);
ALTER TABLE comment_rating  ADD CONSTRAINT fkey_comment_rating_2 FOREIGN KEY(commentId) REFERENCES COMMENT(id);
ALTER TABLE posts_tags  ADD CONSTRAINT fkey_posts_tags_1 FOREIGN KEY(postId) REFERENCES POST(id);
ALTER TABLE posts_tags  ADD CONSTRAINT fkey_posts_tags_2 FOREIGN KEY(tagId) REFERENCES TAG(id);
ALTER TABLE post_rating  ADD CONSTRAINT fkey_post_rating_1 FOREIGN KEY(userId) REFERENCES USER(id);
ALTER TABLE post_rating  ADD CONSTRAINT fkey_post_rating_2 FOREIGN KEY(postId) REFERENCES POST(id);
ALTER TABLE users_roles  ADD CONSTRAINT fkey_users_roles_1 FOREIGN KEY(userId) REFERENCES USER(id);
ALTER TABLE users_roles  ADD CONSTRAINT fkey_users_roles_2 FOREIGN KEY(roleId) REFERENCES ROLE(id);
