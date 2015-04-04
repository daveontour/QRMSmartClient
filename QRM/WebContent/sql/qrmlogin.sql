CREATE DATABASE IF NOT EXISTS qrmlogin;
USE qrmlogin;

CREATE TABLE processemail (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    toName varchar(200) ,
    toEmail varchar(1000) ,
    additionalUsers varchar(2000) DEFAULT NULL,
    emailTitle varchar(200) NOT NULL,
    emailType varchar(45) NOT NULL,
    attachFileName varchar(200) DEFAULT NULL,
    attachZipFileName varchar(200) DEFAULT NULL,
    jobID bigint(20) DEFAULT NULL,
    schedJob tinyint(1) DEFAULT NULL,
    adminEmail tinyint(1) DEFAULT NULL,
    bodyContent mediumblob,
    processed tinyint(1) DEFAULT NULL,
    processedTime datetime DEFAULT NULL,
    submittedTime datetime DEFAULT NULL,
    jobURL varchar(1000),
    PRIMARY KEY (id)
)  ENGINE=InnoDB AUTO_INCREMENT=10;

CREATE TABLE emailrecord (
    id BIGINT NOT NULL AUTO_INCREMENT,
    emaildate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    successful BOOLEAN NOT NULL DEFAULT TRUE,
    errorMsg VARCHAR(1000),
    messageObject LONGTEXT,
    PRIMARY KEY (id)
)  AUTO_INCREMENT=100000 ENGINE=InnoDB;

CREATE TABLE stakeholders (
    stakeholderID bigint NOT NULL AUTO_INCREMENT,
    name varchar(200) NOT NULL,
    email varchar(400) NOT NULL,
    lastlogon timestamp,
	active bool DEFAULT true,
    allowLogon bool DEFAULT true,
	allowUserMgmt bool default false,
    emailmsgtypes bigint default 8190,
    PRIMARY KEY (stakeholderID)
)  AUTO_INCREMENT=100000 ENGINE=InnoDB;

CREATE TABLE repositories (
    repID BIGINT NOT NULL AUTO_INCREMENT,
    rep VARCHAR(100) NOT NULL UNIQUE KEY,
    repmgr BIGINT NOT NULL,
    url VARCHAR(3000),
	orgcode VARCHAR(20),
    databaseID BIGINT NOT NULL DEFAULT 1,
    dbUser VARCHAR(15) NOT NULL,
    dbPass VARCHAR(15) NOT NULL,
    orgname VARCHAR(1000),
    activateOnStartup bool default true,
    active boolean default true,
    userlimit INT NOT NULL DEFAULT 10,
    sessionlimit INT NOT NULL DEFAULT - 1,
    autoAddUsers boolean NOT NULL default false,
    repLogonMessage VARCHAR(10000),
    PRIMARY KEY (repID),
    CONSTRAINT repositories_FK2 FOREIGN KEY (repmgr)
        REFERENCES stakeholders (stakeholderID)
        ON DELETE RESTRICT ON UPDATE RESTRICT
)  AUTO_INCREMENT=100000 ENGINE=InnoDB;

CREATE TABLE repositorysession (
    id bigint NOT NULL AUTO_INCREMENT,
    sessionstartdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sessionenddate TIMESTAMP,
    repID BIGINT NOT NULL,
    userid BIGINT NOT NULL,
    sessionid VARCHAR(300) NOT NULL,
    sessioncountatstart INT NOT NULL,
    sessioncountatend INT,
    PRIMARY KEY (id),
    INDEX (repID),
    INDEX (sessionid)
)  AUTO_INCREMENT=100000 ENGINE=InnoDB;

CREATE TABLE userrepository (
    id bigint NOT NULL AUTO_INCREMENT,
    stakeholderID BIGINT NOT NULL,
    repID BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX (stakeholderID),
    CONSTRAINT stakeholdercatalog_FK1 FOREIGN KEY (stakeholderID)
        REFERENCES stakeholders (stakeholderID)
        ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT stakeholdercatalog_FK2 FOREIGN KEY (repID)
        REFERENCES repositories (repID)
        ON DELETE CASCADE ON UPDATE RESTRICT
)  AUTO_INCREMENT=100000 ENGINE=InnoDB;

CREATE TABLE stakeholderpassword (
    stakeholderID bigint NOT NULL,
    password varchar(200) NOT NULL,
    secretquestion varchar(500),
    secretanswer varchar(200),
    PRIMARY KEY (stakeholderID),
    CONSTRAINT stakeholderspassword_FK1 FOREIGN KEY (stakeholderID)
        REFERENCES stakeholders (stakeholderID)
        ON DELETE CASCADE ON UPDATE RESTRICT
)  ENGINE=InnoDB;

DELIMITER $$


CREATE FUNCTION `resetPassword`(var_name VARCHAR(200), var_email VARCHAR(500), var_newpass VARCHAR(20)) RETURNS BIGINT
SQL SECURITY INVOKER READS SQL DATA
BEGIN
  DECLARE userID BIGINT;
  SELECT stakeholderID from qrmlogin.stakeholders where LOWER(name) = LOWER(var_name) and LOWER(email) = LOWER(var_email) INTO userID;
  IF userID > 0 THEN
    UPDATE stakeholderpassword SET password = PASSWORD(var_newpass) WHERE stakeholderID = userID;
  END IF;
  RETURN userID;
END $$


CREATE FUNCTION `resetPasswordWithAnswer`(var_name VARCHAR(200), var_email VARCHAR(500), var_answer VARCHAR(500), var_newpass VARCHAR(20)) RETURNS BIGINT
SQL SECURITY INVOKER READS SQL DATA
BEGIN
  DECLARE userID BIGINT;
  SELECT stakeholderID from qrmlogin.stakeholders where LOWER(name) = LOWER(var_name) and LOWER(email) = LOWER(var_email) INTO userID;

  IF userID > 0 THEN
  SELECT stakeholderID from stakeholderpassword WHERE stakeholderID = userID AND secretanswer = PASSWORD(LOWER(var_answer)) INTO userID;
  	IF userID > 0 THEN
    		UPDATE stakeholderpassword SET password = PASSWORD(var_newpass) WHERE stakeholderID = userID AND secretanswer = PASSWORD(LOWER(var_answer));
  	END IF;
  END IF;

  RETURN userID;

END $$

CREATE PROCEDURE `addRepUserByRepURL`(IN var_userID BIGINT, IN var_repURL VARCHAR(300))
BEGIN
     DECLARE repositoryID BIGINT;
     SELECT repID INTO repositoryID FROM repositories WHERE url = var_repURL;
     INSERT INTO userrepository (stakeholderID, repID) VALUES (var_userID, repositoryID);
END $$

CREATE PROCEDURE `checkNumUser`(IN var_email VARCHAR(300))
BEGIN
	SELECT * FROM stakeholders WHERE LOWER(email) = LOWER(var_email);
END $$


CREATE PROCEDURE `checkUser`(IN var_name VARCHAR(300), IN var_pass VARCHAR(300))
BEGIN
      SELECT stakeholders.* from stakeholders
      NATURAL JOIN stakeholderpassword
      WHERE  ( LOWER(name) = LOWER(var_name) || LOWER(email) = LOWER(var_name) ) AND password = PASSWORD(var_pass);
END $$


CREATE PROCEDURE `checkUserID`(IN var_userID BIGINT, IN var_pass VARCHAR(300))
BEGIN
      SELECT stakeholders.* from stakeholders
      NATURAL JOIN stakeholderpassword
      WHERE  stakeholders.stakeholderID = var_userID AND password = PASSWORD(var_pass);
END $$


CREATE PROCEDURE `getUserReps`(IN var_userID BIGINT)
BEGIN
      SELECT DISTINCT repositories.*, userrepository.stakeholderID FROM repositories
      JOIN userrepository ON repositories.repID = userrepository.repID
      WHERE stakeholderID = var_userID;
END $$

CREATE PROCEDURE `getRepUsers`(IN var_REPID BIGINT)
BEGIN
	  SELECT DISTINCT stakeholders.* FROM stakeholders
      JOIN userrepository ON stakeholders.stakeholderID = userrepository.stakeholderID
      WHERE repID = var_REPID;
END $$


CREATE PROCEDURE `getRepUsersFromRepURL`(IN var_repURL VARCHAR(3000))
BEGIN
	  SELECT DISTINCT stakeholders.* FROM stakeholders
      JOIN userrepository ON stakeholders.stakeholderID = userrepository.stakeholderID
      JOIN repositories ON userrepository.repID = repositories.repID
      WHERE url = var_repURL;
END $$

CREATE PROCEDURE `getAllRepMgrs`()
BEGIN
	  SELECT DISTINCT stakeholders.* FROM stakeholders
      JOIN repositories ON stakeholders.stakeholderID = repositories.repmgr;
END $$

CREATE PROCEDURE `getAllPeople`()
BEGIN
	SELECT DISTINCT stakeholders.* FROM stakeholders;
END $$

CREATE PROCEDURE `createUser`(IN var_name VARCHAR (100), IN var_pass VARCHAR(20), IN var_email VARCHAR (200), OUT userID BIGINT)
BEGIN

   DECLARE checkUser BIGINT;


   SELECT COUNT(*) INTO checkUser FROM stakeholders WHERE LOWER(email) = LOWER(var_email) AND LOWER(name) != LOWER(var_name);

   IF checkUser > 0 THEN
     SET userID = -200;
   END IF;

   SELECT COUNT(*) INTO checkUser FROM stakeholders WHERE LOWER(email) = LOWER(var_email) AND LOWER(name) = LOWER(var_name);

   IF checkUser = 1 THEN
      SELECT stakeholderID INTO userID FROM stakeholders WHERE LOWER(email) = var_email AND LOWER(name) = LOWER(var_name);
   END IF;

   IF userID IS NULL THEN
      INSERT INTO stakeholders (name, email) VALUES (var_name, var_email);
      SET userID = LAST_INSERT_ID();

     INSERT INTO stakeholderpassword (stakeholderID, password) VALUES (userID, PASSWORD(var_pass));
   END IF;

END $$

CREATE PROCEDURE `checkAndCreateUser`(IN var_name VARCHAR (100), IN var_pass VARCHAR(20), IN var_email VARCHAR (200),IN var_repID BIGINT, OUT userID BIGINT)
BEGIN

   DECLARE var_userLimit BIGINT;
   DECLARE currentUsers BIGINT;
   DECLARE checkUser BIGINT;

   SELECT COUNT(*) INTO currentUsers FROM userrepository WHERE repID = var_repID;
   SELECT userlimit INTO var_userLimit FROM repositories WHERE repID = var_repID;
   SELECT COUNT(*) INTO checkUser FROM stakeholders WHERE LOWER(email) = var_email;

   IF currentUsers > var_userLimit AND var_userLimit >= 0 THEN
     SET userID = -100;
   END IF;

   IF  checkUser > 0 THEN
     SET userID = -200;
    
--   SELECT stakeholderID INTO userID FROM stakeholders WHERE LOWER(email) = var_email;
  
   END IF;

   IF userID IS NULL THEN
      INSERT INTO stakeholders (name, email) VALUES (var_name, var_email);
      SET userID = LAST_INSERT_ID();
      INSERT INTO stakeholderpassword (stakeholderID, password) VALUES (userID,PASSWORD(var_pass));
   END IF;

END $$

CREATE PROCEDURE `updateRepository`(

	IN var_rep VARCHAR(100),
	IN var_url VARCHAR(3000),
	IN var_orgname VARCHAR(1000),
	IN var_repmgr BIGINT,
	IN var_sessionLimit BIGINT,
	IN var_userLimit BIGINT,
	IN var_active BOOLEAN,
	IN var_autoAddUsers BOOLEAN,
  	IN var_userID BIGINT,
    IN var_repID BIGINT,
  	IN var_repLogonMsg VARCHAR(10000),
	OUT res	BIGINT)
BEGIN
  SET res = 0;

  IF var_userID != 1 THEN
    SET res = -1;
  END IF;

  IF res >= 0 THEN
    UPDATE repositories SET rep = var_rep, repmgr = var_repmgr, url = var_url,
     active = var_active, userlimit = var_userlimit, sessionlimit = var_sessionlimit, autoAddUsers = var_autoAddUsers, repLogonMessage = var_repLogonMsg
      WHERE repID = var_repID;
    SET res = 0;
  END IF;
END$$

CREATE PROCEDURE `deleteUser`(IN var_userID BIGINT, IN var_repID BIGINT,OUT res BIGINT)
BEGIN
  DECLARE EXIT HANDLER FOR SQLSTATE '23000'  SET res = -1;
  SET res = 0;
  DELETE FROM qrmlogin.userrepository WHERE stakeholderID = var_userID and repID = var_repID;
END $$

DELIMITER ;

INSERT INTO stakeholders (stakeholderID,name,email,active,allowLogon,emailmsgtypes,allowUserMgmt) 
VALUES (1,'QRM Technical Support','support@quaysystems.com.au',1,1,126,1);

INSERT INTO stakeholderpassword (stakeholderID,password) 
VALUES (1,PASSWORD('pass'));

ALTER TABLE stakeholders AUTO_INCREMENT = 100000;