CREATE TABLE Friends_by_profile (
    profile_id      INT ,
    friend_id       INT ,
    PRIMARY KEY (profile_id, friend_id)
);



CREATE TABLE Posts_by_profile (
    profile_id          INT ,
    post_id             INT ,
    text                VARCHAR(50),
    PRIMARY KEY (profile_id, post_id)
);