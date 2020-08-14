alter table HFJ_BLK_EXPORT_COLFILE drop foreign key FK_BLKEXCOLFILE_COLLECT;

alter table HFJ_BLK_EXPORT_COLFILE
  add constraint FK_BLKEXCOLFILE_COLLECT
    foreign key (COLLECTION_PID) references HFJ_BLK_EXPORT_COLLECTION (PID);

alter table HFJ_BLK_EXPORT_COLLECTION drop foreign key FK_BLKEXCOL_JOB;

alter table HFJ_BLK_EXPORT_COLLECTION
  add constraint FK_BLKEXCOL_JOB
    foreign key (JOB_PID) references HFJ_BLK_EXPORT_JOB (PID);

alter table HFJ_FORCED_ID modify FORCED_ID varchar(100) not null;

alter table HFJ_FORCED_ID modify RESOURCE_TYPE varchar(100) default '' null;

alter table HFJ_FORCED_ID drop foreign key FK_FORCEDID_RESOURCE;

alter table HFJ_FORCED_ID charset=latin1;

create unique index IDX_FORCEDID_TYPE_FID
  on HFJ_FORCED_ID (RESOURCE_TYPE, FORCED_ID);

alter table HFJ_FORCED_ID
  add constraint IDX_FORCEDID_TYPE_FID
    unique (RESOURCE_TYPE, FORCED_ID);

alter table HFJ_FORCED_ID
  add constraint FK_FORCEDID_RESOURCE
    foreign key (RESOURCE_PID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_HISTORY_TAG modify RES_TYPE varchar(40) not null;

alter table HFJ_HISTORY_TAG engine=InnoDB;

alter table HFJ_HISTORY_TAG charset=latin1;

alter table HFJ_HISTORY_TAG
  add constraint FK_HISTORYTAG_HISTORY
    foreign key (RES_VER_PID) references HFJ_RES_VER (PID);

alter table HFJ_HISTORY_TAG
  add constraint FKtderym7awj6q8iq5c51xv4ndw
    foreign key (TAG_ID) references HFJ_TAG_DEF (TAG_ID);

alter table HFJ_IDX_CMP_STRING_UNIQ modify IDX_STRING varchar(200) not null;

alter table HFJ_IDX_CMP_STRING_UNIQ engine=InnoDB;

alter table HFJ_IDX_CMP_STRING_UNIQ charset=latin1;

alter table HFJ_IDX_CMP_STRING_UNIQ
  add constraint FK_IDXCMPSTRUNIQ_RES_ID
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RESOURCE modify RES_VERSION varchar(7) null;

alter table HFJ_RESOURCE drop column RES_TITLE;

alter table HFJ_RESOURCE modify HASH_SHA256 varchar(64) null;

alter table HFJ_RESOURCE modify RES_LANGUAGE varchar(20) null;

alter table HFJ_RESOURCE modify RES_PROFILE varchar(200) null;

alter table HFJ_RESOURCE modify RES_TYPE varchar(40) not null;

alter table HFJ_RESOURCE engine=InnoDB;

alter table HFJ_RESOURCE charset=latin1;

alter table HFJ_RESOURCE
  add constraint FKhjgj8cp879gfxko25cx5o692r
    foreign key (FORCED_ID_PID) references HFJ_FORCED_ID (PID);

alter table HFJ_RES_LINK modify SRC_PATH varchar(200) not null;

alter table HFJ_RES_LINK modify SOURCE_RESOURCE_TYPE varchar(40) not null;

alter table HFJ_RES_LINK modify TARGET_RESOURCE_TYPE varchar(40) not null;

alter table HFJ_RES_LINK modify TARGET_RESOURCE_URL varchar(200) null;

alter table HFJ_RES_LINK engine=InnoDB;

alter table HFJ_RES_LINK charset=latin1;

alter table HFJ_RES_LINK
  add constraint FK_RESLINK_SOURCE
    foreign key (SRC_RESOURCE_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_LINK
  add constraint FK_RESLINK_TARGET
    foreign key (TARGET_RESOURCE_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_PARAM_PRESENT engine=InnoDB;

alter table HFJ_RES_PARAM_PRESENT charset=latin1;

alter table HFJ_RES_PARAM_PRESENT
  add constraint FK_RESPARMPRES_RESID
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_REINDEX_JOB modify SUSPENDED_UNTIL datetime null;

alter table HFJ_RES_REINDEX_JOB modify UPDATE_THRESHOLD_HIGH datetime not null;

alter table HFJ_RES_REINDEX_JOB modify UPDATE_THRESHOLD_LOW datetime null;

alter table HFJ_RES_TAG modify RES_TYPE varchar(40) not null;

alter table HFJ_RES_TAG engine=InnoDB;

alter table HFJ_RES_TAG charset=latin1;

alter table HFJ_RES_TAG
  add constraint FK_RESTAG_RESOURCE
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_RES_TAG
  add constraint FKbfcjbaftmiwr3rxkwsy23vneo
    foreign key (TAG_ID) references HFJ_TAG_DEF (TAG_ID);

alter table HFJ_RES_VER modify RES_ENCODING varchar(5) not null after RES_UPDATED;

alter table HFJ_RES_VER modify RES_VERSION varchar(7) null;

alter table HFJ_RES_VER drop column RES_TITLE;

alter table HFJ_RES_VER modify RES_TYPE varchar(40) not null;

alter table HFJ_RES_VER engine=InnoDB;

alter table HFJ_RES_VER charset=latin1;

alter table HFJ_RES_VER
  add constraint FKh20i7lcbchkaxekvwg9ix4hc5
    foreign key (FORCED_ID_PID) references HFJ_FORCED_ID (PID);

alter table HFJ_RES_VER_PROV drop foreign key FK_RESVERPROV_RESVER_PID;

alter table HFJ_RES_VER_PROV drop foreign key FK_RESVERPROV_RES_PID;

alter table HFJ_RES_VER_PROV charset=latin1;

alter table HFJ_RES_VER_PROV
  add constraint FK_RESVERPROV_RESVER_PID
    foreign key (RES_VER_PID) references HFJ_RES_VER (PID);

alter table HFJ_RES_VER_PROV
  add constraint FK_RESVERPROV_RES_PID
    foreign key (RES_PID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SEARCH modify FAILURE_MESSAGE varchar(500) null;

alter table HFJ_SEARCH modify RESOURCE_TYPE varchar(200) null;

alter table HFJ_SEARCH modify SEARCH_QUERY_STRING longtext null;

alter table HFJ_SEARCH modify SEARCH_STATUS varchar(10) not null;

alter table HFJ_SEARCH modify SEARCH_UUID varchar(36) not null;

alter table HFJ_SEARCH engine=InnoDB;

alter table HFJ_SEARCH charset=latin1;

alter table HFJ_SEARCH_INCLUDE modify SEARCH_INCLUDE varchar(200) not null;

alter table HFJ_SEARCH_INCLUDE engine=InnoDB;

alter table HFJ_SEARCH_INCLUDE charset=latin1;

alter table HFJ_SEARCH_INCLUDE
  add constraint FK_SEARCHINC_SEARCH
    foreign key (SEARCH_PID) references HFJ_SEARCH (PID);

drop table HFJ_SEARCH_PARM;

alter table HFJ_SPIDX_COORDS modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_COORDS modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_COORDS engine=InnoDB;

alter table HFJ_SPIDX_COORDS charset=latin1;

alter table HFJ_SPIDX_COORDS
  add constraint FKc97mpk37okwu8qvtceg2nh9vn
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_DATE modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_DATE modify RES_TYPE varchar(100) not null;

drop index IDX_SP_DATE on HFJ_SPIDX_DATE;

alter table HFJ_SPIDX_DATE engine=InnoDB;

alter table HFJ_SPIDX_DATE charset=latin1;

alter table HFJ_SPIDX_DATE
  add constraint FK17s70oa59rm9n61k9thjqrsqm
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_NUMBER modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_NUMBER modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_NUMBER engine=InnoDB;

alter table HFJ_SPIDX_NUMBER charset=latin1;

alter table HFJ_SPIDX_NUMBER
  add constraint FKcltihnc5tgprj9bhpt7xi5otb
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_QUANTITY modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_QUANTITY modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_QUANTITY modify SP_SYSTEM varchar(200) null;

alter table HFJ_SPIDX_QUANTITY modify SP_UNITS varchar(200) null;

alter table HFJ_SPIDX_QUANTITY drop column HASH_UNITS_AND_VALPREFIX;

alter table HFJ_SPIDX_QUANTITY drop column HASH_VALPREFIX;

alter table HFJ_SPIDX_QUANTITY engine=InnoDB;

alter table HFJ_SPIDX_QUANTITY charset=latin1;

alter table HFJ_SPIDX_QUANTITY
  add constraint FKn603wjjoi1a6asewxbbd78bi5
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_STRING modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_STRING modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_STRING modify SP_VALUE_EXACT varchar(200) null;

alter table HFJ_SPIDX_STRING modify SP_VALUE_NORMALIZED varchar(200) null;

alter table HFJ_SPIDX_STRING engine=InnoDB;

alter table HFJ_SPIDX_STRING charset=latin1;

create index IDX_SP_STRING_HASH_IDENT
  on HFJ_SPIDX_STRING (HASH_IDENTITY);

alter table HFJ_SPIDX_STRING
  add constraint FK_SPIDXSTR_RESOURCE
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_TOKEN modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_TOKEN modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_TOKEN modify SP_SYSTEM varchar(200) null;

alter table HFJ_SPIDX_TOKEN modify SP_VALUE varchar(200) null;

alter table HFJ_SPIDX_TOKEN engine=InnoDB;

alter table HFJ_SPIDX_TOKEN charset=latin1;

alter table HFJ_SPIDX_TOKEN
  add constraint FK7ulx3j1gg3v7maqrejgc7ybc4
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SPIDX_URI modify SP_NAME varchar(100) not null;

alter table HFJ_SPIDX_URI modify RES_TYPE varchar(100) not null;

alter table HFJ_SPIDX_URI modify SP_URI varchar(254) null;

alter table HFJ_SPIDX_URI engine=InnoDB;

alter table HFJ_SPIDX_URI charset=latin1;

alter table HFJ_SPIDX_URI
  add constraint FKgxsreutymmfjuwdswv3y887do
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_SUBSCRIPTION_STATS engine=InnoDB;

alter table HFJ_SUBSCRIPTION_STATS charset=latin1;

alter table HFJ_SUBSCRIPTION_STATS
  add constraint FK_SUBSC_RESOURCE_ID
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table HFJ_TAG_DEF drop column myHashCode;

alter table HFJ_TAG_DEF engine=InnoDB;

drop table SEQ_SEARCHPARM_ID;

drop table TEMP_IDS;

alter table TRM_CODESYSTEM modify CODE_SYSTEM_URI varchar(200) not null;

alter table TRM_CODESYSTEM modify CS_NAME varchar(200) null;

alter table TRM_CODESYSTEM engine=InnoDB;

alter table TRM_CODESYSTEM charset=latin1;

alter table TRM_CODESYSTEM
  add constraint FK_TRMCODESYSTEM_CURVER
    foreign key (CURRENT_VERSION_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CODESYSTEM
  add constraint FK_TRMCODESYSTEM_RES
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table TRM_CODESYSTEM_VER modify CS_VERSION_ID varchar(200) null;

alter table TRM_CODESYSTEM_VER engine=InnoDB;

alter table TRM_CODESYSTEM_VER charset=latin1;

alter table TRM_CODESYSTEM_VER
  add constraint FK_CODESYSVER_CS_ID
    foreign key (CODESYSTEM_PID) references TRM_CODESYSTEM (PID);

alter table TRM_CODESYSTEM_VER
  add constraint FK_CODESYSVER_RES_ID
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table TRM_CONCEPT modify CODEVAL varchar(500) not null;

alter table TRM_CONCEPT modify DISPLAY varchar(400) null;

alter table TRM_CONCEPT modify PARENT_PIDS longtext null;

alter table TRM_CONCEPT engine=InnoDB;

alter table TRM_CONCEPT charset=latin1;

alter table TRM_CONCEPT
  add constraint FK_CONCEPT_PID_CS_PID
    foreign key (CODESYSTEM_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CONCEPT_DESIG engine=InnoDB;

alter table TRM_CONCEPT_DESIG
  add constraint FK_CONCEPTDESIG_CONCEPT
    foreign key (CONCEPT_PID) references TRM_CONCEPT (PID);

alter table TRM_CONCEPT_DESIG
  add constraint FK_CONCEPTDESIG_CSV
    foreign key (CS_VER_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CONCEPT_MAP engine=InnoDB;

alter table TRM_CONCEPT_MAP
  add constraint FK_TRMCONCEPTMAP_RES
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table TRM_CONCEPT_MAP_GROUP modify SOURCE_VERSION varchar(200) null;

alter table TRM_CONCEPT_MAP_GROUP modify TARGET_VERSION varchar(200) null;

alter table TRM_CONCEPT_MAP_GROUP engine=InnoDB;

alter table TRM_CONCEPT_MAP_GROUP
  add constraint FK_TCMGROUP_CONCEPTMAP
    foreign key (CONCEPT_MAP_PID) references TRM_CONCEPT_MAP (PID);

alter table TRM_CONCEPT_MAP_GRP_ELEMENT engine=InnoDB;

create index IDX_CNCPT_MAP_GRP_CD
  on TRM_CONCEPT_MAP_GRP_ELEMENT (SOURCE_CODE);

alter table TRM_CONCEPT_MAP_GRP_ELEMENT
  add constraint FK_TCMGELEMENT_GROUP
    foreign key (CONCEPT_MAP_GROUP_PID) references TRM_CONCEPT_MAP_GROUP (PID);

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT engine=InnoDB;

create index IDX_CNCPT_MP_GRP_ELM_TGT_CD
  on TRM_CONCEPT_MAP_GRP_ELM_TGT (TARGET_CODE);

alter table TRM_CONCEPT_MAP_GRP_ELM_TGT
  add constraint FK_TCMGETARGET_ELEMENT
    foreign key (CONCEPT_MAP_GRP_ELM_PID) references TRM_CONCEPT_MAP_GRP_ELEMENT (PID);

alter table TRM_CONCEPT_PC_LINK engine=InnoDB;

alter table TRM_CONCEPT_PC_LINK charset=latin1;

alter table TRM_CONCEPT_PC_LINK
  add constraint FK_TERM_CONCEPTPC_CHILD
    foreign key (CHILD_PID) references TRM_CONCEPT (PID);

alter table TRM_CONCEPT_PC_LINK
  add constraint FK_TERM_CONCEPTPC_CS
    foreign key (CODESYSTEM_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_CONCEPT_PC_LINK
  add constraint FK_TERM_CONCEPTPC_PARENT
    foreign key (PARENT_PID) references TRM_CONCEPT (PID);

alter table TRM_CONCEPT_PROPERTY modify PROP_VAL varchar(500) null;

alter table TRM_CONCEPT_PROPERTY modify PROP_CODESYSTEM varchar(500) null after PID;

alter table TRM_CONCEPT_PROPERTY modify PROP_DISPLAY varchar(500) null after PROP_CODESYSTEM;

alter table TRM_CONCEPT_PROPERTY modify PROP_KEY varchar(500) not null;

alter table TRM_CONCEPT_PROPERTY engine=InnoDB;

alter table TRM_CONCEPT_PROPERTY charset=latin1;

alter table TRM_CONCEPT_PROPERTY
  add constraint FK_CONCEPTPROP_CONCEPT
    foreign key (CONCEPT_PID) references TRM_CONCEPT (PID);

alter table TRM_CONCEPT_PROPERTY
  add constraint FK_CONCEPTPROP_CSV
    foreign key (CS_VER_PID) references TRM_CODESYSTEM_VER (PID);

alter table TRM_VALUESET drop foreign key FK_TRMVALUESET_RES;

alter table TRM_VALUESET
  add constraint FK_TRMVALUESET_RES
    foreign key (RES_ID) references HFJ_RESOURCE (RES_ID);

alter table TRM_VALUESET_CONCEPT drop foreign key FK_TRM_VALUESET_PID;

alter table TRM_VALUESET_CONCEPT
  add constraint FK_TRM_VALUESET_PID
    foreign key (VALUESET_PID) references TRM_VALUESET (PID);

alter table TRM_VALUESET_C_DESIGNATION drop foreign key FK_TRM_VALUESET_CONCEPT_PID;

alter table TRM_VALUESET_C_DESIGNATION
  add constraint FK_TRM_VALUESET_CONCEPT_PID
    foreign key (VALUESET_CONCEPT_PID) references TRM_VALUESET_CONCEPT (PID);

alter table TRM_VALUESET_C_DESIGNATION drop foreign key FK_TRM_VSCD_VS_PID;

alter table TRM_VALUESET_C_DESIGNATION
  add constraint FK_TRM_VSCD_VS_PID
    foreign key (VALUESET_PID) references TRM_VALUESET (PID);


