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


ALTER TABLE HFJ_FORCED_ID DROP INDEX IDX_FORCEDID_TYPE_FORCEDID;
ALTER TABLE HFJ_FORCED_ID DROP INDEX IDX_FORCEDID_TYPE_RESID;
CREATE INDEX IDX_FORCEDID_TYPE_FID ON HFJ_FORCED_ID (RESOURCE_TYPE, FORCED_ID);

ALTER TABLE HFJ_SPIDX_COORDS ADD HASH_IDENTITY BIGINT NULL;

ALTER TABLE HFJ_SPIDX_DATE ADD HASH_IDENTITY BIGINT NULL;

ALTER TABLE HFJ_SPIDX_NUMBER ADD HASH_IDENTITY BIGINT NULL;

ALTER TABLE HFJ_SPIDX_QUANTITY ADD HASH_IDENTITY BIGINT NULL;
ALTER TABLE HFJ_SPIDX_QUANTITY ADD HASH_IDENTITY_AND_UNITS BIGINT NULL;
ALTER TABLE HFJ_SPIDX_QUANTITY ADD HASH_IDENTITY_SYS_UNITS BIGINT NULL;

ALTER TABLE HFJ_SPIDX_STRING ADD HASH_IDENTITY BIGINT NULL;

ALTER TABLE HFJ_SPIDX_TOKEN ADD HASH_IDENTITY BIGINT NULL;

ALTER TABLE HFJ_SPIDX_URI ADD HASH_IDENTITY BIGINT NULL;

ALTER TABLE HFJ_RES_PARAM_PRESENT DROP INDEX IDX_RESPARMPRESENT_SPID_RESID;
ALTER TABLE HFJ_RES_PARAM_PRESENT ADD HASH_PRESENCE BIGINT NULL;
CREATE INDEX IDX_RESPARMPRESENT_HASHPRES ON HFJ_RES_PARAM_PRESENT (HASH_PRESENCE);

ALTER TABLE TRM_CONCEPT ADD PARENT_PIDS LONGTEXT NULL; 
ALTER TABLE TRM_CONCEPT ADD CONCEPT_UPDATED DATETIME NULL; 
ALTER TABLE TRM_CONCEPT MODIFY COLUMN `CODE` VARCHAR(500);

ALTER TABLE TRM_CONCEPT_DESIG ADD CS_VER_PID BIGINT;
ALTER TABLE TRM_CONCEPT_DESIG ENGINE = MyISAM;
ALTER TABLE TRM_CONCEPT_DESIG ADD CONSTRAINT FK_CONCEPTDESIG_CSV FOREIGN KEY (CS_VER_PID) REFERENCES TRM_CODESYSTEM_VER(PID);

ALTER TABLE TRM_CONCEPT_PROPERTY ADD CS_VER_PID BIGINT;
ALTER TABLE TRM_CONCEPT_PROPERTY ADD CONSTRAINT FK_CONCEPTPROP_CSV FOREIGN KEY (CS_VER_PID) REFERENCES TRM_CODESYSTEM_VER(PID);

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT DROP INDEX IDX_CNCPT_MAP_GRP_CD;
ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT MODIFY COLUMN SOURCE_CODE VARCHAR(500) NOT NULL;
-- CREATE INDEX IDX_CNCPT_MAP_GRP_CD ON TRM_CONCEPT_MAP_GRP_ELEMENT (SOURCE_CODE);

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT DROP INDEX IDX_CNCPT_MP_GRP_ELM_TGT_CD;
ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT MODIFY COLUMN TARGET_CODE VARCHAR(500) NOT NULL;
-- CREATE INDEX IDX_CNCPT_MP_GRP_ELM_TGT_CD ON TRM_CONCEPT_MAP_GRP_ELM_TGT (TARGET_CODE);

ALTER TABLE HFJ_RES_LINK MODIFY COLUMN SRC_PATH VARCHAR(200) NOT NULL;

ALTER TABLE HFJ_SEARCH ADD SEARCH_DELETED BIT NULL;
ALTER TABLE HFJ_SEARCH ADD SEARCH_PARAM_MAP LONGBLOB NULL;
ALTER TABLE HFJ_SEARCH ADD OPTLOCK_VERSION INTEGER NULL;

CREATE TABLE HFJ_RES_REINDEX_JOB (PID bigint not null, JOB_DELETED bit not null, RES_TYPE varchar(255), SUSPENDED_UNTIL datetime(6), UPDATE_THRESHOLD_HIGH datetime(6) not null, UPDATE_THRESHOLD_LOW datetime(6), primary key (PID));

CREATE TABLE SEQ_RES_REINDEX_JOB (`next_val` bigint(20) DEFAULT NULL);
INSERT INTO SEQ_RES_REINDEX_JOB(next_val) VALUES (1);

-- 3.7.0
ALTER TABLE HFJ_RES_REINDEX_JOB ADD REINDEX_COUNT int(11) NULL;