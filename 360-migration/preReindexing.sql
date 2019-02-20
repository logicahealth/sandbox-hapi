--
--  * #%L
--  *
--  * %%
--  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
--  * %%
--  * Licensed under the Apache License, Version 2.0 (the "License");
--  * you may not use this file except in compliance with the License.
--  * You may obtain a copy of the License at
--  *
--  *      http://www.apache.org/licenses/LICENSE-2.0
--  *
--  * Unless required by applicable law or agreed to in writing, software
--  * distributed under the License is distributed on an "AS IS" BASIS,
--  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  * See the License for the specific language governing permissions and
--  * limitations under the License.
--  * #L%
--
DELIMITER $$

DROP PROCEDURE IF EXISTS drop_index_if_exists $$
CREATE PROCEDURE drop_index_if_exists(in theTable varchar(128), in theIndexName varchar(128) )
BEGIN
 IF((SELECT COUNT(*) AS index_exists FROM information_schema.statistics WHERE TABLE_SCHEMA = DATABASE() and table_name =
theTable AND index_name = theIndexName) > 0) THEN
   SET @s = CONCAT('DROP INDEX ' , theIndexName , ' ON ' , theTable);
   PREPARE stmt FROM @s;
   EXECUTE stmt;
 END IF;
END $$

DELIMITER ^^

DROP PROCEDURE IF EXISTS create_index_if_not_exists ^^
CREATE PROCEDURE create_index_if_not_exists(in theTable varchar(128), in theIndexName varchar(128), in callStr varchar(256) )
BEGIN
 IF((SELECT COUNT(*) AS index_exists FROM information_schema.statistics WHERE TABLE_SCHEMA = DATABASE() and table_name =
theTable AND index_name = theIndexName) = 0) THEN
   SET @s = callStr;
   PREPARE stmt FROM @s;
   EXECUTE stmt;
 END IF;
END ^^

DELIMITER !!

DROP PROCEDURE IF EXISTS add_column_if_not_exists !!
CREATE PROCEDURE add_column_if_not_exists(in theTable varchar(128), in theColumnName varchar(128), in typeStr varchar(128) )
BEGIN
 IF((SELECT COUNT(*) AS index_exists FROM information_schema.columns WHERE TABLE_SCHEMA = DATABASE() and table_name =
theTable AND column_name = theColumnName) = 0) THEN
   SET @s = CONCAT('ALTER TABLE ' , theTable , ' ADD ' , theColumnName, ' ', typeStr);
   PREPARE stmt FROM @s;
   EXECUTE stmt;
 END IF;
END !!

DELIMITER **

DROP PROCEDURE IF EXISTS add_reindex_value **
CREATE PROCEDURE add_reindex_value()
BEGIN
 IF((SELECT COUNT(*) FROM SEQ_RES_REINDEX_JOB) = 0 ) THEN
   INSERT INTO SEQ_RES_REINDEX_JOB(next_val) VALUES (1);
 END IF;
END **

DELIMITER ;

CALL drop_index_if_exists('HFJ_FORCED_ID', 'IDX_FORCEDID_TYPE_FORCEDID');
CALL drop_index_if_exists('HFJ_FORCED_ID', 'IDX_FORCEDID_TYPE_RESID');
CALL create_index_if_not_exists('HFJ_FORCED_ID', 'IDX_FORCEDID_TYPE_FID', 'CREATE INDEX IDX_FORCEDID_TYPE_FID ON HFJ_FORCED_ID (RESOURCE_TYPE, FORCED_ID)');

CALL add_column_if_not_exists('HFJ_SPIDX_COORDS', 'HASH_IDENTITY', 'BIGINT NULL');

CALL add_column_if_not_exists('HFJ_SPIDX_DATE', 'HASH_IDENTITY', 'BIGINT NULL');

CALL add_column_if_not_exists('HFJ_SPIDX_NUMBER', 'HASH_IDENTITY', 'BIGINT NULL');

CALL add_column_if_not_exists('HFJ_SPIDX_QUANTITY', 'HASH_IDENTITY', 'BIGINT NULL');
CALL add_column_if_not_exists('HFJ_SPIDX_QUANTITY', 'HASH_IDENTITY_AND_UNITS', 'BIGINT NULL');
CALL add_column_if_not_exists('HFJ_SPIDX_QUANTITY', 'HASH_IDENTITY_SYS_UNITS', 'BIGINT NULL');

CALL add_column_if_not_exists('HFJ_SPIDX_STRING', 'HASH_IDENTITY', 'BIGINT NULL');

CALL add_column_if_not_exists('HFJ_SPIDX_TOKEN', 'HASH_IDENTITY', 'BIGINT NULL');

CALL add_column_if_not_exists('HFJ_SPIDX_URI', 'HASH_IDENTITY', 'BIGINT NULL');

CALL drop_index_if_exists('HFJ_RES_PARAM_PRESENT', 'IDX_RESPARMPRESENT_SPID_RESID');
CALL add_column_if_not_exists('HFJ_RES_PARAM_PRESENT', 'HASH_PRESENCE', 'BIGINT NULL');
CALL create_index_if_not_exists('HFJ_RES_PARAM_PRESENT', 'IDX_RESPARMPRESENT_HASHPRES', 'CREATE INDEX IDX_RESPARMPRESENT_HASHPRES ON HFJ_RES_PARAM_PRESENT (HASH_PRESENCE)');

CALL add_column_if_not_exists('TRM_CONCEPT', 'PARENT_PIDS', 'LONGTEXT NULL');
CALL add_column_if_not_exists('TRM_CONCEPT', 'CONCEPT_UPDATED', 'DATETIME NULL');
ALTER TABLE TRM_CONCEPT MODIFY COLUMN `CODE` VARCHAR(500);

CALL add_column_if_not_exists('TRM_CONCEPT_DESIG', 'CS_VER_PID', 'BIGINT');
ALTER TABLE TRM_CONCEPT_DESIG ENGINE = MyISAM;
ALTER TABLE TRM_CONCEPT_DESIG ADD CONSTRAINT FK_CONCEPTDESIG_CSV FOREIGN KEY (CS_VER_PID) REFERENCES TRM_CODESYSTEM_VER(PID);

CALL add_column_if_not_exists('TRM_CONCEPT_PROPERTY', 'CS_VER_PID', 'BIGINT');
ALTER TABLE TRM_CONCEPT_PROPERTY ADD CONSTRAINT FK_CONCEPTPROP_CSV FOREIGN KEY (CS_VER_PID) REFERENCES TRM_CODESYSTEM_VER(PID);

CALL drop_index_if_exists('TRM_CONCEPT_MAP_GRP_ELEMENT', 'IDX_CNCPT_MAP_GRP_CD');

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT MODIFY COLUMN SOURCE_CODE VARCHAR(500) NOT NULL;

CALL drop_index_if_exists('TRM_CONCEPT_MAP_GRP_ELM_TGT', 'IDX_CNCPT_MP_GRP_ELM_TGT_CD');
ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT MODIFY COLUMN TARGET_CODE VARCHAR(500) NOT NULL;

ALTER TABLE HFJ_RES_LINK MODIFY COLUMN SRC_PATH VARCHAR(200) NOT NULL;

CALL add_column_if_not_exists('HFJ_SEARCH', 'SEARCH_DELETED', 'BIT NULL');
CALL add_column_if_not_exists('HFJ_SEARCH', 'SEARCH_PARAM_MAP', 'LONGBLOB NULL');
CALL add_column_if_not_exists('HFJ_SEARCH', 'OPTLOCK_VERSION', 'INTEGER NULL');

CREATE TABLE IF NOT EXISTS HFJ_RES_REINDEX_JOB (PID bigint not null, JOB_DELETED bit not null, RES_TYPE varchar(255), SUSPENDED_UNTIL datetime(6), UPDATE_THRESHOLD_HIGH datetime(6) not null, UPDATE_THRESHOLD_LOW datetime(6), REINDEX_COUNT int(11) NULL, primary key (PID));

CREATE TABLE IF NOT EXISTS SEQ_RES_REINDEX_JOB (`next_val` bigint(20) DEFAULT NULL);
CALL add_reindex_value();


