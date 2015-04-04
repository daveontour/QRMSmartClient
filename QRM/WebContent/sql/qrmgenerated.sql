SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


-- -----------------------------------------------------
-- Table `analysistools`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `analysistools` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(1000) NOT NULL,
  `clazz` VARCHAR(1000) NOT NULL,
  `param1` VARCHAR(1000) NULL DEFAULT NULL,
  `b3D` TINYINT(1) NULL DEFAULT NULL,
  `bTol` TINYINT(1) NULL DEFAULT NULL,
  `bMatrix` TINYINT(1) NULL DEFAULT NULL,
  `bContext` TINYINT(1) NULL DEFAULT NULL,
  `bDescend` TINYINT(1) NULL DEFAULT NULL,
  `bNumElem` TINYINT(1) NULL DEFAULT NULL,
  `bReverse` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `attachment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attachment` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `hostID` BIGINT(20) NOT NULL,
  `hostType` ENUM('RISK','PROJECT','INCIDENT','REVIEW','DELIVERABVLE','ISSUE','ISSUEACTION','TEMPFILE') NULL DEFAULT NULL,
  `attachmentURL` VARCHAR(2000) NULL DEFAULT NULL,
  `attachmentFileName` VARCHAR(2000) NULL DEFAULT NULL,
  `fileType` VARCHAR(100) NULL DEFAULT NULL,
  `dateUploaded` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` VARCHAR(2000) NULL DEFAULT NULL,
  `contents` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `hostID` (`hostID` ASC),
  INDEX `INDEX_ATTACHMENT_1` (`hostID` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `riskproject`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskproject` (
  `projectID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parentID` BIGINT(20) NULL DEFAULT NULL,
  `lft` INT(11) NOT NULL,
  `rgt` INT(11) NOT NULL,
  `projectRiskManagerID` BIGINT(20) NULL DEFAULT NULL,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `projectDescription` VARCHAR(5000) NULL DEFAULT NULL,
  `projectTitle` VARCHAR(500) NULL DEFAULT NULL,
  `projectStartDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `projectEndDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `projectCode` VARCHAR(6) NULL DEFAULT NULL,
  `minimumSecurityLevel` INT(11) NOT NULL DEFAULT '1',
  `tabsToUse` BIGINT(20) NOT NULL DEFAULT '8190',
  `riskIndex` BIGINT(20) NOT NULL DEFAULT '0',
  `incidentIndex` BIGINT(20) NOT NULL DEFAULT '0',
  `securityMask` BIGINT(20) NOT NULL DEFAULT '63',
  `singlePhase` TINYINT(1) NOT NULL DEFAULT '0',
  `useAdvancedConsequences` TINYINT(1) NOT NULL DEFAULT '1',
  `useAdvancedLiklihood` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`projectID`),
  INDEX `FK_RISKPROJECT_4` (`parentID` ASC),
  INDEX `FK_RISKPROJECT_3` (`projectRiskManagerID` ASC),
  INDEX `INDEX_RISKPROJECT_1` (`lft` ASC),
  INDEX `INDEX_RISKPROJECT_2` (`rgt` ASC),
  CONSTRAINT `FK_RISKPROJECT_3`
    FOREIGN KEY (`projectRiskManagerID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`),
  CONSTRAINT `FK_RISKPROJECT_4`
    FOREIGN KEY (`parentID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100026
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `tolerancematrix`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tolerancematrix` (
  `matrixID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `maxprob` SMALLINT(6) NULL DEFAULT NULL,
  `maximpact` SMALLINT(6) NULL DEFAULT NULL,
  `tolString` VARCHAR(64) NULL DEFAULT NULL,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `prob1` VARCHAR(100) NULL DEFAULT NULL,
  `prob2` VARCHAR(100) NULL DEFAULT NULL,
  `prob3` VARCHAR(100) NULL DEFAULT NULL,
  `prob4` VARCHAR(100) NULL DEFAULT NULL,
  `prob5` VARCHAR(100) NULL DEFAULT NULL,
  `prob6` VARCHAR(100) NULL DEFAULT NULL,
  `prob7` VARCHAR(100) NULL DEFAULT NULL,
  `prob8` VARCHAR(100) NULL DEFAULT NULL,
  `probVal1` DOUBLE NOT NULL DEFAULT '20',
  `probVal2` DOUBLE NOT NULL DEFAULT '40',
  `probVal3` DOUBLE NOT NULL DEFAULT '60',
  `probVal4` DOUBLE NOT NULL DEFAULT '70',
  `probVal5` DOUBLE NOT NULL DEFAULT '100',
  `probVal6` DOUBLE NOT NULL DEFAULT '100',
  `probVal7` DOUBLE NOT NULL DEFAULT '100',
  `probVal8` DOUBLE NOT NULL DEFAULT '100',
  `impact1` VARCHAR(100) NULL DEFAULT NULL,
  `impact2` VARCHAR(100) NULL DEFAULT NULL,
  `impact3` VARCHAR(100) NULL DEFAULT NULL,
  `impact4` VARCHAR(100) NULL DEFAULT NULL,
  `impact5` VARCHAR(100) NULL DEFAULT NULL,
  `impact6` VARCHAR(100) NULL DEFAULT NULL,
  `impact7` VARCHAR(100) NULL DEFAULT NULL,
  `impact8` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`matrixID`),
  INDEX `FX_MATRIX_1` (`projectID` ASC),
  CONSTRAINT `FX_MATRIX_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100001
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `risk`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `risk` (
  `riskID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `externalID` VARCHAR(500) NULL DEFAULT NULL,
  `projectID` BIGINT(20) NOT NULL,
  `promotedProjectID` BIGINT(20) NULL DEFAULT NULL,
  `riskProjectCode` VARCHAR(30) NULL DEFAULT NULL,
  `securityLevel` INT(11) NOT NULL DEFAULT '0',
  `ownerID` BIGINT(20) NOT NULL,
  `manager1ID` BIGINT(20) NOT NULL,
  `manager2ID` BIGINT(20) NULL DEFAULT NULL,
  `manager3ID` BIGINT(20) NULL DEFAULT NULL,
  `matrixID` BIGINT(20) NOT NULL DEFAULT '1',
  `title` VARCHAR(200) NULL DEFAULT NULL,
  `description` VARCHAR(5000) NULL DEFAULT NULL,
  `mitPlanSummary` VARCHAR(5000) NULL DEFAULT NULL,
  `mitPlanSummaryUpdate` VARCHAR(5000) NULL DEFAULT NULL,
  `impact` VARCHAR(5000) NULL DEFAULT NULL,
  `cause` VARCHAR(5000) NULL DEFAULT NULL,
  `consequences` VARCHAR(5000) NULL DEFAULT NULL,
  `impSafety` TINYINT(1) NULL DEFAULT '0',
  `impSpec` TINYINT(1) NULL DEFAULT '0',
  `impCost` TINYINT(1) NULL DEFAULT '0',
  `impTime` TINYINT(1) NULL DEFAULT '0',
  `impReputation` TINYINT(1) NULL DEFAULT '0',
  `impEnvironment` TINYINT(1) NULL DEFAULT '0',
  `inherentProb` DOUBLE NULL DEFAULT NULL,
  `inherentImpact` DOUBLE NULL DEFAULT NULL,
  `treatedProb` DOUBLE NULL DEFAULT NULL,
  `treatedImpact` DOUBLE NULL DEFAULT NULL,
  `startExposure` DATE NULL DEFAULT NULL,
  `endExposure` DATE NULL DEFAULT NULL,
  `inherentTolerance` SMALLINT(6) NULL DEFAULT NULL,
  `treatedTolerance` SMALLINT(6) NULL DEFAULT NULL,
  `currentTolerance` SMALLINT(6) NULL DEFAULT NULL,
  `subjectiveRank` INT(11) NULL DEFAULT NULL,
  `objectiveRank` INT(11) NULL DEFAULT NULL,
  `levelRank` INT(11) NULL DEFAULT NULL,
  `active` SMALLINT(6) NULL DEFAULT '1',
  `treated` SMALLINT(6) NULL DEFAULT '0',
  `dateUpdated` DATE NULL DEFAULT NULL,
  `timeUpdated` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dateEvalApp` DATE NULL DEFAULT NULL,
  `dateEvalRev` DATE NULL DEFAULT NULL,
  `dateIDApp` DATE NULL DEFAULT NULL,
  `dateIDRev` DATE NULL DEFAULT NULL,
  `dateMitApp` DATE NULL DEFAULT NULL,
  `dateMitPrep` DATE NULL DEFAULT NULL,
  `dateMitRev` DATE NULL DEFAULT NULL,
  `idEvalApp` BIGINT(20) NULL DEFAULT NULL,
  `idEvalRev` BIGINT(20) NULL DEFAULT NULL,
  `idIDApp` BIGINT(20) NULL DEFAULT NULL,
  `idIDRev` BIGINT(20) NULL DEFAULT NULL,
  `idMitApp` BIGINT(20) NULL DEFAULT NULL,
  `idMitPrep` BIGINT(20) NULL DEFAULT NULL,
  `idMitRev` BIGINT(20) NULL DEFAULT NULL,
  `treatmentID` BIGINT(20) NULL DEFAULT NULL,
  `treatmentAvoidance` TINYINT(1) NULL DEFAULT '0',
  `treatmentReduction` TINYINT(1) NULL DEFAULT '0',
  `treatmentRetention` TINYINT(1) NULL DEFAULT '0',
  `treatmentTransfer` TINYINT(1) NULL DEFAULT '0',
  `summaryRisk` TINYINT(1) NULL DEFAULT '0',
  `parentSummaryRisk` BIGINT(20) NULL DEFAULT NULL,
  `private` TINYINT(1) NOT NULL DEFAULT '1',
  `restricted` TINYINT(1) NOT NULL DEFAULT '1',
  `pub` TINYINT(1) NOT NULL DEFAULT '0',
  `liketype` INT(11) NULL DEFAULT '4',
  `likeprob` DOUBLE NULL DEFAULT NULL,
  `likealpha` DOUBLE NULL DEFAULT NULL,
  `liket` DOUBLE NULL DEFAULT NULL,
  `likepostType` INT(11) NULL DEFAULT '4',
  `likepostProb` DOUBLE NULL DEFAULT NULL,
  `likepostAlpha` DOUBLE NULL DEFAULT NULL,
  `likepostT` DOUBLE NULL DEFAULT NULL,
  `primCatID` BIGINT(20) NULL DEFAULT NULL,
  `secCatID` BIGINT(20) NULL DEFAULT NULL,
  `estimatedContingencey` DOUBLE NOT NULL DEFAULT '0',
  `useCalculatedContingency` TINYINT(1) NULL DEFAULT '0',
  `preMitContingency` DOUBLE NOT NULL DEFAULT '0',
  `postMitContingency` DOUBLE NOT NULL DEFAULT '0',
  `preMitContingencyWeighted` DOUBLE NOT NULL DEFAULT '0',
  `postMitContingencyWeighted` DOUBLE NOT NULL DEFAULT '0',
  `contingencyPercentile` DOUBLE NOT NULL DEFAULT '0',
  `mitigationCost` DOUBLE NULL DEFAULT '0',
  `useCalculatedProb` TINYINT(1) NOT NULL DEFAULT '0',
  `extObject` BLOB NULL DEFAULT NULL,
  `preMitImage` MEDIUMBLOB NULL DEFAULT NULL,
  `postMitImage` MEDIUMBLOB NULL DEFAULT NULL,
  `matImage` MEDIUMBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`riskID`),
  INDEX `FK_RISK_2` (`ownerID` ASC),
  INDEX `FK_RISK_3` (`manager1ID` ASC),
  INDEX `FK_RISK_4` (`manager2ID` ASC),
  INDEX `FK_RISK_5` (`manager3ID` ASC),
  INDEX `FK_RISK_6` (`matrixID` ASC),
  INDEX `FK_RISK_7` (`projectID` ASC),
  INDEX `INDEX_RISK_1` (`inherentTolerance` ASC),
  INDEX `INDEX_RISK_2` (`treatedTolerance` ASC),
  INDEX `INDEX_RISK_3` (`currentTolerance` ASC),
  CONSTRAINT `FK_RISK_2`
    FOREIGN KEY (`ownerID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`),
  CONSTRAINT `FK_RISK_3`
    FOREIGN KEY (`manager1ID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`),
  CONSTRAINT `FK_RISK_4`
    FOREIGN KEY (`manager2ID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`)
    ON DELETE SET NULL
    ON UPDATE SET NULL,
  CONSTRAINT `FK_RISK_5`
    FOREIGN KEY (`manager3ID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`)
    ON DELETE SET NULL
    ON UPDATE SET NULL,
  CONSTRAINT `FK_RISK_6`
    FOREIGN KEY (`matrixID`)
    REFERENCES `tolerancematrix` (`matrixID`),
  CONSTRAINT `FK_RISK_7`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100500
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `auditcomments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `auditcomments` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `schedReviewDate` DATE NULL DEFAULT NULL,
  `projectID` BIGINT(20) NULL DEFAULT NULL,
  `riskID` BIGINT(20) NULL DEFAULT NULL,
  `enteredByID` BIGINT(20) NULL DEFAULT NULL,
  `comment` VARCHAR(5000) NULL DEFAULT NULL,
  `commenturl` VARCHAR(1000) NULL DEFAULT NULL,
  `attachment` LONGBLOB NULL DEFAULT NULL,
  `attachmentFileName` VARCHAR(1000) NULL DEFAULT NULL,
  `identification` SMALLINT(6) NULL DEFAULT '0',
  `evaluation` SMALLINT(6) NULL DEFAULT '0',
  `mitigation` SMALLINT(6) NULL DEFAULT '0',
  `review` SMALLINT(6) NULL DEFAULT '0',
  `approval` SMALLINT(6) NULL DEFAULT '0',
  `schedReview` SMALLINT(6) NULL DEFAULT '0',
  `schedReviewID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `auditcomments_FK1` (`riskID` ASC),
  CONSTRAINT `auditcomments_FK1`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 102450
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `childparent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `childparent` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `childID` BIGINT(20) NOT NULL,
  `parentID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `parentID` (`parentID` ASC),
  INDEX `FK_RISKCHILDPARENT_1` (`childID` ASC),
  CONSTRAINT `FK_RISKCHILDPARENT_1`
    FOREIGN KEY (`childID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_RISKCHILDPARENT_2`
    FOREIGN KEY (`parentID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `control_effectiveness_defn`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `control_effectiveness_defn` (
  `controlEffectivenessID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `title` VARCHAR(100) NULL DEFAULT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `rank` SMALLINT(6) NULL DEFAULT NULL,
  PRIMARY KEY (`controlEffectivenessID`),
  INDEX `FK_CONTROL_DEFN_1` (`projectID` ASC),
  CONSTRAINT `FK_CONTROL_DEFN_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100005
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `emailrecord`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `emailrecord` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `emaildate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `successful` TINYINT(1) NOT NULL DEFAULT '1',
  `errorMsg` VARCHAR(1000) NULL DEFAULT NULL,
  `messageObject` LONGTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `importtemplate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `importtemplate` (
  `templateID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `userID` BIGINT(20) NULL DEFAULT NULL,
  `templateName` VARCHAR(400) NULL DEFAULT NULL,
  `template` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`templateID`),
  INDEX `FK_TEMPLATE_1` (`userID` ASC),
  CONSTRAINT `FK_TEMPLATE_1`
    FOREIGN KEY (`userID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `incident`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `incident` (
  `incidentID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `incidentProjectCode` VARCHAR(30) NULL DEFAULT NULL,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `contextID` BIGINT(20) NULL DEFAULT NULL,
  `title` VARCHAR(200) NULL DEFAULT NULL,
  `description` VARCHAR(10000) NULL DEFAULT NULL,
  `lessonsLearnt` VARCHAR(10000) NULL DEFAULT NULL,
  `controls` VARCHAR(10000) NULL DEFAULT NULL,
  `dateIncident` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dateStakeHoldersNotified` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `impTime` TINYINT(1) NOT NULL DEFAULT '0',
  `impSpec` TINYINT(1) NOT NULL DEFAULT '0',
  `impEnviron` TINYINT(1) NOT NULL DEFAULT '0',
  `impCost` TINYINT(1) NOT NULL DEFAULT '0',
  `impSafety` TINYINT(1) NOT NULL DEFAULT '0',
  `impReputation` TINYINT(1) NOT NULL DEFAULT '0',
  `notifyStakeHoldersEntered` TINYINT(1) NOT NULL DEFAULT '0',
  `notifyStakeHoldersUpdate` TINYINT(1) NOT NULL DEFAULT '0',
  `stakeHolderNotified` TINYINT(1) NOT NULL DEFAULT '0',
  `bIdentified` TINYINT(1) NOT NULL DEFAULT '0',
  `bCauses` TINYINT(1) NOT NULL DEFAULT '0',
  `bRated` TINYINT(1) NOT NULL DEFAULT '0',
  `bControl` TINYINT(1) NOT NULL DEFAULT '0',
  `bMitigated` TINYINT(1) NOT NULL DEFAULT '0',
  `bReviews` TINYINT(1) NOT NULL DEFAULT '0',
  `bActive` TINYINT(1) NOT NULL DEFAULT '0',
  `bIssue` TINYINT(1) NOT NULL DEFAULT '1',
  `reportedByID` BIGINT(20) NULL DEFAULT NULL,
  `projectID` BIGINT(20) NULL DEFAULT NULL,
  `promotedProjectID` BIGINT(20) NULL DEFAULT NULL,
  `severity` INT(11) NOT NULL DEFAULT '2',
  `reportedByStr` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`incidentID`),
  INDEX `projectID` (`projectID` ASC, `promotedProjectID` ASC),
  INDEX `incidentProjectCode` (`incidentProjectCode` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 100239
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `incidentconseq`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `incidentconseq` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `incidentID` BIGINT(20) NOT NULL,
  `typeID` BIGINT(20) NULL DEFAULT NULL,
  `description` VARCHAR(500) NULL DEFAULT NULL,
  `value` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_IT_1` (`incidentID` ASC),
  CONSTRAINT `FK_IT_1`
    FOREIGN KEY (`incidentID`)
    REFERENCES `incident` (`incidentID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 101044
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `objective`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `objective` (
  `objectiveID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `parentID` BIGINT(20) NULL DEFAULT NULL,
  `objective` VARCHAR(1000) NOT NULL,
  `lft` INT(11) NOT NULL,
  `rgt` INT(11) NOT NULL,
  PRIMARY KEY (`objectiveID`),
  INDEX `projectID` (`projectID` ASC),
  INDEX `FK_OBJECTIVE_2` (`parentID` ASC),
  INDEX `INDEX_OBJECTIVE_1` (`lft` ASC),
  INDEX `INDEX_OBJECTIVE_2` (`rgt` ASC),
  CONSTRAINT `FK_OBJECTIVES_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_OBJECTIVE_2`
    FOREIGN KEY (`parentID`)
    REFERENCES `objective` (`objectiveID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100014
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `incidentobjective`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `incidentobjective` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `incidentID` BIGINT(20) NOT NULL,
  `objectiveID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_IO_1` (`incidentID` ASC),
  INDEX `FK_IO_2` (`objectiveID` ASC),
  CONSTRAINT `FK_IO_1`
    FOREIGN KEY (`incidentID`)
    REFERENCES `incident` (`incidentID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_IO_2`
    FOREIGN KEY (`objectiveID`)
    REFERENCES `objective` (`objectiveID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 101526
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `incidentrisk`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `incidentrisk` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `incidentID` BIGINT(20) NOT NULL,
  `riskID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_IR_1` (`incidentID` ASC),
  INDEX `FK_IR_2` (`riskID` ASC),
  CONSTRAINT `FK_IR_1`
    FOREIGN KEY (`incidentID`)
    REFERENCES `incident` (`incidentID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_IR_2`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 103132
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `incidentupdate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `incidentupdate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `incidentID` BIGINT(20) NOT NULL,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` VARCHAR(10000) NULL DEFAULT NULL,
  `reportedByID` BIGINT(20) NULL DEFAULT NULL,
  `reportedByStr` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `incidentID` (`incidentID` ASC),
  CONSTRAINT `FK_IU_1`
    FOREIGN KEY (`incidentID`)
    REFERENCES `incident` (`incidentID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `jobqueue`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jobqueue` (
  `jobID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `readyToExecute` TINYINT(1) NOT NULL DEFAULT '0',
  `processing` TINYINT(1) NOT NULL DEFAULT '0',
  `readyToCollect` TINYINT(1) NOT NULL DEFAULT '0',
  `collected` TINYINT(1) NOT NULL DEFAULT '0',
  `failed` TINYINT(1) NOT NULL DEFAULT '0',
  `state` VARCHAR(1000) NOT NULL DEFAULT '',
  `rootProjectID` BIGINT(20) NULL DEFAULT NULL,
  `userID` BIGINT(20) NOT NULL,
  `jobDescription` VARCHAR(300) NULL DEFAULT NULL,
  `jobJdbcURL` VARCHAR(1000) NULL DEFAULT NULL,
  `queuedDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `executedDate` TIMESTAMP NULL DEFAULT NULL,
  `collectedDate` DATE NULL DEFAULT NULL,
  `projectID` BIGINT(20) NULL DEFAULT NULL,
  `reportFormat` VARCHAR(20) NULL DEFAULT NULL,
  `jobType` VARCHAR(40) NOT NULL DEFAULT 'REPORT',
  PRIMARY KEY (`jobID`),
  INDEX `userID` (`userID` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `jobdata`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jobdata` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `jobID` BIGINT(20) NOT NULL,
  `riskID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `jobID` (`jobID` ASC),
  CONSTRAINT `FK_JOBDATA_1`
    FOREIGN KEY (`jobID`)
    REFERENCES `jobqueue` (`jobID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `jobmontedata`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jobmontedata` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `jobID` BIGINT(20) NOT NULL,
  `projectID` BIGINT(20) NOT NULL,
  `monthlyAnalysis` BIGINT(20) NOT NULL,
  `startDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `endDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `numIterations` INT(11) NULL DEFAULT NULL,
  `forceConsequencesActive` TINYINT(1) NOT NULL DEFAULT '0',
  `forceRiskActive` TINYINT(1) NOT NULL DEFAULT '0',
  `simType` INT(11) NULL DEFAULT NULL,
  `processed` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `jobID` (`jobID` ASC),
  CONSTRAINT `FK_JOBMONTEDATA_1`
    FOREIGN KEY (`jobID`)
    REFERENCES `jobqueue` (`jobID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `jobmonteresult`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jobmonteresult` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `jobID` BIGINT(20) NOT NULL,
  `typeID` BIGINT(20) NOT NULL,
  `preMit` TINYINT(1) NOT NULL DEFAULT '0',
  `resultStr` LONGBLOB NULL DEFAULT NULL,
  `image` MEDIUMBLOB NULL DEFAULT NULL,
  `p10` DOUBLE NULL DEFAULT NULL,
  `p20` DOUBLE NULL DEFAULT NULL,
  `p30` DOUBLE NULL DEFAULT NULL,
  `p40` DOUBLE NULL DEFAULT NULL,
  `p50` DOUBLE NULL DEFAULT NULL,
  `p60` DOUBLE NULL DEFAULT NULL,
  `p70` DOUBLE NULL DEFAULT NULL,
  `p80` DOUBLE NULL DEFAULT NULL,
  `p90` DOUBLE NULL DEFAULT NULL,
  `p100` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `jobID` (`jobID` ASC),
  CONSTRAINT `FK_JOBMONTERESULT_1`
    FOREIGN KEY (`jobID`)
    REFERENCES `jobqueue` (`jobID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `jobresult`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jobresult` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `jobID` BIGINT(20) NOT NULL,
  `resultStr` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `jobID` (`jobID` ASC),
  CONSTRAINT `FK_JOBRESULT_1`
    FOREIGN KEY (`jobID`)
    REFERENCES `jobqueue` (`jobID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `metric`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `metric` (
  `metricID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `method` VARCHAR(100) NULL DEFAULT NULL,
  `projectID` BIGINT(20) NOT NULL DEFAULT '-1',
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `title` VARCHAR(1000) NULL DEFAULT NULL,
  `grayl` DOUBLE NULL DEFAULT NULL,
  `grayu` DOUBLE NULL DEFAULT NULL,
  `greenl` DOUBLE NULL DEFAULT NULL,
  `greenu` DOUBLE NULL DEFAULT NULL,
  `redl` DOUBLE NULL DEFAULT NULL,
  `redu` DOUBLE NULL DEFAULT NULL,
  `yellowl` DOUBLE NULL DEFAULT NULL,
  `yellowu` DOUBLE NULL DEFAULT NULL,
  `low` DOUBLE NULL DEFAULT NULL,
  `high` DOUBLE NULL DEFAULT NULL,
  `configRange` TINYINT(1) NULL DEFAULT '1',
  `configLimit` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`metricID`))
ENGINE = InnoDB
AUTO_INCREMENT = 100003
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mitigationstep`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mitigationstep` (
  `mitstepID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `riskID` BIGINT(20) NULL DEFAULT NULL,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `description` VARCHAR(5000) NULL DEFAULT NULL,
  `startDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `endDate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `personID` BIGINT(20) NULL DEFAULT NULL,
  `estCost` DOUBLE NOT NULL DEFAULT '0',
  `percentComplete` DOUBLE NOT NULL DEFAULT '0',
  `response` TINYINT(1) NULL DEFAULT '0',
  `mitPlanUpdate` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`mitstepID`),
  INDEX `mitigationstep_FK2` (`riskID` ASC),
  INDEX `FK_MITSTEP_3` (`personID` ASC),
  INDEX `mitigationstep_FK1` (`projectID` ASC),
  CONSTRAINT `mitigationstep_FK2`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_MITSTEP_3`
    FOREIGN KEY (`personID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`),
  CONSTRAINT `mitigationstep_FK1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 102423
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `objectives_impacted`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `objectives_impacted` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `riskID` BIGINT(20) NOT NULL,
  `objectiveID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_OBJECTIVES_IMPACTED_1` (`riskID` ASC),
  INDEX `FK_OBJECTIVES_IMPACTED_2` (`objectiveID` ASC),
  CONSTRAINT `FK_OBJECTIVES_IMPACTED_1`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_OBJECTIVES_IMPACTED_2`
    FOREIGN KEY (`objectiveID`)
    REFERENCES `objective` (`objectiveID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100430
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `projectmetric`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `projectmetric` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `metricID` BIGINT(20) NOT NULL,
  `projectID` BIGINT(20) NOT NULL,
  `grayl` DOUBLE NULL DEFAULT NULL,
  `grayu` DOUBLE NULL DEFAULT NULL,
  `greenl` DOUBLE NULL DEFAULT NULL,
  `greenu` DOUBLE NULL DEFAULT NULL,
  `redl` DOUBLE NULL DEFAULT NULL,
  `redu` DOUBLE NULL DEFAULT NULL,
  `yellowl` DOUBLE NULL DEFAULT NULL,
  `yellowu` DOUBLE NULL DEFAULT NULL,
  `low` DOUBLE NULL DEFAULT NULL,
  `high` DOUBLE NULL DEFAULT NULL,
  `configRange` TINYINT(1) NULL DEFAULT '1',
  `configLimit` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  INDEX `FK_METRIC_1` (`metricID` ASC),
  INDEX `FK_METRIC_2` (`projectID` ASC),
  CONSTRAINT `FK_METRIC_1`
    FOREIGN KEY (`metricID`)
    REFERENCES `metric` (`metricID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_METRIC_2`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `projectowners`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `projectowners` (
  `projectID` BIGINT(20) NOT NULL,
  `stakeholderID` BIGINT(20) NOT NULL,
  INDEX `projectID` (`projectID` ASC),
  INDEX `stakeholderID` (`stakeholderID` ASC),
  CONSTRAINT `projectowners_FK1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE,
  CONSTRAINT `projectowners_FK2`
    FOREIGN KEY (`stakeholderID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `projectriskmanagers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `projectriskmanagers` (
  `projectID` BIGINT(20) NOT NULL,
  `stakeholderID` BIGINT(20) NOT NULL,
  INDEX `projectID` (`projectID` ASC),
  INDEX `stakeholderID` (`stakeholderID` ASC),
  INDEX `INDEX_PROJECTRISKMANAGERS_1` (`projectID` ASC),
  INDEX `INDEX_PROJECTRISKMANAGERS_2` (`stakeholderID` ASC),
  CONSTRAINT `projectmanagers_FK1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE,
  CONSTRAINT `projectmanagers_FK2`
    FOREIGN KEY (`stakeholderID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `projectusers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `projectusers` (
  `projectID` BIGINT(20) NOT NULL,
  `stakeholderID` BIGINT(20) NOT NULL,
  INDEX `projectID` (`projectID` ASC),
  INDEX `stakeholderID` (`stakeholderID` ASC),
  CONSTRAINT `projectusers_FK1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE,
  CONSTRAINT `projectusers_FK2`
    FOREIGN KEY (`stakeholderID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `quantimpacttype`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `quantimpacttype` (
  `typeID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `description` VARCHAR(400) NULL DEFAULT NULL,
  `costCategroy` TINYINT(1) NOT NULL DEFAULT '1',
  `costSummary` TINYINT(1) NOT NULL DEFAULT '0',
  `nillQuantImpact` SMALLINT(6) NOT NULL DEFAULT '0',
  `units` VARCHAR(200) NULL DEFAULT NULL,
  `lower1` DOUBLE NOT NULL DEFAULT '0',
  `upper1` DOUBLE NOT NULL DEFAULT '0',
  `description1` VARCHAR(200) NOT NULL DEFAULT '',
  `lower2` DOUBLE NOT NULL DEFAULT '0',
  `upper2` DOUBLE NOT NULL DEFAULT '0',
  `description2` VARCHAR(200) NOT NULL DEFAULT '',
  `lower3` DOUBLE NOT NULL DEFAULT '0',
  `upper3` DOUBLE NOT NULL DEFAULT '0',
  `description3` VARCHAR(200) NOT NULL DEFAULT '',
  `lower4` DOUBLE NOT NULL DEFAULT '0',
  `upper4` DOUBLE NOT NULL DEFAULT '0',
  `description4` VARCHAR(200) NOT NULL DEFAULT '',
  `lower5` DOUBLE NOT NULL DEFAULT '0',
  `upper5` DOUBLE NOT NULL DEFAULT '0',
  `description5` VARCHAR(200) NOT NULL DEFAULT '',
  `lower6` DOUBLE NOT NULL DEFAULT '0',
  `upper6` DOUBLE NOT NULL DEFAULT '0',
  `description6` VARCHAR(200) NOT NULL DEFAULT '',
  `lower7` DOUBLE NOT NULL DEFAULT '0',
  `upper7` DOUBLE NOT NULL DEFAULT '0',
  `description7` VARCHAR(200) NOT NULL DEFAULT '',
  `lower8` DOUBLE NOT NULL DEFAULT '0',
  `upper8` DOUBLE NOT NULL DEFAULT '0',
  `description8` VARCHAR(200) NOT NULL DEFAULT '',
  `projectID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`typeID`),
  INDEX `FK_QUANTIMPACTTYPE_1` (`projectID` ASC),
  CONSTRAINT `FK_QUANTIMPACTTYPE_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100005
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `reportdata`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reportdata` (
  `jobID` BIGINT(20) NOT NULL DEFAULT '0',
  `reportID` BIGINT(20) NULL DEFAULT NULL,
  `schedJobID` BIGINT(20) NULL DEFAULT NULL,
  `reportType` VARCHAR(1000) NULL DEFAULT NULL,
  `userID` BIGINT(20) NULL DEFAULT NULL,
  `projectID` BIGINT(20) NULL DEFAULT NULL,
  `title` VARCHAR(1000) NULL DEFAULT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `jdbcURL` VARCHAR(1000) NULL DEFAULT NULL,
  `sendStatusUpdates` TINYINT(1) NULL DEFAULT '0',
  `processRiskIDs` TINYINT(1) NULL DEFAULT '0',
  `processElementIDs` TINYINT(1) NULL DEFAULT '0',
  `sendEmail` TINYINT(1) NULL DEFAULT '0',
  `schedJob` TINYINT(1) NULL DEFAULT '0',
  `emailAttachment` TINYINT(1) NULL DEFAULT '0',
  `processed` TINYINT(1) NULL DEFAULT '0',
  `readyToProcess` TINYINT(1) NULL DEFAULT '0',
  `format` VARCHAR(1000) NULL DEFAULT NULL,
  `emailFormat` VARCHAR(1000) NULL DEFAULT NULL,
  `emailTitle` VARCHAR(1000) NULL DEFAULT NULL,
  `emailContent` VARCHAR(1000) NULL DEFAULT NULL,
  `emailSent` TINYINT(1) NULL DEFAULT '0',
  `dateEmailSent` DATETIME NULL DEFAULT NULL,
  `jobStr` MEDIUMTEXT NULL DEFAULT NULL,
  `taskParamMapStr` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`jobID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reports` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `reportName` VARCHAR(1000) NULL DEFAULT NULL,
  `reportDescription` VARCHAR(1000) NULL DEFAULT NULL,
  `reportFileName` VARCHAR(100) NULL DEFAULT NULL,
  `coreFile` SMALLINT(6) NULL DEFAULT '1',
  `id` VARCHAR(1000) NULL DEFAULT NULL,
  `template` SMALLINT(6) NULL DEFAULT NULL,
  `reporttype` ENUM('REGISTER','PROJECT','RISK','REVIEW','INCIDENT','REPOSITORY') NULL DEFAULT NULL,
  `visitor` VARCHAR(400) NULL DEFAULT NULL,
  `bodyText` LONGBLOB NULL DEFAULT NULL,
  `version` VARCHAR(1000) NULL DEFAULT NULL,
  `reqTemplateRoot` VARCHAR(2000) NULL DEFAULT NULL,
  `detailConfigWindow` TINYINT(1) NOT NULL DEFAULT '0',
  `excelOnlyFormat` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`internalID`))
ENGINE = InnoDB
AUTO_INCREMENT = 100009
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `reportsession`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reportsession` (
  `sessionID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NULL DEFAULT NULL,
  `reportName` VARCHAR(1000) NULL DEFAULT NULL,
  `timeInit` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`sessionID`))
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `reportsessiondata`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reportsessiondata` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `sessionID` BIGINT(20) NOT NULL,
  `dataElement` VARCHAR(200) NULL DEFAULT NULL,
  `dataString` VARCHAR(200) NULL DEFAULT NULL,
  `rank` INT(11) NULL DEFAULT NULL,
  `dataID` BIGINT(20) NULL DEFAULT NULL,
  `dataBlob` MEDIUMBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_REPORTDSESSIONDATA_1` (`sessionID` ASC),
  CONSTRAINT `FK_REPORTDSESSIONDATA_1`
    FOREIGN KEY (`sessionID`)
    REFERENCES `reportsession` (`sessionID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `results`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `results` (
  `id` BIGINT(20) NOT NULL,
  `category` VARCHAR(100) NULL DEFAULT NULL,
  `label` VARCHAR(100) NULL DEFAULT NULL,
  `param1` DOUBLE NULL DEFAULT NULL,
  `param2` DOUBLE NULL DEFAULT NULL,
  `param3` DOUBLE NULL DEFAULT NULL,
  `param4` DOUBLE NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `review` (
  `reviewID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `title` VARCHAR(2000) NULL DEFAULT NULL,
  `scheduledDate` DATE NULL DEFAULT NULL,
  `actualDate` DATE NULL DEFAULT NULL,
  `reviewComplete` TINYINT(1) NULL DEFAULT '0',
  `reviewComments` VARCHAR(32672) NULL DEFAULT NULL,
  `mailUpdateDate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `mailUpdateComplete` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`reviewID`),
  INDEX `FK_REVIEW_1` (`projectID` ASC),
  CONSTRAINT `FK_REVIEW_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100024
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `reviewrisk`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reviewrisk` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `reviewID` BIGINT(20) NOT NULL,
  `riskID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `INDEX_REVIEWRISK_1` (`riskID` ASC),
  INDEX `INDEX_REVIEWRISK_2` (`reviewID` ASC),
  CONSTRAINT `FK_REVIEWRISK_1`
    FOREIGN KEY (`reviewID`)
    REFERENCES `review` (`reviewID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_REVIEWRISK_2`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100447
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `riskcategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskcategory` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parentID` BIGINT(20) NOT NULL DEFAULT '1',
  `description` VARCHAR(200) NULL DEFAULT NULL,
  `contextID` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`internalID`),
  INDEX `riskcategory_FK2` (`contextID` ASC),
  INDEX `riskcategory_FK1` (`parentID` ASC),
  CONSTRAINT `riskcategory_FK1`
    FOREIGN KEY (`parentID`)
    REFERENCES `riskcategory` (`internalID`)
    ON DELETE CASCADE,
  CONSTRAINT `riskcategory_FK2`
    FOREIGN KEY (`contextID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100013
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `riskconsequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskconsequence` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `riskID` BIGINT(20) NULL DEFAULT NULL,
  `quantifiable` TINYINT(1) NULL DEFAULT NULL,
  `quantType` BIGINT(20) NULL DEFAULT NULL,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` VARCHAR(2000) NULL DEFAULT NULL,
  `riskConsequenceProb` DOUBLE NULL DEFAULT NULL,
  `costDistributionType` VARCHAR(500) NULL DEFAULT NULL,
  `costDistributionParams` VARCHAR(2000) NULL DEFAULT NULL,
  `postRiskConsequenceProb` DOUBLE NULL DEFAULT NULL,
  `postCostDistributionType` VARCHAR(500) NULL DEFAULT NULL,
  `postCostDistributionParams` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `riskconsequence_FK1` (`riskID` ASC),
  CONSTRAINT `riskconsequence_FK1`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100463
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `riskcontrols`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskcontrols` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `control` VARCHAR(2000) NULL DEFAULT NULL,
  `effectiveness` BIGINT(20) NULL DEFAULT NULL,
  `riskID` BIGINT(20) NOT NULL,
  `contribution` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `INDEX_RISKCONTROLS_1` (`riskID` ASC),
  CONSTRAINT `FK_RISKCONTROLS`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 101230
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `riskrisk`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskrisk` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parentID` BIGINT(20) NOT NULL,
  `childID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `parentID` (`parentID` ASC),
  INDEX `FK_RISKRISK_2` (`childID` ASC),
  CONSTRAINT `FK_RISKRISK_1`
    FOREIGN KEY (`parentID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_RISKRISK_2`
    FOREIGN KEY (`childID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100250
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `riskstakeholder`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskstakeholder` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `riskID` BIGINT(20) NOT NULL,
  `stakeholderID` BIGINT(20) NOT NULL,
  `description` VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `riskID` (`riskID` ASC),
  INDEX `FK_RISKSTAKE_2` (`stakeholderID` ASC),
  CONSTRAINT `FK_RISKSTAKE_1`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_RISKSTAKE_2`
    FOREIGN KEY (`stakeholderID`)
    REFERENCES `qrmlogin`.`stakeholders` (`stakeholderID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `schedjob`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `schedjob` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `Mon` TINYINT(1) NOT NULL DEFAULT '0',
  `Tue` TINYINT(1) NOT NULL DEFAULT '0',
  `Wed` TINYINT(1) NOT NULL DEFAULT '0',
  `Thu` TINYINT(1) NOT NULL DEFAULT '0',
  `Fri` TINYINT(1) NOT NULL DEFAULT '0',
  `Sat` TINYINT(1) NOT NULL DEFAULT '0',
  `Sun` TINYINT(1) NOT NULL DEFAULT '0',
  `userID` BIGINT(20) NOT NULL,
  `repository` BIGINT(20) NOT NULL,
  `reportID` BIGINT(20) NULL DEFAULT NULL,
  `projectID` BIGINT(20) NULL DEFAULT NULL,
  `descendants` TINYINT(1) NOT NULL DEFAULT '0',
  `description` VARCHAR(500) NULL DEFAULT NULL,
  `timeStr` VARCHAR(20) NULL DEFAULT NULL,
  `email` TINYINT(1) NOT NULL DEFAULT '1',
  `additionalUsers` VARCHAR(3000) NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`))
ENGINE = InnoDB
AUTO_INCREMENT = 100002
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `subjrank`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subjrank` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `riskID` BIGINT(20) NOT NULL,
  `rank` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_SUBJRANK_1` (`projectID` ASC),
  INDEX `FK_SUBJRANK_2` (`riskID` ASC),
  CONSTRAINT `FK_SUBJRANK_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE,
  CONSTRAINT `FK_SUBJRANK_2`
    FOREIGN KEY (`riskID`)
    REFERENCES `risk` (`riskID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100000
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `toldescriptors`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `toldescriptors` (
  `descriptorID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `projectID` BIGINT(20) NOT NULL,
  `shortName` VARCHAR(20) NULL DEFAULT NULL,
  `longName` VARCHAR(500) NULL DEFAULT NULL,
  `tolAction` VARCHAR(500) NULL DEFAULT NULL,
  `tolLevel` SMALLINT(6) NULL DEFAULT NULL,
  PRIMARY KEY (`descriptorID`),
  INDEX `FK_TOLDESCRIPTORS_1` (`projectID` ASC),
  CONSTRAINT `FK_TOLDESCRIPTORS_1`
    FOREIGN KEY (`projectID`)
    REFERENCES `riskproject` (`projectID`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 100005
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `updatecomment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `updatecomment` (
  `internalID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dateUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dateEntered` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `hostID` BIGINT(20) NOT NULL,
  `hostType` ENUM('RISK','PROJECT','INCIDENT','REVIEW','DELIVERABLE','ISSUE','ISSUEACTION','AUDITITEM','MITIGATION') NULL DEFAULT NULL,
  `description` VARCHAR(2000) NULL DEFAULT NULL,
  `contents` LONGBLOB NULL DEFAULT NULL,
  `personID` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`internalID`),
  INDEX `hostID` (`hostID` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 105419
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `welcomedata`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `welcomedata` (
  `sessionID` BIGINT(20) NULL DEFAULT NULL,
  `element` VARCHAR(50) NULL DEFAULT NULL,
  `id` BIGINT(20) NULL DEFAULT NULL,
  `name` VARCHAR(1000) NULL DEFAULT NULL,
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `addInfo` VARCHAR(1000) NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Placeholder table for view `allriskstakeholders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `allriskstakeholders` (`stakeholderID` INT, `name` INT, `email` INT, `riskID` INT, `description` INT);

-- -----------------------------------------------------
-- Placeholder table for view `jobemailready`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jobemailready` (`jobID` INT, `sendEmail` INT, `emailSent` INT, `emailTitle` INT, `description` INT, `emailFormat` INT, `emailContent` INT, `dateEmailSent` INT, `resultStr` INT, `additionalUsers` INT, `description2` INT, `readyToCollect` INT, `reportFormat` INT, `email` INT, `name` INT);

-- -----------------------------------------------------
-- Placeholder table for view `projectriskdetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `projectriskdetails` (`spprojectID` INT, `subprojectID` INT, `riskID` INT, `externalID` INT, `projectID` INT, `promotedProjectID` INT, `riskProjectCode` INT, `securityLevel` INT, `ownerID` INT, `manager1ID` INT, `manager2ID` INT, `manager3ID` INT, `matrixID` INT, `title` INT, `description` INT, `mitPlanSummary` INT, `mitPlanSummaryUpdate` INT, `impact` INT, `cause` INT, `consequences` INT, `impSafety` INT, `impSpec` INT, `impCost` INT, `impTime` INT, `impReputation` INT, `impEnvironment` INT, `inherentProb` INT, `inherentImpact` INT, `treatedProb` INT, `treatedImpact` INT, `startExposure` INT, `endExposure` INT, `inherentTolerance` INT, `treatedTolerance` INT, `currentTolerance` INT, `subjectiveRank` INT, `objectiveRank` INT, `levelRank` INT, `active` INT, `treated` INT, `dateUpdated` INT, `timeUpdated` INT, `dateEntered` INT, `dateEvalApp` INT, `dateEvalRev` INT, `dateIDApp` INT, `dateIDRev` INT, `dateMitApp` INT, `dateMitPrep` INT, `dateMitRev` INT, `idEvalApp` INT, `idEvalRev` INT, `idIDApp` INT, `idIDRev` INT, `idMitApp` INT, `idMitPrep` INT, `idMitRev` INT, `treatmentID` INT, `treatmentAvoidance` INT, `treatmentReduction` INT, `treatmentRetention` INT, `treatmentTransfer` INT, `summaryRisk` INT, `parentSummaryRisk` INT, `private` INT, `restricted` INT, `pub` INT, `liketype` INT, `likeprob` INT, `likealpha` INT, `liket` INT, `likepostType` INT, `likepostProb` INT, `likepostAlpha` INT, `likepostT` INT, `primCatID` INT, `secCatID` INT, `estimatedContingencey` INT, `useCalculatedContingency` INT, `preMitContingency` INT, `postMitContingency` INT, `preMitContingencyWeighted` INT, `postMitContingencyWeighted` INT, `contingencyPercentile` INT, `mitigationCost` INT, `useCalculatedProb` INT, `extObject` INT, `preMitImage` INT, `postMitImage` INT, `matImage` INT, `ownerName` INT, `manager1Name` INT, `manager2Name` INT, `manager3Name` INT, `primCatName` INT, `secCatName` INT, `fromProjCode` INT, `toProjCode` INT, `parentRiskProjectCode` INT);

-- -----------------------------------------------------
-- Placeholder table for view `reportrisks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reportrisks` (`riskID` INT, `externalID` INT, `projectID` INT, `promotedProjectID` INT, `riskProjectCode` INT, `securityLevel` INT, `ownerID` INT, `manager1ID` INT, `manager2ID` INT, `manager3ID` INT, `matrixID` INT, `title` INT, `description` INT, `mitPlanSummary` INT, `mitPlanSummaryUpdate` INT, `impact` INT, `cause` INT, `consequences` INT, `impSafety` INT, `impSpec` INT, `impCost` INT, `impTime` INT, `impReputation` INT, `impEnvironment` INT, `inherentProb` INT, `inherentImpact` INT, `treatedProb` INT, `treatedImpact` INT, `startExposure` INT, `endExposure` INT, `inherentTolerance` INT, `treatedTolerance` INT, `currentTolerance` INT, `subjectiveRank` INT, `objectiveRank` INT, `levelRank` INT, `active` INT, `treated` INT, `dateUpdated` INT, `timeUpdated` INT, `dateEntered` INT, `dateEvalApp` INT, `dateEvalRev` INT, `dateIDApp` INT, `dateIDRev` INT, `dateMitApp` INT, `dateMitPrep` INT, `dateMitRev` INT, `idEvalApp` INT, `idEvalRev` INT, `idIDApp` INT, `idIDRev` INT, `idMitApp` INT, `idMitPrep` INT, `idMitRev` INT, `treatmentID` INT, `treatmentAvoidance` INT, `treatmentReduction` INT, `treatmentRetention` INT, `treatmentTransfer` INT, `summaryRisk` INT, `parentSummaryRisk` INT, `private` INT, `restricted` INT, `pub` INT, `liketype` INT, `likeprob` INT, `likealpha` INT, `liket` INT, `likepostType` INT, `likepostProb` INT, `likepostAlpha` INT, `likepostT` INT, `primCatID` INT, `secCatID` INT, `estimatedContingencey` INT, `useCalculatedContingency` INT, `preMitContingency` INT, `postMitContingency` INT, `preMitContingencyWeighted` INT, `postMitContingencyWeighted` INT, `contingencyPercentile` INT, `mitigationCost` INT, `useCalculatedProb` INT, `extObject` INT, `preMitImage` INT, `postMitImage` INT, `matImage` INT, `ownerName` INT, `manager1Name` INT, `manager2Name` INT, `manager3Name` INT, `contextRank` INT, `primCatName` INT, `secCatName` INT, `sessionRank` INT, `id` INT, `sessionID` INT, `dataElement` INT, `dataString` INT, `rank` INT, `dataID` INT, `dataBlob` INT);

-- -----------------------------------------------------
-- Placeholder table for view `riskdetail`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskdetail` (`riskID` INT, `externalID` INT, `projectID` INT, `promotedProjectID` INT, `riskProjectCode` INT, `securityLevel` INT, `ownerID` INT, `manager1ID` INT, `manager2ID` INT, `manager3ID` INT, `matrixID` INT, `title` INT, `description` INT, `mitPlanSummary` INT, `mitPlanSummaryUpdate` INT, `impact` INT, `cause` INT, `consequences` INT, `impSafety` INT, `impSpec` INT, `impCost` INT, `impTime` INT, `impReputation` INT, `impEnvironment` INT, `inherentProb` INT, `inherentImpact` INT, `treatedProb` INT, `treatedImpact` INT, `startExposure` INT, `endExposure` INT, `inherentTolerance` INT, `treatedTolerance` INT, `currentTolerance` INT, `subjectiveRank` INT, `objectiveRank` INT, `levelRank` INT, `active` INT, `treated` INT, `dateUpdated` INT, `timeUpdated` INT, `dateEntered` INT, `dateEvalApp` INT, `dateEvalRev` INT, `dateIDApp` INT, `dateIDRev` INT, `dateMitApp` INT, `dateMitPrep` INT, `dateMitRev` INT, `idEvalApp` INT, `idEvalRev` INT, `idIDApp` INT, `idIDRev` INT, `idMitApp` INT, `idMitPrep` INT, `idMitRev` INT, `treatmentID` INT, `treatmentAvoidance` INT, `treatmentReduction` INT, `treatmentRetention` INT, `treatmentTransfer` INT, `summaryRisk` INT, `parentSummaryRisk` INT, `private` INT, `restricted` INT, `pub` INT, `liketype` INT, `likeprob` INT, `likealpha` INT, `liket` INT, `likepostType` INT, `likepostProb` INT, `likepostAlpha` INT, `likepostT` INT, `primCatID` INT, `secCatID` INT, `estimatedContingencey` INT, `useCalculatedContingency` INT, `preMitContingency` INT, `postMitContingency` INT, `preMitContingencyWeighted` INT, `postMitContingencyWeighted` INT, `contingencyPercentile` INT, `mitigationCost` INT, `useCalculatedProb` INT, `extObject` INT, `preMitImage` INT, `postMitImage` INT, `matImage` INT, `ownerName` INT, `manager1Name` INT, `manager2Name` INT, `manager3Name` INT, `primCatName` INT, `secCatName` INT, `fromProjCode` INT, `toProjCode` INT, `parentRiskProjectCode` INT);

-- -----------------------------------------------------
-- Placeholder table for view `riskview`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskview` (`riskID` INT, `projectID` INT, `matrixID` INT, `inherentimpact` INT, `inherentprob` INT, `treatedimpact` INT, `treatedprob` INT, `treated` INT, `summaryRisk` INT);

-- -----------------------------------------------------
-- Placeholder table for view `riskviewparent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `riskviewparent` (`riskID` INT, `projectID` INT, `matrixID` INT, `summaryRisk` INT, `inherentimpact` INT, `inherentprob` INT, `treatedimpact` INT, `treatedprob` INT, `treated` INT, `parentID` INT);

-- -----------------------------------------------------
-- Placeholder table for view `stakeholders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `stakeholders` (`stakeholderID` INT, `name` INT, `email` INT, `lastlogon` INT, `active` INT, `allowLogon` INT, `allowUserMgmt` INT, `emailmsgtypes` INT);

-- -----------------------------------------------------
-- Placeholder table for view `subprojects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subprojects` (`projectID` INT, `subprojectID` INT);

-- -----------------------------------------------------
-- Placeholder table for view `subprojectsandparent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subprojectsandparent` (`projectID` INT, `subprojectID` INT);

-- -----------------------------------------------------
-- Placeholder table for view `superprojects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `superprojects` (`projectID` INT, `superprojectID` INT);

-- -----------------------------------------------------
-- Placeholder table for view `userrepository`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `userrepository` (`id` INT, `stakeholderID` INT, `repID` INT);

-- -----------------------------------------------------
-- procedure addExistingUserToRep
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `addExistingUserToRep`(IN var_ID VARCHAR (100), IN var_repID BIGINT, IN autoEnable BOOLEAN)
BEGIN   DECLARE var_projectID BIGINT;   DELETE FROM qrmlogin.userrepository WHERE repID = var_repID AND stakeholderID = var_ID;   INSERT INTO qrmlogin.userrepository (stakeholderID, repID) VALUES (var_ID, var_repID);   IF autoEnable = TRUE THEN     SELECT projectID INTO var_projectID from riskproject WHERE parentID = -2;     INSERT INTO projectusers (stakeholderID, projectID) VALUES (var_ID,var_projectID);   END IF; END$$

-- -----------------------------------------------------
-- procedure addProject
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `addProject`( IN `parent` BIGINT, IN `projectDescription` VARCHAR(5000), IN `projectTitle` VARCHAR(500), IN `projectCode` VARCHAR(6), IN `projectRiskManagerID` BIGINT, IN `projectStartDate` DATETIME, IN `projectEndDate` DATETIME, IN `var_minimumSecurityLevel` INT, OUT  idx BIGINT)
BEGIN       DECLARE right_most_sibling INT;       DECLARE var_singlePhase BOOLEAN;       DECLARE var_useAdvancedLiklihood BOOLEAN;       DECLARE var_useAdvancedConsequences BOOLEAN;       SELECT rgt INTO right_most_sibling FROM riskproject WHERE projectID = parent;       SELECT singlePhase INTO var_singlePhase FROM riskproject WHERE projectID = parent;       SELECT useAdvancedConsequences INTO var_useAdvancedConsequences FROM riskproject WHERE projectID = parent;       SELECT useAdvancedLiklihood INTO var_useAdvancedLiklihood FROM riskproject WHERE projectID = parent;       IF right_most_sibling IS NOT NULL THEN          UPDATE riskproject          SET lft = CASE WHEN lft > right_most_sibling                     THEN lft + 2                     ELSE lft END,          rgt = CASE WHEN rgt >= right_most_sibling                     THEN rgt + 2                     ELSE rgt END          WHERE rgt >= right_most_sibling;          INSERT INTO riskproject (parentID, projectDescription , projectTitle, projectCode, projectRiskManagerID, projectStartDate,projectEndDate,lft, rgt, singlePhase, useAdvancedConsequences, useAdvancedLiklihood, minimumSecurityLevel) VALUES (parent, projectDescription , projectTitle, projectCode, projectRiskManagerID, projectStartDate,projectEndDate, right_most_sibling, right_most_sibling+1, var_singlePhase, var_useAdvancedConsequences, var_useAdvancedLiklihood, var_minimumSecurityLevel);          SET idx = LAST_INSERT_ID();       ELSE        SET idx = -1;       END IF;    IF idx > 0 THEN        INSERT INTO projectowners (stakeholderID, projectID) SELECT stakeholderID, idx  FROM projectowners WHERE projectID = parent;        INSERT INTO projectriskmanagers (stakeholderID, projectID) SELECT stakeholderID, idx  FROM projectriskmanagers WHERE projectID = parent;        INSERT INTO projectusers (stakeholderID, projectID) SELECT stakeholderID, idx  FROM projectusers WHERE projectID = parent;    END IF; END$$

-- -----------------------------------------------------
-- procedure addUserToRep
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `addUserToRep`(IN var_ID VARCHAR (100), IN var_name VARCHAR(20), IN var_email VARCHAR (200), IN autoEnable BOOLEAN)
BEGIN   DECLARE projectID BIGINT;   IF autoEnable = TRUE THEN     SELECT projectID INTO projectID from riskproject WHERE parentID = -2;     INSERT INTO projectusers (stakeholderID, projectID) VALUES (var_ID,projectID);   END IF; END$$

-- -----------------------------------------------------
-- procedure analysisGetAllocation
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `analysisGetAllocation`(IN `var_context_id` BIGINT, IN `var_user_id` BIGINT, IN `var_param_id` BIGINT,IN `var_param_str` VARCHAR(500), IN `var_descendants` TINYINT)
BEGIN       CREATE TEMPORARY TABLE IF NOT EXISTS projs (projID BIGINT) ENGINE=MEMORY;       DELETE FROM projs;       CREATE TEMPORARY TABLE IF NOT EXISTS tol1 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;       DELETE FROM tol1;       CREATE TEMPORARY TABLE IF NOT EXISTS tol2 (cat VARCHAR(100), label BIGINT, val BIGINT)ENGINE=MEMORY;       DELETE FROM tol2;       CREATE TEMPORARY TABLE IF NOT EXISTS tol3 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;       DELETE FROM tol3;       CREATE TEMPORARY TABLE IF NOT EXISTS tol4 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;       DELETE FROM tol4;       CREATE TEMPORARY TABLE IF NOT EXISTS tol5 (cat VARCHAR(100), label BIGINT, val BIGINT) ENGINE=MEMORY;       DELETE FROM tol5;       CREATE TEMPORARY TABLE IF NOT EXISTS results (category VARCHAR(100), label VARCHAR(100),param1 DOUBLE, param2 DOUBLE, param3 DOUBLE, param4 DOUBLE) ENGINE=MEMORY;       DELETE FROM results;       IF var_descendants > 0 THEN             INSERT INTO projs (projID) SELECT O3.internalID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.internalID = var_context_id;       ELSE             INSERT INTO projs (projID) VALUES (var_context_id);       END IF;       INSERT INTO tol1 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =1 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;       INSERT INTO tol2 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =2 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;       INSERT INTO tol3 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =3 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;       INSERT INTO tol4 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =4 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;       INSERT INTO tol5 (cat,label,val) SELECT 'OWNERS',ownerID,COUNT(*) FROM risk WHERE currentTolerance =5 AND projectID IN (SELECT projID FROM projs) GROUP BY ownerID;       INSERT INTO tol1 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =1 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;       INSERT INTO tol2 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =2 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;       INSERT INTO tol3 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =3 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;       INSERT INTO tol4 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =4 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;       INSERT INTO tol5 (cat,label,val) SELECT 'MANAGERS',manager1ID,COUNT(*) FROM risk WHERE currentTolerance =5 AND projectID IN (SELECT projID FROM projs) GROUP BY manager1ID;       INSERT INTO tol1 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =1 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;       INSERT INTO tol2 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =2 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;       INSERT INTO tol3 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =3 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;       INSERT INTO tol4 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =4 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;       INSERT INTO tol5 (cat,label,val) SELECT 'CATEGORIES',primCatID,COUNT(*) FROM risk WHERE currentTolerance =5 AND projectID IN (SELECT projID FROM projs) GROUP BY primCatID;       INSERT results (category, label, param1,param2,param3,param4) SELECT 'OWNERS', S.name AS label, T1.val AS param1, T2.val AS param2, T3.val AS param3, T4.val AS param4       FROM stakeholders AS S       LEFT OUTER JOIN tol1 AS T1 ON S.internalID = T1.label AND T1.cat = 'OWNERS'       LEFT OUTER JOIN tol2 AS T2 ON S.internalID = T2.label AND T2.cat = 'OWNERS'       LEFT OUTER JOIN tol3 AS T3 ON S.internalID = T3.label AND T3.cat = 'OWNERS'       LEFT OUTER JOIN tol4 AS T4 ON S.internalID = T4.label AND T4.cat = 'OWNERS'       LEFT OUTER JOIN tol5 AS T5 ON S.internalID = T5.label AND T5.cat = 'OWNERS'       WHERE S.stakeholderID > 0 AND S.internalID IN (SELECT stakeholderID FROM projectowners WHERE projectID IN(SELECT projID FROM projs));       INSERT results (category, label, param1,param2,param3,param4) SELECT 'MANAGERS', S.name AS label, T1.val AS param1, T2.val AS param2, T3.val AS param3, T4.val AS param4       FROM stakeholders AS S       LEFT OUTER JOIN tol1 AS T1 ON S.internalID = T1.label AND T1.cat = 'MANAGERS'       LEFT OUTER JOIN tol2 AS T2 ON S.internalID = T2.label AND T2.cat = 'MANAGERS'       LEFT OUTER JOIN tol3 AS T3 ON S.internalID = T3.label AND T3.cat = 'MANAGERS'       LEFT OUTER JOIN tol4 AS T4 ON S.internalID = T4.label AND T4.cat = 'MANAGERS'       LEFT OUTER JOIN tol5 AS T5 ON S.internalID = T5.label AND T5.cat = 'MANAGERS'       WHERE S.stakeholderID > 0  AND S.internalID IN (SELECT stakeholderID FROM projectriskmanagers WHERE projectID IN(SELECT projID FROM projs));       INSERT results (category, label, param1,param2,param3,param4) SELECT 'CATEGORIES', S.description AS label, T1.val AS param1, T2.val AS param2, T3.val AS param3, T4.val AS param4       FROM riskCategory AS S       LEFT OUTER JOIN tol1 AS T1 ON S.internalID = T1.label AND T1.cat = 'CATEGORIES'       LEFT OUTER JOIN tol2 AS T2 ON S.internalID = T2.label AND T2.cat = 'CATEGORIES'       LEFT OUTER JOIN tol3 AS T3 ON S.internalID = T3.label AND T3.cat = 'CATEGORIES'       LEFT OUTER JOIN tol4 AS T4 ON S.internalID = T4.label AND T4.cat = 'CATEGORIES'       LEFT OUTER JOIN tol5 AS T5 ON S.internalID = T5.label AND T5.cat = 'CATEGORIES';       INSERT INTO results (category,label,param1,param2) SELECT 'STATUS','PENDING', currentTolerance, COUNT(*) FROM risk  WHERE SYSDATE() < startExposure AND projectID IN(SELECT projID FROM projs)  GROUP BY risk.currentTolerance;       INSERT INTO results (category,label,param1,param2) SELECT 'STATUS', 'ACTIVE', currentTolerance, COUNT(*) FROM risk  WHERE SYSDATE() BETWEEN  startExposure AND endExposure AND projectID IN(SELECT projID FROM projs) GROUP BY risk.currentTolerance;       INSERT INTO results (category,label,param1,param2) SELECT 'STATUS','INACTIVE', currentTolerance, COUNT(*) FROM risk  WHERE SYSDATE() > endExposure AND projectID IN(SELECT projID FROM projs) GROUP BY risk.currentTolerance;       SELECT * FROM results;       DROP TEMPORARY TABLE projs;       DROP TEMPORARY TABLE tol1;       DROP TEMPORARY TABLE tol2;       DROP TEMPORARY TABLE tol3;       DROP TEMPORARY TABLE tol4;       DROP TEMPORARY TABLE tol5;       DROP TEMPORARY TABLE results; END$$

-- -----------------------------------------------------
-- procedure analysisGetRiskGeneral
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `analysisGetRiskGeneral`(IN `var_context_id` BIGINT, IN `var_user_id` BIGINT, IN `var_param_id` BIGINT,IN `var_param_str` VARCHAR(500), IN `var_descendants` TINYINT)
BEGIN       CREATE TEMPORARY TABLE IF NOT EXISTS projs (projID BIGINT) ENGINE=MEMORY;       DELETE FROM projs;       CREATE TEMPORARY TABLE IF NOT EXISTS results (category VARCHAR(100), label VARCHAR(100),param1 DOUBLE, param2 DOUBLE, param3 DOUBLE, param4 DOUBLE) ENGINE=MEMORY;       DELETE FROM results;       IF var_descendants > 0 THEN             INSERT INTO projs (projID) SELECT O3.internalID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.internalID = var_context_id;       ELSE             INSERT INTO projs (projID) VALUES (var_context_id);       END IF;        INSERT INTO results (category, label, param1) SELECT 'LASTUPDATE',riskProjectCode, DATEDIFF(SYSDATE(),risk.dateUpdated) AS diff FROM risk WHERE projectID IN (SELECT projID FROM projs) ORDER BY diff LIMIT 20;        INSERT INTO results (category, param1, label) SELECT 'NUMCONTROLS', COUNT(*) AS cnt, risk.riskProjectCode FROM riskcontrols,risk WHERE risk.riskID = riskcontrols.riskID AND risk.projectID IN (SELECT projID FROM projs) GROUP BY riskcontrols.riskID ORDER BY cnt LIMIT 20;        INSERT INTO results (category, param1, label) SELECT 'NUMMITACTIONS', COUNT(*) AS cnt, risk.riskProjectCode FROM mitigationstep,risk WHERE risk.riskID = mitigationstep.riskID AND risk.projectID IN (SELECT projID FROM projs)  GROUP BY mitigationstep.riskID ORDER BY cnt LIMIT 20;        INSERT INTO results (category, param1, label) SELECT 'MITCOST', SUM(estCost) AS cost, risk.riskProjectCode FROM mitigationstep,risk WHERE risk.riskID = mitigationstep.riskID AND risk.projectID IN (SELECT projID FROM projs)  GROUP BY mitigationstep.riskID ORDER BY cost LIMIT 20;        SELECT * FROM results;       DROP TEMPORARY TABLE results;       DROP TEMPORARY TABLE projs; END$$

-- -----------------------------------------------------
-- function checkReassignSecurity
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` FUNCTION `checkReassignSecurity`( var_riskID BIGINT, var_projectID BIGINT, var_userID BIGINT) RETURNS tinyint(1)
    READS SQL DATA
BEGIN   DECLARE mID BIGINT;   DECLARE oID BIGINT;   DECLARE oldProjectID BIGINT;   DECLARE newProjectMgr BIGINT;   DECLARE oldProjectMgr BIGINT;   DECLARE owner BIGINT default 0;   DECLARE manager BIGINT default 0;   SELECT ownerID INTO oID FROM risk WHERE riskID = var_riskID;   SELECT manager1ID INTO mID  FROM risk WHERE riskID = var_riskID;   SELECT projectID INTO oldProjectID FROM risk WHERE riskID = var_riskID;   SELECT projectRiskManagerID INTO newProjectMgr FROM riskproject WHERE projectID = var_projectID;   SELECT projectRiskManagerID INTO oldProjectMgr FROM riskproject WHERE projectID = oldProjectID;   IF var_userID = oldProjectID AND var_userID = newProjectMgr THEN     RETURN true;   END IF;   IF (var_userID = mID OR var_userID = oID) AND var_userID = newProjectMgr THEN     RETURN true;   END IF;   SELECT DISTINCT stakeholderID INTO owner FROM projectowners WHERE stakeholderID = var_userID AND projectID = var_projectID;   IF owner > 0 THEN      RETURN true;   END IF;   SELECT DISTINCT stakeholderID INTO manager FROM projectriskmanagers WHERE stakeholderID = var_userID AND projectID = var_projectID;   IF manager > 0 THEN      RETURN true;   END IF;   RETURN false; END$$

-- -----------------------------------------------------
-- function checkRiskSecurity
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` FUNCTION `checkRiskSecurity`( var_riskID BIGINT, var_userID BIGINT) RETURNS tinyint(1)
    READS SQL DATA
BEGIN   DECLARE secLev INT;   DECLARE result BOOLEAN;   DECLARE numResult INT;   DECLARE var_projectID BIGINT;   DECLARE projectMgrID BIGINT;   SELECT securityLevel from riskdetail where riskID = var_riskID INTO secLev;   SELECT projectID FROM risk WHERE riskID = var_riskID INTO var_projectID;   SELECT projectRiskManagerID FROM riskproject WHERE projectID = var_projectID INTO projectMgrID;   IF secLev = 2 THEN     SELECT (ownerID = var_userID || manager1ID = var_userID || manager2ID = var_userID || manager3ID = var_userID || projectMgrID = var_userID) FROM risk WHERE RISKID = var_riskID INTO result;     RETURN result;   END IF;   IF secLev = 1 THEN     SELECT COUNT(*) FROM risk WHERE riskID = var_riskID AND var_userID IN (       SELECT stakeholderID FROM projectowners WHERE projectID = var_projectID       UNION       SELECT stakeholderID FROM projectriskmanagers WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)       UNION        SELECT projectRiskManagerID FROM riskproject WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)) INTO numResult;       IF numResult > 0 THEN RETURN TRUE;     ELSE       RETURN FALSE;     END IF;   END IF;   RETURN true; END$$

-- -----------------------------------------------------
-- function checkRiskSecurityView
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` FUNCTION `checkRiskSecurityView`( var_riskID BIGINT, var_userID BIGINT, secLev BIGINT, var_projectID BIGINT) RETURNS tinyint(1)
    READS SQL DATA
BEGIN   DECLARE result BOOLEAN;   DECLARE numResult INT;   DECLARE projectMgrID BIGINT;   SELECT projectRiskManagerID FROM riskproject WHERE projectID = var_projectID INTO projectMgrID;    IF secLev = 2 THEN     SELECT (ownerID = var_userID || manager1ID = var_userID || manager2ID = var_userID || manager3ID = var_userID  || var_userID = projectMgrID) FROM riskdetail WHERE riskID = var_riskID INTO result;     RETURN result;   END IF;   IF secLev = 1 THEN     SELECT COUNT(*) FROM risk WHERE riskID = var_riskID AND var_userID IN (       SELECT stakeholderID FROM projectusers WHERE projectID = var_projectID       UNION       SELECT stakeholderID FROM projectowners WHERE projectID  IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)       UNION       SELECT stakeholderID FROM projectriskmanagers WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)       UNION        SELECT projectRiskManagerID FROM riskproject WHERE projectID IN (SELECT DISTINCT R1.projectID FROM riskproject as R1, riskproject AS R2 WHERE (R2.lft BETWEEN R1.lft AND R1.rgt) AND R2.projectID = var_projectID)) INTO numResult;     IF numResult > 0 THEN       RETURN TRUE;     ELSE       RETURN FALSE;     END IF;   END IF;   RETURN true; END$$

-- -----------------------------------------------------
-- procedure createNewRepository
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `createNewRepository`(IN var_org VARCHAR(1000), IN var_RMID BIGINT, OUT newProjectID BIGINT)
BEGIN   DECLARE vendorID BIGINT;   DECLARE regID BIGINT;   INSERT INTO riskproject ( dateEntered, parentID,lft,rgt,projectRiskManagerID,projectDescription,projectTitle,projectStartDate,projectEndDate,projectCode,tabsToUse,riskIndex) VALUES      ( CURRENT_TIMESTAMP, -2,1,52,var_RMID,'Corporate Risk Repository',var_org,CURRENT_TIMESTAMP,ADDDATE( CURRENT_DATE(), 365),'RK',8190,30);   SELECT LAST_INSERT_ID() INTO newProjectID;   INSERT INTO projectowners (projectID, stakeholderID) VALUES (newProjectID, var_RMID);   INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (newProjectID, var_RMID);   INSERT INTO projectusers (projectID, stakeholderID) VALUES (newProjectID, var_RMID);   INSERT INTO tolerancematrix (dateEntered, projectID,maxprob,maximpact,tolString, prob1, prob2, prob3, prob4, prob5, impact1, impact2, impact3, impact4, impact5) VALUES    (CURRENT_TIMESTAMP,newProjectID,5,5,'1123312234223443345534455', 'Low','Moderate','Significant', 'High', 'Extreme', 'Low','Moderate','Significant', 'High', 'Extreme');   INSERT INTO control_effectiveness_defn (projectID,title,description,rank) VALUES    (newProjectID,'Integrated','The control process is intergrated into the process management lifcycle',5),    (newProjectID,'Monitored','The control process is monitored on a prescribed basis',4),    (newProjectID,'Documented','The control process is documented as part of the process procedure',3),    (newProjectID,'Repeatable','The control process can be repeated',2),    (newProjectID,'Ad Hoc','The control process is none of the above',1);   INSERT INTO toldescriptors (projectID,shortName,longName,tolAction,tolLevel) VALUES    (newProjectID,'Extreme','Management Critical','',5),    (newProjectID,'High','Active Management Required','',4),    (newProjectID,'Significant','Continuous Monitoring Required','',3),    (newProjectID,'Moderate','Periodic Monitoring','',2),    (newProjectID,'Low','Monitoring','',1); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES (1,'Vendor',newProjectID); 	SELECT LAST_INSERT_ID() INTO vendorID; 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Supply',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Billing',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Delivery',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (vendorID,'Account Management',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (1,'Financial',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (1,'Safety',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (1,'Regulatory',newProjectID); 	SELECT LAST_INSERT_ID() INTO regID; 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'Local Government',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'Industry',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'Security Commision',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'State Government',newProjectID); 	INSERT INTO riskcategory (parentID,description,contextID) VALUES  (regID,'National Government',newProjectID); END$$

-- -----------------------------------------------------
-- procedure deleteCategory
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `deleteCategory`(IN var_internalID BIGINT, IN var_user_id BIGINT)
BEGIN      DELETE FROM riskcategory WHERE internalID = var_internalID AND internalID != 1; END$$

-- -----------------------------------------------------
-- procedure deleteContextSubjRank
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `deleteContextSubjRank`(IN context BIGINT, IN var_user_id BIGINT)
BEGIN      DELETE FROM subjrank WHERE projectID = context; END$$

-- -----------------------------------------------------
-- procedure deleteProject
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `deleteProject`(IN `var_projectID` BIGINT)
BEGIN     DECLARE done INT DEFAULT 0;     DECLARE id BIGINT;     DECLARE cur1 CURSOR FOR SELECT projectID FROM riskproject WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID);     DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;   IF var_projectID != 1 THEN     OPEN cur1;     REPEAT        FETCH cur1 INTO id;        IF NOT done THEN           IF id >1 THEN              DELETE FROM riskproject WHERE projectID = id;              DELETE FROM risk WHERE projectID = id;           END IF;        END IF;     UNTIL done END REPEAT;     CLOSE cur1;     DELETE FROM riskproject WHERE projectID = var_projectID;     DELETE FROM risk WHERE projectID = var_projectID;    END IF; END$$

-- -----------------------------------------------------
-- procedure getAllProjectRisk
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getAllProjectRisk`( 			IN var_user_id BIGINT, 			IN var_projectID BIGINT, 			IN var_descendants  BOOLEAN )
BEGIN   	SELECT risk.*, s1.name AS ownerName, s2.name AS manager1Name, s2.name AS manager2Name, s4.name AS manager3Name, r1.rank AS contextRank, c1.description AS primCatName,  c2.description AS secCatName FROM risk 		LEFT OUTER JOIN qrmlogin.stakeholders AS s1 ON risk.ownerID    = s1.stakeholderID 		LEFT OUTER JOIN qrmlogin.stakeholders AS s2 ON risk.manager1ID = s2.stakeholderID 		LEFT OUTER JOIN qrmlogin.stakeholders AS s3 ON risk.manager2ID = s3.stakeholderID 		LEFT OUTER JOIN qrmlogin.stakeholders AS s4 ON risk.manager3ID = s4.stakeholderID 		LEFT OUTER JOIN subjrank     AS r1 ON (risk.riskID = r1.riskID  AND  r1.projectID  = var_projectID) 		LEFT OUTER JOIN riskcategory AS c1 ON risk.primCatID = c1.internalID 		LEFT OUTER JOIN riskcategory AS c2 ON risk.secCatID = c2.internalID 		WHERE (var_descendants > 0 AND risk.projectID IN (SELECT o3.projectID  FROM riskproject AS o3, riskproject AS o4 WHERE o3.lft BETWEEN o4.lft AND o4.rgt  AND o4.projectID = var_projectID) AND checkRiskSecurityView(risk.riskID, var_user_id, risk.securityLevel, risk.projectID))         OR (var_descendants = 0 AND risk.projectID = var_projectID AND checkRiskSecurityView(risk.riskID, var_user_id, risk.securityLevel, risk.projectID)); END$$

-- -----------------------------------------------------
-- procedure getAllocations
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getAllocations`()
BEGIN  SET  @rownum:=0; SELECT @rownum:=@rownum+1 AS id,3 AS FLAG, riskproject.projectID, lft, rgt, r1.matrixID, r1.RCOUNT, r1.PROB, r1.IMPACT FROM riskproject 	 JOIN 	 (SELECT  COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT ,matrixID, projectID, treated FROM riskview r 	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated 	 HAVING treated = 0 	 UNION 	 SELECT  COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT ,matrixID, projectID, treated FROM riskview r 	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated 	 HAVING treated = 1) AS r1 ON riskproject.projectID = r1.projectID 	 UNION 	 SELECT @rownum:=@rownum+1 AS id,r2.FLAG, riskproject.projectID, lft, rgt, matrixID, r2.RCOUNT, r2.PROB, r2.IMPACT FROM riskproject 	 JOIN 	 (SELECT @rownum:=@rownum+1 AS id,AVG(0) AS FLAG, COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT, matrixID, projectID, treated FROM riskview r 	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated 	 HAVING treated = 0 	 UNION 	 SELECT @rownum:=@rownum+1 AS id,AVG(1) AS FLAG, COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT, matrixID, projectID, treated FROM riskview r 	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated 	 HAVING  treated = 1) AS r2 ON riskproject.projectID = r2.projectID; END$$

-- -----------------------------------------------------
-- procedure getFamily
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getFamily`(var_projectID BIGINT,var_userID BIGINT)
BEGIN SELECT   PC1.parentID, PC1.childID, S1.riskProjectCode AS pcode, S1.title as ptitle, S1.description as pdescription, S2.riskProjectCode AS ccode, S2.title as ctitle, S2.description as cdescription from riskdetail AS S1 JOIN childparent AS PC1 ON S1.riskID = PC1.parentID  LEFT OUTER JOIN riskdetail AS S2 ON S2.riskID = PC1.childID WHERE (S1.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = var_projectID UNION SELECT var_projectID) OR (S1.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = var_projectID UNION SELECT var_projectID) AND S1.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = var_projectID  UNION SELECT var_projectID) ) OR S1.promotedProjectID = var_projectID ) AND checkRiskSecurityView(S1.riskID, var_userID, S1.securityLevel, S1.projectID); END$$

-- -----------------------------------------------------
-- procedure getMetricConfig
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getMetricConfig`(IN `var_projectID` BIGINT, IN `var_metric_id` BIGINT)
BEGIN 		select metric.title,metric.description,metric.method,metric.metricID, 		projectmetric.projectID,projectmetric.grayl,projectmetric.grayu,projectmetric.greenl, 		projectmetric.greenu,projectmetric.redl,projectmetric.redu,projectmetric.yellowl, 		projectmetric.yellowu,projectmetric.low,projectmetric.high,riskproject.lft 		from metric, projectmetric, riskproject 		where metric.metricID = projectmetric.metricID 		AND projectmetric.projectID IN (SELECT superProjectID FROM superprojects WHERE projectID = var_projectID UNION SELECT var_projectID as superprojectID) 		AND metric.metricID = var_metric_id AND riskproject.projectID = projectmetric.projectID 		UNION 		SELECT 		metric.title,metric.description,metric.method,metric.metricID,metric.projectID, 		metric.grayl,metric.grayu,metric.greenl,metric.greenu,metric.redl, 		metric.redu,metric.yellowl,metric.yellowu,metric.low,metric.high,-1 AS lft 		FROM metric WHERE metric.metricID = var_metric_id ORDER BY lft DESC; END$$

-- -----------------------------------------------------
-- procedure getNumProjectsMetric
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getNumProjectsMetric`(IN `var_projectID` BIGINT, IN `var_userID` BIGINT)
BEGIN   IF var_projectID  < 0 THEN 			SELECT COUNT(*) AS param1 FROM riskproject WHERE projectID IN( 		      SELECT DISTINCT (projectID) FROM riskproject  WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = var_userID 			    UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_userID  			   UNION SELECT projectID from projectowners WHERE stakeholderID = var_userID)) AND  projectID > 0     	);   ELSE     SELECT COUNT(*) AS param1 FROM riskproject     WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4  WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID);   END IF; END$$

-- -----------------------------------------------------
-- procedure getNumRisksMetric
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getNumRisksMetric`(IN `var_projectID` BIGINT, IN `var_userID` BIGINT)
BEGIN   IF var_projectID  < 0 THEN 			SELECT COUNT(*) AS param1, 0 AS param2,0 AS param3, 0 AS param4, 0 AS param5 FROM risk WHERE projectID IN( 		      SELECT DISTINCT (projectID) FROM riskproject  WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = var_userID 			    UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_userID  			   UNION SELECT projectID from projectowners WHERE stakeholderID = var_userID)) AND  projectID > 0     	);   ELSE  			SELECT COUNT(*) AS param1, 0 AS param2,0 AS param3, 0 AS param4, 0 AS param5  FROM risk WHERE projectID IN (         SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4 				WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID        );   END IF;   END$$

-- -----------------------------------------------------
-- procedure getPercentApprovedMitPlanMetric
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getPercentApprovedMitPlanMetric`(IN `var_projectID` BIGINT, IN `var_userID` BIGINT)
BEGIN   IF var_projectID  < 0 THEN       SELECT COUNT(*) AS CNT FROM risk WHERE risk.dateMitApp IS NOT NULL AND risk.projectID IN(           SELECT DISTINCT (projectID) FROM riskproject  WHERE projectID IN (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN  (SELECT projectID from projectusers WHERE stakeholderID = var_userID 			    UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_userID  			   UNION SELECT projectID from projectowners WHERE stakeholderID = var_userID)) AND  projectID > 0     	);   ELSE        SELECT COUNT(*) AS CNT FROM risk WHERE  risk.dateMitApp IS NOT NULL AND projectID IN (        SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4         WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = var_projectID 				);   END IF; END$$

-- -----------------------------------------------------
-- procedure getProject
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProject`(IN id BIGINT)
BEGIN      SELECT *  FROM riskproject WHERE projectID = id; END$$

-- -----------------------------------------------------
-- procedure getProjectByTitle
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectByTitle`(IN title VARCHAR(1000))
BEGIN      SELECT *  FROM riskproject WHERE projectTitle = title; END$$

-- -----------------------------------------------------
-- procedure getProjectCategories
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectCategories`(IN `varprojectID` BIGINT)
BEGIN       SELECT -1 AS generation, riskcategory.* FROM riskcategory WHERE riskcategory.contextID IN (SELECT superprojectID from superprojects WHERE projectID = varprojectID) AND riskcategory.internalID != 1       UNION 	  SELECT 1 AS generation, riskcategory.* FROM riskcategory WHERE riskcategory.contextID IN (SELECT subprojectID from subprojects WHERE projectID = varprojectID) AND riskcategory.internalID != 1       UNION 	  SELECT 0 AS generation, riskcategory.* FROM riskcategory WHERE riskcategory.contextID = varprojectID AND riskcategory.internalID != 1; END$$

-- -----------------------------------------------------
-- procedure getProjectIDForUser
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectIDForUser`(IN var_user_id BIGINT)
BEGIN      SELECT DISTINCT projectID  FROM riskproject 				WHERE projectID IN 				 (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4    WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN 				     (SELECT projectID from projectusers WHERE stakeholderID = var_user_id UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_user_id UNION SELECT projectID from projectowners WHERE stakeholderID = var_user_id)); END$$

-- -----------------------------------------------------
-- function getProjectMatID
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` FUNCTION `getProjectMatID`( var_projID BIGINT) RETURNS bigint(20)
    READS SQL DATA
BEGIN   DECLARE matID BIGINT;   SELECT matrixID INTO matID from tolerancematrix   LEFT JOIN superprojects ON tolerancematrix.projectID = superprojects.superprojectID   WHERE superprojects.projectID = var_projID ORDER BY superprojects.superprojectID DESC LIMIT 1;    IF matID IS NULL THEN     SELECT matrixID INTO matID FROM tolerancematrix WHERE projectID = var_projID;   END IF;   RETURN matID; END$$

-- -----------------------------------------------------
-- procedure getProjectMats
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectMats`(IN `projectID` BIGINT)
BEGIN 		SELECT 0 AS generation, tolerancematrix.*, riskproject.projectTitle, riskproject.lft FROM tolerancematrix, riskproject 				 WHERE tolerancematrix.projectID = projectID AND tolerancematrix.projectID = riskproject.projectID 				 UNION 				 SELECT -1 AS generation, tolerancematrix.*, riskproject.projectTitle, riskproject.lft FROM tolerancematrix, riskproject 				 WHERE tolerancematrix.projectID IN (SELECT O2.projectID FROM riskproject AS O1, riskproject AS O2  WHERE O1.lft BETWEEN O2.lft AND O2.rgt  AND O1.projectID = projectID) 				 AND tolerancematrix.projectID = riskproject.projectID 				 AND tolerancematrix.projectID != projectID 				 ORDER BY generation DESC, lft DESC; END$$

-- -----------------------------------------------------
-- procedure getProjectObjectives
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectObjectives`(IN `varprojectID` BIGINT)
BEGIN 			SELECT -1 AS generation, objective.* FROM objective WHERE objective.projectID IN (SELECT superprojectID from superprojects WHERE projectID = varprojectID)       UNION 			SELECT 1 AS generation, objective.* FROM objective WHERE objective.projectID IN (SELECT subprojectID from subprojects WHERE projectID = varprojectID)       UNION 			SELECT 0 AS generation, objective.* FROM objective WHERE objective.projectID = varprojectID; END$$

-- -----------------------------------------------------
-- procedure getProjectQuanttypes
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectQuanttypes`(IN `varprojectID` BIGINT)
BEGIN 			SELECT -1 AS generation, quantimpacttype.* FROM quantimpacttype WHERE quantimpacttype.projectID IN (SELECT superprojectID from superprojects WHERE projectID = varprojectID)       UNION 			SELECT 1 AS generation, quantimpacttype.* FROM quantimpacttype WHERE quantimpacttype.projectID IN (SELECT subprojectID from subprojects WHERE projectID = varprojectID)       UNION 			SELECT 0 AS generation, quantimpacttype.* FROM quantimpacttype WHERE quantimpacttype.projectID = varprojectID; END$$

-- -----------------------------------------------------
-- procedure getProjectRisks
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectRisks`(IN `var_userID` BIGINT, IN `var_projectID` BIGINT, IN `var_child` BOOLEAN)
BEGIN         IF var_child > 0 THEN           SELECT riskdetail.*, r1.rank AS contextRank FROM riskdetail   	      LEFT OUTER JOIN subjrank AS r1 ON (riskdetail.riskID = r1.riskID  AND  r1.projectID  = var_projectID)   	      WHERE riskdetail.projectID IN (SELECT p3.projectID  FROM riskproject AS p3, riskproject AS p4 WHERE p3.lft BETWEEN p4.lft AND p4.rgt  AND p4.projectID = var_projectID)    	      AND checkRiskSecurityView ( riskdetail.riskID, var_userID, riskdetail.securityLevel, riskdetail.projectID) ORDER BY riskID;         ELSE   	      SELECT riskdetail.*, r1.rank AS contextRank FROM riskdetail   	      LEFT OUTER JOIN subjrank  AS r1 ON (riskdetail.riskID = r1.riskID  AND  r1.projectID  = var_projectID)   	      WHERE riskdetail.projectID = var_projectID    	      AND checkRiskSecurityView ( riskdetail.riskID, var_userID, riskdetail.securityLevel, riskdetail.projectID) ORDER BY riskID;       END IF; END$$

-- -----------------------------------------------------
-- procedure getProjectTolActions
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectTolActions`(IN `projectID` BIGINT)
BEGIN SELECT 0 AS generation, toldescriptors.*, riskproject.projectTitle, riskproject.lft FROM toldescriptors, riskproject 				  WHERE toldescriptors.projectID = projectID AND toldescriptors.projectID = riskproject.projectID 				 UNION 				 SELECT -1 AS generation, toldescriptors.*, riskproject.projectTitle, riskproject.lft FROM toldescriptors, riskproject 						 WHERE toldescriptors.projectID IN (SELECT O2.projectID FROM riskproject AS O1, riskproject AS O2  WHERE O1.lft BETWEEN O2.lft AND O2.rgt  AND O1.projectID = projectID) 				 AND toldescriptors.projectID = riskproject.projectID 				 AND toldescriptors.projectID != projectID 				 ORDER BY generation DESC, lft DESC, tolLevel ASC; END$$

-- -----------------------------------------------------
-- procedure getProjectsForUser
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getProjectsForUser`(IN `var_user_id` BIGINT)
BEGIN 				SELECT DISTINCT *  FROM riskproject WHERE projectID IN 					 (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4 WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID IN 				   (SELECT projectID from projectusers WHERE stakeholderID = var_user_id               UNION SELECT projectID from projectriskmanagers WHERE stakeholderID = var_user_id               UNION SELECT projectID from projectowners WHERE stakeholderID = var_user_id)); END$$

-- -----------------------------------------------------
-- procedure getReportSessionID
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getReportSessionID`(IN `var_userID` BIGINT, IN `var_reportName` VARCHAR(1000))
BEGIN 			INSERT INTO reportsession (user_id, reportName) VALUES (var_userID,var_reportName); END$$

-- -----------------------------------------------------
-- procedure getRisk
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getRisk`(IN `var_user_id` BIGINT, IN `var_riskID` BIGINT, IN `var_context_id` BIGINT)
BEGIN   IF var_context_id != -1 THEN        SELECT riskdetail.*, R1.rank AS contextRank  FROM riskdetail        LEFT OUTER JOIN subjrank AS R1 ON (riskdetail.riskID = R1.riskID  AND  R1.projectID = var_context_id)        WHERE riskdetail.riskID = var_riskID  AND checkRiskSecurityView(riskdetail.riskID, var_user_id, riskdetail.securityLevel, riskdetail.projectID);     ELSE        SELECT riskdetail.*, 0 AS contextRank FROM riskdetail        WHERE riskdetail.riskID = var_riskID  AND checkRiskSecurityView(riskdetail.riskID, var_user_id, riskdetail.securityLevel, riskdetail.projectID);     END IF; END$$

-- -----------------------------------------------------
-- procedure getRolledAllocations
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getRolledAllocations`()
BEGIN  SET  @rownum:=0; SELECT @rownum:=@rownum+1 AS id,3 AS FLAG, riskproject.projectID, lft, rgt, r1.matrixID, r1.RCOUNT, r1.PROB, r1.IMPACT FROM riskproject 	 JOIN 	 (SELECT  COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT ,matrixID, projectID, treated FROM riskviewparent r  	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated 	 HAVING treated = 0  	 UNION 	 SELECT  COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT ,matrixID, projectID, treated FROM riskviewparent r  	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated 	 HAVING treated = 1) AS r1 ON riskproject.projectID = r1.projectID 	 UNION 	 SELECT @rownum:=@rownum+1 AS id,r2.FLAG, riskproject.projectID, lft, rgt, matrixID, r2.RCOUNT, r2.PROB, r2.IMPACT FROM riskproject 	 JOIN 	 (SELECT @rownum:=@rownum+1 AS id,AVG(0) AS FLAG, COUNT(*) AS RCOUNT, inherentProb AS PROB, inherentImpact AS IMPACT, matrixID, projectID, treated FROM riskviewparent r  	 GROUP BY inherentProb, inherentImpact, matrixID, projectID, treated 	 HAVING treated = 0 	 UNION 	 SELECT @rownum:=@rownum+1 AS id,AVG(1) AS FLAG, COUNT(*) AS RCOUNT, treatedProb AS PROB, treatedImpact AS IMPACT, matrixID, projectID, treated FROM riskviewparent r  	 GROUP BY treatedProb, treatedImpact, matrixID, projectID, treated 	 HAVING  treated = 1) AS r2 ON riskproject.projectID = r2.projectID; END$$

-- -----------------------------------------------------
-- procedure getTolAllocationMetric
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getTolAllocationMetric`(IN `var_projectID` BIGINT, IN `var_desc` INT)
BEGIN   IF var_desc < 1 THEN    SELECT count(riskID) AS COUNT, currentTolerance, projectID from risk    WHERE risk.projectID = var_projectID    GROUP BY currentTolerance;   ELSE    SELECT count(riskID) AS COUNT, currentTolerance, projectID from risk    WHERE risk.projectID IN (SELECT subprojectID FROM subprojects WHERE projectID = var_projectID  UNION SELECT var_projectID)    GROUP BY currentTolerance;   END IF; END$$

-- -----------------------------------------------------
-- procedure getTolAllocationMetricDeep
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getTolAllocationMetricDeep`(IN `var_projectID` BIGINT)
BEGIN    SELECT count(riskID) AS COUNT, currentTolerance, r1.projectTitle, r1.projectID FROM risk    JOIN riskproject AS r1 ON risk.projectID = r1.projectID    WHERE risk.projectID IN (SELECT subprojectID FROM subprojects WHERE projectID = var_projectID UNION SELECT var_projectID)    GROUP BY r1.projectID, currentTolerance; END$$

-- -----------------------------------------------------
-- procedure getUserMetric
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getUserMetric`(IN var_user_id BIGINT)
BEGIN 			SELECT DISTINCT * FROM metric WHERE id IN (SELECT metricID FROM usermetric WHERE userID = var_user_id); END$$

-- -----------------------------------------------------
-- procedure getWelcomeData
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `getWelcomeData`(IN `var_projectID` BIGINT)
BEGIN   SELECT  riskID AS internalID, 'RISK' AS element,  @rownum:=@rownum+1 AS id, CONCAT (riskProjectCode , CONCAT ( ': ' , title)) AS name, dateEntered AS date, description AS addInfo FROM risk, (SELECT @rownum := 0) r WHERE projectID = var_projectID   UNION SELECT reviewID AS internalID, 'REVIEW' AS element,  @rownum:=@rownum+1 AS id, title AS name, scheduledDate AS date, actualDate  AS addInfo FROM review WHERE CURRENT_TIMESTAMP < scheduledDate OR actualDate IS NULL   UNION SELECT incidentID AS internalID, 'INCIDENT' AS element,  @rownum:=@rownum+1 AS id, CONCAT (incidentProjectCode , CONCAT ( ': ' , title)) AS name, dateUpdated AS date, dateIncident  AS addInfo FROM incident   UNION SELECT riskID AS internalID, 'TASKS' AS element,  @rownum:=@rownum+1 AS id, CONCAT(CONCAT ('Identification Not Approved - ', riskProjectCode) , CONCAT(': ' , title)) AS name, CURRENT_TIMESTAMP AS date , NULL AS addInfo FROM risk WHERE  projectID = var_projectID AND dateIDApp IS NULL   UNION SELECT riskID AS internalID, 'TASKS' AS element,  @rownum:=@rownum+1 AS id, CONCAT(CONCAT ('Evaluation Not Approved - ', riskProjectCode) , CONCAT(': ' , title)) AS name, CURRENT_TIMESTAMP AS date  , NULL AS addInfo FROM risk WHERE projectID = var_projectID AND  dateEvalApp IS NULL AND dateIDApp IS NOT NULL   UNION SELECT riskID AS internalID, 'TASKS' AS element,  @rownum:=@rownum+1 AS id, CONCAT(CONCAT ('Mitigation Not Approved - ', riskProjectCode) , CONCAT(': ' , title)) AS name, CURRENT_TIMESTAMP AS date , NULL AS addInfo FROM risk WHERE projectID = var_projectID AND  dateMitApp IS NULL AND dateEvalApp IS NOT NULL AND dateIDApp IS NOT NULL; END$$

-- -----------------------------------------------------
-- procedure insertCategory
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `insertCategory`(IN var_parentID BIGINT, IN var_description VARCHAR(1000), IN var_contextID BIGINT, OUT idx BIGINT)
BEGIN 	INSERT INTO riskcategory (parentID, description, contextID) VALUES (var_parentID,var_description,var_contextID); 	SET idx = LAST_INSERT_ID(); END$$

-- -----------------------------------------------------
-- procedure insertObjective
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `insertObjective`( IN var_projectID BIGINT, IN var_objective VARCHAR(1000), IN var_parentID BIGINT, OUT idx BIGINT )
BEGIN         DECLARE right_most_sibling BIGINT DEFAULT 0;         SELECT rgt FROM objective WHERE objectiveID = var_parentID INTO right_most_sibling;         UPDATE objective         SET lft = CASE WHEN lft > right_most_sibling                     THEN lft + 2                     ELSE lft END,              rgt = CASE WHEN rgt >= right_most_sibling                     THEN rgt + 2                     ELSE rgt END         WHERE rgt >= right_most_sibling; 				INSERT INTO objective (projectID, objective, parentID, lft, rgt) VALUES (var_projectID,var_objective,var_parentID, right_most_sibling,right_most_sibling+1);         SET idx = LAST_INSERT_ID(); END$$

-- -----------------------------------------------------
-- procedure reassignRisk
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reassignRisk`(IN `var_riskID` BIGINT, IN `newProjectID` BIGINT)
BEGIN   DECLARE var_riskIndex BIGINT DEFAULT 1000000;   DECLARE var_projectCode VARCHAR(6);   SELECT riskIndex INTO var_riskIndex FROM riskproject  WHERE projectID = newProjectID;   SELECT projectCode  INTO var_projectCode FROM riskproject WHERE projectID = newProjectID;   UPDATE riskproject SET riskIndex = riskIndex+1 WHERE projectID = newProjectID;   UPDATE risk SET riskProjectCode = CONCAT(var_projectCode,var_riskIndex), projectID = newProjectID WHERE riskID = var_riskID; END$$

-- -----------------------------------------------------
-- procedure reportGetAuditComments
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetAuditComments`()
BEGIN  select auditcomments.*, S1.stakeholderID, S1.name AS enteredByName  from auditcomments  left outer join stakeholders AS S1  on auditcomments.enteredByID = S1.stakeholderID; END$$

-- -----------------------------------------------------
-- procedure reportGetContexts
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetContexts`()
BEGIN  select riskproject.*, stakeholders.*  from riskproject  left outer join stakeholders  on riskproject.projectRiskManagerID = stakeholders.stakeholderID; END$$

-- -----------------------------------------------------
-- procedure reportGetControls
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetControls`()
BEGIN select riskcontrols.*, control_effectiveness_defn.title  from riskcontrols  left outer join control_effectiveness_defn  on riskcontrols.effectiveness = control_effectiveness_defn.controlEffectivenessID; END$$

-- -----------------------------------------------------
-- procedure reportGetIncidents
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetIncidents`()
BEGIN select incident.*, stakeholders.name, stakeholders.stakeholderID  from incident  left outer join stakeholders  on incident.reportedByID = stakeholders.stakeholderID ; END$$

-- -----------------------------------------------------
-- procedure reportGetProjectRisks
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetProjectRisks`( IN userID BIGINT, IN projectID BIGINT, IN descendats BOOLEAN )
BEGIN  SELECT riskdetail.*  FROM riskdetail  WHERE ((riskdetail.projectID = projectID) OR (descendats > 0 AND riskdetail.projectID IN  (SELECT O3.projectID  FROM riskproject AS O3, riskproject AS O4  WHERE O3.lft BETWEEN O4.lft AND O4.rgt  AND O4.projectID = projectID) ))  AND (checkRiskSecurityView(riskdetail.riskID, userID, riskdetail.securityLevel, riskdetail.projectID)); END$$

-- -----------------------------------------------------
-- procedure reportGetReportRisks
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetReportRisks`(IN var_SessionID BIGINT, IN var_userID BIGINT)
BEGIN  select * from reportrisks  where reportrisks.sessionID = var_SessionID AND checkRiskSecurityView(reportrisks.riskID, var_userID, reportrisks.securityLevel, reportrisks.projectID)  order by rank; END$$

-- -----------------------------------------------------
-- procedure reportGetSessionData
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `reportGetSessionData`(IN var_SessionID BIGINT)
BEGIN  select * from reportsessiondata  where reportsessiondata.sessionID = var_SessionID  order by reportsessiondata.rank; END$$

-- -----------------------------------------------------
-- function resetPassword
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` FUNCTION `resetPassword`(var_name VARCHAR(200), var_email VARCHAR(500), var_newpass VARCHAR(20)) RETURNS bigint(20)
    READS SQL DATA
BEGIN   DECLARE userID BIGINT;   SELECT stakeholderID from qrmlogin.stakeholders where LOWER(name) = LOWER(var_name) and LOWER(email) = LOWER(var_email) INTO userID;   IF userID > 0 THEN     UPDATE stakeholderpassword SET password = PASSWORD(var_newpass) WHERE stakeholderID = userID;   END IF;   RETURN userID; END$$

-- -----------------------------------------------------
-- function resetPasswordWithAnswer
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` FUNCTION `resetPasswordWithAnswer`(var_name VARCHAR(200), var_email VARCHAR(500), var_answer VARCHAR(500), var_newpass VARCHAR(20)) RETURNS bigint(20)
    READS SQL DATA
BEGIN   DECLARE userID BIGINT;   SELECT stakeholderID from qrmlogin.stakeholders where LOWER(name) = LOWER(var_name) and LOWER(email) = LOWER(var_email) INTO userID;   IF userID > 0 THEN   SELECT stakeholderID from stakeholderpassword WHERE stakeholderID = userID AND secretanswer = PASSWORD(LOWER(var_answer)) INTO userID;   	IF userID > 0 THEN     		UPDATE stakeholderpassword SET password = PASSWORD(var_newpass) WHERE stakeholderID = userID AND secretanswer = PASSWORD(LOWER(var_answer));   	END IF;   END IF;   RETURN userID; END$$

-- -----------------------------------------------------
-- procedure setSecurity
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `setSecurity`( IN userName VARCHAR(100) )
BEGIN    GRANT SELECT, INSERT, UPDATE, DELETE ON risk TO userName; END$$

-- -----------------------------------------------------
-- procedure updateRiskActive
-- -----------------------------------------------------

DELIMITER $$
CREATE DEFINER=`qrm`@`%` PROCEDURE `updateRiskActive`(IN `var_riskID` BIGINT)
BEGIN   DECLARE numActiveRiskIncidents BIGINT;   SELECT COUNT(*) INTO numActiveRiskIncidents FROM incidentrisk WHERE riskID = var_riskID;   IF numActiveRiskIncidents > 0 THEN     UPDATE risk SET active = true WHERE riskID = var_riskID;    ELSE    UPDATE risk SET active = false WHERE riskID = var_riskID;    END IF; END$$

-- -----------------------------------------------------
-- View `allriskstakeholders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `allriskstakeholders`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `allriskstakeholders` AS select `stakeholders`.`stakeholderID` AS `stakeholderID`,`stakeholders`.`name` AS `name`,`stakeholders`.`email` AS `email`,`riskstakeholder`.`riskID` AS `riskID`,`riskstakeholder`.`description` AS `description` from (`stakeholders` join `riskstakeholder` on((`stakeholders`.`stakeholderID` = `riskstakeholder`.`stakeholderID`))) union select `stakeholders`.`stakeholderID` AS `stakeholderID`,`stakeholders`.`name` AS `name`,`stakeholders`.`email` AS `email`,`mitigationstep`.`riskID` AS `riskID`,'Mitigatipon Step Owner' AS `description` from (`stakeholders` join `mitigationstep` on((`stakeholders`.`stakeholderID` = `mitigationstep`.`personID`))) union select `stakeholders`.`stakeholderID` AS `stakeholderID`,`stakeholders`.`name` AS `name`,`stakeholders`.`email` AS `email`,`risk`.`riskID` AS `riskID`,'Risk Owner' AS `description` from (`stakeholders` join `risk` on((`stakeholders`.`stakeholderID` = `risk`.`ownerID`))) union select `stakeholders`.`stakeholderID` AS `stakeholderID`,`stakeholders`.`name` AS `name`,`stakeholders`.`email` AS `email`,`risk`.`riskID` AS `riskID`,'Risk Manager' AS `flag` from (`stakeholders` join `risk` on((`stakeholders`.`stakeholderID` = `risk`.`manager1ID`))) union select `stakeholders`.`stakeholderID` AS `stakeholderID`,`stakeholders`.`name` AS `name`,`stakeholders`.`email` AS `email`,`risk`.`riskID` AS `riskID`,'Contributing Risk Owner' AS `description` from (((`risk` join `riskrisk` on((`riskrisk`.`parentID` = `risk`.`riskID`))) join `riskdetail` `r2` on((`r2`.`riskID` = `riskrisk`.`childID`))) join `stakeholders` on((`stakeholders`.`stakeholderID` = `r2`.`ownerID`))) union select `stakeholders`.`stakeholderID` AS `stakeholderID`,`stakeholders`.`name` AS `name`,`stakeholders`.`email` AS `email`,`risk`.`riskID` AS `riskID`,'Contributing Risk Manager' AS `description` from (((`risk` join `riskrisk` on((`riskrisk`.`parentID` = `risk`.`riskID`))) join `riskdetail` `r2` on((`r2`.`riskID` = `riskrisk`.`childID`))) join `stakeholders` on((`stakeholders`.`stakeholderID` = `r2`.`manager1ID`)));

-- -----------------------------------------------------
-- View `jobemailready`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `jobemailready`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `jobemailready` AS select `t1`.`jobID` AS `jobID`,`t1`.`sendEmail` AS `sendEmail`,`t1`.`emailSent` AS `emailSent`,`t1`.`emailTitle` AS `emailTitle`,`t1`.`description` AS `description`,`t1`.`emailFormat` AS `emailFormat`,`t1`.`emailContent` AS `emailContent`,`t1`.`dateEmailSent` AS `dateEmailSent`,`t2`.`resultStr` AS `resultStr`,`t3`.`additionalUsers` AS `additionalUsers`,`t3`.`description` AS `description2`,`t4`.`readyToCollect` AS `readyToCollect`,`t4`.`reportFormat` AS `reportFormat`,`t5`.`email` AS `email`,`t5`.`name` AS `name` from ((((`reportdata` `t1` join `jobresult` `t2` on((`t1`.`jobID` = `t2`.`jobID`))) join `schedjob` `t3` on((`t1`.`schedJobID` = `t3`.`internalID`))) join `jobqueue` `t4` on((`t1`.`jobID` = `t4`.`jobID`))) join `qrmlogin`.`stakeholders` `t5` on((`t3`.`userID` = `t5`.`stakeholderID`))) where ((`t1`.`sendEmail` = 1) and (`t1`.`emailSent` = 0) and (`t4`.`readyToCollect` = 1));

-- -----------------------------------------------------
-- View `projectriskdetails`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `projectriskdetails`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `projectriskdetails` AS select `subprojectsandparent`.`projectID` AS `spprojectID`,`subprojectsandparent`.`subprojectID` AS `subprojectID`,`riskdetail`.`riskID` AS `riskID`,`riskdetail`.`externalID` AS `externalID`,`riskdetail`.`projectID` AS `projectID`,`riskdetail`.`promotedProjectID` AS `promotedProjectID`,`riskdetail`.`riskProjectCode` AS `riskProjectCode`,`riskdetail`.`securityLevel` AS `securityLevel`,`riskdetail`.`ownerID` AS `ownerID`,`riskdetail`.`manager1ID` AS `manager1ID`,`riskdetail`.`manager2ID` AS `manager2ID`,`riskdetail`.`manager3ID` AS `manager3ID`,`riskdetail`.`matrixID` AS `matrixID`,`riskdetail`.`title` AS `title`,`riskdetail`.`description` AS `description`,`riskdetail`.`mitPlanSummary` AS `mitPlanSummary`,`riskdetail`.`mitPlanSummaryUpdate` AS `mitPlanSummaryUpdate`,`riskdetail`.`impact` AS `impact`,`riskdetail`.`cause` AS `cause`,`riskdetail`.`consequences` AS `consequences`,`riskdetail`.`impSafety` AS `impSafety`,`riskdetail`.`impSpec` AS `impSpec`,`riskdetail`.`impCost` AS `impCost`,`riskdetail`.`impTime` AS `impTime`,`riskdetail`.`impReputation` AS `impReputation`,`riskdetail`.`impEnvironment` AS `impEnvironment`,`riskdetail`.`inherentProb` AS `inherentProb`,`riskdetail`.`inherentImpact` AS `inherentImpact`,`riskdetail`.`treatedProb` AS `treatedProb`,`riskdetail`.`treatedImpact` AS `treatedImpact`,`riskdetail`.`startExposure` AS `startExposure`,`riskdetail`.`endExposure` AS `endExposure`,`riskdetail`.`inherentTolerance` AS `inherentTolerance`,`riskdetail`.`treatedTolerance` AS `treatedTolerance`,`riskdetail`.`currentTolerance` AS `currentTolerance`,`riskdetail`.`subjectiveRank` AS `subjectiveRank`,`riskdetail`.`objectiveRank` AS `objectiveRank`,`riskdetail`.`levelRank` AS `levelRank`,`riskdetail`.`active` AS `active`,`riskdetail`.`treated` AS `treated`,`riskdetail`.`dateUpdated` AS `dateUpdated`,`riskdetail`.`timeUpdated` AS `timeUpdated`,`riskdetail`.`dateEntered` AS `dateEntered`,`riskdetail`.`dateEvalApp` AS `dateEvalApp`,`riskdetail`.`dateEvalRev` AS `dateEvalRev`,`riskdetail`.`dateIDApp` AS `dateIDApp`,`riskdetail`.`dateIDRev` AS `dateIDRev`,`riskdetail`.`dateMitApp` AS `dateMitApp`,`riskdetail`.`dateMitPrep` AS `dateMitPrep`,`riskdetail`.`dateMitRev` AS `dateMitRev`,`riskdetail`.`idEvalApp` AS `idEvalApp`,`riskdetail`.`idEvalRev` AS `idEvalRev`,`riskdetail`.`idIDApp` AS `idIDApp`,`riskdetail`.`idIDRev` AS `idIDRev`,`riskdetail`.`idMitApp` AS `idMitApp`,`riskdetail`.`idMitPrep` AS `idMitPrep`,`riskdetail`.`idMitRev` AS `idMitRev`,`riskdetail`.`treatmentID` AS `treatmentID`,`riskdetail`.`treatmentAvoidance` AS `treatmentAvoidance`,`riskdetail`.`treatmentReduction` AS `treatmentReduction`,`riskdetail`.`treatmentRetention` AS `treatmentRetention`,`riskdetail`.`treatmentTransfer` AS `treatmentTransfer`,`riskdetail`.`summaryRisk` AS `summaryRisk`,`riskdetail`.`parentSummaryRisk` AS `parentSummaryRisk`,`riskdetail`.`private` AS `private`,`riskdetail`.`restricted` AS `restricted`,`riskdetail`.`pub` AS `pub`,`riskdetail`.`liketype` AS `liketype`,`riskdetail`.`likeprob` AS `likeprob`,`riskdetail`.`likealpha` AS `likealpha`,`riskdetail`.`liket` AS `liket`,`riskdetail`.`likepostType` AS `likepostType`,`riskdetail`.`likepostProb` AS `likepostProb`,`riskdetail`.`likepostAlpha` AS `likepostAlpha`,`riskdetail`.`likepostT` AS `likepostT`,`riskdetail`.`primCatID` AS `primCatID`,`riskdetail`.`secCatID` AS `secCatID`,`riskdetail`.`estimatedContingencey` AS `estimatedContingencey`,`riskdetail`.`useCalculatedContingency` AS `useCalculatedContingency`,`riskdetail`.`preMitContingency` AS `preMitContingency`,`riskdetail`.`postMitContingency` AS `postMitContingency`,`riskdetail`.`preMitContingencyWeighted` AS `preMitContingencyWeighted`,`riskdetail`.`postMitContingencyWeighted` AS `postMitContingencyWeighted`,`riskdetail`.`contingencyPercentile` AS `contingencyPercentile`,`riskdetail`.`mitigationCost` AS `mitigationCost`,`riskdetail`.`useCalculatedProb` AS `useCalculatedProb`,`riskdetail`.`extObject` AS `extObject`,`riskdetail`.`preMitImage` AS `preMitImage`,`riskdetail`.`postMitImage` AS `postMitImage`,`riskdetail`.`matImage` AS `matImage`,`riskdetail`.`ownerName` AS `ownerName`,`riskdetail`.`manager1Name` AS `manager1Name`,`riskdetail`.`manager2Name` AS `manager2Name`,`riskdetail`.`manager3Name` AS `manager3Name`,`riskdetail`.`primCatName` AS `primCatName`,`riskdetail`.`secCatName` AS `secCatName`,`riskdetail`.`fromProjCode` AS `fromProjCode`,`riskdetail`.`toProjCode` AS `toProjCode`,`riskdetail`.`parentRiskProjectCode` AS `parentRiskProjectCode` from (`subprojectsandparent` join `riskdetail` on((`riskdetail`.`projectID` = `subprojectsandparent`.`subprojectID`)));

-- -----------------------------------------------------
-- View `reportrisks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reportrisks`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `reportrisks` AS select `risk`.`riskID` AS `riskID`,`risk`.`externalID` AS `externalID`,`risk`.`projectID` AS `projectID`,`risk`.`promotedProjectID` AS `promotedProjectID`,`risk`.`riskProjectCode` AS `riskProjectCode`,`risk`.`securityLevel` AS `securityLevel`,`risk`.`ownerID` AS `ownerID`,`risk`.`manager1ID` AS `manager1ID`,`risk`.`manager2ID` AS `manager2ID`,`risk`.`manager3ID` AS `manager3ID`,`risk`.`matrixID` AS `matrixID`,`risk`.`title` AS `title`,`risk`.`description` AS `description`,`risk`.`mitPlanSummary` AS `mitPlanSummary`,`risk`.`mitPlanSummaryUpdate` AS `mitPlanSummaryUpdate`,`risk`.`impact` AS `impact`,`risk`.`cause` AS `cause`,`risk`.`consequences` AS `consequences`,`risk`.`impSafety` AS `impSafety`,`risk`.`impSpec` AS `impSpec`,`risk`.`impCost` AS `impCost`,`risk`.`impTime` AS `impTime`,`risk`.`impReputation` AS `impReputation`,`risk`.`impEnvironment` AS `impEnvironment`,`risk`.`inherentProb` AS `inherentProb`,`risk`.`inherentImpact` AS `inherentImpact`,`risk`.`treatedProb` AS `treatedProb`,`risk`.`treatedImpact` AS `treatedImpact`,`risk`.`startExposure` AS `startExposure`,`risk`.`endExposure` AS `endExposure`,`risk`.`inherentTolerance` AS `inherentTolerance`,`risk`.`treatedTolerance` AS `treatedTolerance`,`risk`.`currentTolerance` AS `currentTolerance`,`risk`.`subjectiveRank` AS `subjectiveRank`,`risk`.`objectiveRank` AS `objectiveRank`,`risk`.`levelRank` AS `levelRank`,`risk`.`active` AS `active`,`risk`.`treated` AS `treated`,`risk`.`dateUpdated` AS `dateUpdated`,`risk`.`timeUpdated` AS `timeUpdated`,`risk`.`dateEntered` AS `dateEntered`,`risk`.`dateEvalApp` AS `dateEvalApp`,`risk`.`dateEvalRev` AS `dateEvalRev`,`risk`.`dateIDApp` AS `dateIDApp`,`risk`.`dateIDRev` AS `dateIDRev`,`risk`.`dateMitApp` AS `dateMitApp`,`risk`.`dateMitPrep` AS `dateMitPrep`,`risk`.`dateMitRev` AS `dateMitRev`,`risk`.`idEvalApp` AS `idEvalApp`,`risk`.`idEvalRev` AS `idEvalRev`,`risk`.`idIDApp` AS `idIDApp`,`risk`.`idIDRev` AS `idIDRev`,`risk`.`idMitApp` AS `idMitApp`,`risk`.`idMitPrep` AS `idMitPrep`,`risk`.`idMitRev` AS `idMitRev`,`risk`.`treatmentID` AS `treatmentID`,`risk`.`treatmentAvoidance` AS `treatmentAvoidance`,`risk`.`treatmentReduction` AS `treatmentReduction`,`risk`.`treatmentRetention` AS `treatmentRetention`,`risk`.`treatmentTransfer` AS `treatmentTransfer`,`risk`.`summaryRisk` AS `summaryRisk`,`risk`.`parentSummaryRisk` AS `parentSummaryRisk`,`risk`.`private` AS `private`,`risk`.`restricted` AS `restricted`,`risk`.`pub` AS `pub`,`risk`.`liketype` AS `liketype`,`risk`.`likeprob` AS `likeprob`,`risk`.`likealpha` AS `likealpha`,`risk`.`liket` AS `liket`,`risk`.`likepostType` AS `likepostType`,`risk`.`likepostProb` AS `likepostProb`,`risk`.`likepostAlpha` AS `likepostAlpha`,`risk`.`likepostT` AS `likepostT`,`risk`.`primCatID` AS `primCatID`,`risk`.`secCatID` AS `secCatID`,`risk`.`estimatedContingencey` AS `estimatedContingencey`,`risk`.`useCalculatedContingency` AS `useCalculatedContingency`,`risk`.`preMitContingency` AS `preMitContingency`,`risk`.`postMitContingency` AS `postMitContingency`,`risk`.`preMitContingencyWeighted` AS `preMitContingencyWeighted`,`risk`.`postMitContingencyWeighted` AS `postMitContingencyWeighted`,`risk`.`contingencyPercentile` AS `contingencyPercentile`,`risk`.`mitigationCost` AS `mitigationCost`,`risk`.`useCalculatedProb` AS `useCalculatedProb`,`risk`.`extObject` AS `extObject`,`risk`.`preMitImage` AS `preMitImage`,`risk`.`postMitImage` AS `postMitImage`,`risk`.`matImage` AS `matImage`,`s1`.`name` AS `ownerName`,`s2`.`name` AS `manager1Name`,`s2`.`name` AS `manager2Name`,`s4`.`name` AS `manager3Name`,`r1`.`rank` AS `contextRank`,`c1`.`description` AS `primCatName`,`c2`.`description` AS `secCatName`,`reportsessiondata`.`rank` AS `sessionRank`,`reportsessiondata`.`id` AS `id`,`reportsessiondata`.`sessionID` AS `sessionID`,`reportsessiondata`.`dataElement` AS `dataElement`,`reportsessiondata`.`dataString` AS `dataString`,`reportsessiondata`.`rank` AS `rank`,`reportsessiondata`.`dataID` AS `dataID`,`reportsessiondata`.`dataBlob` AS `dataBlob` from ((((((((`risk` left join `stakeholders` `s1` on((`risk`.`ownerID` = `s1`.`stakeholderID`))) left join `stakeholders` `s2` on((`risk`.`manager1ID` = `s2`.`stakeholderID`))) left join `stakeholders` `s3` on((`risk`.`manager2ID` = `s3`.`stakeholderID`))) left join `stakeholders` `s4` on((`risk`.`manager3ID` = `s4`.`stakeholderID`))) left join `subjrank` `r1` on(((`risk`.`riskID` = `r1`.`riskID`) and (`r1`.`projectID` = `risk`.`projectID`)))) left join `riskcategory` `c1` on((`risk`.`primCatID` = `c1`.`internalID`))) left join `riskcategory` `c2` on((`risk`.`secCatID` = `c2`.`internalID`))) join `reportsessiondata` on(((`risk`.`riskID` = `reportsessiondata`.`dataID`) and (`reportsessiondata`.`dataElement` = 'RISK')))) order by `reportsessiondata`.`rank`;

-- -----------------------------------------------------
-- View `riskdetail`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `riskdetail`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `riskdetail` AS select `risk`.`riskID` AS `riskID`,`risk`.`externalID` AS `externalID`,`risk`.`projectID` AS `projectID`,`risk`.`promotedProjectID` AS `promotedProjectID`,`risk`.`riskProjectCode` AS `riskProjectCode`,`risk`.`securityLevel` AS `securityLevel`,`risk`.`ownerID` AS `ownerID`,`risk`.`manager1ID` AS `manager1ID`,`risk`.`manager2ID` AS `manager2ID`,`risk`.`manager3ID` AS `manager3ID`,`risk`.`matrixID` AS `matrixID`,`risk`.`title` AS `title`,`risk`.`description` AS `description`,`risk`.`mitPlanSummary` AS `mitPlanSummary`,`risk`.`mitPlanSummaryUpdate` AS `mitPlanSummaryUpdate`,`risk`.`impact` AS `impact`,`risk`.`cause` AS `cause`,`risk`.`consequences` AS `consequences`,`risk`.`impSafety` AS `impSafety`,`risk`.`impSpec` AS `impSpec`,`risk`.`impCost` AS `impCost`,`risk`.`impTime` AS `impTime`,`risk`.`impReputation` AS `impReputation`,`risk`.`impEnvironment` AS `impEnvironment`,`risk`.`inherentProb` AS `inherentProb`,`risk`.`inherentImpact` AS `inherentImpact`,`risk`.`treatedProb` AS `treatedProb`,`risk`.`treatedImpact` AS `treatedImpact`,`risk`.`startExposure` AS `startExposure`,`risk`.`endExposure` AS `endExposure`,`risk`.`inherentTolerance` AS `inherentTolerance`,`risk`.`treatedTolerance` AS `treatedTolerance`,`risk`.`currentTolerance` AS `currentTolerance`,`risk`.`subjectiveRank` AS `subjectiveRank`,`risk`.`objectiveRank` AS `objectiveRank`,`risk`.`levelRank` AS `levelRank`,`risk`.`active` AS `active`,`risk`.`treated` AS `treated`,`risk`.`dateUpdated` AS `dateUpdated`,`risk`.`timeUpdated` AS `timeUpdated`,`risk`.`dateEntered` AS `dateEntered`,`risk`.`dateEvalApp` AS `dateEvalApp`,`risk`.`dateEvalRev` AS `dateEvalRev`,`risk`.`dateIDApp` AS `dateIDApp`,`risk`.`dateIDRev` AS `dateIDRev`,`risk`.`dateMitApp` AS `dateMitApp`,`risk`.`dateMitPrep` AS `dateMitPrep`,`risk`.`dateMitRev` AS `dateMitRev`,`risk`.`idEvalApp` AS `idEvalApp`,`risk`.`idEvalRev` AS `idEvalRev`,`risk`.`idIDApp` AS `idIDApp`,`risk`.`idIDRev` AS `idIDRev`,`risk`.`idMitApp` AS `idMitApp`,`risk`.`idMitPrep` AS `idMitPrep`,`risk`.`idMitRev` AS `idMitRev`,`risk`.`treatmentID` AS `treatmentID`,`risk`.`treatmentAvoidance` AS `treatmentAvoidance`,`risk`.`treatmentReduction` AS `treatmentReduction`,`risk`.`treatmentRetention` AS `treatmentRetention`,`risk`.`treatmentTransfer` AS `treatmentTransfer`,`risk`.`summaryRisk` AS `summaryRisk`,`risk`.`parentSummaryRisk` AS `parentSummaryRisk`,`risk`.`private` AS `private`,`risk`.`restricted` AS `restricted`,`risk`.`pub` AS `pub`,`risk`.`liketype` AS `liketype`,`risk`.`likeprob` AS `likeprob`,`risk`.`likealpha` AS `likealpha`,`risk`.`liket` AS `liket`,`risk`.`likepostType` AS `likepostType`,`risk`.`likepostProb` AS `likepostProb`,`risk`.`likepostAlpha` AS `likepostAlpha`,`risk`.`likepostT` AS `likepostT`,`risk`.`primCatID` AS `primCatID`,`risk`.`secCatID` AS `secCatID`,`risk`.`estimatedContingencey` AS `estimatedContingencey`,`risk`.`useCalculatedContingency` AS `useCalculatedContingency`,`risk`.`preMitContingency` AS `preMitContingency`,`risk`.`postMitContingency` AS `postMitContingency`,`risk`.`preMitContingencyWeighted` AS `preMitContingencyWeighted`,`risk`.`postMitContingencyWeighted` AS `postMitContingencyWeighted`,`risk`.`contingencyPercentile` AS `contingencyPercentile`,`risk`.`mitigationCost` AS `mitigationCost`,`risk`.`useCalculatedProb` AS `useCalculatedProb`,`risk`.`extObject` AS `extObject`,`risk`.`preMitImage` AS `preMitImage`,`risk`.`postMitImage` AS `postMitImage`,`risk`.`matImage` AS `matImage`,`s1`.`name` AS `ownerName`,`s2`.`name` AS `manager1Name`,`s2`.`name` AS `manager2Name`,`s4`.`name` AS `manager3Name`,`c1`.`description` AS `primCatName`,`c2`.`description` AS `secCatName`,`p1`.`projectCode` AS `fromProjCode`,`p2`.`projectCode` AS `toProjCode`,`r2`.`riskProjectCode` AS `parentRiskProjectCode` from (((((((((`risk` left join `qrmlogin`.`stakeholders` `s1` on((`risk`.`ownerID` = `s1`.`stakeholderID`))) left join `qrmlogin`.`stakeholders` `s2` on((`risk`.`manager1ID` = `s2`.`stakeholderID`))) left join `qrmlogin`.`stakeholders` `s3` on((`risk`.`manager2ID` = `s3`.`stakeholderID`))) left join `qrmlogin`.`stakeholders` `s4` on((`risk`.`manager3ID` = `s4`.`stakeholderID`))) left join `riskcategory` `c1` on((`risk`.`primCatID` = `c1`.`internalID`))) left join `riskcategory` `c2` on((`risk`.`secCatID` = `c2`.`internalID`))) left join `riskproject` `p1` on((`p1`.`projectID` = `risk`.`projectID`))) left join `riskproject` `p2` on((`p2`.`projectID` = `risk`.`promotedProjectID`))) left join `risk` `r2` on((`r2`.`riskID` = `risk`.`parentSummaryRisk`)));

-- -----------------------------------------------------
-- View `riskview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `riskview`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `riskview` AS select `risk`.`riskID` AS `riskID`,`risk`.`projectID` AS `projectID`,`risk`.`matrixID` AS `matrixID`,floor(`risk`.`inherentImpact`) AS `inherentimpact`,floor(`risk`.`inherentProb`) AS `inherentprob`,floor(`risk`.`treatedImpact`) AS `treatedimpact`,floor(`risk`.`treatedProb`) AS `treatedprob`,`risk`.`treated` AS `treated`,`risk`.`summaryRisk` AS `summaryRisk` from `risk`;

-- -----------------------------------------------------
-- View `riskviewparent`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `riskviewparent`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `riskviewparent` AS select distinct `risk`.`riskID` AS `riskID`,`risk`.`projectID` AS `projectID`,`risk`.`matrixID` AS `matrixID`,`risk`.`summaryRisk` AS `summaryRisk`,floor(`risk`.`inherentImpact`) AS `inherentimpact`,floor(`risk`.`inherentProb`) AS `inherentprob`,floor(`risk`.`treatedImpact`) AS `treatedimpact`,floor(`risk`.`treatedProb`) AS `treatedprob`,`risk`.`treated` AS `treated`,`childparent`.`parentID` AS `parentID` from (`risk` join `childparent` on((`childparent`.`parentID` = `risk`.`riskID`)));

-- -----------------------------------------------------
-- View `stakeholders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `stakeholders`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `stakeholders` AS select distinct `qrmlogin`.`stakeholders`.`stakeholderID` AS `stakeholderID`,`qrmlogin`.`stakeholders`.`name` AS `name`,`qrmlogin`.`stakeholders`.`email` AS `email`,`qrmlogin`.`stakeholders`.`lastlogon` AS `lastlogon`,`qrmlogin`.`stakeholders`.`active` AS `active`,`qrmlogin`.`stakeholders`.`allowLogon` AS `allowLogon`,`qrmlogin`.`stakeholders`.`allowUserMgmt` AS `allowUserMgmt`,`qrmlogin`.`stakeholders`.`emailmsgtypes` AS `emailmsgtypes` from (`qrmlogin`.`stakeholders` left join `qrmlogin`.`userrepository` on((`qrmlogin`.`stakeholders`.`stakeholderID` = `qrmlogin`.`userrepository`.`stakeholderID`)));

-- -----------------------------------------------------
-- View `subprojects`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `subprojects`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `subprojects` AS select `p1`.`projectID` AS `projectID`,`p2`.`projectID` AS `subprojectID` from (`riskproject` `p1` join `riskproject` `p2`) where ((`p2`.`lft` between `p1`.`lft` and `p1`.`rgt`) and (`p1`.`projectID` <> `p2`.`projectID`));

-- -----------------------------------------------------
-- View `subprojectsandparent`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `subprojectsandparent`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `subprojectsandparent` AS select `subprojects`.`projectID` AS `projectID`,`subprojects`.`subprojectID` AS `subprojectID` from `subprojects` where ((`subprojects`.`projectID` > 0) and (`subprojects`.`subprojectID` > 0)) union select `p1`.`projectID` AS `projectID`,`p2`.`projectID` AS `subprojectID` from (`riskproject` `p1` join `riskproject` `p2`) where ((`p1`.`projectID` = `p2`.`projectID`) and (`p1`.`projectID` > 0) and (`p2`.`projectID` > 0)) order by `projectID`;

-- -----------------------------------------------------
-- View `superprojects`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `superprojects`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `superprojects` AS select `p1`.`projectID` AS `projectID`,`p2`.`projectID` AS `superprojectID` from (`riskproject` `p1` join `riskproject` `p2`) where ((`p1`.`lft` between `p2`.`lft` and `p2`.`rgt`) and (`p1`.`projectID` <> `p2`.`projectID`));

-- -----------------------------------------------------
-- View `userrepository`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `userrepository`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`qrm`@`%` SQL SECURITY DEFINER VIEW `userrepository` AS select `qrmlogin`.`userrepository`.`id` AS `id`,`qrmlogin`.`userrepository`.`stakeholderID` AS `stakeholderID`,`qrmlogin`.`userrepository`.`repID` AS `repID` from `qrmlogin`.`userrepository`;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

DELIMITER $$
CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertAttachmentTrigger`
BEFORE INSERT ON `qrm0`.`attachment`
FOR EACH ROW
BEGIN   SET NEW.dateUploaded = SYSDATE(); END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertProjectAfterTrigger`
AFTER INSERT ON `qrm0`.`riskproject`
FOR EACH ROW
BEGIN    INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (NEW.projectID, NEW.projectRiskManagerID);    INSERT INTO projectowners (projectID, stakeholderID) VALUES (NEW.projectID, NEW.projectRiskManagerID); END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`updateProjectTrigger`
AFTER UPDATE ON `qrm0`.`riskproject`
FOR EACH ROW
BEGIN   IF OLD.projectCode != NEW.projectCode THEN   	UPDATE risk set riskProjectCode = CONCAT(NEW.projectCode, SUBSTR(riskProjectCode, LENGTH(OLD.projectCode)+1)) WHERE risk.projectID = OLD.projectID AND OLD.projectCode != NEW.projectCode;   END IF;   IF NEW.minimumSecurityLevel > OLD.minimumSecurityLevel THEN     UPDATE risk SET securityLevel = GREATEST(NEW.minimumSecurityLevel, risk.securityLevel) WHERE risk.projectID = OLD.projectID;   END IF; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`updateToleranceMatrixTrigger`
AFTER UPDATE ON `qrm0`.`tolerancematrix`
FOR EACH ROW
BEGIN       UPDATE risk SET matrixID = matrixID,          inherentProb = LEAST(NEW.maxProb+0.5, inherentProb),          inherentImpact = LEAST(NEW.maxImpact+0.5, inherentImpact),         treatedProb = LEAST(NEW.maxProb+0.5, treatedProb),          treatedImpact = LEAST(NEW.maxImpact+0.5,treatedImpact)          WHERE matrixID = NEW.matrixID; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertRiskBeforeTrigger`
BEFORE INSERT ON `qrm0`.`risk`
FOR EACH ROW
BEGIN   DECLARE var_riskIndex BIGINT DEFAULT 1000000;   DECLARE var_projectCode VARCHAR(6);   DECLARE var_tol INT DEFAULT 0;   DECLARE var_minimumSecurityLevel INT DEFAULT 0;   DECLARE var_matID BIGINT;   DECLARE var_singlePhase BOOLEAN DEFAULT FALSE;   SELECT riskIndex INTO var_riskIndex FROM riskproject  WHERE projectID = NEW.projectID;   SELECT projectCode  INTO var_projectCode FROM riskproject WHERE projectID = NEW.projectID;   SELECT minimumSecurityLevel  INTO var_minimumSecurityLevel FROM riskproject WHERE projectID = NEW.projectID;   SELECT getProjectMatID(NEW.projectID) INTO var_matID;   SELECT singlePhase  INTO var_singlePhase FROM riskproject WHERE projectID = NEW.projectID;   UPDATE riskproject SET riskIndex = riskIndex+1 WHERE projectID = NEW.projectID;   SET NEW.securityLevel = GREATEST(var_minimumSecurityLevel, NEW.securityLevel); 	IF NEW.inherentProb IS NULL OR NEW.inherentProb = 0 THEN   		 SET NEW.inherentProb = 5.5; 	END IF; 	IF NEW.inherentImpact IS NULL OR NEW.inherentImpact = 0 THEN  		SET NEW.inherentImpact = 5.5; 	END IF; 	IF NEW.treatedProb IS NULL OR NEW.treatedProb = 0 THEN    		SET NEW.treatedProb = 1.5; 	END IF; 	IF NEW.treatedImpact IS NULL OR NEW.treatedImpact = 0 THEN    		SET NEW.treatedImpact = 1.5; 	END IF;   IF var_singlePhase THEN      SET NEW.treatedProb   = NEW.inherentProb;      SET NEW.treatedImpact = NEW.inherentImpact;   END IF; 	IF NEW.endExposure IS NULL THEN    		SET NEW.endExposure = ADDDATE( CURRENT_DATE(), 365); 	END IF; 	IF NEW.startExposure IS NULL THEN    		SET NEW.startExposure = CURRENT_DATE(); 	END IF; 	SET NEW.riskProjectCode = CONCAT(var_projectCode,var_riskIndex);   SET NEW.matrixID = var_matID;   SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.inherentProb-1)*tolerancematrix.maximpact+FLOOR (NEW.inherentImpact)),1) FROM tolerancematrix WHERE tolerancematrix.matrixID = var_matID INTO var_tol; 	SET NEW.inherentTolerance = var_tol;   SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.treatedProb-1)*tolerancematrix.maximpact+FLOOR (NEW.treatedImpact)),1) FROM tolerancematrix WHERE tolerancematrix.matrixID = var_matID INTO var_tol; 	SET NEW.treatedTolerance = var_tol;   IF NEW.treated > 0 THEN       SET NEW.currentTolerance = NEW.treatedTolerance;   ELSE       SET NEW.currentTolerance = NEW.inherentTolerance;   END IF;   IF NEW.primCatID IS NULL THEN      SET NEW.primCatID = 0;   END IF;   IF NEW.secCatID IS NULL THEN      SET NEW.secCatID = 0;   END IF; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`updateRiskTrigger`
BEFORE UPDATE ON `qrm0`.`risk`
FOR EACH ROW
BEGIN     DECLARE var_tol INT DEFAULT 0;     DECLARE var_minimumSecurityLevel INT DEFAULT 0;     DECLARE var_singlePhase BOOLEAN DEFAULT FALSE;     SELECT minimumSecurityLevel  INTO var_minimumSecurityLevel FROM riskproject WHERE projectID = NEW.projectID;     SELECT singlePhase  INTO var_singlePhase FROM riskproject WHERE projectID = NEW.projectID;   SET NEW.securityLevel = GREATEST(var_minimumSecurityLevel, NEW.securityLevel);   IF NEW.manager2ID IS NULL THEN       SET NEW.manager2ID = NEW.manager1ID;   END IF;   IF NEW.manager3ID = NULL THEN       SET NEW.manager3ID = NEW.manager1ID;   END IF;   IF var_singlePhase THEN       IF NEW.inherentProb != OLD.inherentProb ||  NEW.inherentImpact != OLD.inherentImpact THEN           SET NEW.treatedProb   = NEW.inherentProb;           SET NEW.treatedImpact = NEW.inherentImpact;       END IF;       IF NEW.treatedProb != OLD.treatedProb ||  NEW.treatedImpact != OLD.treatedImpact THEN           SET NEW.inherentProb   = NEW.treatedProb;           SET NEW.inherentImpact = NEW.treatedImpact;       END IF;   END IF;   SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.inherentProb-1)*tolerancematrix.maximpact+FLOOR (NEW.inherentImpact)),1) AS tolStr FROM tolerancematrix WHERE tolerancematrix.matrixID = NEW.matrixID INTO var_tol;   SET NEW.inherentTolerance = var_tol;   SELECT SUBSTR(tolerancematrix.tolString,(FLOOR(NEW.treatedProb-1)*tolerancematrix.maximpact+FLOOR (NEW.treatedImpact)),1) AS tolStr FROM tolerancematrix WHERE tolerancematrix.matrixID = NEW.matrixID INTO var_tol;   SET NEW.treatedTolerance = var_tol;   IF (NEW.treated != OLD.treated || NEW.treatedTolerance != OLD.treatedTolerance) && NEW.treated > 0 THEN     SET NEW.currentTolerance = NEW.treatedTolerance;   END IF;   IF (NEW.treated != OLD.treated || NEW.inherentTolerance != OLD.inherentTolerance) && NEW.treated = 0 THEN     SET NEW.currentTolerance = NEW.inherentTolerance;   END IF; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertauditcommentsTrigger`
BEFORE INSERT ON `qrm0`.`auditcomments`
FOR EACH ROW
BEGIN    UPDATE risk SET dateIDRev = CURDATE(), idIDRev = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.identification = true AND NEW.review = true;    UPDATE risk SET dateIDApp = CURDATE(), idIDApp = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.identification = true AND NEW.approval = true;    UPDATE risk SET dateEvalRev = CURDATE(), idEvalRev = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.evaluation = true AND NEW.review = true;    UPDATE risk SET dateEvalApp = CURDATE(), idEvalApp = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.evaluation = true AND NEW.approval = true;    UPDATE risk SET dateMitRev = CURDATE(), idMitRev = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.mitigation = true AND NEW.review = true;    UPDATE risk SET dateMitApp = CURDATE(), idMitApp = NEW.enteredByID WHERE riskID = NEW.riskID AND NEW.mitigation = true AND NEW.approval = true; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertIncidentBeforeTrigger`
BEFORE INSERT ON `qrm0`.`incident`
FOR EACH ROW
BEGIN   DECLARE var_incidentIndex BIGINT DEFAULT 1000000;   DECLARE var_projectCode VARCHAR(6);   SELECT incidentIndex INTO var_incidentIndex FROM riskproject  WHERE projectID = NEW.projectID;   SELECT projectCode  INTO var_projectCode FROM riskproject WHERE projectID = NEW.projectID;   UPDATE riskproject SET incidentIndex = incidentIndex+1 WHERE projectID = NEW.projectID;   SET NEW.incidentProjectCode = CONCAT(var_projectCode,var_incidentIndex);   IF NEW.bIssue > 0 THEN      SET NEW.incidentProjectCode = CONCAT("ISS-",NEW.incidentProjectCode);   ELSE      SET NEW.incidentProjectCode = CONCAT("IND-",NEW.incidentProjectCode);   END IF;  END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertObjectiveTrigger`
BEFORE INSERT ON `qrm0`.`objective`
FOR EACH ROW
BEGIN     IF NEW.parentID IS NULL THEN         SET NEW.parentID = 1;     END IF; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`deleteIncidentRiskTriggerAfter`
AFTER DELETE ON `qrm0`.`incidentrisk`
FOR EACH ROW
BEGIN 	CALL updateRiskActive(OLD.riskID); END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertIncidentRiskTriggerAfter`
AFTER INSERT ON `qrm0`.`incidentrisk`
FOR EACH ROW
BEGIN 	CALL updateRiskActive(NEW.riskID); END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`updateIncidentRiskTriggerAfter`
AFTER UPDATE ON `qrm0`.`incidentrisk`
FOR EACH ROW
BEGIN 	CALL updateRiskActive(NEW.riskID); END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`deleteMitigationStep`
AFTER DELETE ON `qrm0`.`mitigationstep`
FOR EACH ROW
BEGIN   SELECT SUM(estCost) INTO @mitcost FROM mitigationstep WHERE riskID = OLD.riskID AND response = 0;   UPDATE risk SET mitigationCost = @mitcost WHERE riskID = OLD.riskID;   DELETE FROM updatecomment WHERE hostID = OLD.mitstepID AND hostType = 'MITIGATION'; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertMitStepTrigger`
BEFORE INSERT ON `qrm0`.`mitigationstep`
FOR EACH ROW
BEGIN    UPDATE risk SET dateMitPrep = CURRENT_DATE WHERE riskID = NEW.riskID AND dateMitPrep IS NULL;   SET NEW.dateUpdated = CURRENT_TIMESTAMP;   SET NEW.dateEntered = CURRENT_TIMESTAMP; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`insertMitigationStep`
AFTER INSERT ON `qrm0`.`mitigationstep`
FOR EACH ROW
BEGIN   SELECT SUM(estCost) INTO @mitcost FROM mitigationstep WHERE riskID = NEW.riskID AND response = 0;   UPDATE risk SET mitigationCost = @mitcost WHERE riskID = NEW.riskID; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`updateMitStepTrigger`
BEFORE UPDATE ON `qrm0`.`mitigationstep`
FOR EACH ROW
BEGIN   IF NEW.description IS NULL THEN      SET NEW.description = "Enter Mitigation Action";   END IF;   SET  NEW.dateUpdated = CURRENT_TIMESTAMP; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`updateMitigationStep`
AFTER UPDATE ON `qrm0`.`mitigationstep`
FOR EACH ROW
BEGIN   SELECT SUM(estCost) INTO @mitcost FROM mitigationstep WHERE riskID = NEW.riskID AND response = 0;   UPDATE risk SET mitigationCost = @mitcost WHERE riskID = NEW.riskID; END$$

CREATE
DEFINER=`qrm`@`%`
TRIGGER `qrm0`.`deleteSessionIDTriggerBefore`
AFTER DELETE ON `qrm0`.`reportsession`
FOR EACH ROW
BEGIN    DELETE FROM reportsessiondata WHERE  reportsessiondata.sessionID = OLD.sessionID; END$$


DELIMITER ;
