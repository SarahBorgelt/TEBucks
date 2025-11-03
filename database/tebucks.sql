BEGIN TRANSACTION;

DROP TABLE IF EXISTS tebucks_user;
DROP SEQUENCE IF EXISTS seq_user_id;


CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tebucks_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
	password_hash varchar(200) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
	role varchar(20),
	CONSTRAINT PK_tebucks_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

COMMIT;
