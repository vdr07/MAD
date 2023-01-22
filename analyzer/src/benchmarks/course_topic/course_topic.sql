CREATE TABLE Topics (
    topic_name        VARCHAR(50) ,
    course_name           VARCHAR(50) ,
    course_type           VARCHAR(50) ,
    PRIMARY KEY (topic_name)
);

CREATE TABLE Courses (
    course_name        VARCHAR(50) ,
    course_type           VARCHAR(50) ,
    PRIMARY KEY (course_name)
);