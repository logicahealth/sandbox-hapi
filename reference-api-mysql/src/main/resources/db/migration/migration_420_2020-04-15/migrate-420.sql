SET FOREIGN_KEY_CHECKS = 0;

create table HFJ_BINARY_STORAGE_BLOB
(
    BLOB_ID varchar(200) not null
        primary key,
    BLOB_DATA longblob not null,
    CONTENT_TYPE varchar(100) not null,
    BLOB_HASH varchar(128) null,
    PUBLISHED_DATE datetime not null,
    RESOURCE_ID varchar(100) not null,
    BLOB_SIZE int null
)
    engine=InnoDB;

create table HFJ_BLK_EXPORT_COLFILE
(
    PID bigint not null
        primary key,
    RES_ID varchar(100) not null,
    COLLECTION_PID bigint not null
)
    engine=InnoDB;

create index FK_BLKEXCOLFILE_COLLECT
    on HFJ_BLK_EXPORT_COLFILE (COLLECTION_PID);

create table HFJ_BLK_EXPORT_COLLECTION
(
    PID bigint not null
        primary key,
    TYPE_FILTER varchar(1000) null,
    RES_TYPE varchar(40) not null,
    OPTLOCK int not null,
    JOB_PID bigint not null
)
    engine=InnoDB;

create index FK_BLKEXCOL_JOB
    on HFJ_BLK_EXPORT_COLLECTION (JOB_PID);

create table HFJ_BLK_EXPORT_JOB
(
    PID bigint not null
        primary key,
    CREATED_TIME datetime not null,
    EXP_TIME datetime not null,
    JOB_ID varchar(36) not null,
    REQUEST varchar(500) not null,
    EXP_SINCE datetime null,
    JOB_STATUS varchar(10) not null,
    STATUS_MESSAGE varchar(500) null,
    STATUS_TIME datetime not null,
    OPTLOCK int not null,
    constraint IDX_BLKEX_JOB_ID
        unique (JOB_ID)
)
    engine=InnoDB;

create index IDX_BLKEX_EXPTIME
    on HFJ_BLK_EXPORT_JOB (EXP_TIME);

alter table HFJ_HISTORY_TAG modify RES_TYPE varchar(40) not null;

alter table HFJ_RESOURCE modify RES_TYPE varchar(40) not null;

alter table HFJ_RES_LINK modify SOURCE_RESOURCE_TYPE varchar(40) not null;

alter table HFJ_RES_LINK modify TARGET_RESOURCE_TYPE varchar(40) not null;

alter table HFJ_RES_REINDEX_JOB modify RES_TYPE varchar(100) null;

alter table HFJ_RES_TAG modify RES_TYPE varchar(40) not null;

alter table HFJ_RES_VER modify RES_TYPE varchar(40) not null;

create table HFJ_RES_VER_PROV
(
    RES_VER_PID bigint not null
        primary key,
    REQUEST_ID varchar(16) null,
    SOURCE_URI varchar(100) null,
    RES_PID bigint not null
)
    engine=InnoDB;

create index FK_RESVERPROV_RES_PID
    on HFJ_RES_VER_PROV (RES_PID);

create index IDX_RESVERPROV_REQUESTID
    on HFJ_RES_VER_PROV (REQUEST_ID);

create index IDX_RESVERPROV_SOURCEURI
    on HFJ_RES_VER_PROV (SOURCE_URI);

drop index IDX_SEARCH_LASTRETURNED on HFJ_SEARCH;

alter table HFJ_SEARCH drop column SEARCH_LAST_RETURNED;

alter table HFJ_SEARCH
    add EXPIRY_OR_NULL datetime null;

alter table HFJ_SEARCH
    add NUM_BLOCKED int null;

create index IDX_SEARCH_CREATED
    on HFJ_SEARCH (CREATED);

alter table HFJ_SPIDX_COORDS modify RES_ID bigint not null;

alter table HFJ_SPIDX_COORDS modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_DATE modify RES_ID bigint not null;

alter table HFJ_SPIDX_DATE modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_NUMBER modify RES_ID bigint not null;

alter table HFJ_SPIDX_NUMBER modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_QUANTITY modify RES_ID bigint not null;

alter table HFJ_SPIDX_QUANTITY modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_STRING modify RES_ID bigint not null;

alter table HFJ_SPIDX_STRING modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_TOKEN modify RES_ID bigint not null;

alter table HFJ_SPIDX_TOKEN modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_URI modify RES_ID bigint not null;

alter table HFJ_SPIDX_URI modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_URI modify SP_URI varchar(254) null;

create table SEQ_BLKEXCOLFILE_PID
(
    next_val bigint null
)
    engine=InnoDB;

LOCK TABLES `SEQ_BLKEXCOLFILE_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_BLKEXCOLFILE_PID` DISABLE KEYS */;
INSERT INTO `SEQ_BLKEXCOLFILE_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_BLKEXCOLFILE_PID` ENABLE KEYS */;
UNLOCK TABLES;

create table SEQ_BLKEXCOL_PID
(
    next_val bigint null
)
    engine=InnoDB;

LOCK TABLES `SEQ_BLKEXCOL_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_BLKEXCOL_PID` DISABLE KEYS */;
INSERT INTO `SEQ_BLKEXCOL_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_BLKEXCOL_PID` ENABLE KEYS */;
UNLOCK TABLES;

create table SEQ_BLKEXJOB_PID
(
    next_val bigint null
)
    engine=InnoDB;

LOCK TABLES `SEQ_BLKEXJOB_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_BLKEXJOB_PID` DISABLE KEYS */;
INSERT INTO `SEQ_BLKEXJOB_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_BLKEXJOB_PID` ENABLE KEYS */;
UNLOCK TABLES;

create table SEQ_VALUESET_CONCEPT_PID
(
    next_val bigint null
)
    engine=InnoDB;

LOCK TABLES `SEQ_VALUESET_CONCEPT_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_VALUESET_CONCEPT_PID` DISABLE KEYS */;
INSERT INTO `SEQ_VALUESET_CONCEPT_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_VALUESET_CONCEPT_PID` ENABLE KEYS */;
UNLOCK TABLES;

create table SEQ_VALUESET_C_DSGNTN_PID
(
    next_val bigint null
)
    engine=InnoDB;

LOCK TABLES `SEQ_VALUESET_C_DSGNTN_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_VALUESET_C_DSGNTN_PID` DISABLE KEYS */;
INSERT INTO `SEQ_VALUESET_C_DSGNTN_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_VALUESET_C_DSGNTN_PID` ENABLE KEYS */;
UNLOCK TABLES;

create table SEQ_VALUESET_PID
(
    next_val bigint null
)
    engine=InnoDB;

LOCK TABLES `SEQ_VALUESET_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_VALUESET_PID` DISABLE KEYS */;
INSERT INTO `SEQ_VALUESET_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_VALUESET_PID` ENABLE KEYS */;
UNLOCK TABLES;

alter table TRM_CODESYSTEM modify CODE_SYSTEM_URI varchar(200) not null;

alter table TRM_CODESYSTEM modify CS_NAME varchar(200) null;

alter table TRM_CODESYSTEM_VER modify CS_VERSION_ID varchar(200) null;

alter table TRM_CODESYSTEM_VER
    add CS_DISPLAY varchar(200) null after PID;

ALTER TABLE TRM_CONCEPT CHANGE `CODE` `CODEVAL` varchar(500) not null;

create index FK_CONCEPT_PID_CS_PID
    on TRM_CONCEPT (CODESYSTEM_PID);

alter table TRM_CONCEPT_DESIG modify VAL varchar(2000) not null;

ALTER TABLE TRM_CONCEPT_MAP_GROUP CHANGE `myConceptMapUrl` `CONCEPT_MAP_URL` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GROUP CHANGE `mySourceValueSet` `SOURCE_VS` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GROUP CHANGE `myTargetValueSet` `TARGET_VS` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT CHANGE `myConceptMapUrl` `CONCEPT_MAP_URL` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT CHANGE `mySystem` `SYSTEM_URL` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT CHANGE `mySystemVersion` `SYSTEM_VERSION` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELEMENT CHANGE `myValueSet` `VALUESET_URL` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT CHANGE `myConceptMapUrl` `CONCEPT_MAP_URL` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT CHANGE `mySystem` `SYSTEM_URL` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT CHANGE `mySystemVersion` `SYSTEM_VERSION` varchar(200) null;

ALTER TABLE TRM_CONCEPT_MAP_GRP_ELM_TGT CHANGE `myValueSet` `VALUESET_URL` varchar(200) null;

alter table TRM_CONCEPT_PROPERTY
    add PROP_VAL_LOB longblob null;

create table TRM_VALUESET
(
    PID bigint not null
        primary key,
    EXPANSION_STATUS varchar(50) not null,
    VSNAME varchar(200) null,
    RES_ID bigint null,
    TOTAL_CONCEPT_DESIGNATIONS bigint default 0 not null,
    TOTAL_CONCEPTS bigint default 0 not null,
    URL varchar(200) not null,
    constraint IDX_VALUESET_URL
        unique (URL)
)
    engine=InnoDB;

create index FK_TRMVALUESET_RES
    on TRM_VALUESET (RES_ID);

create table TRM_VALUESET_CONCEPT
(
    PID bigint not null
        primary key,
    CODEVAL varchar(500) not null,
    DISPLAY varchar(400) null,
    VALUESET_ORDER int not null,
    SYSTEM_URL varchar(200) not null,
    VALUESET_PID bigint not null,
    constraint IDX_VS_CONCEPT_ORDER
        unique (VALUESET_PID, VALUESET_ORDER)
)
    engine=InnoDB;

create table TRM_VALUESET_C_DESIGNATION
(
    PID bigint not null
        primary key,
    VALUESET_CONCEPT_PID bigint not null,
    LANG varchar(500) null,
    USE_CODE varchar(500) null,
    USE_DISPLAY varchar(500) null,
    USE_SYSTEM varchar(500) null,
    VAL varchar(2000) not null,
    VALUESET_PID bigint not null
)
    engine=InnoDB;

create index FK_TRM_VALUESET_CONCEPT_PID
    on TRM_VALUESET_C_DESIGNATION (VALUESET_CONCEPT_PID);

create index FK_TRM_VSCD_VS_PID
    on TRM_VALUESET_C_DESIGNATION (VALUESET_PID);

alter table TRM_CONCEPT_MAP_GROUP modify SOURCE_VERSION varchar(200) null;

alter table TRM_CONCEPT_MAP_GROUP modify TARGET_VERSION varchar(200) null;

SET FOREIGN_KEY_CHECKS = 1;