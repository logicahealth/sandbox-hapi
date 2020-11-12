alter table HFJ_HISTORY_TAG add PARTITION_DATE date null after PID;

alter table HFJ_HISTORY_TAG add PARTITION_ID int null after PARTITION_DATE;

create table HFJ_PARTITION
(
    PART_ID int not null primary key,
    PART_DESC varchar(200) null,
    PART_NAME varchar(200) not null,
    constraint IDX_PART_NAME unique (PART_NAME)
);

alter table HFJ_RESOURCE drop column RES_PROFILE;

alter table HFJ_RESOURCE drop foreign key FKhjgj8cp879gfxko25cx5o692r;

alter table HFJ_RESOURCE drop column FORCED_ID_PID;

alter table HFJ_RESOURCE add PARTITION_DATE date null after RES_ID;

alter table HFJ_RESOURCE add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_RES_PARAM_PRESENT add PARTITION_DATE date null after PID;

alter table HFJ_RES_PARAM_PRESENT	add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_RES_TAG add PARTITION_DATE date null after PID;

alter table HFJ_RES_TAG add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_RES_VER modify RES_ENCODING varchar(5) not null after RES_UPDATED;

alter table HFJ_RES_VER modify RES_VERSION varchar(7) null;

alter table HFJ_RES_VER modify RES_ID bigint not null;

alter table HFJ_RES_VER drop foreign key FKh20i7lcbchkaxekvwg9ix4hc5;

alter table HFJ_RES_VER drop column FORCED_ID_PID;

alter table HFJ_RES_VER add PARTITION_DATE date null after PID;

alter table HFJ_RES_VER add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_RES_VER add constraint FK_RESOURCE_HISTORY_RESOURCE foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_VER_PROV add PARTITION_DATE date null after RES_VER_PID;

alter table HFJ_RES_VER_PROV	add PARTITION_ID int null after PARTITION_DATE;


create table MPI_LINK
(
    PID bigint not null
        primary key,
    CREATED datetime not null,
    EID_MATCH bit null,
    TARGET_TYPE varchar(40) null,
    LINK_SOURCE int not null,
    MATCH_RESULT int not null,
    NEW_PERSON bit null,
    PERSON_PID bigint not null,
    SCORE double null,
    TARGET_PID bigint not null,
    UPDATED datetime not null,
    VECTOR bigint null,
    VERSION varchar(16) not null,
    constraint IDX_EMPI_PERSON_TGT
        unique (PERSON_PID, TARGET_PID),
    constraint FK_EMPI_LINK_PERSON
        foreign key (PERSON_PID) references HFJ_RESOURCE (RES_ID),
    constraint FK_EMPI_LINK_TARGET
        foreign key (TARGET_PID) references HFJ_RESOURCE (RES_ID)
);

create table NPM_PACKAGE
(
    PID bigint not null
        primary key,
    CUR_VERSION_ID varchar(200) null,
    PACKAGE_DESC varchar(200) null,
    PACKAGE_ID varchar(200) not null,
    UPDATED_TIME datetime not null,
    constraint IDX_PACK_ID
        unique (PACKAGE_ID)
);

create table NPM_PACKAGE_VER
(
    PID bigint not null
        primary key,
    CURRENT_VERSION bit not null,
    PKG_DESC varchar(200) null,
    DESC_UPPER varchar(200) null,
    FHIR_VERSION varchar(10) not null,
    FHIR_VERSION_ID varchar(10) not null,
    PACKAGE_ID varchar(200) not null,
    PACKAGE_SIZE_BYTES bigint not null,
    SAVED_TIME datetime not null,
    UPDATED_TIME datetime not null,
    VERSION_ID varchar(200) not null,
    PACKAGE_PID bigint not null,
    BINARY_RES_ID bigint not null,
    constraint IDX_PACKVER
        unique (PACKAGE_ID, VERSION_ID),
    constraint FK_NPM_PKV_PKG
        foreign key (PACKAGE_PID) references NPM_PACKAGE (PID),
    constraint FK_NPM_PKV_RESID
        foreign key (BINARY_RES_ID) references HFJ_RESOURCE (RES_ID)
);

create table NPM_PACKAGE_VER_RES
(
    PID bigint not null
        primary key,
    CANONICAL_URL varchar(200) null,
    CANONICAL_VERSION varchar(200) null,
    FILE_DIR varchar(200) null,
    FHIR_VERSION varchar(10) not null,
    FHIR_VERSION_ID varchar(10) not null,
    FILE_NAME varchar(200) null,
    RES_SIZE_BYTES bigint not null,
    RES_TYPE varchar(40) not null,
    UPDATED_TIME datetime not null,
    PACKVER_PID bigint not null,
    BINARY_RES_ID bigint not null,
    constraint FK_NPM_PACKVERRES_PACKVER
        foreign key (PACKVER_PID) references NPM_PACKAGE_VER (PID),
    constraint FK_NPM_PKVR_RESID
        foreign key (BINARY_RES_ID) references HFJ_RESOURCE (RES_ID)
);

create index IDX_PACKVERRES_URL
    on NPM_PACKAGE_VER_RES (CANONICAL_URL);

create table SEQ_EMPI_LINK_ID
(
    next_val bigint null
);

create table SEQ_NPM_PACK
(
    next_val bigint null
);

create table SEQ_NPM_PACKVER
(
    next_val bigint null
);

create table SEQ_NPM_PACKVERRES
(
    next_val bigint null
);

alter table TRM_CONCEPT_MAP_GROUP modify SOURCE_VERSION varchar(200) null;

alter table TRM_CONCEPT_MAP_GROUP modify TARGET_VERSION varchar(200) null;

alter table TRM_CONCEPT_PROPERTY modify PROP_CODESYSTEM varchar(500) null after PID;

alter table TRM_CONCEPT_PROPERTY modify PROP_DISPLAY varchar(500) null after PROP_CODESYSTEM;

alter table HFJ_FORCED_ID add PARTITION_DATE date null after PID;

alter table HFJ_FORCED_ID add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_IDX_CMP_STRING_UNIQ add PARTITION_DATE date null after PID;

alter table HFJ_IDX_CMP_STRING_UNIQ add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_RES_LINK add PARTITION_DATE date null after PID;

alter table HFJ_RES_LINK add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_COORDS add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_COORDS add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_COORDS modify SP_MISSING bit not null;

create index IDX_SP_DATE_HASH_LOW on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW);

alter table HFJ_SPIDX_DATE add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_DATE add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_DATE modify SP_MISSING bit not null;

alter table HFJ_SPIDX_DATE add SP_VALUE_HIGH_DATE_ORDINAL int null after SP_VALUE_HIGH;

alter table HFJ_SPIDX_DATE add SP_VALUE_LOW_DATE_ORDINAL int null after SP_VALUE_LOW;

create index IDX_SP_DATE_ORD_HASH on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL);

create index IDX_SP_DATE_ORD_HASH_LOW on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL);

alter table HFJ_SPIDX_NUMBER add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_NUMBER add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_NUMBER modify SP_MISSING bit not null;

alter table HFJ_SPIDX_QUANTITY add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_QUANTITY add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_QUANTITY modify SP_MISSING bit not null;

alter table HFJ_SPIDX_STRING add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_STRING add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_STRING modify SP_MISSING bit not null;

alter table HFJ_SPIDX_TOKEN add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_TOKEN add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_TOKEN modify SP_MISSING bit not null;

alter table HFJ_SPIDX_URI add PARTITION_DATE date null after SP_ID;

alter table HFJ_SPIDX_URI add PARTITION_ID int null after PARTITION_DATE;

alter table HFJ_SPIDX_URI modify SP_MISSING bit not null;

alter table HFJ_BLK_EXPORT_COLFILE charset=utf8;

alter table HFJ_BLK_EXPORT_COLLECTION charset=utf8;

alter table TRM_VALUESET charset=utf8;

alter table TRM_VALUESET_CONCEPT charset=utf8;

alter table TRM_VALUESET_C_DESIGNATION charset=utf8;

alter table HFJ_BLK_EXPORT_COLFILE modify RES_ID varchar(100) charset utf8 not null;

alter table HFJ_BLK_EXPORT_COLLECTION modify TYPE_FILTER varchar(1000) charset utf8 null;

alter table HFJ_BLK_EXPORT_COLLECTION modify RES_TYPE varchar(40) charset utf8 not null;

alter table HFJ_FORCED_ID modify FORCED_ID varchar(100) charset utf8 not null;

alter table HFJ_FORCED_ID modify RESOURCE_TYPE varchar(100) charset utf8 default '' null;

alter table HFJ_HISTORY_TAG modify RES_TYPE varchar(40) charset utf8 not null;

alter table HFJ_IDX_CMP_STRING_UNIQ modify IDX_STRING varchar(200) charset utf8 not null;

alter table HFJ_RESOURCE modify RES_VERSION varchar(7) charset utf8 null;

alter table HFJ_RESOURCE modify HASH_SHA256 varchar(64) charset utf8 null;

alter table HFJ_RESOURCE modify RES_LANGUAGE varchar(20) charset utf8 null;

alter table HFJ_RESOURCE modify RES_TYPE varchar(40) charset utf8 not null;

alter table HFJ_RES_LINK modify SRC_PATH varchar(200) charset utf8 not null;

alter table HFJ_RES_LINK modify SOURCE_RESOURCE_TYPE varchar(40) charset utf8 not null;

alter table HFJ_RES_LINK modify TARGET_RESOURCE_TYPE varchar(40) charset utf8 not null;

alter table HFJ_RES_LINK modify TARGET_RESOURCE_URL varchar(200) charset utf8 null;

alter table HFJ_RES_TAG modify RES_TYPE varchar(40) charset utf8 not null;

alter table HFJ_RES_VER modify RES_TYPE varchar(40) charset utf8 not null;

alter table HFJ_SEARCH modify FAILURE_MESSAGE varchar(500) charset utf8 null;

alter table HFJ_SEARCH modify RESOURCE_TYPE varchar(200) charset utf8 null;

alter table HFJ_SEARCH modify SEARCH_QUERY_STRING longtext charset utf8 null;

alter table HFJ_SEARCH modify SEARCH_STATUS varchar(10) charset utf8 not null;

alter table HFJ_SEARCH modify SEARCH_UUID varchar(36) charset utf8 not null;

alter table HFJ_SEARCH_INCLUDE modify SEARCH_INCLUDE varchar(200) charset utf8 not null;

alter table HFJ_SPIDX_COORDS modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_COORDS modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_DATE modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_DATE modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_NUMBER modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_NUMBER modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_QUANTITY modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_QUANTITY modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_QUANTITY modify SP_SYSTEM varchar(200) charset utf8 null;

alter table HFJ_SPIDX_QUANTITY modify SP_UNITS varchar(200) charset utf8 null;

alter table HFJ_SPIDX_STRING modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_STRING modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_STRING modify SP_VALUE_EXACT varchar(200) charset utf8 null;

alter table HFJ_SPIDX_STRING modify SP_VALUE_NORMALIZED varchar(200) charset utf8 null;

alter table HFJ_SPIDX_TOKEN modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_TOKEN modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_TOKEN modify SP_SYSTEM varchar(200) charset utf8 null;

alter table HFJ_SPIDX_TOKEN modify SP_VALUE varchar(200) charset utf8 null;

alter table HFJ_SPIDX_URI modify SP_NAME varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_URI modify RES_TYPE varchar(100) charset utf8 not null;

alter table HFJ_SPIDX_URI modify SP_URI varchar(254) charset utf8 null;

alter table TRM_CODESYSTEM modify CODE_SYSTEM_URI varchar(200) charset utf8 not null;

alter table TRM_CODESYSTEM modify CS_NAME varchar(200) charset utf8 null;

alter table TRM_CODESYSTEM_VER modify CS_VERSION_ID varchar(200) charset utf8 null;

alter table TRM_CONCEPT modify CODEVAL varchar(500) charset utf8 not null;

alter table TRM_CONCEPT modify DISPLAY varchar(400) charset utf8 null;

alter table TRM_CONCEPT modify PARENT_PIDS longtext charset utf8 null;

alter table TRM_CONCEPT_PROPERTY modify PROP_KEY varchar(500) charset utf8 not null;

alter table TRM_CONCEPT_PROPERTY modify PROP_VAL varchar(500) charset utf8 null;

alter table TRM_VALUESET modify EXPANSION_STATUS varchar(50) charset utf8 not null;

alter table TRM_VALUESET modify VSNAME varchar(200) charset utf8 null;

alter table TRM_VALUESET modify URL varchar(200) charset utf8 not null;

alter table TRM_VALUESET_CONCEPT modify CODEVAL varchar(500) charset utf8 not null;

alter table TRM_VALUESET_CONCEPT modify DISPLAY varchar(400) charset utf8 null;

alter table TRM_VALUESET_CONCEPT modify SYSTEM_URL varchar(200) charset utf8 not null;

alter table TRM_VALUESET_C_DESIGNATION modify LANG varchar(500) charset utf8 null;

alter table TRM_VALUESET_C_DESIGNATION modify USE_CODE varchar(500) charset utf8 null;

alter table TRM_VALUESET_C_DESIGNATION modify USE_DISPLAY varchar(500) charset utf8 null;

alter table TRM_VALUESET_C_DESIGNATION modify USE_SYSTEM varchar(500) charset utf8 null;

alter table TRM_VALUESET_C_DESIGNATION modify VAL varchar(2000) charset utf8 not null;



