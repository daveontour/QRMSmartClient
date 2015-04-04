CREATE DATABASE IF NOT EXISTS qrm;
USE qrm;

CREATE  VIEW userrepository AS SELECT * FROM qrmlogin.userrepository;

CREATE VIEW stakeholders AS SELECT DISTINCT qrmlogin.stakeholders.* FROM qrmlogin.stakeholders
 LEFT JOIN qrmlogin.userrepository ON qrmlogin.stakeholders.stakeholderID = qrmlogin.userrepository.stakeholderID;


CREATE TABLE results (
  id BIGINT NOT NULL,
  category VARCHAR(100), 
  label VARCHAR(100),
  param1 DOUBLE,
  param2 DOUBLE, 
  param3 DOUBLE, 
  param4 DOUBLE
)AUTO_INCREMENT = 100000  ENGINE = InnoDB;

CREATE TABLE emailrecord (
  id BIGINT NOT NULL  AUTO_INCREMENT,
  emaildate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
  successful BOOLEAN NOT NULL DEFAULT TRUE,
  errorMsg VARCHAR(1000),
  messageObject LONGTEXT,
  PRIMARY KEY (id)
)AUTO_INCREMENT = 100000  ENGINE = InnoDB;

CREATE TABLE jobqueue(
  jobID BIGINT NOT NULL AUTO_INCREMENT,
  readyToExecute BOOLEAN NOT NULL DEFAULT FALSE,
  processing BOOLEAN NOT NULL DEFAULT FALSE,
  readyToCollect BOOLEAN NOT NULL DEFAULT FALSE,
  collected BOOLEAN NOT NULL DEFAULT FALSE,
  failed BOOLEAN NOT NULL DEFAULT FALSE,
  state VARCHAR(1000) DEFAULT '',
  rootProjectID BIGINT,
  userID BIGINT NOT NULL,
  jobDescription VARCHAR(300),
  jobJdbcURL VARCHAR(1000),
  queuedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  executedDate TIMESTAMP NULL,
  collectedDate DATE,
  projectID BIGINT,
  reportFormat VARCHAR(20),
  jobType VARCHAR(40) NOT NULL DEFAULT 'REPORT',
  downloadOnly BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY(jobID),
  INDEX(userID)
)AUTO_INCREMENT = 100000  ENGINE = InnoDB;

CREATE TABLE jobdata(
  id BIGINT NOT NULL AUTO_INCREMENT,
  jobID BIGINT NOT NULL,
  riskID BIGINT NOT NULL,
  PRIMARY KEY(id),
  INDEX (jobID),
  CONSTRAINT FK_JOBDATA_1 FOREIGN KEY (jobID)
    REFERENCES jobqueue(jobID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000  ENGINE = InnoDB;

CREATE TABLE jobresult(
  id BIGINT NOT NULL AUTO_INCREMENT,
  jobID BIGINT NOT NULL,
  resultStr LONGBLOB,
  PRIMARY KEY(id),
  INDEX (jobID),
  CONSTRAINT FK_JOBRESULT_1 FOREIGN KEY (jobID)
    REFERENCES jobqueue(jobID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000  ENGINE = InnoDB;

CREATE TABLE reportdata (
	jobID BIGINT,
	reportID BIGINT,
  schedJobID BIGINT, 
	reportType varchar(1000),
	userID BIGINT,
	projectID BIGINT,
	title varchar(1000),
	description varchar(1000),
	jdbcURL varchar(1000),
	sendStatusUpdates bool default  false,
	processRiskIDs bool default  false,
	processElementIDs bool default  false,
	sendEmail bool default  false,
	schedJob bool default  false,
	emailAttachment bool default false,
	processed bool default false,
	readyToProcess bool default false,
	format varchar(1000),
	emailFormat varchar(1000),
	emailTitle varchar(1000),
	emailContent varchar(1000),
	emailSent bool default false,
	dateEmailSent DATETIME,
	jobStr MEDIUMTEXT,
	taskParamMapStr MEDIUMTEXT,  
  PRIMARY KEY(jobID)
) ENGINE = InnoDB;

CREATE TABLE importTemplate (
	templateID BIGINT NOT NULL AUTO_INCREMENT,
	userID BIGINT,    
	templateName varchar(400),
	template varchar(2000),
  
  PRIMARY KEY(templateID),
  CONSTRAINT FK_TEMPLATE_1 FOREIGN KEY (userID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE jobmonteresult(
  id BIGINT NOT NULL AUTO_INCREMENT,
  jobID BIGINT NOT NULL,
  typeID BIGINT NOT NULL,
  preMit BOOLEAN NOT NULL DEFAULT FALSE,
  resultStr LONGBLOB,
  image MEDIUMBLOB,
  p10 DOUBLE,
  p20 DOUBLE,
  p30 DOUBLE,
  p40 DOUBLE,
  p50 DOUBLE,
  p60 DOUBLE,
  p70 DOUBLE,
  p80 DOUBLE,
  p90 DOUBLE,
  p100 DOUBLE,
  PRIMARY KEY(id),
  INDEX (jobID),
  CONSTRAINT FK_JOBMONTERESULT_1 FOREIGN KEY (jobID)
    REFERENCES jobqueue(jobID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE jobmontedata(
  id BIGINT NOT NULL AUTO_INCREMENT,
  jobID BIGINT NOT NULL,
  projectID BIGINT NOT NULL,
  monthlyAnalysis BIGINT NOT NULL,
  startDate timestamp,
  endDate timestamp,
  numIterations INT,
  forceConsequencesActive BOOLEAN NOT NULL DEFAULT FALSE,
  forceRiskActive BOOLEAN NOT NULL DEFAULT FALSE,
  simType INT,
  processed BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY(id),
  INDEX (jobID),
  CONSTRAINT FK_JOBMONTEDATA_1 FOREIGN KEY (jobID)
    REFERENCES jobqueue(jobID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE analysistools (
  id BIGINT NOT NULL AUTO_INCREMENT,
	title VARCHAR(1000) NOT NULL,
	clazz VARCHAR(1000) NOT NULL,
	param1 VARCHAR(1000),
	b3D BOOLEAN,
	bTol BOOLEAN,
	bMatrix BOOLEAN,
	bContext BOOLEAN,
	bDescend BOOLEAN,
	bNumElem BOOLEAN,
	bReverse BOOLEAN,
  PRIMARY KEY(id)
) AUTO_INCREMENT = 100000 ENGINE = InnoDB;


CREATE TABLE welcomedata(
  sessionID BIGINT,
  element VARCHAR(50),
  id BIGINT,
  name VARCHAR(1000),
  date timestamp,
  addInfo VARCHAR(1000)
) ENGINE = InnoDB;


CREATE TABLE attachment (
  internalID bigint NOT NULL AUTO_INCREMENT,
  hostID bigint NOT NULL,
  hostType ENUM('RISK', 'PROJECT', 'INCIDENT', 'REVIEW', 'DELIVERABVLE','ISSUE', 'ISSUEACTION', 'TEMPFILE'),
  attachmentURL varchar(2000),
  attachmentFileName varchar(2000),
  fileType varchar(100),
  dateUploaded timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description varchar(2000),
  contents LONGBLOB,
  INDEX(hostID),
  PRIMARY KEY(internalID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE schedjob (
  internalID bigint NOT NULL AUTO_INCREMENT,
  Mon BOOLEAN NOT NULL DEFAULT false,
  Tue BOOLEAN NOT NULL DEFAULT false,
  Wed BOOLEAN NOT NULL DEFAULT false,
  Thu BOOLEAN NOT NULL DEFAULT false,
  Fri BOOLEAN NOT NULL DEFAULT false,
  Sat BOOLEAN NOT NULL DEFAULT false,
  Sun BOOLEAN NOT NULL DEFAULT false,
  userID BIGINT NOT NULL,
  repository BIGINT NOT NULL,
  reportID BIGINT,
  projectID BIGINT,
  descendants BOOLEAN NOT NULL DEFAULT false,
  description VARCHAR(500),
  timeStr VARCHAR(20),
  email BOOLEAN NOT NULL DEFAULT true,
  additionalUsers VARCHAR(3000), 
  taskParamMapStr MEDIUMTEXT,  
  PRIMARY KEY(internalID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE riskproject (
  projectID bigint NOT NULL AUTO_INCREMENT,
  parentID bigint,
  lft integer NOT NULL,
  rgt integer NOT NULL,
  projectRiskManagerID bigint,
  dateUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  dateEntered timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  projectDescription varchar(5000),
  projectTitle varchar(500),
  projectStartDate timestamp,
  projectEndDate timestamp,
  projectCode varchar(6),
  minimumSecurityLevel INT NOT NULL DEFAULT 1,
  tabsToUse BIGINT NOT NULL DEFAULT 8190,
  riskIndex bigint NOT NULL DEFAULT 0,
  incidentIndex bigint NOT NULL DEFAULT 0,
  securityMask bigint NOT NULL DEFAULT 63,
  singlePhase boolean NOT NULL DEFAULT false,
  useAdvancedConsequences boolean NOT NULL DEFAULT true,
  useAdvancedLiklihood boolean NOT NULL DEFAULT true,
  PRIMARY KEY(projectID), 
  CONSTRAINT FK_RISKPROJECT_4 FOREIGN KEY (parentID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_RISKPROJECT_3 FOREIGN KEY (projectRiskManagerID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE tolerancematrix (
  matrixID bigint NOT NULL AUTO_INCREMENT,
  projectID bigint NOT NULL,  
  maxprob smallint,
  maximpact smallint,
  tolString varchar(64),
  dateUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  dateEntered timestamp NOT NULL,
  prob1 varchar(100),
  prob2 varchar(100),
  prob3 varchar(100),
  prob4 varchar(100),
  prob5 varchar(100),
  prob6 varchar(100),
  prob7 varchar(100),
  prob8 varchar(100),
  probVal1 double not null default 20,
  probVal2 double not null default 40,
  probVal3 double not null default 60,
  probVal4 double not null default 70,
  probVal5 double not null default 100,
  probVal6 double not null default 100,
  probVal7 double not null default 100,
  probVal8 double not null default 100,
  impact1 varchar (100),
  impact2 varchar (100),
  impact3 varchar (100),
  impact4 varchar (100),
  impact5 varchar (100),
  impact6 varchar (100),
  impact7 varchar (100),
  impact8 varchar (100),
  PRIMARY KEY(matrixID),
  CONSTRAINT FX_MATRIX_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE review (
  reviewID bigint NOT NULL AUTO_INCREMENT, 
  projectID bigint NOT NULL,
  title varchar(2000),
  scheduledDate date,
  actualDate date,
  reviewComplete bool default false,
  reviewComments varchar(32672),
  mailUpdateDate timestamp,
  mailUpdateComplete bool,
  PRIMARY KEY(reviewID),
  CONSTRAINT FK_REVIEW_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE control_effectiveness_defn (
  controlEffectivenessID bigint NOT NULL AUTO_INCREMENT,
  projectID bigint NOT NULL, 
  title varchar(100),
  description varchar(1000),
  rank smallint,
  PRIMARY KEY(controlEffectivenessID),
  CONSTRAINT FK_CONTROL_DEFN_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE objective (
  objectiveID bigint NOT NULL AUTO_INCREMENT,
  projectID bigint NOT NULL, 
  parentID bigint,
  objective varchar(1000) NOT NULL,
  lft integer NOT NULL,
  rgt integer NOT NULL,
  PRIMARY KEY(objectiveID),
  INDEX(projectID),
  CONSTRAINT FK_OBJECTIVES_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_OBJECTIVE_2 FOREIGN KEY (parentID)
    REFERENCES objective(objectiveID)  ON DELETE CASCADE

)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE quantimpacttype (
  typeID bigint NOT NULL AUTO_INCREMENT,
  dateUpdated timestamp,
  dateEntered timestamp,
  description varchar(400),
  costCategroy bool NOT NULL DEFAULT true,
  costSummary bool NOT NULL DEFAULT false,
  nillQuantImpact smallint NOT NULL DEFAULT false,
  units varchar(200),
  lower1 double NOT NULL DEFAULT 0.0,
  upper1 double NOT NULL DEFAULT 0.0,
  description1 varchar(200) NOT NULL DEFAULT '',
  lower2 double NOT NULL DEFAULT 0.0,
  upper2 double NOT NULL DEFAULT 0.0,
  description2 varchar(200) NOT NULL DEFAULT '',
  lower3 double NOT NULL DEFAULT 0.0,
  upper3 double NOT NULL DEFAULT 0.0,
  description3 varchar(200) NOT NULL DEFAULT '',
  lower4 double NOT NULL DEFAULT 0.0,
  upper4 double NOT NULL DEFAULT 0.0,
  description4 varchar(200) NOT NULL DEFAULT '',
  lower5 double NOT NULL DEFAULT 0.0,
  upper5 double NOT NULL DEFAULT 0.0,
  description5 varchar(200) NOT NULL DEFAULT '',
  lower6 double NOT NULL DEFAULT 0.0,
  upper6 double NOT NULL DEFAULT 0.0,
  description6 varchar(200) NOT NULL DEFAULT '',
  lower7 double NOT NULL DEFAULT 0.0,
  upper7 double NOT NULL DEFAULT 0.0,
  description7 varchar(200) NOT NULL DEFAULT '',
  lower8 double NOT NULL DEFAULT 0.0,
  upper8 double NOT NULL DEFAULT 0.0,
  description8 varchar(200) NOT NULL DEFAULT '',
  projectID bigint NOT NULL,
  PRIMARY KEY(typeID),
  CONSTRAINT FK_QUANTIMPACTTYPE_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE projectowners (
  projectID bigint NOT NULL,
  stakeholderID bigint NOT NULL, 
  INDEX (projectID),
  INDEX (stakeholderID),
  CONSTRAINT projectowners_FK1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT projectowners_FK2 FOREIGN KEY (stakeholderID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE projectriskmanagers (
  projectID bigint NOT NULL,
  stakeholderID bigint NOT NULL,
  INDEX (projectID),
  INDEX (stakeholderID),
  CONSTRAINT projectmanagers_FK1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT projectmanagers_FK2 FOREIGN KEY (stakeholderID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT

) ENGINE = InnoDB;

CREATE TABLE projectusers (
  projectID bigint NOT NULL,
  stakeholderID bigint NOT NULL,
  INDEX (projectID),
  INDEX (stakeholderID),
  CONSTRAINT projectusers_FK1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT projectusers_FK2 FOREIGN KEY (stakeholderID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE riskcategory (
  internalID bigint NOT NULL AUTO_INCREMENT,
  parentID bigint NOT NULL DEFAULT 1,
  description varchar(200),
  contextID bigint NOT NULL DEFAULT 0,
  PRIMARY KEY(internalID),
 CONSTRAINT riskcategory_FK2 FOREIGN KEY (contextID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT riskcategory_FK1 FOREIGN KEY (parentID)
    REFERENCES riskcategory(internalID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE toldescriptors (
  descriptorID bigint NOT NULL AUTO_INCREMENT,
  projectID bigint NOT NULL,
  shortName varchar(20),
  longName varchar(500),
  tolAction varchar(500),
  tolLevel smallint,
  PRIMARY KEY(descriptorID),
CONSTRAINT FK_TOLDESCRIPTORS_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE risk (
  riskID bigint NOT NULL AUTO_INCREMENT,
  externalID VARCHAR(500),
  projectID bigint NOT NULL,
  promotedProjectID bigint,
  riskProjectCode varchar(30),
  securityLevel INT NOT NULL DEFAULT 0,
  ownerID bigint NOT NULL,
  manager1ID bigint NOT NULL,
  manager2ID bigint,
  manager3ID bigint,
  matrixID bigint NOT NULL DEFAULT 1,
  title varchar(200),
  description varchar(5000),
  mitPlanSummary varchar (5000),
  mitPlanSummaryUpdate varchar (5000),
  impact varchar (5000),
  cause varchar(5000),
  consequences varchar(5000),
  impSafety bool DEFAULT false,
  impSpec bool DEFAULT false,
  impCost bool DEFAULT false,
  impTime bool DEFAULT false,
  impReputation bool DEFAULT false,
  impEnvironment bool DEFAULT false,
  forceDownParent bool DEFAULT false,
  forceDownChild bool DEFAULT false,
  inherentProb double,
  inherentImpact double,
  treatedProb double,
  treatedImpact double,
  startExposure date,
  endExposure date,
  inherentTolerance smallint,
  treatedTolerance smallint,
  currentTolerance smallint,
  subjectiveRank integer,
  objectiveRank integer,
  levelRank integer,
  active smallint DEFAULT 1,
  treated smallint DEFAULT 0,
  dateUpdated date,
  timeUpdated timestamp  ON UPDATE CURRENT_TIMESTAMP,
  dateEntered timestamp,
  dateEvalApp date,
  dateEvalRev date,
  dateIDApp date,
  dateIDRev date,
  dateMitApp date,
  dateMitPrep date,
  dateMitRev date,
  idEvalApp bigint,
  idEvalRev bigint,
  idIDApp bigint,
  idIDRev bigint,
  idMitApp bigint,
  idMitPrep bigint,
  idMitRev bigint,
  treatmentID bigint,
  treatmentAvoidance bool DEFAULT false,
  treatmentReduction bool DEFAULT false,
  treatmentRetention bool DEFAULT false,
  treatmentTransfer bool DEFAULT false,
  summaryRisk bool DEFAULT false,
  private bool NOT NULL DEFAULT true,
  restricted bool NOT NULL DEFAULT true,
  pub bool NOT NULL DEFAULT false,
  liketype integer DEFAULT 4,
  likeprob double,
  likealpha double,
  liket double,
  likepostType integer DEFAULT 4,
  likepostProb double,
  likepostAlpha double,
  likepostT double,
  primCatID bigint,
  secCatID bigint,
  estimatedContingencey double NOT NULL DEFAULT 0.0,
  useCalculatedContingency bool DEFAULT false,
  preMitContingency double NOT NULL DEFAULT 0.0,
  postMitContingency double NOT NULL DEFAULT 0.0,
  preMitContingencyWeighted double NOT NULL DEFAULT 0.0,
  postMitContingencyWeighted double NOT NULL DEFAULT 0.0,
  contingencyPercentile double NOT NULL DEFAULT 0.0,
  mitigationCost double DEFAULT 0.0,
  useCalculatedProb boolean NOT NULL DEFAULT false,
  extObject blob,
  preMitImage mediumblob,
  postMitImage mediumblob,
  matImage mediumblob,
  PRIMARY KEY(riskID),  
  CONSTRAINT FK_RISK_2 FOREIGN KEY (ownerID) REFERENCES qrmlogin.stakeholders(stakeholderID),
  CONSTRAINT FK_RISK_3 FOREIGN KEY (manager1ID) REFERENCES qrmlogin.stakeholders(stakeholderID),
  CONSTRAINT FK_RISK_4 FOREIGN KEY (manager2ID) REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE SET NULL
    ON UPDATE SET NULL,
  CONSTRAINT FK_RISK_5 FOREIGN KEY (manager3ID) REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE SET NULL
    ON UPDATE SET NULL,
  CONSTRAINT FK_RISK_6 FOREIGN KEY (matrixID) REFERENCES tolerancematrix(matrixID),
  CONSTRAINT FK_RISK_7 FOREIGN KEY (projectID) REFERENCES riskproject(projectID)
    ON DELETE CASCADE
) AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE riskrisk (
  internalID bigint NOT NULL AUTO_INCREMENT,
  parentID bigint NOT NULL,
  childID bigint NOT NULL,
  PRIMARY KEY (internalID),
  INDEX (parentID),
CONSTRAINT FK_RISKRISK_1 FOREIGN KEY (parentID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
CONSTRAINT FK_RISKRISK_2 FOREIGN KEY (childID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
UNIQUE INDEX (parentId, childID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE riskstakeholder (
  internalID bigint NOT NULL AUTO_INCREMENT,
  riskID bigint NOT NULL,
  stakeholderID bigint NOT NULL,
  description varchar(200),
  PRIMARY KEY (internalID),
  INDEX (riskID),
CONSTRAINT FK_RISKSTAKE_1 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
CONSTRAINT FK_RISKSTAKE_2 FOREIGN KEY (stakeholderID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;


CREATE VIEW riskdetail AS SELECT risk.*, s1.name AS ownerName, s2.name AS manager1Name, s2.name AS manager2Name, s4.name AS manager3Name, c1.description AS primCatName,  c2.description AS secCatName, p1.projectCode AS fromProjCode, p2.projectCode AS toProjCode FROM risk
LEFT OUTER JOIN stakeholders AS s1 ON risk.ownerID    = s1.stakeholderID
LEFT OUTER JOIN stakeholders AS s2 ON risk.manager1ID = s2.stakeholderID
LEFT OUTER JOIN stakeholders AS s3 ON risk.manager2ID = s3.stakeholderID
LEFT OUTER JOIN stakeholders AS s4 ON risk.manager3ID = s4.stakeholderID
LEFT OUTER JOIN riskcategory AS c1 ON risk.primCatID = c1.internalID
LEFT OUTER JOIN riskcategory AS c2 ON risk.secCatID = c2.internalID
LEFT OUTER JOIN riskproject AS p1 ON p1.projectID = risk.projectID
LEFT OUTER JOIN riskproject AS p2 ON p2.projectID = risk.promotedProjectID;



CREATE  VIEW riskview (riskID,projectID,matrixID,inherentimpact,inherentprob,treatedimpact,treatedprob,treated, summaryRisk)
AS SELECT riskID, projectID, matrixID, floor(inherentimpact), floor(inherentprob), floor(treatedimpact), floor(treatedprob), treated, summaryRisk from risk;

CREATE VIEW subprojects(projectID, subprojectID)
AS SELECT p1.projectID AS projectID, p2.projectID AS subprojectID FROM riskproject as p1, riskproject AS p2 WHERE (p2.lft BETWEEN p1.lft AND p1.rgt) AND p1.projectID != p2.projectID;

CREATE VIEW subprojectsandparent (projectID, subprojectID) 
AS SELECT subprojects.* FROM subprojects WHERE projectID > 0 AND subprojectID > 0
UNION SELECT  `p1`.`projectID` AS `projectID`, `p2`.`projectID` AS `subprojectID`  from  (`riskproject` `p1`   join `riskproject` `p2`) WHERE p1.projectID = p2.projectID AND p1.projectID > 0 AND p2.projectID > 0 ORDER BY projectID;

CREATE VIEW projectriskdetails
AS SELECT subprojectsandparent.projectID AS spprojectID, subprojectsandparent.subprojectID, riskdetail.* FROM subprojectsandparent
JOIN riskdetail ON riskdetail.projectID = subprojectsandparent.subprojectID;

CREATE VIEW superprojects(projectID, superprojectID)
AS SELECT p1.projectID AS projectID, p2.projectID AS superprojectID FROM riskproject as p1, riskproject AS p2 WHERE (p1.lft BETWEEN p2.lft AND p2.rgt) AND p1.projectID != p2.projectID;

CREATE TABLE childparent (
  internalID bigint NOT NULL AUTO_INCREMENT,
  childID bigint NOT NULL,
  parentID bigint NOT NULL,
  PRIMARY KEY (internalID),
  INDEX(parentID),
  CONSTRAINT FK_RISKCHILDPARENT_1 FOREIGN KEY (childID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_RISKCHILDPARENT_2 FOREIGN KEY (parentID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE 
    ALGORITHM = UNDEFINED 
VIEW `riskviewparent` AS
    select distinct
        `risk`.`riskID` AS `riskID`,
        `risk`.`projectID` AS `projectID`,
        `risk`.`matrixID` AS `matrixID`,
        `risk`.`summaryRisk` AS `summaryRisk`,
        floor(`risk`.`inherentImpact`) AS `inherentimpact`,
        floor(`risk`.`inherentProb`) AS `inherentprob`,
        floor(`risk`.`treatedImpact`) AS `treatedimpact`,
        floor(`risk`.`treatedProb`) AS `treatedprob`,
        `risk`.`treated` AS `treated`,
        `childparent`.`parentID` AS `parentID`
    from
        (`risk`
        join `childparent` ON ((`childparent`.`parentID` = `risk`.`riskID`)));


CREATE TABLE riskcontrols (
  internalID bigint NOT NULL AUTO_INCREMENT, 
  control varchar(2000),
  effectiveness bigint,
  riskID bigint NOT NULL,
  contribution varchar(1000),
  PRIMARY KEY (internalID),
  CONSTRAINT FK_RISKCONTROLS FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE riskconsequence (
  internalID bigint NOT NULL AUTO_INCREMENT,
  riskID bigint, 
  quantifiable bool,
  quantType bigint,
  dateUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description varchar(2000),
  riskConsequenceProb double,
  costDistributionType varchar(500),
  costDistributionParams varchar(2000),
  postRiskConsequenceProb double,
  postCostDistributionType varchar(500),
  postCostDistributionParams varchar(2000),
  PRIMARY KEY(internalID),
  CONSTRAINT riskconsequence_FK1 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE auditcomments (
  internalID bigint NOT NULL AUTO_INCREMENT,  
  dateEntered timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  schedReviewDate date,
  projectID bigint,
  riskID bigint,
  enteredByID bigint,
  comment varchar(5000),
  commenturl varchar(1000),
  attachment LONGBLOB,
  attachmentFileName varchar(1000),
  identification smallint DEFAULT 0,
  evaluation smallint DEFAULT 0,
  mitigation smallint DEFAULT 0,
  review smallint DEFAULT 0,
  approval smallint DEFAULT 0,
  schedReview smallint DEFAULT 0,
  schedReviewID bigint,
  PRIMARY KEY(internalID),
 CONSTRAINT auditcomments_FK1 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

DELIMITER $$
CREATE TRIGGER insertauditcommentsTrigger BEFORE INSERT ON auditcomments
FOR EACH ROW BEGIN
   UPDATE risk SET dateIDRev = CURDATE(), idIDRev = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.identification = true AND NEW.review = true;
   UPDATE risk SET dateIDApp = CURDATE(), idIDApp = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.identification = true AND NEW.approval = true;
   UPDATE risk SET dateEvalRev = CURDATE(), idEvalRev = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.evaluation = true AND NEW.review = true;
   UPDATE risk SET dateEvalApp = CURDATE(), idEvalApp = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.evaluation = true AND NEW.approval = true;
   UPDATE risk SET dateMitRev = CURDATE(), idMitRev = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.mitigation = true AND NEW.review = true;
   UPDATE risk SET dateMitApp = CURDATE(), idMitApp = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.mitigation = true AND NEW.approval = true;

END $$
DELIMITER ;

CREATE TABLE reviewrisk (
  id bigint NOT NULL AUTO_INCREMENT,
  reviewID bigint NOT NULL,
  riskID bigint NOT NULL,
  PRIMARY KEY(id),
 CONSTRAINT FK_REVIEWRISK_1 FOREIGN KEY (reviewID)
    REFERENCES review(reviewID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_REVIEWRISK_2 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE subjrank (
  id bigint NOT NULL AUTO_INCREMENT,
  projectID bigint NOT NULL,
  riskID bigint NOT NULL,
  rank bigint NOT NULL, 
  PRIMARY KEY(id),
  CONSTRAINT FK_SUBJRANK_1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_SUBJRANK_2 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE objectives_impacted (
  id bigint NOT NULL AUTO_INCREMENT,
  riskID bigint NOT NULL,
  objectiveID bigint NOT NULL,  
  PRIMARY KEY(id),
 CONSTRAINT FK_OBJECTIVES_IMPACTED_1 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_OBJECTIVES_IMPACTED_2 FOREIGN KEY (objectiveID)
    REFERENCES objective(objectiveID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE mitigationstep (
  mitstepID bigint NOT NULL AUTO_INCREMENT,
  projectID bigint NOT NULL,  
  riskID bigint,
  dateUpdated timestamp,
  dateEntered timestamp,
  description varchar(5000),
  startDate timestamp,
  endDate timestamp,
  personID bigint,
  estCost double NOT NULL DEFAULT 0.0,
  percentComplete double NOT NULL DEFAULT 0.0,
  response bool DEFAULT false,
  mitPlanUpdate bool DEFAULT false,
  PRIMARY KEY(mitstepID),
  CONSTRAINT mitigationstep_FK2 FOREIGN KEY (riskID)
    REFERENCES risk(riskID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT FK_MITSTEP_3 FOREIGN KEY (personID)
    REFERENCES qrmlogin.stakeholders(stakeholderID)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT mitigationstep_FK1 FOREIGN KEY (projectID)
    REFERENCES riskproject(projectID)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

DELIMITER $$
CREATE TRIGGER insertMitigationStep AFTER INSERT ON mitigationstep
FOR EACH ROW BEGIN
  SELECT SUM(estCost) INTO @mitcost FROM mitigationstep WHERE riskID = NEW.riskID AND response = 0;
  UPDATE risk SET mitigationCost = @mitcost WHERE riskID = NEW.riskID;
END $$
CREATE TRIGGER updateMitigationStep AFTER UPDATE ON mitigationstep
FOR EACH ROW BEGIN
  SELECT SUM(estCost) INTO @mitcost FROM mitigationstep WHERE riskID = NEW.riskID AND response = 0;
  UPDATE risk SET mitigationCost = @mitcost WHERE riskID = NEW.riskID;
END $$
CREATE TRIGGER deleteMitigationStep AFTER DELETE ON mitigationstep
FOR EACH ROW BEGIN
  SELECT SUM(estCost) INTO @mitcost FROM mitigationstep WHERE riskID = OLD.riskID AND response = 0;
  UPDATE risk SET mitigationCost = @mitcost WHERE riskID = OLD.riskID;
  DELETE FROM updatecomment WHERE hostID = OLD.mitstepID AND hostType = 'MITIGATION';
END $$
DELIMITER ;

CREATE VIEW allriskstakeholders AS
    select 
        `stakeholders`.`stakeholderID`,
        `stakeholders`.`name` AS `name`,
        `stakeholders`.`email` AS `email`,
        `riskstakeholder`.`riskID` AS `riskID`,
        `riskstakeholder`.`description` AS `description`
    from
        (`stakeholders`
        join `riskstakeholder` ON ((`stakeholders`.`stakeholderID` = `riskstakeholder`.`stakeholderID`))) 
    union select 
         `stakeholders`.`stakeholderID`,
       `stakeholders`.`name` AS `name`,
        `stakeholders`.`email` AS `email`,
        `mitigationstep`.`riskID` AS `riskID`,
        'Mitigatipon Step Owner' AS `description`
    from
        (`stakeholders`
        join `mitigationstep` ON ((`stakeholders`.`stakeholderID` = `mitigationstep`.`personID`))) 
    union select 
        `stakeholders`.`stakeholderID`,
        `stakeholders`.`name` AS `name`,
        `stakeholders`.`email` AS `email`,
        `risk`.`riskID` AS `riskID`,
        'Risk Owner' AS `description`
    from
        (`stakeholders`
        join `risk` ON ((`stakeholders`.`stakeholderID` = `risk`.`ownerID`))) 
    union select 
        `stakeholders`.`stakeholderID`,
        `stakeholders`.`name` AS `name`,
        `stakeholders`.`email` AS `email`,
        `risk`.`riskID` AS `riskID`,
        'Risk Manager' AS `flag`
    from
        (`stakeholders`
        join `risk` ON ((`stakeholders`.`stakeholderID` = `risk`.`manager1ID`))) 
    union select 
        `stakeholders`.`stakeholderID`,
        `stakeholders`.`name` AS `name`,
        `stakeholders`.`email` AS `email`,
        `risk`.`riskID` AS `riskID`,
        'Contributing Risk Owner' AS `description`
    from
        (((`risk`
        join `riskrisk` ON ((`riskrisk`.`parentID` = `risk`.`riskID`)))
        join `riskdetail` `r2` ON ((`r2`.`riskID` = `riskrisk`.`childID`)))
        join `stakeholders` ON ((`stakeholders`.`stakeholderID` = `r2`.`ownerID`))) 
    union select 
        `stakeholders`.`stakeholderID`,
        `stakeholders`.`name` AS `name`,
        `stakeholders`.`email` AS `email`,
        `risk`.`riskID` AS `riskID`,
        'Contributing Risk Manager' AS `description`
    from
        (((`risk`
        join `riskrisk` ON ((`riskrisk`.`parentID` = `risk`.`riskID`)))
        join `riskdetail` `r2` ON ((`r2`.`riskID` = `riskrisk`.`childID`)))
        join `stakeholders` ON ((`stakeholders`.`stakeholderID` = `r2`.`manager1ID`)));

CREATE TABLE updatecomment (
  internalID bigint NOT NULL AUTO_INCREMENT,
  dateUpdated timestamp,
  dateEntered timestamp,
  hostID bigint NOT NULL, 
  hostType ENUM('RISK', 'PROJECT', 'INCIDENT', 'REVIEW', 'DELIVERABLE','ISSUE', 'ISSUEACTION', 'AUDITITEM','MITIGATION'),
  description varchar(2000),
  contents LONGBLOB,
  personID bigint,
  INDEX(hostID),
  PRIMARY KEY(internalID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE reportsession (
	sessionID BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT,
	reportName VARCHAR(1000),
  timeInit TIMESTAMP,
  PRIMARY KEY(sessionID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE reportsessiondata (
  id BIGINT NOT NULL AUTO_INCREMENT,
  sessionID BIGINT NOT NULL,
  dataElement VARCHAR(200),
  dataString VARCHAR(200),
  rank INTEGER,
  dataID BIGINT,
  dataBlob MEDIUMBLOB, 
  PRIMARY KEY(id),
  CONSTRAINT FK_REPORTDSESSIONDATA_1 FOREIGN KEY (sessionID)
  REFERENCES reportsession(sessionID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE innerreportsessiondata (
  id BIGINT NOT NULL AUTO_INCREMENT,
  sessionID BIGINT NOT NULL,
  outerID BIGINT NOT NULL,
  innerType VARCHAR(200),
  dataBlob MEDIUMTEXT, 
  PRIMARY KEY(id),
  CONSTRAINT FK_INNERREPORTDSESSIONDATA_1 FOREIGN KEY (sessionID)
  REFERENCES reportsession(sessionID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE VIEW reportrisks AS
	SELECT  risk.*, S1.name AS ownerName, S2.name AS manager1Name, S2.name AS manager2Name, S4.name AS manager3Name,
	R1.rank AS contextRank, C1.description AS primCatName,  C2.description AS secCatName, reportsessiondata.rank AS sessionRank,
	reportsessiondata.* FROM risk
	LEFT OUTER JOIN stakeholders AS S1 ON risk.ownerID    = S1.stakeholderID
	LEFT OUTER JOIN stakeholders AS S2 ON risk.manager1ID = S2.stakeholderID
	LEFT OUTER JOIN stakeholders AS S3 ON risk.manager2ID = S3.stakeholderID
	LEFT OUTER JOIN stakeholders AS S4 ON risk.manager3ID = S4.stakeholderID
	LEFT OUTER JOIN subjrank     AS R1 ON (risk.riskID = R1.riskID  AND  R1.projectID = risk.projectID)
	LEFT OUTER JOIN riskcategory AS C1 ON risk.primCatID = C1.internalID
	LEFT OUTER JOIN riskcategory AS C2 ON risk.secCatID = C2.internalID
	JOIN reportsessiondata  ON risk.riskID = reportsessiondata.dataID AND reportsessiondata.dataElement = 'RISK' ORDER BY reportsessiondata.rank ASC;

CREATE TABLE reports (
  internalID BIGINT NOT NULL AUTO_INCREMENT,
  reportName VARCHAR(1000),
  reportDescription VARCHAR(1000),  
  reportFileName VARCHAR(100) DEFAULT NULL,
  coreFile SMALLINT DEFAULT 1,
  id VARCHAR(1000),
  template smallint,
  reporttype  ENUM('REGISTER', 'PROJECT', 'RISK', 'REVIEW', 'INCIDENT', 'REPOSITORY'),
  visitor VARCHAR(400),
  bodyText LONGBLOB,
  version VARCHAR(1000),
  reqTemplateRoot VARCHAR(2000),
  detailConfigWindow bool NOT NULL DEFAULT FALSE,
  excelOnlyFormat bool NOT NULL DEFAULT FALSE,
  PRIMARY KEY(internalID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE incident (
  incidentID BIGINT NOT NULL AUTO_INCREMENT,  
  incidentProjectCode varchar(30),
  dateEntered timestamp,
  dateUpdated TIMESTAMP,
  contextID BIGINT,
  title VARCHAR(200),
  description VARCHAR(10000),
  lessonsLearnt VARCHAR(10000),
  controls VARCHAR(10000),
  dateIncident timestamp,
  dateStakeHoldersNotified timestamp,
  impTime bool NOT NULL DEFAULT false,
  impSpec bool NOT NULL DEFAULT false,
  impEnviron bool NOT NULL DEFAULT false,
  impCost bool NOT NULL DEFAULT false,
  impSafety bool NOT NULL DEFAULT false,
  impReputation bool NOT NULL DEFAULT false,
  notifyStakeHoldersEntered bool NOT NULL DEFAULT false,
  notifyStakeHoldersUpdate bool NOT NULL DEFAULT false,
  stakeHolderNotified bool NOT NULL DEFAULT false,
  bIdentified bool NOT NULL DEFAULT false,
  bCauses bool NOT NULL DEFAULT false,
  bRated bool NOT NULL DEFAULT false,
  bControl bool NOT NULL DEFAULT false,
  bMitigated bool NOT NULL DEFAULT false,
  bReviews bool NOT NULL DEFAULT false,
  bActive bool NOT NULL DEFAULT false,
  bIssue bool NOT NULL DEFAULT true,
  reportedByID BIGINT,
  projectID BIGINT,
  promotedProjectID BIGINT,
  severity INT NOT NULL default 2,
  reportedByStr VARCHAR(100),
  PRIMARY KEY(incidentID),
  INDEX (projectID, promotedProjectID),
  INDEX(incidentProjectCode)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE incidentobjective (
  id BIGINT NOT NULL AUTO_INCREMENT,
  incidentID BIGINT NOT NULL,
  objectiveID BIGINT NOT NULL,
   PRIMARY KEY(id),
 CONSTRAINT FK_IO_1 FOREIGN KEY (incidentID)
  REFERENCES incident(incidentID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT,
  CONSTRAINT FK_IO_2 FOREIGN KEY (objectiveID)
  REFERENCES objective(objectiveID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE incidentupdate (
  id BIGINT NOT NULL AUTO_INCREMENT,
  incidentID BIGINT NOT NULL,
  dateEntered timestamp,
  description VARCHAR(10000),
  reportedByID BIGINT,
  reportedByStr VARCHAR(100),  
  PRIMARY KEY(id),
  INDEX(incidentID),
  CONSTRAINT FK_IU_1 FOREIGN KEY (incidentID)
  REFERENCES incident(incidentID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE incidentrisk (
  id BIGINT NOT NULL AUTO_INCREMENT,
  incidentID BIGINT NOT NULL,
  riskID BIGINT NOT NULL, 
  PRIMARY KEY(id),
  CONSTRAINT FK_IR_1 FOREIGN KEY (incidentID)
  REFERENCES incident(incidentID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT,
  CONSTRAINT FK_IR_2 FOREIGN KEY (riskID)
  REFERENCES risk(riskID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT

)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

DELIMITER $$
CREATE TRIGGER insertIncidentRiskTriggerAfter AFTER INSERT ON incidentrisk
FOR EACH ROW BEGIN
	CALL updateRiskActive(NEW.riskID);
END $$

CREATE TRIGGER updateIncidentRiskTriggerAfter AFTER UPDATE ON incidentrisk
FOR EACH ROW BEGIN
	CALL updateRiskActive(NEW.riskID);
END $$

CREATE TRIGGER deleteIncidentRiskTriggerAfter AFTER DELETE ON incidentrisk
FOR EACH ROW BEGIN
	CALL updateRiskActive(OLD.riskID);
END $$

DELIMITER ;

CREATE TABLE incidentconseq (
  id BIGINT NOT NULL AUTO_INCREMENT,  
  incidentID BIGINT NOT NULL,
  typeID BIGINT,
  description VARCHAR(500),
  value DOUBLE,
  PRIMARY KEY(id),
  CONSTRAINT FK_IT_1 FOREIGN KEY (incidentID)
  REFERENCES incident(incidentID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT

)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE metric (
  metricID BIGINT NOT NULL AUTO_INCREMENT,
  method VARCHAR(100),
  projectID BIGINT NOT NULL DEFAULT -1,
  description VARCHAR(1000),
  title VARCHAR(1000),
  grayl DOUBLE,
  grayu DOUBLE,
  greenl DOUBLE,
  greenu DOUBLE,
  redl DOUBLE,
  redu DOUBLE,
  yellowl DOUBLE,
  yellowu DOUBLE,
  low DOUBLE,
  high DOUBLE,
  configRange BOOLEAN DEFAULT true,
  configLimit BOOLEAN DEFAULT true,
  PRIMARY KEY(metricID)
)AUTO_INCREMENT = 100000 ENGINE = InnoDB;

CREATE TABLE projectmetric (
  id BIGINT NOT NULL AUTO_INCREMENT,
  metricID BIGINT NOT NULL,
  projectID BIGINT NOT NULL, 
  grayl DOUBLE,
  grayu DOUBLE,
  greenl DOUBLE,
  greenu DOUBLE,
  redl DOUBLE,
  redu DOUBLE,
  yellowl DOUBLE,
  yellowu DOUBLE,
  low DOUBLE,
  high DOUBLE,
  configRange BOOLEAN DEFAULT true,
  configLimit BOOLEAN DEFAULT true,
  PRIMARY KEY(id),
 CONSTRAINT FK_METRIC_1 FOREIGN KEY (metricID)
  REFERENCES metric(metricID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT,
 CONSTRAINT FK_METRIC_2 FOREIGN KEY (projectID)
  REFERENCES riskproject(projectID)
  ON DELETE CASCADE
  ON UPDATE RESTRICT

)AUTO_INCREMENT = 100000 ENGINE = InnoDB;
  
 
   -- View to get the jobs and data ready to email results
   
   
 CREATE ALGORITHM=UNDEFINED VIEW `jobemailready` AS 
 select `t1`.`jobID` AS `jobID`,`t1`.`sendEmail` AS `sendEmail`,`t1`.`emailSent` AS `emailSent`,`t1`.`emailTitle` AS `emailTitle`,`t1`.`description` AS `description`,`t1`.`emailFormat` AS `emailFormat`,`t1`.`emailContent` AS `emailContent`,`t1`.`dateEmailSent` AS `dateEmailSent`,`t2`.`resultStr` AS `resultStr`,`t3`.`additionalUsers` AS `additionalUsers`,`t3`.`description` AS `description2`,`t4`.`readyToCollect` AS `readyToCollect`,`t4`.`reportFormat` AS `reportFormat`,`t5`.`email` AS `email`,`t5`.`name` AS `name` 
 from ((((`reportdata` `t1` join `jobresult` `t2` on((`t1`.`jobID` = `t2`.`jobID`))) 
 join `schedjob` `t3` on((`t1`.`schedJobID` = `t3`.`internalID`))) 
 join `jobqueue` `t4` on((`t1`.`jobID` = `t4`.`jobID`))) 
 join `stakeholders` `t5` on((`t3`.`userID` = `t5`.`stakeholderID`))) 
 where ((`t1`.`sendEmail` = 1) and (`t1`.`emailSent` = 0) and (`t4`.`readyToCollect` = 1));
  



 -- Additional Indexes
 

CREATE INDEX INDEX_RISKCONTROLS_1 ON riskcontrols(riskID);
CREATE INDEX INDEX_PROJECTRISKMANAGERS_1 ON projectriskmanagers(projectID);
CREATE INDEX INDEX_PROJECTRISKMANAGERS_2 ON projectriskmanagers(stakeholderID);
CREATE INDEX INDEX_ATTACHMENT_1 ON attachment(hostID);
CREATE INDEX INDEX_RISKPROJECT_1 ON riskproject(lft);
CREATE INDEX INDEX_RISKPROJECT_2 ON riskproject(rgt);
CREATE INDEX INDEX_OBJECTIVE_1 ON objective(lft);
CREATE INDEX INDEX_OBJECTIVE_2 ON objective(rgt);
CREATE INDEX INDEX_RISK_1 ON risk(inherentTolerance);
CREATE INDEX INDEX_RISK_2 ON risk(treatedTolerance);
CREATE INDEX INDEX_RISK_3 ON risk(currentTolerance);
CREATE INDEX INDEX_REVIEWRISK_1 ON reviewrisk(riskID);
CREATE INDEX INDEX_REVIEWRISK_2 ON reviewrisk(reviewID);

 -- Create Triggers

DELIMITER $$

CREATE TRIGGER insertAttachmentTrigger BEFORE INSERT ON attachment
FOR EACH ROW BEGIN
  SET NEW.dateUploaded = SYSDATE();
END $$


CREATE TRIGGER insertProjectAfterTrigger AFTER INSERT ON riskproject
FOR EACH ROW BEGIN
   INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (NEW.projectID, NEW.projectRiskManagerID);
   INSERT INTO projectowners (projectID, stakeholderID) VALUES (NEW.projectID, NEW.projectRiskManagerID);
END $$


CREATE TRIGGER insertObjectiveTrigger BEFORE INSERT ON objective
FOR EACH ROW BEGIN
    IF NEW.parentID IS NULL THEN
        SET NEW.parentID = 1;
    END IF;
END $$

CREATE TRIGGER deleteSessionIDTriggerBefore AFTER DELETE ON reportsession
FOR EACH ROW BEGIN
   DELETE FROM reportsessiondata WHERE  reportsessiondata.sessionID = OLD.sessionID;
END $$


CREATE TRIGGER insertMitStepTrigger BEFORE INSERT ON mitigationstep
FOR EACH ROW BEGIN
   UPDATE risk SET dateMitPrep = CURRENT_DATE WHERE riskID = NEW.riskID AND dateMitPrep IS NULL;
  SET NEW.dateUpdated = CURRENT_TIMESTAMP;
  SET NEW.dateEntered = CURRENT_TIMESTAMP;
END $$


CREATE TRIGGER updateProjectTrigger AFTER UPDATE ON riskproject
FOR EACH ROW BEGIN
  IF OLD.projectCode != NEW.projectCode THEN
  	UPDATE risk set riskProjectCode = CONCAT(NEW.projectCode, SUBSTR(riskProjectCode, LENGTH(OLD.projectCode)+1)) WHERE risk.projectID = OLD.projectID AND OLD.projectCode != NEW.projectCode;
  END IF;

  IF NEW.minimumSecurityLevel > OLD.minimumSecurityLevel THEN
    UPDATE risk SET securityLevel = GREATEST(NEW.minimumSecurityLevel, risk.securityLevel) WHERE risk.projectID = OLD.projectID;
  END IF;
END $$

CREATE TRIGGER insertIncidentBeforeTrigger BEFORE INSERT ON incident
FOR EACH ROW BEGIN

  DECLARE var_incidentIndex BIGINT DEFAULT 1000000;
  DECLARE var_projectCode VARCHAR(6);

  SELECT incidentIndex INTO var_incidentIndex FROM riskproject  WHERE projectID = NEW.projectID;
  SELECT projectCode  INTO var_projectCode FROM riskproject WHERE projectID = NEW.projectID;
   
  UPDATE riskproject SET incidentIndex = incidentIndex+1 WHERE projectID = NEW.projectID;
  SET NEW.incidentProjectCode = CONCAT(var_projectCode,var_incidentIndex);

  IF NEW.bIssue > 0 THEN
     SET NEW.incidentProjectCode = CONCAT("ISS-",NEW.incidentProjectCode);
  ELSE
     SET NEW.incidentProjectCode = CONCAT("IND-",NEW.incidentProjectCode);
  END IF;
 END $$



CREATE TRIGGER insertRiskBeforeTrigger BEFORE INSERT ON risk
FOR EACH ROW BEGIN

  DECLARE var_riskIndex BIGINT DEFAULT 1000000;
  DECLARE var_projectCode VARCHAR(6);
  DECLARE var_tol INT DEFAULT 0;
  DECLARE var_minimumSecurityLevel INT DEFAULT 0;
  DECLARE var_matID BIGINT;
  DECLARE var_singlePhase BOOLEAN DEFAULT FALSE;

  SELECT riskIndex INTO var_riskIndex FROM riskproject  WHERE projectID = NEW.projectID;
  SELECT projectCode  INTO var_projectCode FROM riskproject WHERE projectID = NEW.projectID;
  SELECT minimumSecurityLevel  INTO var_minimumSecurityLevel FROM riskproject WHERE projectID = NEW.projectID;
  SELECT getProjectMatID(NEW.projectID) INTO var_matID;
  SELECT singlePhase  INTO var_singlePhase FROM riskproject WHERE projectID = NEW.projectID;

  
  UPDATE riskproject SET riskIndex = riskIndex+1 WHERE projectID = NEW.projectID;

  SET NEW.securityLevel = GREATEST(var_minimumSecurityLevel, NEW.securityLevel);


	IF NEW.inherentProb IS NULL OR NEW.inherentProb = 0 THEN
  		 SET NEW.inherentProb = 5.5;
	END IF;

	IF NEW.inherentImpact IS NULL OR NEW.inherentImpact = 0 THEN
 		SET NEW.inherentImpact = 5.5;
	END IF;

	IF NEW.treatedProb IS NULL OR NEW.treatedProb = 0 THEN
   		SET NEW.treatedProb = 1.5;
	END IF;

	IF NEW.treatedImpact IS NULL OR NEW.treatedImpact = 0 THEN
   		SET NEW.treatedImpact = 1.5;
	END IF;


--  The next section will take care of synchronising inherent and treated if the project operates in "singlePhase" mode


  IF var_singlePhase THEN
     SET NEW.treatedProb   = NEW.inherentProb;
     SET NEW.treatedImpact = NEW.inherentImpact;
  END IF;


	IF NEW.endExposure IS NULL THEN
   		SET NEW.endExposure = ADDDATE( CURRENT_DATE(), 365);
	END IF;

	IF NEW.startExposure IS NULL THEN
   		SET NEW.startExposure = CURRENT_DATE();
	END IF;

	SET NEW.riskProjectCode = CONCAT(var_projectCode,var_riskIndex);


  SET NEW.matrixID = var_matID;

  SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.inherentProb-1)*tolerancematrix.maximpact+FLOOR (NEW.inherentImpact)),1) FROM tolerancematrix WHERE tolerancematrix.matrixID = var_matID INTO var_tol;
	SET NEW.inherentTolerance = var_tol;

  SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.treatedProb-1)*tolerancematrix.maximpact+FLOOR (NEW.treatedImpact)),1) FROM tolerancematrix WHERE tolerancematrix.matrixID = var_matID INTO var_tol;
	SET NEW.treatedTolerance = var_tol;

  IF NEW.treated > 0 THEN
      SET NEW.currentTolerance = NEW.treatedTolerance;
  ELSE
      SET NEW.currentTolerance = NEW.inherentTolerance;
  END IF;

  IF NEW.primCatID IS NULL THEN
     SET NEW.primCatID = 0;
  END IF;

  IF NEW.secCatID IS NULL THEN
     SET NEW.secCatID = 0;
  END IF;
END $$

CREATE TRIGGER updateRiskTrigger BEFORE UPDATE ON risk
FOR EACH ROW BEGIN

    DECLARE var_tol INT DEFAULT 0;
    DECLARE var_minimumSecurityLevel INT DEFAULT 0;
    DECLARE var_singlePhase BOOLEAN DEFAULT FALSE;
    SELECT minimumSecurityLevel  INTO var_minimumSecurityLevel FROM riskproject WHERE projectID = NEW.projectID;
    SELECT singlePhase  INTO var_singlePhase FROM riskproject WHERE projectID = NEW.projectID;

  SET NEW.securityLevel = GREATEST(var_minimumSecurityLevel, NEW.securityLevel);



  IF NEW.manager2ID IS NULL THEN
      SET NEW.manager2ID = NEW.manager1ID;
  END IF;
  IF NEW.manager3ID = NULL THEN
      SET NEW.manager3ID = NEW.manager1ID;
  END IF;

--  The next section will take care of synchronising inherent and treated if the project operates in "singlePhase" mode


  IF var_singlePhase THEN
      IF NEW.inherentProb != OLD.inherentProb ||  NEW.inherentImpact != OLD.inherentImpact THEN
          SET NEW.treatedProb   = NEW.inherentProb;
          SET NEW.treatedImpact = NEW.inherentImpact;
      END IF;
      IF NEW.treatedProb != OLD.treatedProb ||  NEW.treatedImpact != OLD.treatedImpact THEN
          SET NEW.inherentProb   = NEW.treatedProb;
          SET NEW.inherentImpact = NEW.treatedImpact;
      END IF;
  END IF;


  SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.inherentProb-1)*tolerancematrix.maximpact+FLOOR (NEW.inherentImpact)),1) AS tolStr FROM tolerancematrix WHERE tolerancematrix.matrixID = NEW.matrixID INTO var_tol;
  SET NEW.inherentTolerance = var_tol;

  SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.treatedProb-1)*tolerancematrix.maximpact+FLOOR (NEW.treatedImpact)),1) AS tolStr FROM tolerancematrix WHERE tolerancematrix.matrixID = NEW.matrixID INTO var_tol;
  SET NEW.treatedTolerance = var_tol;


  IF (NEW.treated != OLD.treated || NEW.treatedTolerance != OLD.treatedTolerance) && NEW.treated > 0 THEN
    SET NEW.currentTolerance = NEW.treatedTolerance;
  END IF;

  IF (NEW.treated != OLD.treated || NEW.inherentTolerance != OLD.inherentTolerance) && NEW.treated = 0 THEN
    SET NEW.currentTolerance = NEW.inherentTolerance;
  END IF;



END $$


CREATE TRIGGER updateToleranceMatrixTrigger AFTER UPDATE ON tolerancematrix
FOR EACH ROW BEGIN
      UPDATE risk SET matrixID = matrixID, 
        inherentProb = LEAST(NEW.maxProb+0.5, inherentProb), 
        inherentImpact = LEAST(NEW.maxImpact+0.5, inherentImpact),
        treatedProb = LEAST(NEW.maxProb+0.5, treatedProb), 
        treatedImpact = LEAST(NEW.maxImpact+0.5,treatedImpact) 
        WHERE matrixID = NEW.matrixID;
END $$


CREATE TRIGGER updateMitStepTrigger BEFORE UPDATE ON mitigationstep
FOR EACH ROW BEGIN

  IF NEW.description IS NULL THEN
     SET NEW.description = "Enter Mitigation Action";
  END IF;

  SET  NEW.dateUpdated = CURRENT_TIMESTAMP;
END $$


 -- Stored Procedures

CREATE FUNCTION `checkRiskSecurity`( var_riskID BIGINT, var_userID BIGINT) RETURNS tinyint(1)
 READS SQL DATA
BEGIN


  DECLARE secLev INT;
  DECLARE result BOOLEAN;
  DECLARE numResult INT;
  DECLARE var_projectID BIGINT;
  DECLARE projectMgrID BIGINT;

  SELECT securityLevel from riskdetail where riskID = var_riskID INTO secLev;
  SELECT projectID FROM risk WHERE riskID = var_riskID INTO var_projectID;
  SELECT projectRiskManagerID FROM riskproject WHERE projectID = var_projectID INTO projectMgrID;


  IF secLev = 2 THEN
    SELECT (ownerID = var_userID || manager1ID = var_userID || manager2ID = var_userID || manager3ID = var_userID || projectMgrID = var_userID) FROM risk WHERE RISKID = var_riskID INTO result;
    RETURN result;
  END IF;

  IF secLev = 1 THEN
  
    SELECT COUNT(*) FROM risk WHERE riskID = var_riskID AND var_userID IN (
      SELECT stakeholderID FROM projectowners WHERE projectID = var_projectID
      UNION
      SELECT stakeholderID FROM projectriskmanagers WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)
      UNION 
      SELECT projectRiskManagerID FROM riskproject WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)) INTO numResult;

      IF numResult > 0 THEN RETURN TRUE;
      
    ELSE
      RETURN FALSE;
    END IF;
  END IF;

  RETURN true;

END $$

CREATE FUNCTION `resetPassword`(var_name VARCHAR(200), var_email VARCHAR(500), var_newpass VARCHAR(20)) RETURNS BIGINT
 READS SQL DATA
BEGIN


  DECLARE userID BIGINT;

  SELECT stakeholderID from stakeholders where LOWER(name) = LOWER(var_name) and LOWER(email) = LOWER(var_email) INTO userID;
  IF userID > 0 THEN
    UPDATE stakeholderpassword SET password = PASSWORD(var_newpass) WHERE stakeholderID = userID;
  END IF;

  RETURN userID;

END $$


CREATE PROCEDURE `getFamily`(var_projectID BIGINT,var_userID BIGINT)
 
BEGIN

SELECT  
PC1.parentID, PC1.childID,
S1.riskProjectCode AS pcode, S1.title as ptitle, S1.description as pdescription,
S2.riskProjectCode AS ccode, S2.title as ctitle, S2.description as cdescription
from riskdetail AS S1
JOIN childparent AS PC1 ON S1.riskID = PC1.parentID 
LEFT OUTER JOIN riskdetail AS S2 ON S2.riskID = PC1.childID
WHERE (S1.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = var_projectID UNION SELECT var_projectID) OR (S1.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = var_projectID UNION SELECT var_projectID) AND S1.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = var_projectID  UNION SELECT var_projectID) ) OR S1.promotedProjectID = var_projectID )
AND checkRiskSecurityView(S1.riskID, var_userID, S1.securityLevel, S1.projectID);

END $$

CREATE FUNCTION `resetPasswordWithAnswer`(var_name VARCHAR(200), var_email VARCHAR(500), var_answer VARCHAR(500), var_newpass VARCHAR(20)) RETURNS BIGINT
 READS SQL DATA
BEGIN
  DECLARE userID BIGINT;

  SELECT stakeholderID from stakeholders where LOWER(name) = LOWER(var_name) and LOWER(email) = LOWER(var_email) INTO userID;

  IF userID > 0 THEN
  SELECT stakeholderID from stakeholderpassword WHERE stakeholderID = userID AND secretanswer = PASSWORD(LOWER(var_answer)) INTO userID;
  	IF userID > 0 THEN
    		UPDATE stakeholderpassword SET password = PASSWORD(var_newpass) WHERE stakeholderID = userID AND secretanswer = PASSWORD(LOWER(var_answer));
  	END IF;
  END IF;

  RETURN userID;

END $$

CREATE FUNCTION `checkRiskSecurityView`( var_riskID BIGINT, var_userID BIGINT, secLev BIGINT, var_projectID BIGINT) RETURNS tinyint(1)
    READS SQL DATA    
BEGIN

  DECLARE result BOOLEAN;
  DECLARE numResult INT;
  DECLARE projectMgrID BIGINT;

  SELECT projectRiskManagerID FROM riskproject WHERE projectID = var_projectID INTO projectMgrID; 

  IF secLev = 2 THEN
    SELECT (ownerID = var_userID || manager1ID = var_userID || manager2ID = var_userID || manager3ID = var_userID  || var_userID = projectMgrID) FROM riskdetail WHERE riskID = var_riskID INTO result;
    RETURN result;
  END IF;

  IF secLev = 1 THEN

    SELECT COUNT(*) FROM risk WHERE riskID = var_riskID AND var_userID IN (
      SELECT stakeholderID FROM projectusers WHERE projectID = var_projectID
      UNION
      SELECT stakeholderID FROM projectowners WHERE projectID  IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)
      UNION
      SELECT stakeholderID FROM projectriskmanagers WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)
      UNION 
      SELECT projectRiskManagerID FROM riskproject WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)) INTO numResult;
    IF numResult > 0 THEN
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  END IF;

  RETURN true;

END $$

CREATE FUNCTION `checkReassignSecurity`( var_riskID BIGINT, var_projectID BIGINT, var_userID BIGINT) RETURNS tinyint(1)
 READS SQL DATA
BEGIN

  DECLARE mID BIGINT;
  DECLARE oID BIGINT;
  DECLARE oldProjectID BIGINT;
  DECLARE newProjectMgr BIGINT;
  DECLARE oldProjectMgr BIGINT;
  DECLARE owner BIGINT default 0;
  DECLARE manager BIGINT default 0;
  
  SELECT ownerID INTO oID FROM risk WHERE riskID = var_riskID;
  SELECT manager1ID INTO mID  FROM risk WHERE riskID = var_riskID;
  SELECT projectID INTO oldProjectID FROM risk WHERE riskID = var_riskID;
  SELECT projectRiskManagerID INTO newProjectMgr FROM riskproject WHERE projectID = var_projectID;
  SELECT projectRiskManagerID INTO oldProjectMgr FROM riskproject WHERE projectID = oldProjectID;

  IF var_userID = oldProjectID AND var_userID = newProjectMgr THEN 
   RETURN true;
  END IF;

  IF (var_userID = mID OR var_userID = oID) AND var_userID = newProjectMgr THEN 
   RETURN true;
  END IF;


  SELECT DISTINCT stakeholderID INTO owner FROM projectowners WHERE stakeholderID = var_userID AND projectID = var_projectID;
  IF owner > 0 THEN
     RETURN true;
  END IF;

  SELECT DISTINCT stakeholderID INTO manager FROM projectriskmanagers WHERE stakeholderID = var_userID AND projectID = var_projectID;
  IF manager > 0 THEN
     RETURN true;
  END IF;

  RETURN false;

END $$

CREATE PROCEDURE `updateRiskActive`(IN `var_riskID` BIGINT)
BEGIN
  DECLARE numActiveRiskIncidents BIGINT;
  
  SELECT COUNT(*) INTO numActiveRiskIncidents FROM incidentrisk WHERE riskID = var_riskID;
  IF numActiveRiskIncidents > 0 THEN
    UPDATE risk SET active = true WHERE riskID = var_riskID;
   ELSE
   UPDATE risk SET active = false WHERE riskID = var_riskID;
   END IF;

END $$

CREATE PROCEDURE `reassignRisk`(IN `var_riskID` BIGINT, IN `newProjectID` BIGINT)

BEGIN
  DECLARE var_riskIndex BIGINT DEFAULT 1000000;
  DECLARE var_projectCode VARCHAR(6);

  SELECT riskIndex INTO var_riskIndex FROM riskproject  WHERE projectID = newProjectID;
  SELECT projectCode  INTO var_projectCode FROM riskproject WHERE projectID = newProjectID;

  UPDATE riskproject SET riskIndex = riskIndex+1 WHERE projectID = newProjectID;
  UPDATE risk SET riskProjectCode = CONCAT(var_projectCode,var_riskIndex), projectID = newProjectID WHERE riskID = var_riskID;
END $$

CREATE PROCEDURE `getProjectMats`(IN `projectID` BIGINT)

BEGIN

		SELECT 0 AS generation, tolerancematrix.*, riskproject.projectTitle, riskproject.lft FROM tolerancematrix, riskproject
				 WHERE tolerancematrix.projectID = projectID AND tolerancematrix.projectID = riskproject.projectID
				 UNION
				 SELECT -1 AS generation, tolerancematrix.*, riskproject.projectTitle, riskproject.lft FROM tolerancematrix, riskproject
				 WHERE tolerancematrix.projectID IN (SELECT O2.projectID FROM riskproject AS O1, riskproject AS O2  WHERE O1.lft BETWEEN O2.lft AND O2.rgt  AND O1.projectID = projectID)
				 AND tolerancematrix.projectID = riskproject.projectID
				 AND tolerancematrix.projectID != projectID
				 ORDER BY generation DESC, lft DESC;

END $$

CREATE PROCEDURE `getProjectTolActions`(IN `projectID` BIGINT)

BEGIN

SELECT 0 AS generation, toldescriptors.*, riskproject.projectTitle, riskproject.lft FROM toldescriptors, riskproject
				  WHERE toldescriptors.projectID = projectID AND toldescriptors.projectID = riskproject.projectID
				 UNION
				 SELECT -1 AS generation, toldescriptors.*, riskproject.projectTitle, riskproject.lft FROM toldescriptors, riskproject
						 WHERE toldescriptors.projectID IN (SELECT O2.projectID FROM riskproject AS O1, riskproject AS O2  WHERE O1.lft BETWEEN O2.lft AND O2.rgt  AND O1.projectID = projectID)
				 AND toldescriptors.projectID = riskproject.projectID
				 AND toldescriptors.projectID != projectID
				 ORDER BY generation DESC, lft DESC, tolLevel ASC;

END $$

CREATE PROCEDURE `getProjectObjectives`(IN `varprojectID` BIGINT)

BEGIN
			SELECT -1 AS generation, objective.* FROM objective WHERE objective.projectID IN (SELECT superprojectID from superprojects WHERE projectID = varprojectID)
      UNION
			SELECT 1 AS generation, objective.* FROM objective WHERE objective.projectID IN (SELECT subprojectID from subprojects WHERE projectID = varprojectID)
      UNION
			SELECT 0 AS generation, objective.* FROM objective WHERE objective.projectID = varprojectID;
END $$

CREATE PROCEDURE `getProjectQuanttypes`(IN `varprojectID` BIGINT)

BEGIN
			SELECT -1 AS generation, quantimpacttype.* FROM quantimpacttype WHERE quantimpacttype.projectID IN (SELECT superprojectID from superprojects WHERE projectID = varprojectID)
      UNION
			SELECT 1 AS generation, quantimpacttype.* FROM quantimpacttype WHERE quantimpacttype.projectID IN (SELECT subprojectID from subprojects WHERE projectID = varprojectID)
      UNION
			SELECT 0 AS generation, quantimpacttype.* FROM quantimpacttype WHERE quantimpacttype.projectID = varprojectID;
END $$

CREATE PROCEDURE `getProjectCategories`(IN `varprojectID` BIGINT)

BEGIN
      SELECT -1 AS generation, riskcategory.* FROM riskcategory WHERE riskcategory.contextID IN (SELECT superprojectID from superprojects WHERE projectID = varprojectID) AND riskcategory.internalID != 1
      UNION
	  SELECT 1 AS generation, riskcategory.* FROM riskcategory WHERE riskcategory.contextID IN (SELECT subprojectID from subprojects WHERE projectID = varprojectID) AND riskcategory.internalID != 1
      UNION
	  SELECT 0 AS generation, riskcategory.* FROM riskcategory WHERE riskcategory.contextID = varprojectID AND riskcategory.internalID != 1;
END $$

CREATE PROCEDURE `getWelcomeData`(IN `var_projectID` BIGINT)

BEGIN
  SELECT  riskID AS internalID, 'RISK' AS element,  @rownum:=@rownum+1 AS id, CONCAT (riskProjectCode , CONCAT ( ': ' , title)) AS name, dateEntered AS date, description AS addInfo FROM risk, (SELECT @rownum := 0) r WHERE projectID = var_projectID
  UNION SELECT reviewID AS internalID, 'REVIEW' AS element,  @rownum:=@rownum+1 AS id, title AS name, scheduledDate AS date, actualDate  AS addInfo FROM review WHERE CURRENT_TIMESTAMP < scheduledDate OR actualDate IS NULL
  UNION SELECT incidentID AS internalID, 'INCIDENT' AS element,  @rownum:=@rownum+1 AS id, CONCAT (incidentProjectCode , CONCAT ( ': ' , title)) AS name, dateUpdated AS date, dateIncident  AS addInfo FROM incident
  UNION SELECT riskID AS internalID, 'TASKS' AS element,  @rownum:=@rownum+1 AS id, CONCAT(CONCAT ('Identification Not Approved - ', riskProjectCode) , CONCAT(': ' , title)) AS name, CURRENT_TIMESTAMP AS date , NULL AS addInfo FROM risk WHERE  projectID = var_projectID AND dateIDApp IS NULL
  UNION SELECT riskID AS internalID, 'TASKS' AS element,  @rownum:=@rownum+1 AS id, CONCAT(CONCAT ('Evaluation Not Approved - ', riskProjectCode) , CONCAT(': ' , title)) AS name, CURRENT_TIMESTAMP AS date  , NULL AS addInfo FROM risk WHERE projectID = var_projectID AND  dateEvalApp IS NULL AND dateIDApp IS NOT NULL
  UNION SELECT riskID AS internalID, 'TASKS' AS element,  @rownum:=@rownum+1 AS id, CONCAT(CONCAT ('Mitigation Not Approved - ', riskProjectCode) , CONCAT(': ' , title)) AS name, CURRENT_TIMESTAMP AS date , NULL AS addInfo FROM risk WHERE projectID = var_projectID AND  dateMitApp IS NULL AND dateEvalApp IS NOT NULL AND dateIDApp IS NOT NULL;
END $$

CREATE PROCEDURE `getRisk` (IN `var_user_id` BIGINT, IN `var_riskID` BIGINT, IN `var_context_id` BIGINT)

BEGIN
  IF var_context_id != -1 THEN

       SELECT riskdetail.*, R1.rank AS contextRank  FROM riskdetail
       LEFT OUTER JOIN subjrank AS R1 ON (riskdetail.riskID = R1.riskID  AND  R1.projectID = var_context_id)
       WHERE riskdetail.riskID = var_riskID  AND checkRiskSecurityView(riskdetail.riskID, var_user_id, riskdetail.securityLevel, riskdetail.projectID);

    ELSE

       SELECT riskdetail.*, 0 AS contextRank FROM riskdetail
       WHERE riskdetail.riskID = var_riskID  AND checkRiskSecurityView(riskdetail.riskID, var_user_id, riskdetail.securityLevel, riskdetail.projectID);

    END IF;
END $$

CREATE PROCEDURE `getProjectsForUser`(IN `var_user_id` BIGINT)

BEGIN

				SELECT DISTINCT *  FROM riskproject WHERE projectID IN
					 (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN
				   (SELECT projectID from projectusers WHERE stakeholderID = var_user_id
              UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_user_id
              UNION SELECT projectID from projectowners WHERE stakeholderID = var_user_id));

END $$

CREATE PROCEDURE `getAllocations`()

BEGIN
 SET  @rownum:=0;
SELECT @rownum:=@rownum+1 AS id,3 AS FLAG, riskproject.projectID, lft, rgt, r1.matrixID, r1.RCOUNT, r1.PROB, r1.IMPACT FROM riskproject
	 JOIN
	 (SELECT  COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT ,matrixID, projectID, treated FROM riskview r
	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated
	 HAVING treated = 0
	 UNION
	 SELECT  COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT ,matrixID, projectID, treated FROM riskview r
	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated
	 HAVING treated = 1) AS r1 ON riskproject.projectID = r1.projectID
	 UNION
	 SELECT @rownum:=@rownum+1 AS id,r2.FLAG, riskproject.projectID, lft, rgt, matrixID, r2.RCOUNT, r2.PROB, r2.IMPACT FROM riskproject
	 JOIN
	 (SELECT @rownum:=@rownum+1 AS id,AVG(0) AS FLAG, COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT, matrixID, projectID, treated FROM riskview r
	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated
	 HAVING treated = 0
	 UNION
	 SELECT @rownum:=@rownum+1 AS id,AVG(1) AS FLAG, COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT, matrixID, projectID, treated FROM riskview r
	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated
	 HAVING  treated = 1) AS r2 ON riskproject.projectID = r2.projectID;


END $$

CREATE PROCEDURE `getRolledAllocations`()

BEGIN
 SET  @rownum:=0;
SELECT @rownum:=@rownum+1 AS id,3 AS FLAG, riskproject.projectID, lft, rgt, r1.matrixID, r1.RCOUNT, r1.PROB, r1.IMPACT FROM riskproject
	 JOIN
	 (SELECT  COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT ,matrixID, projectID, treated FROM riskviewparent r 
	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated
	 HAVING treated = 0 
	 UNION
	 SELECT  COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT ,matrixID, projectID, treated FROM riskviewparent r 
	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated
	 HAVING treated = 1) AS r1 ON riskproject.projectID = r1.projectID
	 UNION
	 SELECT @rownum:=@rownum+1 AS id,r2.FLAG, riskproject.projectID, lft, rgt, matrixID, r2.RCOUNT, r2.PROB, r2.IMPACT FROM riskproject
	 JOIN
	 (SELECT @rownum:=@rownum+1 AS id,AVG(0) AS FLAG, COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT, matrixID, projectID, treated FROM riskviewparent r 
	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated
	 HAVING treated = 0
	 UNION
	 SELECT @rownum:=@rownum+1 AS id,AVG(1) AS FLAG, COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT, matrixID, projectID, treated FROM riskviewparent r 
	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated
	 HAVING  treated = 1) AS r2 ON riskproject.projectID = r2.projectID;

END $$

CREATE PROCEDURE `getProjectRisks`(IN `var_userID` BIGINT, IN `var_projectID` BIGINT, IN `var_child` BOOLEAN)
    
BEGIN
        IF var_child > 0 THEN
          SELECT riskdetail.*, r1.rank AS contextRank FROM riskdetail
  	      LEFT OUTER JOIN subjrank AS r1 ON (riskdetail.riskID = r1.riskID  AND  r1.projectID  = var_projectID)
  	      WHERE riskdetail.projectID IN (SELECT p3.projectID  FROM riskproject AS p3, riskproject AS p4 WHERE p3.lft BETWEEN p4.lft AND p4.rgt  AND p4.projectID = var_projectID) 
  	      AND checkRiskSecurityView ( riskdetail.riskID, var_userID, riskdetail.securityLevel, riskdetail.projectID) ORDER BY riskID;
        ELSE
  	      SELECT riskdetail.*, r1.rank AS contextRank FROM riskdetail
  	      LEFT OUTER JOIN subjrank  AS r1 ON (riskdetail.riskID = r1.riskID  AND  r1.projectID  = var_projectID)
  	      WHERE riskdetail.projectID = var_projectID 
  	      AND checkRiskSecurityView ( riskdetail.riskID, var_userID, riskdetail.securityLevel, riskdetail.projectID) ORDER BY riskID;
      END IF;

END $$

CREATE PROCEDURE `analysisGetAllocation`(IN `var_context_id` BIGINT, IN `var_user_id` BIGINT, IN `var_param_id` BIGINT,IN `var_param_str` VARCHAR(500), IN `var_descendants` TINYINT)

BEGIN

      CREATE TEMPORARY TABLE IF NOT EXISTS projs (projID BIGINT) ENGINE=MEMORY;
      DELETE FROM projs;
      CREATE TEMPORARY TABLE IF NOT EXISTS tol1 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;
      DELETE FROM tol1;
      CREATE TEMPORARY TABLE IF NOT EXISTS tol2 (cat VARCHAR(100), label BIGINT, val BIGINT)ENGINE=MEMORY;
      DELETE FROM tol2;
      CREATE TEMPORARY TABLE IF NOT EXISTS tol3 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;
      DELETE FROM tol3;
      CREATE TEMPORARY TABLE IF NOT EXISTS tol4 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;
      DELETE FROM tol4;
      CREATE TEMPORARY TABLE IF NOT EXISTS tol5 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;
      DELETE FROM tol5;
      CREATE TEMPORARY TABLE IF NOT EXISTS results (category VARCHAR(100), label VARCHAR(100),param1 DOUBLE, param2 DOUBLE, param3 DOUBLE, param4 DOUBLE) ENGINE=MEMORY;
      DELETE FROM results;

      IF var_descendants > 0 THEN
            INSERT INTO projs (projID) SELECT O3.internalID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.internalID = var_context_id;
      ELSE
            INSERT INTO projs (projID) VALUES (var_context_id);
      END IF;

      INSERT INTO tol1 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =1 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;
      INSERT INTO tol2 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =2 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;
      INSERT INTO tol3 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =3 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;
      INSERT INTO tol4 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =4 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;
      INSERT INTO tol5 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =5 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;

      INSERT INTO tol1 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =1 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;
      INSERT INTO tol2 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =2 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;
      INSERT INTO tol3 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =3 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;
      INSERT INTO tol4 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =4 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;
      INSERT INTO tol5 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =5 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;

      INSERT INTO tol1 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =1 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;
      INSERT INTO tol2 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =2 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;
      INSERT INTO tol3 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =3 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;
      INSERT INTO tol4 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =4 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;
      INSERT INTO tol5 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =5 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;



      INSERT results (category, label, param1,param2,param3,param4) SELECT 'OWNERS', S.name AS label, T1.val AS param1, T2.val AS param2, T3.val AS param3, T4.val AS param4
      FROM stakeholders AS S
      LEFT OUTER JOIN tol1 AS T1 ON S.internalID = T1.label AND T1.cat = 'OWNERS'
      LEFT OUTER JOIN tol2 AS T2 ON S.internalID = T2.label AND T2.cat = 'OWNERS'
      LEFT OUTER JOIN tol3 AS T3 ON S.internalID = T3.label AND T3.cat = 'OWNERS'
      LEFT OUTER JOIN tol4 AS T4 ON S.internalID = T4.label AND T4.cat = 'OWNERS'
      LEFT OUTER JOIN tol5 AS T5 ON S.internalID = T5.label AND T5.cat = 'OWNERS'
      WHERE S.stakeholderID > 0 AND S.internalID IN (SELECT stakeholderID FROM projectowners WHERE projectID IN(SELECT projID FROM projs));


      INSERT results (category, label, param1,param2,param3,param4) SELECT 'MANAGERS', S.name AS label, T1.val AS param1, T2.val AS param2, T3.val AS param3, T4.val AS param4
      FROM stakeholders AS S
      LEFT OUTER JOIN tol1 AS T1 ON S.internalID = T1.label AND T1.cat = 'MANAGERS'
      LEFT OUTER JOIN tol2 AS T2 ON S.internalID = T2.label AND T2.cat = 'MANAGERS'
      LEFT OUTER JOIN tol3 AS T3 ON S.internalID = T3.label AND T3.cat = 'MANAGERS'
      LEFT OUTER JOIN tol4 AS T4 ON S.internalID = T4.label AND T4.cat = 'MANAGERS'
      LEFT OUTER JOIN tol5 AS T5 ON S.internalID = T5.label AND T5.cat = 'MANAGERS'
      WHERE S.stakeholderID > 0  AND S.internalID IN (SELECT stakeholderID FROM projectriskmanagers WHERE projectID IN(SELECT projID FROM projs));

      INSERT results (category, label, param1,param2,param3,param4) SELECT 'CATEGORIES', S.description AS label, T1.val AS param1, T2.val AS param2, T3.val AS param3, T4.val AS param4
      FROM riskCategory AS S
      LEFT OUTER JOIN tol1 AS T1 ON S.internalID = T1.label AND T1.cat = 'CATEGORIES'
      LEFT OUTER JOIN tol2 AS T2 ON S.internalID = T2.label AND T2.cat = 'CATEGORIES'
      LEFT OUTER JOIN tol3 AS T3 ON S.internalID = T3.label AND T3.cat = 'CATEGORIES'
      LEFT OUTER JOIN tol4 AS T4 ON S.internalID = T4.label AND T4.cat = 'CATEGORIES'
      LEFT OUTER JOIN tol5 AS T5 ON S.internalID = T5.label AND T5.cat = 'CATEGORIES';

      INSERT INTO results (category,label,param1,param2) SELECT 'STATUS','PENDING', currentTolerance, COUNT(*) FROM risk  WHERE SYSDATE() < startExposure AND projectID IN(SELECT projID FROM projs)  GROUP BY risk.currentTolerance;
      INSERT INTO results (category,label,param1,param2) SELECT 'STATUS', 'ACTIVE', currentTolerance, COUNT(*) FROM risk  WHERE SYSDATE() BETWEEN  startExposure AND endExposure AND projectID IN(SELECT projID FROM projs) GROUP BY risk.currentTolerance;
      INSERT INTO results (category,label,param1,param2) SELECT 'STATUS','INACTIVE', currentTolerance, COUNT(*) FROM risk  WHERE SYSDATE() > endExposure AND projectID IN(SELECT projID FROM projs) GROUP BY risk.currentTolerance;


      SELECT * FROM results;
      DROP TEMPORARY TABLE projs;
      DROP TEMPORARY TABLE tol1;
      DROP TEMPORARY TABLE tol2;
      DROP TEMPORARY TABLE tol3;
      DROP TEMPORARY TABLE tol4;
      DROP TEMPORARY TABLE tol5;
      DROP TEMPORARY TABLE results;
END $$

CREATE PROCEDURE `analysisGetRiskGeneral`(IN `var_context_id` BIGINT, IN `var_user_id` BIGINT, IN `var_param_id` BIGINT,IN `var_param_str` VARCHAR(500), IN `var_descendants` TINYINT)

BEGIN

      CREATE TEMPORARY TABLE IF NOT EXISTS projs (projID BIGINT) ENGINE=MEMORY;
      DELETE FROM projs;
      CREATE TEMPORARY TABLE IF NOT EXISTS results (category VARCHAR(100), label VARCHAR(100),param1 DOUBLE, param2 DOUBLE, param3 DOUBLE, param4 DOUBLE) ENGINE=MEMORY;
      DELETE FROM results;

      IF var_descendants > 0 THEN
            INSERT INTO projs (projID) SELECT O3.internalID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.internalID = var_context_id;
      ELSE
            INSERT INTO projs (projID) VALUES (var_context_id);
      END IF;


       INSERT INTO results (category, label, param1) SELECT 'LASTUPDATE',riskProjectCode, DATEDIFF(SYSDATE(),risk.dateUpdated) AS diff FROM risk WHERE projectID IN (SELECT projID FROM projs) ORDER BY diff LIMIT 20;
       INSERT INTO results (category, param1, label) SELECT 'NUMCONTROLS', COUNT(*) AS cnt, risk.riskProjectCode FROM riskcontrols,risk WHERE risk.riskID = riskcontrols.riskID AND risk.projectID IN (SELECT projID FROM projs) GROUP BY riskcontrols.riskID ORDER BY cnt LIMIT 20;
       INSERT INTO results (category, param1, label) SELECT 'NUMMITACTIONS', COUNT(*) AS cnt, risk.riskProjectCode FROM mitigationstep,risk WHERE risk.riskID = mitigationstep.riskID AND risk.projectID IN (SELECT projID FROM projs)  GROUP BY mitigationstep.riskID ORDER BY cnt LIMIT 20;
       INSERT INTO results (category, param1, label) SELECT 'MITCOST', SUM(estCost) AS cost, risk.riskProjectCode FROM mitigationstep,risk WHERE risk.riskID = mitigationstep.riskID AND risk.projectID IN (SELECT projID FROM projs)  GROUP BY mitigationstep.riskID ORDER BY cost LIMIT 20;
       SELECT * FROM results;

      DROP TEMPORARY TABLE results;
      DROP TEMPORARY TABLE projs;

END $$

CREATE PROCEDURE `deleteProject`(IN `var_projectID` BIGINT)

BEGIN

    DECLARE done INT DEFAULT 0;
    DECLARE id BIGINT;
    DECLARE cur1 CURSOR FOR SELECT projectID FROM riskproject WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID);
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;


  IF var_projectID != 1 THEN

    OPEN cur1;
    REPEAT
       FETCH cur1 INTO id;
       IF NOT done THEN
          IF id >1 THEN
             DELETE FROM riskproject WHERE projectID = id;
             DELETE FROM risk WHERE projectID = id;
          END IF;
       END IF;
    UNTIL done END REPEAT;
    CLOSE cur1;

    DELETE FROM riskproject WHERE projectID = var_projectID;
    DELETE FROM risk WHERE projectID = var_projectID;

   END IF;
END $$

CREATE PROCEDURE `getReportSessionID`(IN `var_userID` BIGINT, IN `var_reportName` VARCHAR(1000))

BEGIN
			INSERT INTO reportsession (user_id, reportName) VALUES (var_userID,var_reportName);
END $$

CREATE PROCEDURE `getProject`(IN id BIGINT) 
BEGIN
     SELECT *  FROM riskproject WHERE projectID = id;
END $$

CREATE PROCEDURE `getProjectByTitle`(IN title VARCHAR(1000)) 
BEGIN
     SELECT *  FROM riskproject WHERE projectTitle = title;
END $$

CREATE PROCEDURE `deleteCategory`(IN var_internalID BIGINT, IN var_user_id BIGINT) 
BEGIN
     DELETE FROM riskcategory WHERE internalID = var_internalID AND internalID != 1;
END $$

CREATE PROCEDURE `deleteContextSubjRank`(IN context BIGINT, IN var_user_id BIGINT) 
BEGIN
     DELETE FROM subjrank WHERE projectID = context;
END $$

CREATE PROCEDURE `getProjectIDForUser`(IN var_user_id BIGINT) 
BEGIN
     SELECT DISTINCT projectID  FROM riskproject
				WHERE projectID IN
				 (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN
				     (SELECT projectID from projectusers WHERE stakeholderID = var_user_id UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_user_id UNION SELECT projectID from projectowners WHERE stakeholderID = var_user_id));

END $$

CREATE PROCEDURE `getUserMetric`(IN var_user_id BIGINT) 
BEGIN
			SELECT DISTINCT * FROM metric WHERE id IN (SELECT metricID FROM usermetric WHERE userID = var_user_id);
END $$

CREATE PROCEDURE `getAllProjectRisk`(
			IN var_user_id BIGINT,
			IN var_projectID BIGINT,
			IN var_descendants  BOOLEAN )

BEGIN

  	SELECT risk.*, s1.name AS ownerName, s2.name AS manager1Name, s2.name AS manager2Name, s4.name AS manager3Name, r1.rank AS contextRank, c1.description AS primCatName,  c2.description AS secCatName FROM risk
		LEFT OUTER JOIN stakeholders AS s1 ON risk.ownerID    = s1.stakeholderID
		LEFT OUTER JOIN stakeholders AS s2 ON risk.manager1ID = s2.stakeholderID
		LEFT OUTER JOIN stakeholders AS s3 ON risk.manager2ID = s3.stakeholderID
		LEFT OUTER JOIN stakeholders AS s4 ON risk.manager3ID = s4.stakeholderID
		LEFT OUTER JOIN subjrank     AS r1 ON (risk.riskID = r1.riskID  AND  r1.projectID  = var_projectID)
		LEFT OUTER JOIN riskcategory AS c1 ON risk.primCatID = c1.internalID
		LEFT OUTER JOIN riskcategory AS c2 ON risk.secCatID = c2.internalID
        
		WHERE (var_descendants > 0 AND risk.projectID IN (SELECT o3.projectID  FROM riskproject AS o3, riskproject AS o4 WHERE o3.lft BETWEEN o4.lft AND o4.rgt  AND o4.projectID = var_projectID) AND checkRiskSecurityView(risk.riskID, var_user_id, risk.securityLevel, risk.projectID))
        OR (var_descendants = 0 AND risk.projectID = var_projectID AND checkRiskSecurityView(risk.riskID, var_user_id, risk.securityLevel, risk.projectID));

END $$

CREATE PROCEDURE `insertObjective`(
IN var_projectID BIGINT,
IN var_objective VARCHAR(1000),
IN var_parentID BIGINT,
OUT idx BIGINT
)


BEGIN


-- Objectives are held in a tree like heirarcial structure using the lft/rgt identifies for tree walking

        DECLARE right_most_sibling BIGINT DEFAULT 0;
        SELECT rgt FROM objective WHERE objectiveID = var_parentID INTO right_most_sibling;

        UPDATE objective
        SET lft = CASE WHEN lft > right_most_sibling
                    THEN lft + 2
                    ELSE lft END,
             rgt = CASE WHEN rgt >= right_most_sibling
                    THEN rgt + 2
                    ELSE rgt END
        WHERE rgt >= right_most_sibling;

				INSERT INTO objective (projectID, objective, parentID, lft, rgt) VALUES (var_projectID,var_objective,var_parentID, right_most_sibling,right_most_sibling+1);

        SET idx = LAST_INSERT_ID();

END $$

CREATE PROCEDURE `insertCategory`(IN var_parentID BIGINT, IN var_description VARCHAR(1000), IN var_contextID BIGINT, OUT idx BIGINT)

BEGIN
	INSERT INTO riskcategory (parentID, description, contextID) VALUES (var_parentID,var_description,var_contextID);
	SET idx = LAST_INSERT_ID();
END $$


-- Set of Metric Procedures


CREATE PROCEDURE `getMetricConfig`(IN `var_projectID` BIGINT, IN `var_metric_id` BIGINT)
    
BEGIN

		select metric.title,metric.description,metric.method,metric.metricID,
		projectmetric.projectID,projectmetric.grayl,projectmetric.grayu,projectmetric.greenl,
		projectmetric.greenu,projectmetric.redl,projectmetric.redu,projectmetric.yellowl,
		projectmetric.yellowu,projectmetric.low,projectmetric.high,riskproject.lft
		from metric, projectmetric, riskproject
		where metric.metricID = projectmetric.metricID
		AND projectmetric.projectID IN (SELECT superProjectID FROM superprojects WHERE projectID = var_projectID UNION SELECT var_projectID as superprojectID)
		AND metric.metricID = var_metric_id AND riskproject.projectID = projectmetric.projectID
		UNION
		SELECT
		metric.title,metric.description,metric.method,metric.metricID,metric.projectID,
		metric.grayl,metric.grayu,metric.greenl,metric.greenu,metric.redl,
		metric.redu,metric.yellowl,metric.yellowu,metric.low,metric.high,-1 AS lft
		FROM metric WHERE metric.metricID = var_metric_id ORDER BY lft DESC;
END $$



CREATE PROCEDURE `getTolAllocationMetric`(IN `var_projectID` BIGINT, IN `var_desc` INT)
    
BEGIN
  IF var_desc < 1 THEN
   SELECT count(riskID) AS COUNT, currentTolerance, projectID from risk
   WHERE risk.projectID = var_projectID
   GROUP BY currentTolerance;
  ELSE
   SELECT count(riskID) AS COUNT, currentTolerance, projectID from risk
   WHERE risk.projectID IN (SELECT subprojectID FROM subprojects WHERE projectID = var_projectID  UNION SELECT var_projectID)
   GROUP BY currentTolerance;
  END IF;

END $$


CREATE PROCEDURE `getTolAllocationMetricDeep`(IN `var_projectID` BIGINT)
    
BEGIN

   SELECT count(riskID) AS COUNT, currentTolerance, r1.projectTitle, r1.projectID FROM risk
   JOIN riskproject AS r1 ON risk.projectID = r1.projectID
   WHERE risk.projectID IN (SELECT subprojectID FROM subprojects WHERE projectID = var_projectID UNION SELECT var_projectID)
   GROUP BY r1.projectID, currentTolerance;

END $$



CREATE PROCEDURE `getNumProjectsMetric`(IN `var_projectID` BIGINT, IN `var_userID` BIGINT)
    
BEGIN

  IF var_projectID  < 0 THEN
			SELECT COUNT(*) AS param1 FROM riskproject WHERE projectID IN(
		      SELECT DISTINCT (projectID) FROM riskproject  WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = var_userID
			    UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_userID
 			   UNION SELECT projectID from projectowners WHERE stakeholderID = var_userID)) AND  projectID > 0
    	);

  ELSE
    SELECT COUNT(*) AS param1 FROM riskproject
    WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4  WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID);

  END IF;
END $$


CREATE PROCEDURE `getNumRisksMetric`(IN `var_projectID` BIGINT, IN `var_userID` BIGINT)
    
BEGIN

  IF var_projectID  < 0 THEN
			SELECT COUNT(*) AS param1, 0 AS param2,0 AS param3, 0 AS param4, 0 AS param5 FROM risk WHERE projectID IN(
		      SELECT DISTINCT (projectID) FROM riskproject  WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = var_userID
			    UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_userID
 			   UNION SELECT projectID from projectowners WHERE stakeholderID = var_userID)) AND  projectID > 0
    	);

  ELSE
 			SELECT COUNT(*) AS param1, 0 AS param2,0 AS param3, 0 AS param4, 0 AS param5  FROM risk WHERE projectID IN (
        SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4
				WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID
       );

  END IF;

  END $$


CREATE PROCEDURE `getPercentApprovedMitPlanMetric`(IN `var_projectID` BIGINT, IN `var_userID` BIGINT)
    
BEGIN

  IF var_projectID  < 0 THEN
      SELECT COUNT(*) AS CNT FROM risk WHERE risk.dateMitApp IS NOT NULL AND risk.projectID IN(
          SELECT DISTINCT (projectID) FROM riskproject  WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = var_userID
			    UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_userID
 			   UNION SELECT projectID from projectowners WHERE stakeholderID = var_userID)) AND  projectID > 0
    	);

  ELSE
       SELECT COUNT(*) AS CNT FROM risk WHERE  risk.dateMitApp IS NOT NULL AND projectID IN (
       SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4
        WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID
				);
  END IF;

END $$



-- This procedure creates a new structure for a new user/org.  Triggers on the INSERT mark the entries as being for the user who calls this
-- procedure.

CREATE PROCEDURE `createNewRepository`(IN var_org VARCHAR(1000), IN var_RMID BIGINT, OUT newProjectID BIGINT)

BEGIN

  DECLARE vendorID BIGINT;
  DECLARE regID BIGINT;

  INSERT INTO riskproject ( dateEntered, parentID,lft,rgt,projectRiskManagerID,projectDescription,projectTitle,projectStartDate,projectEndDate,projectCode,tabsToUse,riskIndex) VALUES
     ( CURRENT_TIMESTAMP, -2,1,52,var_RMID,'Corporate Risk Repository',var_org,CURRENT_TIMESTAMP,ADDDATE( CURRENT_DATE(), 365),'RK',8190,30);

  SELECT LAST_INSERT_ID() INTO newProjectID;

  INSERT INTO projectowners (projectID, stakeholderID) VALUES (newProjectID, var_RMID);
  INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (newProjectID, var_RMID);
  INSERT INTO projectusers (projectID, stakeholderID) VALUES (newProjectID, var_RMID);


  INSERT INTO tolerancematrix (dateEntered, projectID,maxprob,maximpact,tolString, prob1, prob2, prob3, prob4, prob5, impact1, impact2, impact3, impact4, impact5) VALUES
   (CURRENT_TIMESTAMP,newProjectID,5,5,'1123312234223443345534455', 'Low','Moderate','Significant', 'High', 'Extreme', 'Low','Moderate','Significant', 'High', 'Extreme');

  INSERT INTO control_effectiveness_defn (projectID,title,description,rank) VALUES
   (newProjectID,'Integrated','The control process is intergrated into the process management lifcycle',5),
   (newProjectID,'Monitored','The control process is monitored on a prescribed basis',4),
   (newProjectID,'Documented','The control process is documented as part of the process procedure',3),
   (newProjectID,'Repeatable','The control process can be repeated',2),
   (newProjectID,'Ad Hoc','The control process is none of the above',1);


  INSERT INTO toldescriptors (projectID,shortName,longName,tolAction,tolLevel) VALUES
   (newProjectID,'Extreme','Management Critical','',5),
   (newProjectID,'High','Active Management Required','',4),
   (newProjectID,'Significant','Continuous Monitoring Required','',3),
   (newProjectID,'Moderate','Periodic Monitoring','',2),
   (newProjectID,'Low','Monitoring','',1);

	INSERT INTO riskcategory (parentID,description,contextID) VALUES (1,'Vendor',newProjectID);
	SELECT LAST_INSERT_ID() INTO vendorID;
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Supply',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Billing',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Delivery',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Account Management',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (1,'Financial',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (1,'Safety',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (1,'Regulatory',newProjectID);
	SELECT LAST_INSERT_ID() INTO regID;
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'Local Government',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'Industry',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'Security Commision',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'State Government',newProjectID);
	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'National Government',newProjectID);


END $$

CREATE PROCEDURE `addUserToRep`(IN var_ID VARCHAR (100), IN var_name VARCHAR(20), IN var_email VARCHAR (200), IN autoEnable BOOLEAN)

BEGIN
  DECLARE projectID BIGINT;

  IF autoEnable = TRUE THEN
    SELECT projectID INTO projectID from riskproject WHERE parentID = -2;
    INSERT INTO projectusers (stakeholderID, projectID) VALUES (var_ID,projectID);
  END IF;
END $$

CREATE PROCEDURE `addExistingUserToRep`(IN var_ID VARCHAR (100), IN var_repID BIGINT, IN autoEnable BOOLEAN)

BEGIN
  DECLARE var_projectID BIGINT;

  DELETE FROM qrmlogin.userrepository WHERE repID = var_repID AND stakeholderID = var_ID;
  INSERT INTO qrmlogin.userrepository (stakeholderID, repID) VALUES (var_ID, var_repID);


  IF autoEnable = TRUE THEN
    SELECT projectID INTO var_projectID from riskproject WHERE parentID = -2 AND projectID > 0;
    INSERT INTO projectusers (stakeholderID, projectID) VALUES (var_ID,var_projectID);
  END IF;
END$$

CREATE PROCEDURE `addProject`(
IN `parent` BIGINT,
IN `projectDescription` VARCHAR(5000),
IN `projectTitle` VARCHAR(500),
IN `projectCode` VARCHAR(6),
IN `projectRiskManagerID` BIGINT,
IN `projectStartDate` DATETIME,
IN `projectEndDate` DATETIME,
IN `var_minimumSecurityLevel` INT,
OUT  idx BIGINT)

BEGIN


-- Projects are held in a tree like heirarcial structure using the lft/rgt identifies for tree walking


      DECLARE right_most_sibling INT;
      DECLARE var_singlePhase BOOLEAN;
      DECLARE var_useAdvancedLiklihood BOOLEAN;
      DECLARE var_useAdvancedConsequences BOOLEAN;

      SELECT rgt INTO right_most_sibling FROM riskproject WHERE projectID = parent;
      SELECT singlePhase INTO var_singlePhase FROM riskproject WHERE projectID = parent;
      SELECT useAdvancedConsequences INTO var_useAdvancedConsequences FROM riskproject WHERE projectID = parent;
      SELECT useAdvancedLiklihood INTO var_useAdvancedLiklihood FROM riskproject WHERE projectID = parent;


      IF right_most_sibling IS NOT NULL THEN
         UPDATE riskproject
         SET lft = CASE WHEN lft > right_most_sibling
                    THEN lft + 2
                    ELSE lft END,
         rgt = CASE WHEN rgt >= right_most_sibling
                    THEN rgt + 2
                    ELSE rgt END
         WHERE rgt >= right_most_sibling;

         INSERT INTO riskproject (parentID, projectDescription , projectTitle, projectCode, projectRiskManagerID, projectStartDate,projectEndDate,lft, rgt, singlePhase, useAdvancedConsequences, useAdvancedLiklihood, minimumSecurityLevel) VALUES (parent, projectDescription , projectTitle, projectCode, projectRiskManagerID, projectStartDate,projectEndDate, right_most_sibling, right_most_sibling+1, var_singlePhase, var_useAdvancedConsequences, var_useAdvancedLiklihood, var_minimumSecurityLevel);
         SET idx = LAST_INSERT_ID();
      ELSE
       SET idx = -1;
      END IF;


-- Ensure that the users are inherited from the parent

   IF idx > 0 THEN
       INSERT INTO projectowners (stakeholderID, projectID) SELECT stakeholderID, idx  FROM projectowners WHERE projectID = parent;
       INSERT INTO projectriskmanagers (stakeholderID, projectID) SELECT stakeholderID, idx  FROM projectriskmanagers WHERE projectID = parent;
       INSERT INTO projectusers (stakeholderID, projectID) SELECT stakeholderID, idx  FROM projectusers WHERE projectID = parent;
   END IF;
END $$

CREATE PROCEDURE `setSecurity`( IN userName VARCHAR(100) )

BEGIN
   GRANT SELECT, INSERT, UPDATE, DELETE ON risk TO userName;
END $$


CREATE FUNCTION `getProjectMatID`( var_projID BIGINT) RETURNS BIGINT
    READS SQL DATA    
BEGIN

  DECLARE matID BIGINT;

  SELECT matrixID INTO matID from tolerancematrix
  LEFT JOIN superprojects ON tolerancematrix.projectID = superprojects.superprojectID
  WHERE superprojects.projectID = var_projID ORDER BY superprojects.superprojectID DESC LIMIT 1; 

  IF matID IS NULL THEN
    SELECT matrixID INTO matID FROM tolerancematrix WHERE projectID = var_projID;
  END IF;

  RETURN matID;

END$$


 --  Procedures for reports


CREATE PROCEDURE `reportGetProjectRisks`( IN userID BIGINT, IN projectID BIGINT, IN descendats BOOLEAN )
    
BEGIN

 SELECT riskdetail.*  FROM riskdetail
 WHERE ((riskdetail.projectID = projectID) OR (descendats > 0 AND riskdetail.projectID IN  (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4  WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = projectID) ))
 AND (checkRiskSecurityView(riskdetail.riskID, userID, riskdetail.securityLevel, riskdetail.projectID));

END$$

CREATE PROCEDURE `reportGetAuditComments`()

BEGIN

 select auditcomments.*, S1.stakeholderID, S1.name AS enteredByName
 from auditcomments
 left outer join stakeholders AS S1
 on auditcomments.enteredByID = S1.stakeholderID;

END $$

CREATE PROCEDURE `reportGetContexts`()

BEGIN

 select riskproject.*, stakeholders.*
 from riskproject
 left outer join stakeholders
 on riskproject.projectRiskManagerID = stakeholders.stakeholderID;

END $$

CREATE PROCEDURE `reportGetControls`()

BEGIN

select riskcontrols.*, control_effectiveness_defn.title
 from riskcontrols
 left outer join control_effectiveness_defn
 on riskcontrols.effectiveness = control_effectiveness_defn.controlEffectivenessID;
END $$

CREATE PROCEDURE `reportGetIncidents`()

BEGIN

select incident.*, stakeholders.name, stakeholders.stakeholderID
 from incident
 left outer join stakeholders
 on incident.reportedByID = stakeholders.stakeholderID ;

END $$

CREATE PROCEDURE `reportGetSessionData`(IN var_SessionID BIGINT)

BEGIN

 select * from reportsessiondata
 where reportsessiondata.sessionID = var_SessionID
 order by reportsessiondata.rank;

END $$

CREATE PROCEDURE `reportGetReportRisks`(IN var_SessionID BIGINT, IN var_userID BIGINT)

BEGIN

 select * from reportrisks
 where reportrisks.sessionID = var_SessionID AND checkRiskSecurityView(reportrisks.riskID, var_userID, reportrisks.securityLevel, reportrisks.projectID)
 order by rank;

END $$
DELIMITER ;



 -- Seed Data
 
SET FOREIGN_KEY_CHECKS=0;

INSERT INTO analysistools (id,title,clazz,param1,b3d,bTol, bMatrix,bContext,bDescend,bNumElem,bReverse) VALUES
 (1,'Mitigation Cost Analysis','au.com.quaysystems.qrm.server.analysis.GeneralRiskAnalysis','cost',true,false,false,true,true,true,true),
 (2,'Days Since Update Analysis','au.com.quaysystems.qrm.server.analysis.GeneralRiskAnalysis','days',true,false,false,true,true,true,true),
 (3,'Number of Controls Analysis','au.com.quaysystems.qrm.server.analysis.GeneralRiskAnalysis','count',true,false,false,true,true,true,true),
 (4,'Mitigation Steps Analysis','au.com.quaysystems.qrm.server.analysis.GeneralRiskAnalysis','mitsteps',true,false,false,true,true,true,true),
 (5,'Allocation By Risk Owner','au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis','owners',true,true,false,true,true,false,false),
 (6,'Allocation by Risk Manager','au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis','managers',true,true,false,true,true,false,false),
 (7,'Allocation By Risk Status','au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis','status',true,true,false,true,true,false,false),
 (8,'Allocation By Risk Category','au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis','categories',true,true,false,true,true,false,false),
 (9,'Contingency Cost Analysis','au.com.quaysystems.qrm.server.analysis.GeneralRiskAnalysis','contingency',true,false,false,true,true,true,true),
 (10,'Weighted Contingency Cost Analysis','au.com.quaysystems.qrm.server.analysis.GeneralRiskAnalysis','contingencyWeighted',true,false,false,true,true,true,true),
 (11,'Allocation By Tolerance','au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis','tolerance',true,false,false,false,false,false,false),
 (12,'Multi Project Tolerance','au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis','multitolerance',true,false,false,false,false,false,false);

SET foreign_key_checks = 0;

INSERT INTO riskproject (dateEntered, projectID,parentID,lft,rgt,projectRiskManagerID,projectDescription,projectTitle,projectStartDate,projectEndDate,projectCode,tabsToUse,riskIndex) VALUES
  ( CURRENT_TIMESTAMP, -2,-2,1,52,1,'Virtual Root','Virtual Root Project',CURRENT_TIMESTAMP,ADDDATE( CURRENT_DATE(), 365),null,8190,30);

INSERT INTO objective (objectiveID,projectID,parentID,objective,lft,rgt) VALUES
 (1,1,1,'ROOT OBJECTIVE',1,30);


INSERT INTO quantimpacttype (typeID,description,costCategroy,costSummary,nillQuantImpact,units,projectID) VALUES
 (-1,'Cost Summary',1,1,0,'$ dollars',1),
 (1,'General Cost',1,0,0,'$ dollars',1),
 (2,'Un Specified Contingency Allowance',1,0,0,'$ dollars',1),
 (3,'Non Quantifiable',0,0,1,'',1);

INSERT INTO riskcategory (internalID,parentID,description,contextID) VALUES  (1,1,'Root Risk Category',1);


-- INSERT INTO metric (method, description, title) VALUES ("numProjects", "The total number of projects under this context", "Number of Projects");
-- INSERT INTO metric (method, description, title) VALUES ("numRisks", "The total number of risks under this context", "Number of Risks");
-- INSERT INTO metric (method, description, title) VALUES ("percentApproveMitPlanMetric", "The total number of project under this context", "Percentage of Approved Mitigation Plans");

INSERT INTO metric (method, description, title, configRange, configLimit) VALUES ("toleranceAllocation", "The total number of project under this context", "Tolerance Allocation", false,false);
INSERT INTO metric (method, description, title, configRange, configLimit) VALUES ("statusAllocation", "The total number of project under this context", "Exposure Status Allocation", false,false);
INSERT INTO metric (method, description, title, configRange, configLimit) VALUES ("treatmentAllocation", "The total number of project under this context", "Treatment Allocation", false,false);

INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100000,"Tolerance Format", "Tolerannce Fromat", "QRM Registry Report - Tolerance Format", 1,0,'REGISTER');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100001,"Date Format", "Date Fromat", "QRM Registry Report - Date Format", 1,0,'REGISTER');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100002,"Impact Format", "Impact Fromat", "QRM Registry Report - Impact Format", 1,0,'REGISTER');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100003,"Treatment Format", "Treatment Fromat", "QRM Registry Report - Treatment Format", 1,0,'REGISTER');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100004,"Risk Manager Summary", "Risk Manager Summary", "QRM Project Managers Report", 1,0,'PROJECT');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100005,"Risk Owner Summary", "Risk Owner Summary", "QRM Project Owners Report", 1,0,'PROJECT');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(100006,"Risk Mitigation Detail", "Risk Mitigation Detail", "QRM Project Mitigation Report", 1,0,'PROJECT');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor) VALUES(100007,"Allocation Report", "Allocation Report", "QRM Allocation Report", 1,0,'PROJECT','au.com.quaysystems.qrm.server.report.ToleranceAllocationReportVisitor');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor) VALUES(100008,"QRM Multi Analysis Report", "QRM Multi Analysis Report", "QRM Multi Analysis Report", 1,0,'PROJECT','au.com.quaysystems.qrm.server.report.AnalysisReportVisitor');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor) VALUES(-1,"Monte Report", "Monte Report", "MonteReport", 1,0,NULL,'au.com.quaysystems.qrm.server.report.MonteReportVisitor');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor) VALUES(-100, "Tolerance Matrix", "Tolerance Matrix", "QRM Relative Matrix Report", 1,0,'PROJECT','au.com.quaysystems.qrm.server.report.RelMatrixReportVisitor');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor) VALUES(-200,"Subjective Rank Report", "Subjective Rank Report", "QRM Subjective Rank Report", 1,0,'PROJECT','au.com.quaysystems.qrm.server.report.SubjectiveRankingReportVisitor');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor) VALUES(-400,"QRM Multi Analysis Report", "QRM Multi Analysis Report", "QRM Multi Analysis Report", 1,0,'PROJECT','au.com.quaysystems.qrm.server.report.AnalysisReportVisitor');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype) VALUES(-300, "Summary Risk Allocation", "Summary Risk Allocation", "QRM Sorter Report", 1,0,'PROJECT');
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor, detailConfigWindow) VALUES(-10001,"Detail Risk Report", "Detail Risk Report", "RiskReport", 1,0,'RISK','au.com.quaysystems.qrm.server.report.RiskReportVisitor', true);
INSERT INTO reports (internalID, reportName, reportDescription,reportFileName, coreFile, template, reporttype, visitor, detailConfigWindow, excelOnlyFormat) VALUES(-50000,"Detail Risk Report (Excel)", "Risk Detail (Excel)", "RiskReportExcel", 1,0,'RISK','au.com.quaysystems.qrm.server.report.RiskReportExcelVisitor', false, 1);

ALTER TABLE reports AUTO_INCREMENT = 200000;
ALTER TABLE objective AUTO_INCREMENT = 100000;
ALTER TABLE riskproject AUTO_INCREMENT = 100000;
ALTER TABLE control_effectiveness_defn AUTO_INCREMENT = 100000;
ALTER TABLE quantimpacttype AUTO_INCREMENT = 100000;
ALTER TABLE riskcategory AUTO_INCREMENT = 100000;
ALTER TABLE toldescriptors AUTO_INCREMENT = 100000;
ALTER TABLE analysistools AUTO_INCREMENT = 100000;

SET foreign_key_checks = 1;