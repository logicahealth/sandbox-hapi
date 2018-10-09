update HFJ_RESOURCE set SP_INDEX_STATUS = null;

alter table HFJ_SPIDX_QUANTITY add HASH_UNITS_AND_VALPREFIX bigint null;
alter table HFJ_SPIDX_QUANTITY add HASH_VALPREFIX bigint null;

alter table HFJ_SPIDX_STRING add HASH_EXACT bigint null;
alter table HFJ_SPIDX_STRING add HASH_NORM_PREFIX bigint null;

alter table HFJ_SPIDX_TOKEN add HASH_SYS bigint null;
alter table HFJ_SPIDX_TOKEN add HASH_SYS_AND_VALUE bigint null;
alter table HFJ_SPIDX_TOKEN add HASH_VALUE bigint null;

alter table HFJ_SPIDX_URI add HASH_URI bigint null;

create table SEQ_CNCPT_MAP_GRP_ELM_TGT_PID
(
	next_val bigint null
)
engine=MyISAM;

create table SEQ_CONCEPT_DESIG_PID
(
	next_val bigint null
)
engine=MyISAM;

create table SEQ_CONCEPT_MAP_GROUP_PID
(
	next_val bigint null
)
engine=MyISAM;

create table SEQ_CONCEPT_MAP_GRP_ELM_PID
(
	next_val bigint null
)
engine=MyISAM;

create table SEQ_CONCEPT_MAP_PID
(
	next_val bigint null
)
engine=MyISAM;

alter table TRM_CODESYSTEM add CS_NAME varchar(255) null;

alter table TRM_CODESYSTEM_VER add CS_VERSION_ID varchar(255) null;
alter table TRM_CODESYSTEM_VER add CODESYSTEM_PID bigint null;
create index FK_CODESYSVER_CS_ID on TRM_CODESYSTEM_VER (CODESYSTEM_PID);
create index FK_CODESYSVER_RES_ID on TRM_CODESYSTEM_VER (RES_ID);
alter table TRM_CODESYSTEM_VER drop key IDX_CSV_RESOURCEPID_AND_VER;
alter table TRM_CODESYSTEM_VER drop column RES_VERSION_ID;

alter table TRM_CONCEPT add CODE_SEQUENCE int null;

create table TRM_CONCEPT_DESIG
(
	PID bigint not null
		primary key,
	LANG varchar(500) null,
	USE_CODE varchar(500) null,
	USE_DISPLAY varchar(500) null,
	USE_SYSTEM varchar(500) null,
	VAL varchar(500) not null,
	CONCEPT_PID bigint null
)
engine=MyISAM;

create index FK_CONCEPTDESIG_CONCEPT on TRM_CONCEPT_DESIG (CONCEPT_PID);

create table TRM_CONCEPT_MAP
(
	PID bigint not null
		primary key,
	RES_ID bigint null,
	SOURCE_URL varchar(200) null,
	TARGET_URL varchar(200) null,
	URL varchar(200) not null,
	constraint IDX_CONCEPT_MAP_URL
		unique (URL)
)
engine=MyISAM;

create index FK_TRMCONCEPTMAP_RES on TRM_CONCEPT_MAP (RES_ID);

create table TRM_CONCEPT_MAP_GROUP
(
	PID bigint not null
		primary key,
	myConceptMapUrl varchar(255) null,
	SOURCE_URL varchar(200) not null,
	mySourceValueSet varchar(255) null,
	SOURCE_VERSION varchar(100) null,
	TARGET_URL varchar(200) not null,
	myTargetValueSet varchar(255) null,
	TARGET_VERSION varchar(100) null,
	CONCEPT_MAP_PID bigint not null
)
engine=MyISAM;

create index FK_TCMGROUP_CONCEPTMAP on TRM_CONCEPT_MAP_GROUP (CONCEPT_MAP_PID);

create table TRM_CONCEPT_MAP_GRP_ELEMENT
(
	PID bigint not null
		primary key,
	SOURCE_CODE varchar(100) not null,
	myConceptMapUrl varchar(255) null,
	SOURCE_DISPLAY varchar(400) null,
	mySystem varchar(255) null,
	mySystemVersion varchar(255) null,
	myValueSet varchar(255) null,
	CONCEPT_MAP_GROUP_PID bigint not null
)
engine=MyISAM;

create index FK_TCMGELEMENT_GROUP on TRM_CONCEPT_MAP_GRP_ELEMENT (CONCEPT_MAP_GROUP_PID);
create index IDX_CNCPT_MAP_GRP_CD on TRM_CONCEPT_MAP_GRP_ELEMENT (SOURCE_CODE);

create table TRM_CONCEPT_MAP_GRP_ELM_TGT
(
	PID bigint not null
		primary key,
	TARGET_CODE varchar(50) not null,
	myConceptMapUrl varchar(255) null,
	TARGET_DISPLAY varchar(400) null,
	TARGET_EQUIVALENCE varchar(50) null,
	mySystem varchar(255) null,
	mySystemVersion varchar(255) null,
	myValueSet varchar(255) null,
	CONCEPT_MAP_GRP_ELM_PID bigint not null
)
engine=MyISAM;

create index FK_TCMGETARGET_ELEMENT on TRM_CONCEPT_MAP_GRP_ELM_TGT (CONCEPT_MAP_GRP_ELM_PID);
create index IDX_CNCPT_MP_GRP_ELM_TGT_CD on TRM_CONCEPT_MAP_GRP_ELM_TGT (TARGET_CODE);

alter table TRM_CONCEPT_PROPERTY add PROP_CODESYSTEM varchar(500) null;
alter table TRM_CONCEPT_PROPERTY modify PROP_KEY varchar(500) not null;
alter table TRM_CONCEPT_PROPERTY add PROP_DISPLAY varchar(500) null;
alter table TRM_CONCEPT_PROPERTY modify PROP_VAL varchar(500) null;
alter table TRM_CONCEPT_PROPERTY add PROP_TYPE int not null;

