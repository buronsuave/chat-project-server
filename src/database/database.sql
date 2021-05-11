CREATE DATABASE ChatMe;
USE ChatMe;

CREATE TABLE Users 
(
    id int NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    pass varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO Users
(
    name, pass
) 
VALUES 
(
    'David', 'BuronSuave03'
),
(
    'Sam', '123'
), 
(
    'Daniel', '123'
), 
(
    'Andres', '123'
);