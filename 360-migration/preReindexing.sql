alter table HFJ_RES_PARAM_PRESENT add  HASH_PRESENCE bigint null;

alter table HFJ_SPIDX_COORDS add HASH_IDENTITY bigint null;
alter table HFJ_SPIDX_DATE add HASH_IDENTITY bigint null;
alter table HFJ_SPIDX_NUMBER add HASH_IDENTITY bigint null;
alter table HFJ_SPIDX_QUANTITY add HASH_IDENTITY bigint null;
alter table HFJ_SPIDX_TOKEN add HASH_IDENTITY bigint null;
alter table HFJ_SPIDX_URI add HASH_IDENTITY bigint null;
alter table HFJ_SPIDX_QUANTITY add HASH_IDENTITY_AND_UNITS bigint null;
alter table HFJ_SPIDX_QUANTITY add HASH_IDENTITY_SYS_UNITS bigint null;
alter table TRM_CONCEPT_DESIG add CS_VER_PID bigint null;
alter table TRM_CONCEPT_PROPERTY add CS_VER_PID bigint null;
alter table HFJ_SPIDX_STRING add HASH_IDENTITY bigint null;

alter table HFJ_SEARCH add SEARCH_DELETED bit null;
alter table HFJ_SEARCH add SEARCH_PARAM_MAP longblob null;
alter table HFJ_SEARCH add OPTLOCK_VERSION integer null;

alter table TRM_CONCEPT add  PARENT_PIDS longtext null; 
alter table TRM_CONCEPT add  CONCEPT_UPDATED datetime null; 

create table HFJ_RES_REINDEX_JOB (PID bigint not null, JOB_DELETED bit not null, RES_TYPE varchar(255), SUSPENDED_UNTIL datetime(6), UPDATE_THRESHOLD_HIGH datetime(6) not null, UPDATE_THRESHOLD_LOW datetime(6), primary key (PID));
CREATE TABLE SEQ_RES_REINDEX_JOB (
  `next_val` bigint(20) DEFAULT NULL
);
INSERT INTO SEQ_RES_REINDEX_JOB(next_val) VALUES (1);