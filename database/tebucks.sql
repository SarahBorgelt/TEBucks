--ROLLBACK;
BEGIN TRANSACTION;

DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS transfer;
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

CREATE TABLE transfer(
	transfer_id SERIAL,
	transfer_type VARCHAR(100),
	transfer_status VARCHAR(100),
	user_from_id INT,
	user_to_id INT,
	amount DECIMAL(13,2),
	CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
	CONSTRAINT FK_transfer_user_from_id FOREIGN KEY (user_from_id) REFERENCES tebucks_user(user_id),
	CONSTRAINT FK_transfer_user_to_id FOREIGN KEY (user_to_id) REFERENCES tebucks_user(user_id),
	CHECK (amount>=0),
	CHECK (user_to_id <> user_from_id)
);

CREATE TABLE account(
	account_id SERIAL NOT NULL,
	user_id INT NOT NULL,
	balance DECIMAL(13,2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY(account_id),
	CONSTRAINT FK_account_tebucks_user FOREIGN KEY (user_id) REFERENCES tebucks_user(user_id)
);

INSERT INTO transfer (transfer_status) VALUES('Pending');
INSERT INTO transfer (transfer_status) VALUES('Approved');
INSERT INTO transfer (transfer_status) VALUES('Rejected');

INSERT INTO transfer(transfer_type) VALUES('Deposit');
INSERT INTO transfer(transfer_type) VALUES('Withdraw');

COMMIT;
