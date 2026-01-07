CREATE TABLE events (
  eventId INT,
  hostId INT,
  eventType VARCHAR(24),
  eventTime VARCHAR(24),
  eventDate VARCHAR(24),
  eventPlace VARCHAR(24),
  PRIMARY KEY (eventId)
);

CREATE TABLE users (
  userId INT,
  username VARCHAR(24),
  password VARCHAR(24),
  role VARCHAR(24),
  phone VARCHAR(24),
  firstname VARCHAR(24),
  lastname VARCHAR(24),
  PRIMARY KEY (userId)
);

CREATE TABLE event_user (
  eventId INT,
  userId INT,
  PRIMARY KEY (eventId, userId)
);

ALTER TABLE event_user  ADD CONSTRAINT fkey_event_user_1 FOREIGN KEY(eventId) REFERENCES EVENTS(eventId);
ALTER TABLE event_user  ADD CONSTRAINT fkey_event_user_2 FOREIGN KEY(userId) REFERENCES USERS(userId);
