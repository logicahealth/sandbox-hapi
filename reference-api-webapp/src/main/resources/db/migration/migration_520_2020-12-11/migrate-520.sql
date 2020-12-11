-- Default charset for table is now latin1
alter table  HFJ_FORCED_ID charset=latin1;

-- Dropped charset latin 1 for column
alter table  HFJ_FORCED_ID modify FORCED_ID varchar(100) not null;

-- Dropped charset latin 1 for column
alter table  HFJ_FORCED_ID modify FORCED_ID varchar(100) not null;

-- Dropped charset latin 1  for column
alter table  HFJ_FORCED_ID modify RESOURCE_TYPE varchar(100) default '' null;

-- Added two new columns
alter table  HFJ_FORCED_ID
    add PARTITION_DATE date null;

alter table  HFJ_FORCED_ID
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_HISTORY_TAG charset=latin1;

-- Dropped charset latin 1 for column
alter table  HFJ_HISTORY_TAG modify RES_TYPE varchar(40) not null;

-- Moved column order
alter table  HFJ_HISTORY_TAG modify RES_VER_PID bigint not null after TAG_ID;


-- Added two new columns
alter table  HFJ_HISTORY_TAG
    add PARTITION_DATE date null;

alter table  HFJ_HISTORY_TAG
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_IDX_CMP_STRING_UNIQ charset=latin1;


-- Dropped charset latin 1 for column
alter table  HFJ_IDX_CMP_STRING_UNIQ modify IDX_STRING varchar(200) not null;


-- Added two new columns
alter table  HFJ_IDX_CMP_STRING_UNIQ
    add PARTITION_DATE date null;

alter table  HFJ_IDX_CMP_STRING_UNIQ
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------

-- New table
create table  HFJ_PARTITION
(
    PART_ID int not null
        primary key,
    PART_DESC varchar(200) null,
    PART_NAME varchar(200) not null,
    constraint IDX_PART_NAME
        unique (PART_NAME)
);

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_RESOURCE charset=latin1;

-- Dropped charset latin 1 for column
alter table  HFJ_RESOURCE modify RES_VERSION varchar(7) null;

-- Dropped charset latin 1 for column
alter table  HFJ_RESOURCE modify HASH_SHA256 varchar(64) null;

-- Dropped charset latin 1 for column
alter table  HFJ_RESOURCE modify RES_LANGUAGE varchar(20) null;

-- Dropped charset latin 1 for column
alter table  HFJ_RESOURCE modify RES_PROFILE varchar(200) null;

-- Dropped charset latin 1 for column
alter table  HFJ_RESOURCE modify RES_TYPE varchar(40) not null;

alter table  HFJ_RESOURCE
    add PARTITION_DATE date null;

alter table  HFJ_RESOURCE
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_RES_LINK charset=latin1;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_LINK modify SRC_PATH varchar(200) not null;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_LINK modify SOURCE_RESOURCE_TYPE varchar(40) not null;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_LINK modify TARGET_RESOURCE_TYPE varchar(40) not null;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_LINK modify TARGET_RESOURCE_URL varchar(200) null;

-- Added two new columns
alter table  HFJ_RES_LINK
    add PARTITION_DATE date null;

alter table  HFJ_RES_LINK
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_RES_PARAM_PRESENT charset=latin1;

-- Moved column
alter table  HFJ_RES_PARAM_PRESENT modify HASH_PRESENCE bigint null after PID;

-- Added two new columns
alter table  HFJ_RES_PARAM_PRESENT
    add PARTITION_DATE date null;

alter table  HFJ_RES_PARAM_PRESENT
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_RES_REINDEX_JOB charset=latin1;

alter table HFJ_RES_REINDEX_JOB modify RES_TYPE varchar(100) null;

-- Moved column
alter table  HFJ_RES_REINDEX_JOB modify REINDEX_COUNT int null after JOB_DELETED;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_RES_TAG charset=latin1;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_TAG modify RES_TYPE varchar(40) not null;

-- Added two new columns
alter table  HFJ_RES_TAG
    add PARTITION_DATE date null;

alter table  HFJ_RES_TAG
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
-- Default charset for table is now latin1
alter table  HFJ_RES_VER charset=latin1;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_VER modify RES_ENCODING varchar(5) not null after RES_UPDATED;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_VER modify RES_VERSION varchar(7) null;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_VER modify RES_TEXT longblob null after RES_ENCODING;

-- Dropped charset latin 1 for column
alter table  HFJ_RES_VER modify RES_TYPE varchar(40) not null;

-- Added two new columns
alter table  HFJ_RES_VER
    add PARTITION_DATE date null;

alter table  HFJ_RES_VER
    add PARTITION_ID int null;

-- New foreign key
alter table  HFJ_RES_VER
    add constraint FK_RESOURCE_HISTORY_RESOURCE
        foreign key (RES_ID) references  HFJ_RESOURCE (RES_ID);

-- ------------------------------------------------------------------------------------------

-- Default charset for table is now latin1
alter table  HFJ_RES_VER_PROV charset=latin1;

alter table HFJ_RES_VER_PROV modify REQUEST_ID varchar(16) null;

alter table HFJ_RES_VER_PROV modify SOURCE_URI varchar(100) null;

-- Added two new columns
alter table  HFJ_RES_VER_PROV
    add PARTITION_DATE date null;

alter table  HFJ_RES_VER_PROV
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------

# Check if order of columns matches after migration

alter table  HFJ_SEARCH charset=latin1;

alter table  HFJ_SEARCH modify FAILURE_MESSAGE varchar(500) null;

alter table  HFJ_SEARCH modify RESOURCE_TYPE varchar(200) null;

alter table  HFJ_SEARCH modify SEARCH_STATUS varchar(10) not null;

alter table  HFJ_SEARCH modify SEARCH_UUID varchar(36) not null;

alter table  HFJ_SEARCH modify SEARCH_DELETED bit null after CREATED;

alter table  HFJ_SEARCH modify SEARCH_PARAM_MAP longblob null after RESOURCE_TYPE;

alter table  HFJ_SEARCH modify SEARCH_QUERY_STRING longtext null;

alter table  HFJ_SEARCH modify EXPIRY_OR_NULL datetime null after SEARCH_DELETED;

alter table  HFJ_SEARCH modify NUM_BLOCKED int null after LAST_UPDATED_LOW;

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SEARCH_INCLUDE charset=latin1;

alter table  HFJ_SEARCH_INCLUDE modify SEARCH_INCLUDE varchar(200) not null;

-- ------------------------------------------------------------------------------------------

alter table  HFJ_SEARCH_RESULT charset=latin1;

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SPIDX_COORDS charset=latin1;

alter table  HFJ_SPIDX_COORDS modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_COORDS modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_COORDS modify HASH_IDENTITY bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_COORDS
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_COORDS
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SPIDX_DATE charset=latin1;

alter table  HFJ_SPIDX_DATE modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_DATE modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_DATE modify HASH_IDENTITY bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_DATE
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_DATE
    add PARTITION_ID int null;

alter table  HFJ_SPIDX_DATE
    add SP_VALUE_HIGH_DATE_ORDINAL int null;

alter table  HFJ_SPIDX_DATE
    add SP_VALUE_LOW_DATE_ORDINAL int null;

create index IDX_SP_DATE_HASH_LOW
    on  HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW);

create index IDX_SP_DATE_ORD_HASH
    on  HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL);

create index IDX_SP_DATE_ORD_HASH_LOW
    on  HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL);

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SPIDX_NUMBER charset=latin1;

alter table  HFJ_SPIDX_NUMBER modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_NUMBER modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_NUMBER modify HASH_IDENTITY bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_NUMBER
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_NUMBER
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SPIDX_QUANTITY charset=latin1;

alter table  HFJ_SPIDX_QUANTITY modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_QUANTITY modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_QUANTITY modify HASH_IDENTITY bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_QUANTITY modify HASH_IDENTITY_AND_UNITS bigint null after HASH_IDENTITY;

alter table  HFJ_SPIDX_QUANTITY modify HASH_IDENTITY_SYS_UNITS bigint null after HASH_IDENTITY_AND_UNITS;

alter table  HFJ_SPIDX_QUANTITY modify SP_SYSTEM varchar(200) null;

alter table  HFJ_SPIDX_QUANTITY modify SP_UNITS varchar(200) null;

alter table  HFJ_SPIDX_QUANTITY
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_QUANTITY
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SPIDX_STRING charset=latin1;

alter table  HFJ_SPIDX_STRING modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_STRING modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_STRING modify HASH_EXACT bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_STRING modify HASH_IDENTITY bigint null after HASH_EXACT;

alter table  HFJ_SPIDX_STRING modify HASH_NORM_PREFIX bigint null after HASH_IDENTITY;

alter table  HFJ_SPIDX_STRING modify SP_VALUE_EXACT varchar(200) null;

alter table  HFJ_SPIDX_STRING modify SP_VALUE_NORMALIZED varchar(200) null;

alter table  HFJ_SPIDX_STRING
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_STRING
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
# Check order of columns after migration

alter table  HFJ_SPIDX_TOKEN charset=latin1;

alter table  HFJ_SPIDX_TOKEN modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_TOKEN modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_TOKEN modify SP_SYSTEM varchar(200) null after HASH_VALUE;

alter table  HFJ_SPIDX_TOKEN modify SP_VALUE varchar(200) null after SP_SYSTEM;

alter table  HFJ_SPIDX_TOKEN modify HASH_IDENTITY bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_TOKEN
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_TOKEN
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------
alter table  HFJ_SPIDX_URI charset=latin1;

alter table  HFJ_SPIDX_URI modify SP_NAME varchar(100) not null;

alter table  HFJ_SPIDX_URI modify RES_TYPE varchar(100) not null;

alter table  HFJ_SPIDX_URI modify HASH_IDENTITY bigint null after SP_UPDATED;

alter table  HFJ_SPIDX_URI modify HASH_URI bigint null after HASH_IDENTITY;

alter table  HFJ_SPIDX_URI modify SP_URI varchar(254) null;

alter table  HFJ_SPIDX_URI
    add PARTITION_DATE date null;

alter table  HFJ_SPIDX_URI
    add PARTITION_ID int null;

-- ------------------------------------------------------------------------------------------

alter table  HFJ_SUBSCRIPTION_STATS charset=latin1;

-- ------------------------------------------------------------------------------------------

CREATE TABLE  MPI_LINK (
                           `PID` bigint(20) NOT NULL,
                           `CREATED` datetime(6) NOT NULL,
                           `EID_MATCH` bit(1) DEFAULT NULL,
                           `TARGET_TYPE` varchar(40) DEFAULT NULL,
                           `LINK_SOURCE` int(11) NOT NULL,
                           `MATCH_RESULT` int(11) NOT NULL,
                           `NEW_PERSON` bit(1) DEFAULT NULL,
                           `PERSON_PID` bigint(20) NOT NULL,
                           `SCORE` double DEFAULT NULL,
                           `TARGET_PID` bigint(20) NOT NULL,
                           `UPDATED` datetime(6) NOT NULL,
                           `VECTOR` bigint(20) DEFAULT NULL,
                           `VERSION` varchar(16) NOT NULL,
                           PRIMARY KEY (`PID`),
                           UNIQUE KEY `IDX_EMPI_PERSON_TGT` (`PERSON_PID`,`TARGET_PID`),
                           KEY `FK_EMPI_LINK_TARGET` (`TARGET_PID`),
                           CONSTRAINT `FK_EMPI_LINK_PERSON` FOREIGN KEY (`PERSON_PID`) REFERENCES `HFJ_RESOURCE` (`RES_ID`),
                           CONSTRAINT `FK_EMPI_LINK_TARGET` FOREIGN KEY (`TARGET_PID`) REFERENCES `HFJ_RESOURCE` (`RES_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- ------------------------------------------------------------------------------------------

CREATE TABLE  NPM_PACKAGE (
                              `PID` bigint(20) NOT NULL,
                              `CUR_VERSION_ID` varchar(200) DEFAULT NULL,
                              `PACKAGE_DESC` varchar(200) DEFAULT NULL,
                              `PACKAGE_ID` varchar(200) NOT NULL,
                              `UPDATED_TIME` datetime(6) NOT NULL,
                              PRIMARY KEY (`PID`),
                              UNIQUE KEY `IDX_PACK_ID` (`PACKAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------------------------------------------------------

CREATE TABLE  NPM_PACKAGE_VER (
                                  `PID` bigint(20) NOT NULL,
                                  `CURRENT_VERSION` bit(1) NOT NULL,
                                  `PKG_DESC` varchar(200) DEFAULT NULL,
                                  `DESC_UPPER` varchar(200) DEFAULT NULL,
                                  `FHIR_VERSION` varchar(10) NOT NULL,
                                  `FHIR_VERSION_ID` varchar(10) NOT NULL,
                                  `PACKAGE_ID` varchar(200) NOT NULL,
                                  `PACKAGE_SIZE_BYTES` bigint(20) NOT NULL,
                                  `SAVED_TIME` datetime(6) NOT NULL,
                                  `UPDATED_TIME` datetime(6) NOT NULL,
                                  `VERSION_ID` varchar(200) NOT NULL,
                                  `PACKAGE_PID` bigint(20) NOT NULL,
                                  `BINARY_RES_ID` bigint(20) NOT NULL,
                                  PRIMARY KEY (`PID`),
                                  UNIQUE KEY `IDX_PACKVER` (`PACKAGE_ID`,`VERSION_ID`),
                                  KEY `FK_NPM_PKV_PKG` (`PACKAGE_PID`),
                                  KEY `FK_NPM_PKV_RESID` (`BINARY_RES_ID`),
                                  CONSTRAINT `FK_NPM_PKV_PKG` FOREIGN KEY (`PACKAGE_PID`) REFERENCES `NPM_PACKAGE` (`PID`),
                                  CONSTRAINT `FK_NPM_PKV_RESID` FOREIGN KEY (`BINARY_RES_ID`) REFERENCES `HFJ_RESOURCE` (`RES_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------------------------------------------------------

CREATE TABLE  NPM_PACKAGE_VER_RES (
                                      `PID` bigint(20) NOT NULL,
                                      `CANONICAL_URL` varchar(200) DEFAULT NULL,
                                      `CANONICAL_VERSION` varchar(200) DEFAULT NULL,
                                      `FILE_DIR` varchar(200) DEFAULT NULL,
                                      `FHIR_VERSION` varchar(10) NOT NULL,
                                      `FHIR_VERSION_ID` varchar(10) NOT NULL,
                                      `FILE_NAME` varchar(200) DEFAULT NULL,
                                      `RES_SIZE_BYTES` bigint(20) NOT NULL,
                                      `RES_TYPE` varchar(40) NOT NULL,
                                      `UPDATED_TIME` datetime(6) NOT NULL,
                                      `PACKVER_PID` bigint(20) NOT NULL,
                                      `BINARY_RES_ID` bigint(20) NOT NULL,
                                      PRIMARY KEY (`PID`),
                                      KEY `IDX_PACKVERRES_URL` (`CANONICAL_URL`),
                                      KEY `FK_NPM_PACKVERRES_PACKVER` (`PACKVER_PID`),
                                      KEY `FK_NPM_PKVR_RESID` (`BINARY_RES_ID`),
                                      CONSTRAINT `FK_NPM_PACKVERRES_PACKVER` FOREIGN KEY (`PACKVER_PID`) REFERENCES `NPM_PACKAGE_VER` (`PID`),
                                      CONSTRAINT `FK_NPM_PKVR_RESID` FOREIGN KEY (`BINARY_RES_ID`) REFERENCES `HFJ_RESOURCE` (`RES_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------------------------------------------------------
alter table  SEQ_CNCPT_MAP_GRP_ELM_TGT_PID charset=latin1;

alter table  SEQ_CNCPT_MAP_GRP_ELM_TGT_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CODESYSTEMVER_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CODESYSTEM_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------
alter table  SEQ_CONCEPT_DESIG_PID charset=latin1;

alter table  SEQ_CONCEPT_DESIG_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------
alter table  SEQ_CONCEPT_MAP_GROUP_PID charset=latin1;

alter table  SEQ_CONCEPT_MAP_GROUP_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CONCEPT_MAP_GRP_ELM_PID engine=InnoDB;

alter table  SEQ_CONCEPT_MAP_GRP_ELM_PID charset=latin1;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CONCEPT_MAP_PID engine=InnoDB;

alter table  SEQ_CONCEPT_MAP_PID charset=latin1;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CONCEPT_PC_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CONCEPT_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_CONCEPT_PROP_PID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

CREATE TABLE  SEQ_EMPI_LINK_ID (
    `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_FORCEDID_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_HISTORYTAG_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_IDXCMPSTRUNIQ_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

CREATE TABLE  SEQ_NPM_PACK (
    `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------------------------------------------------------

CREATE TABLE  SEQ_NPM_PACKVER (
    `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- ------------------------------------------------------------------------------------------

CREATE TABLE  SEQ_NPM_PACKVERRES (
    `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- ------------------------------------------------------------------------------------------

alter table  SEQ_RESLINK_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_RESOURCE_HISTORY_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_RESOURCE_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_RESPARMPRESENT_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_RESTAG_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_RES_REINDEX_JOB charset=latin1;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SEARCH engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SEARCH_INC engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SEARCH_RES engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_COORDS engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_DATE engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_NUMBER engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_QUANTITY engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_STRING engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_TOKEN engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SPIDX_URI engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_SUBSCRIPTION_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------

alter table  SEQ_TAGDEF_ID engine=InnoDB;

-- ------------------------------------------------------------------------------------------
alter table  TRM_CODESYSTEM charset=latin1;

alter table  TRM_CODESYSTEM modify CODE_SYSTEM_URI varchar(200) not null;

alter table  TRM_CODESYSTEM modify CS_NAME varchar(200) null;

alter table  TRM_CODESYSTEM modify RES_ID bigint null after CS_NAME;

-- ------------------------------------------------------------------------------------------

# Check to see if tables match for FK_CODESYSVER_CS_ID

alter table  TRM_CODESYSTEM_VER charset=latin1;

alter table  TRM_CODESYSTEM_VER modify CS_DISPLAY varchar(200) null;

alter table  TRM_CODESYSTEM_VER modify CODESYSTEM_PID bigint null after CS_DISPLAY;

alter table  TRM_CODESYSTEM_VER modify CS_VERSION_ID varchar(200) null after CODESYSTEM_PID;

-- drop index FK_CODESYSVER_CS_ID on  TRM_CODESYSTEM_VER;

alter table  TRM_CODESYSTEM_VER
    add constraint IDX_CODESYSTEM_AND_VER
        unique (CODESYSTEM_PID, CS_VERSION_ID);

-- ------------------------------------------------------------------------------------------
alter table  TRM_CONCEPT charset=latin1;

alter table  TRM_CONCEPT modify CODEVAL varchar(500) not null;

alter table  TRM_CONCEPT modify DISPLAY varchar(400) null;

alter table  TRM_CONCEPT modify PARENT_PIDS longtext null;

alter table  TRM_CONCEPT modify CODE_SEQUENCE int null after PARENT_PIDS;

-- ------------------------------------------------------------------------------------------
alter table  TRM_CONCEPT_DESIG charset=latin1;

alter table TRM_CONCEPT_DESIG modify LANG varchar(500) null;

alter table TRM_CONCEPT_DESIG modify USE_CODE varchar(500) null;

alter table TRM_CONCEPT_DESIG modify USE_DISPLAY varchar(500) null;

alter table TRM_CONCEPT_DESIG modify USE_SYSTEM varchar(500) null;

alter table TRM_CONCEPT_DESIG modify VAL varchar(2000) not null;

alter table  TRM_CONCEPT_DESIG modify CS_VER_PID bigint null after VAL;

-- ------------------------------------------------------------------------------------------

alter table  TRM_CONCEPT_MAP charset=latin1;

alter table TRM_CONCEPT_MAP modify SOURCE_URL varchar(200) null;

alter table TRM_CONCEPT_MAP modify TARGET_URL varchar(200) null;

alter table TRM_CONCEPT_MAP modify URL varchar(200) not null;

alter table  TRM_CONCEPT_MAP
    add VER varchar(200) null;

-- ------------------------------------------------------------------------------------------
alter table  TRM_CONCEPT_MAP_GROUP charset=latin1;

alter table TRM_CONCEPT_MAP_GROUP modify CONCEPT_MAP_URL varchar(200) null;

alter table TRM_CONCEPT_MAP_GROUP modify SOURCE_URL varchar(200) not null;

alter table TRM_CONCEPT_MAP_GROUP modify SOURCE_VS varchar(200) null;

alter table TRM_CONCEPT_MAP_GROUP modify TARGET_URL varchar(200) not null;

alter table TRM_CONCEPT_MAP_GROUP modify TARGET_VS varchar(200) null;

alter table  TRM_CONCEPT_MAP_GROUP modify SOURCE_VERSION varchar(200) null;

alter table  TRM_CONCEPT_MAP_GROUP modify TARGET_VERSION varchar(200) null;

-- ------------------------------------------------------------------------------------------

alter table  TRM_CONCEPT_MAP_GRP_ELEMENT charset=latin1;

alter table TRM_CONCEPT_MAP_GRP_ELEMENT modify SOURCE_CODE varchar(500) not null;

alter table TRM_CONCEPT_MAP_GRP_ELEMENT modify CONCEPT_MAP_URL varchar(200) null;

alter table TRM_CONCEPT_MAP_GRP_ELEMENT modify SOURCE_DISPLAY varchar(400) null;

alter table TRM_CONCEPT_MAP_GRP_ELEMENT modify SYSTEM_URL varchar(200) null;

alter table TRM_CONCEPT_MAP_GRP_ELEMENT modify SYSTEM_VERSION varchar(200) null;

alter table TRM_CONCEPT_MAP_GRP_ELEMENT modify VALUESET_URL varchar(200) null;
-- ------------------------------------------------------------------------------------------

alter table  TRM_CONCEPT_MAP_GRP_ELM_TGT charset=latin1;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify TARGET_CODE varchar(500) not null;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify CONCEPT_MAP_URL varchar(200) null;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify TARGET_DISPLAY varchar(400) null;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify TARGET_EQUIVALENCE varchar(50) null;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify SYSTEM_URL varchar(200) null;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify SYSTEM_VERSION varchar(200) null;

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT modify VALUESET_URL varchar(200) null;

-- ------------------------------------------------------------------------------------------
alter table  TRM_CONCEPT_PC_LINK charset=latin1;


alter table  TRM_CONCEPT_PC_LINK modify CODESYSTEM_PID bigint not null after CHILD_PID;

-- ------------------------------------------------------------------------------------------
# Check order of columns after migration

alter table  TRM_CONCEPT_PROPERTY charset=latin1;

alter table  TRM_CONCEPT_PROPERTY modify PROP_VAL varchar(500) null after PROP_TYPE;

alter table  TRM_CONCEPT_PROPERTY modify CONCEPT_PID bigint null after CS_VER_PID;

alter table  TRM_CONCEPT_PROPERTY modify PROP_CODESYSTEM varchar(500) null;

alter table  TRM_CONCEPT_PROPERTY modify PROP_DISPLAY varchar(500) null;

alter table  TRM_CONCEPT_PROPERTY modify PROP_KEY varchar(500) not null after PROP_DISPLAY;

alter table  TRM_CONCEPT_PROPERTY modify PROP_VAL_LOB longblob null after PROP_VAL;

-- ------------------------------------------------------------------------------------------

alter table  TRM_VALUESET
    add VER varchar(200) null;

-- ------------------------------------------------------------------------------------------

alter table  TRM_VALUESET_CONCEPT
    add SYSTEM_VER varchar(200) null;

alter table  TRM_VALUESET_CONCEPT
    add constraint IDX_VS_CONCEPT_CS_CODE
        unique (VALUESET_PID, SYSTEM_URL, SYSTEM_VER, CODEVAL);

