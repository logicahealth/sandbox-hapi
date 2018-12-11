alter table HFJ_RES_PARAM_PRESENT add  SP_ID bigint(20) NOT NULL;
CREATE INDEX IDX_FORCEDID_TYPE_FORCEDID ON HFJ_FORCED_ID(RESOURCE_TYPE,FORCED_ID);
CREATE INDEX IDX_FORCEDID_TYPE_RESID ON HFJ_FORCED_ID(RESOURCE_TYPE,FORCED_ID);
CREATE INDEX IDX_RESPARMPRESENT_SPID_RESID ON HFJ_RES_PARAM_PRESENT(SP_ID, RES_ID);

-- DROP TABLE `trm_concept_map`;
-- DROP TABLE `trm_concept_map_group`;
-- DROP TABLE `trm_concept_map_grp_element`;
-- DROP TABLE `trm_concept_map_grp_elm_tgt`;
-- 
-- CREATE TABLE `TRM_CONCEPT_MAP` (
--   `PID` bigint(20) NOT NULL,
--   `RES_ID` bigint(20) DEFAULT NULL,
--   `SOURCE_URL` varchar(200) DEFAULT NULL,
--   `TARGET_URL` varchar(200) DEFAULT NULL,
--   `URL` varchar(200) NOT NULL,
--   PRIMARY KEY (`PID`),
--   UNIQUE KEY `IDX_CONCEPT_MAP_URL` (`URL`),
--   KEY `FK_TRMCONCEPTMAP_RES` (`RES_ID`)
-- );
-- 
-- CREATE TABLE `TRM_CONCEPT_MAP_GROUP` (
--   `PID` bigint(20) NOT NULL,
--   `myConceptMapUrl` varchar(255) DEFAULT NULL,
--   `SOURCE_URL` varchar(200) NOT NULL,
--   `mySourceValueSet` varchar(255) DEFAULT NULL,
--   `SOURCE_VERSION` varchar(100) DEFAULT NULL,
--   `TARGET_URL` varchar(200) NOT NULL,
--   `myTargetValueSet` varchar(255) DEFAULT NULL,
--   `TARGET_VERSION` varchar(100) DEFAULT NULL,
--   `CONCEPT_MAP_PID` bigint(20) NOT NULL,
--   PRIMARY KEY (`PID`),
--   KEY `FK_TCMGROUP_CONCEPTMAP` (`CONCEPT_MAP_PID`)
-- );
-- 
-- CREATE TABLE `TRM_CONCEPT_MAP_GRP_ELEMENT` (
--   `PID` bigint(20) NOT NULL,
--   `SOURCE_CODE` varchar(100) NOT NULL,
--   `myConceptMapUrl` varchar(255) DEFAULT NULL,
--   `SOURCE_DISPLAY` varchar(400) DEFAULT NULL,
--   `mySystem` varchar(255) DEFAULT NULL,
--   `mySystemVersion` varchar(255) DEFAULT NULL,
--   `myValueSet` varchar(255) DEFAULT NULL,
--   `CONCEPT_MAP_GROUP_PID` bigint(20) NOT NULL,
--   PRIMARY KEY (`PID`),
--   KEY `FK_TCMGELEMENT_GROUP` (`CONCEPT_MAP_GROUP_PID`),
--   KEY `IDX_CNCPT_MAP_GRP_CD` (`SOURCE_CODE`)
-- );
-- 
-- CREATE TABLE `TRM_CONCEPT_MAP_GRP_ELM_TGT` (
--   `PID` bigint(20) NOT NULL,
--   `TARGET_CODE` varchar(50) NOT NULL,
--   `myConceptMapUrl` varchar(255) DEFAULT NULL,
--   `TARGET_DISPLAY` varchar(400) DEFAULT NULL,
--   `TARGET_EQUIVALENCE` varchar(50) DEFAULT NULL,
--   `mySystem` varchar(255) DEFAULT NULL,
--   `mySystemVersion` varchar(255) DEFAULT NULL,
--   `myValueSet` varchar(255) DEFAULT NULL,
--   `CONCEPT_MAP_GRP_ELM_PID` bigint(20) NOT NULL,
--   PRIMARY KEY (`PID`),
--   KEY `FK_TCMGETARGET_ELEMENT` (`CONCEPT_MAP_GRP_ELM_PID`),
--   KEY `IDX_CNCPT_MP_GRP_ELM_TGT_CD` (`TARGET_CODE`)
-- );