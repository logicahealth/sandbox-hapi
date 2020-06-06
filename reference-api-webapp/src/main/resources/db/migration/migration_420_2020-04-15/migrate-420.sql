--
--  * #%L
--  *
--  * %%
--  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
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

create index FK_BLKEXCOLFILE_COLLECT on HFJ_BLK_EXPORT_COLFILE (COLLECTION_PID);

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

alter table HFJ_FORCED_ID drop foreign key FK_FORCEDID_RESOURCE;

alter table HFJ_FORCED_ID charset=utf8;

alter table HFJ_FORCED_ID
    add constraint FK_FORCEDID_RESOURCE
        foreign key (RESOURCE_PID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_HISTORY_TAG modify RES_TYPE varchar(40) not null;

alter table HFJ_HISTORY_TAG drop foreign key FK_HISTORYTAG_HISTORY;

alter table HFJ_HISTORY_TAG drop foreign key FKtderym7awj6q8iq5c51xv4ndw;

alter table HFJ_HISTORY_TAG charset=utf8;

alter table HFJ_HISTORY_TAG
    add constraint FK_HISTORYTAG_HISTORY
        foreign key (RES_VER_PID) references HFJ_RES_VER (PID);

alter table HFJ_HISTORY_TAG
    add constraint FKtderym7awj6q8iq5c51xv4ndw
        foreign key (TAG_ID) references HFJ_TAG_DEF (TAG_ID);

alter table HFJ_IDX_CMP_STRING_UNIQ drop foreign key FK_IDXCMPSTRUNIQ_RES_ID;

alter table HFJ_IDX_CMP_STRING_UNIQ charset=utf8;

alter table HFJ_IDX_CMP_STRING_UNIQ
    add constraint FK_IDXCMPSTRUNIQ_RES_ID
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RESOURCE modify RES_TYPE varchar(40) not null;

alter table HFJ_RESOURCE drop foreign key FKhjgj8cp879gfxko25cx5o692r;

alter table HFJ_RESOURCE charset=utf8;

alter table HFJ_RESOURCE
    add constraint FKhjgj8cp879gfxko25cx5o692r
        foreign key (FORCED_ID_PID) references HFJ_FORCED_ID (PID);

alter table HFJ_RES_LINK modify SOURCE_RESOURCE_TYPE varchar(40) not null;

alter table HFJ_RES_LINK modify TARGET_RESOURCE_TYPE varchar(40) not null;

alter table HFJ_RES_LINK drop foreign key FK_RESLINK_SOURCE;

alter table HFJ_RES_LINK drop foreign key FK_RESLINK_TARGET;

alter table HFJ_RES_LINK charset=utf8;

alter table HFJ_RES_LINK
    add constraint FK_RESLINK_SOURCE
        foreign key (SRC_RESOURCE_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_LINK
    add constraint FK_RESLINK_TARGET
        foreign key (TARGET_RESOURCE_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_PARAM_PRESENT drop foreign key FK_RESPARMPRES_RESID;

alter table HFJ_RES_PARAM_PRESENT charset=utf8;

alter table HFJ_RES_PARAM_PRESENT
    add constraint FK_RESPARMPRES_RESID
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_REINDEX_JOB modify RES_TYPE varchar(100) null;

alter table HFJ_RES_TAG modify RES_TYPE varchar(40) not null;

alter table HFJ_RES_TAG drop foreign key FK_RESTAG_RESOURCE;

alter table HFJ_RES_TAG drop foreign key FKbfcjbaftmiwr3rxkwsy23vneo;

alter table HFJ_RES_TAG charset=utf8;

alter table HFJ_RES_TAG
    add constraint FK_RESTAG_RESOURCE
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_TAG
    add constraint FKbfcjbaftmiwr3rxkwsy23vneo
        foreign key (TAG_ID) references HFJ_TAG_DEF (TAG_ID);

alter table HFJ_RES_VER modify RES_TYPE varchar(40) not null;

alter table HFJ_RES_VER drop foreign key FKh20i7lcbchkaxekvwg9ix4hc5;

alter table HFJ_RES_VER charset=utf8;

alter table HFJ_RES_VER
    add constraint FKh20i7lcbchkaxekvwg9ix4hc5
        foreign key (FORCED_ID_PID) references HFJ_FORCED_ID (PID);

create table HFJ_RES_VER_PROV
(
    RES_VER_PID bigint not null
        primary key,
    REQUEST_ID varchar(16) null,
    SOURCE_URI varchar(100) null,
    RES_PID bigint not null,
    constraint FK_RESVERPROV_RESVER_PID
        foreign key (RES_VER_PID) references HFJ_RES_VER (PID),
    constraint FK_RESVERPROV_RES_PID
        foreign key (RES_PID) references HFJ_RESOURCE (RES_ID)
) engine=InnoDB DEFAULT CHARSET=utf8;

create index IDX_RESVERPROV_REQUESTID
    on HFJ_RES_VER_PROV (REQUEST_ID);

create index IDX_RESVERPROV_SOURCEURI
    on HFJ_RES_VER_PROV (SOURCE_URI);

ALTER TABLE HFJ_SEARCH DROP INDEX IDX_SEARCH_LASTRETURNED;

alter table HFJ_SEARCH drop column SEARCH_LAST_RETURNED;

alter table HFJ_SEARCH charset=utf8;

alter table HFJ_SEARCH
    add EXPIRY_OR_NULL datetime null;

alter table HFJ_SEARCH
    add NUM_BLOCKED int null;

create index IDX_SEARCH_CREATED
    on HFJ_SEARCH (CREATED);

alter table HFJ_SEARCH_INCLUDE drop foreign key FK_SEARCHINC_SEARCH;

alter table HFJ_SEARCH_INCLUDE charset=utf8;

alter table HFJ_SEARCH_INCLUDE
    add constraint FK_SEARCHINC_SEARCH
        foreign key (SEARCH_PID) references HFJ_SEARCH (PID);

alter table HFJ_SEARCH_RESULT drop foreign key FK_SEARCHRES_RES;

alter table HFJ_SEARCH_RESULT drop index FK_SEARCHRES_RES;

alter table HFJ_SEARCH_RESULT drop foreign key FK_SEARCHRES_SEARCH;

alter table HFJ_SEARCH_RESULT charset=utf8;

alter table HFJ_SPIDX_COORDS modify RES_ID bigint not null;

alter table HFJ_SPIDX_COORDS modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_COORDS drop foreign key FKc97mpk37okwu8qvtceg2nh9vn;

alter table HFJ_SPIDX_COORDS charset=utf8;

alter table HFJ_SPIDX_COORDS
    add constraint FKc97mpk37okwu8qvtceg2nh9vn
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_DATE modify RES_ID bigint not null;

alter table HFJ_SPIDX_DATE modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_DATE drop foreign key FK17s70oa59rm9n61k9thjqrsqm;

alter table HFJ_SPIDX_DATE charset=utf8;

alter table HFJ_SPIDX_DATE
    add constraint FK17s70oa59rm9n61k9thjqrsqm
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_NUMBER modify RES_ID bigint not null;

alter table HFJ_SPIDX_NUMBER modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_NUMBER drop foreign key FKcltihnc5tgprj9bhpt7xi5otb;

alter table HFJ_SPIDX_NUMBER charset=utf8;

alter table HFJ_SPIDX_NUMBER
    add constraint FKcltihnc5tgprj9bhpt7xi5otb
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_QUANTITY modify RES_ID bigint not null;

alter table HFJ_SPIDX_QUANTITY modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_QUANTITY drop foreign key FKn603wjjoi1a6asewxbbd78bi5;

alter table HFJ_SPIDX_QUANTITY charset=utf8;

alter table HFJ_SPIDX_QUANTITY
    add constraint FKn603wjjoi1a6asewxbbd78bi5
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_STRING modify RES_ID bigint not null;

alter table HFJ_SPIDX_STRING modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_STRING drop foreign key FK_SPIDXSTR_RESOURCE;

alter table HFJ_SPIDX_STRING charset=utf8;

alter table HFJ_SPIDX_STRING
    add constraint FK_SPIDXSTR_RESOURCE
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_TOKEN modify RES_ID bigint not null;

alter table HFJ_SPIDX_TOKEN modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_TOKEN drop foreign key FK7ulx3j1gg3v7maqrejgc7ybc4;

alter table HFJ_SPIDX_TOKEN charset=utf8;

alter table HFJ_SPIDX_TOKEN
    add constraint FK7ulx3j1gg3v7maqrejgc7ybc4
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_URI modify RES_ID bigint not null;

alter table HFJ_SPIDX_URI modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_URI modify SP_URI varchar(254) null;

alter table HFJ_SPIDX_URI drop foreign key FKgxsreutymmfjuwdswv3y887do;

alter table HFJ_SPIDX_URI charset=utf8;

alter table HFJ_SPIDX_URI
    add constraint FKgxsreutymmfjuwdswv3y887do
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SUBSCRIPTION_STATS drop foreign key FK_SUBSC_RESOURCE_ID;

alter table HFJ_SUBSCRIPTION_STATS charset=utf8;

alter table HFJ_SUBSCRIPTION_STATS
    add constraint FK_SUBSC_RESOURCE_ID
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

create table SEQ_BLKEXCOLFILE_PID
(
    next_val bigint null
);

create table SEQ_BLKEXCOL_PID
(
    next_val bigint null
);

create table SEQ_BLKEXJOB_PID
(
    next_val bigint null
);

create table SEQ_VALUESET_CONCEPT_PID
(
    next_val bigint null
);

create table SEQ_VALUESET_C_DSGNTN_PID
(
    next_val bigint null
);

create table SEQ_VALUESET_PID
(
    next_val bigint null
);

alter table TRM_CODESYSTEM modify CODE_SYSTEM_URI varchar(200) not null;

alter table TRM_CODESYSTEM modify CS_NAME varchar(200) null;

alter table TRM_CODESYSTEM drop foreign key FK_TRMCODESYSTEM_CURVER;

alter table TRM_CODESYSTEM drop foreign key FK_TRMCODESYSTEM_RES;

alter table TRM_CODESYSTEM charset=utf8;

alter table TRM_CODESYSTEM
    add constraint FK_TRMCODESYSTEM_CURVER
        foreign key (CURRENT_VERSION_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CODESYSTEM
    add constraint FK_TRMCODESYSTEM_RES
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table TRM_CODESYSTEM_VER modify CS_VERSION_ID varchar(200) null;

alter table TRM_CODESYSTEM_VER drop foreign key FK_CODESYSVER_CS_ID;

alter table TRM_CODESYSTEM_VER drop foreign key FK_CODESYSVER_RES_ID;

alter table TRM_CODESYSTEM_VER charset=utf8;

alter table TRM_CODESYSTEM_VER
    add CS_DISPLAY varchar(200) null after PID;

alter table TRM_CODESYSTEM_VER
    add constraint FK_CODESYSVER_CS_ID
        foreign key (CODESYSTEM_PID) references TRM_CODESYSTEM (PID);

alter table TRM_CODESYSTEM_VER
    add constraint FK_CODESYSVER_RES_ID
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

ALTER TABLE TRM_CONCEPT change CODE CODEVAL varchar(500) not null;

alter table TRM_CONCEPT drop foreign key FK_CONCEPT_PID_CS_PID;

alter table TRM_CONCEPT charset=utf8;

alter table TRM_CONCEPT
    add constraint FK_CONCEPT_PID_CS_PID
        foreign key (CODESYSTEM_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CONCEPT_DESIG modify VAL varchar(2000) not null;

alter table TRM_CONCEPT_DESIG drop foreign key FK_CONCEPTDESIG_CONCEPT;

alter table TRM_CONCEPT_DESIG
    add constraint FK_CONCEPTDESIG_CONCEPT
        foreign key (CONCEPT_PID) references TRM_CONCEPT (PID);

alter table TRM_CONCEPT_DESIG drop foreign key FK_CONCEPTDESIG_CSV;

alter table TRM_CONCEPT_DESIG
    add constraint FK_CONCEPTDESIG_CSV
        foreign key (CS_VER_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CONCEPT_MAP drop foreign key FK_TRMCONCEPTMAP_RES;

alter table TRM_CONCEPT_MAP
    add constraint FK_TRMCONCEPTMAP_RES
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

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

alter table TRM_CONCEPT_PC_LINK charset=utf8;

alter table TRM_CONCEPT_PROPERTY charset=utf8;

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
        unique (URL),
    constraint FK_TRMVALUESET_RES
        foreign key (RES_ID) references HFJ_RESOURCE (RES_ID)
) engine=InnoDB;

create table TRM_VALUESET_CONCEPT
(
    PID bigint not null
        primary key,
    CODEVAL varchar(500) not null,
    DISPLAY varchar(400) null,
    VALUESET_ORDER int not null,
    SYSTEM_URL varchar(200) not null,
    VALUESET_PID bigint not null,
    constraint IDX_VS_CONCEPT_CS_CD
        unique (VALUESET_PID, SYSTEM_URL, CODEVAL),
    constraint IDX_VS_CONCEPT_ORDER
        unique (VALUESET_PID, VALUESET_ORDER),
    constraint FK_TRM_VALUESET_PID
        foreign key (VALUESET_PID) references TRM_VALUESET (PID)
) engine=InnoDB;

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
    VALUESET_PID bigint not null,
    constraint FK_TRM_VALUESET_CONCEPT_PID
        foreign key (VALUESET_CONCEPT_PID) references TRM_VALUESET_CONCEPT (PID),
    constraint FK_TRM_VSCD_VS_PID
        foreign key (VALUESET_PID) references TRM_VALUESET (PID)
) engine=InnoDB;

alter table HFJ_BLK_EXPORT_COLFILE
    add constraint FK_BLKEXCOLFILE_COLLECT
        foreign key (COLLECTION_PID) references HFJ_BLK_EXPORT_COLLECTION (PID);

alter table HFJ_BLK_EXPORT_COLLECTION
    add constraint FK_BLKEXCOL_JOB
        foreign key (JOB_PID) references HFJ_BLK_EXPORT_JOB (PID);

alter table HFJ_RES_PARAM_PRESENT drop column SP_ID; # not all tables has it, so put it at the very last

INSERT INTO SEQ_BLKEXCOLFILE_PID VALUES (1);

INSERT INTO SEQ_BLKEXCOL_PID VALUES (1);

INSERT INTO SEQ_BLKEXJOB_PID VALUES (1);

INSERT INTO SEQ_VALUESET_CONCEPT_PID VALUES (1);

INSERT INTO SEQ_VALUESET_C_DSGNTN_PID VALUES (1);

INSERT INTO SEQ_VALUESET_PID VALUES (1);

alter table HFJ_SPIDX_COORDS drop index IDX_SP_COORDS;

create index IDX_SP_COORDS_HASH on HFJ_SPIDX_COORDS (HASH_IDENTITY, SP_LATITUDE, SP_LONGITUDE);

create index IDX_SP_DATE_HASH on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH);

alter table HFJ_SPIDX_NUMBER drop index IDX_SP_NUMBER;

create index IDX_SP_NUMBER_HASH_VAL on HFJ_SPIDX_NUMBER(HASH_IDENTITY, SP_VALUE);

alter table HFJ_SPIDX_QUANTITY drop index IDX_SP_QUANTITY;

create index IDX_SP_QUANTITY_HASH on HFJ_SPIDX_QUANTITY(HASH_IDENTITY, SP_VALUE);

create index IDX_SP_QUANTITY_HASH_SYSUN on HFJ_SPIDX_QUANTITY (HASH_IDENTITY_SYS_UNITS, SP_VALUE);

create index IDX_SP_QUANTITY_HASH_UN on HFJ_SPIDX_QUANTITY (HASH_IDENTITY_AND_UNITS, SP_VALUE);

alter table HFJ_SPIDX_STRING drop index IDX_SP_STRING;

create index IDX_SP_STRING_HASH_EXCT on HFJ_SPIDX_STRING(HASH_EXACT);

create index IDX_SP_STRING_HASH_NRM on HFJ_SPIDX_STRING (HASH_NORM_PREFIX, SP_VALUE_NORMALIZED);

alter table HFJ_SPIDX_TOKEN drop index IDX_SP_TOKEN;

create index IDX_SP_TOKEN_HASH on HFJ_SPIDX_TOKEN(HASH_IDENTITY);

create index IDX_SP_TOKEN_HASH_S on HFJ_SPIDX_TOKEN (HASH_SYS);

create index IDX_SP_TOKEN_HASH_SV on HFJ_SPIDX_TOKEN (HASH_SYS_AND_VALUE);

create index IDX_SP_TOKEN_HASH_V on HFJ_SPIDX_TOKEN (HASH_VALUE);

drop index IDX_SP_TOKEN_UNQUAL on HFJ_SPIDX_TOKEN;

create index IDX_SP_URI_HASH_IDENTITY on HFJ_SPIDX_URI (HASH_IDENTITY, SP_URI);

create index IDX_SP_URI_HASH_URI	on HFJ_SPIDX_URI (HASH_URI);

create index IDX_CONCEPT_UPDATED on TRM_CONCEPT (CONCEPT_UPDATED);


SET FOREIGN_KEY_CHECKS = 1;

-- Script version 2.0

