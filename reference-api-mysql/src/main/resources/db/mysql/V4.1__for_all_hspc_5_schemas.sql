-- pick some schema for the temp table
use hspc_5_hspc6;

-- create a table of all the schema to migrate
CREATE TEMPORARY TABLE IF NOT EXISTS schema_to_migrate AS (
SELECT schema_name FROM information_schema.SCHEMATA WHERE UPPER(schema_name) LIKE 'HSPC_5_%'
);

DROP PROCEDURE IF EXISTS MIGRATE_HAPI_33_TO_HAPI_34;

DELIMITER //
CREATE PROCEDURE MIGRATE_HAPI_33_TO_HAPI_34
(IN MYSCHEMANAME VARCHAR(255))
BEGIN
  DECLARE SQLStmt TEXT;

  SELECT CONCAT('Migrating schema: ', MYSCHEMANAME, '...') as status;

  SET @SQLStmt = CONCAT('update ', MYSCHEMANAME, '.HFJ_RESOURCE set SP_INDEX_STATUS = null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_QUANTITY add HASH_UNITS_AND_VALPREFIX bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_QUANTITY add HASH_VALPREFIX bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_STRING add HASH_EXACT bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_STRING add HASH_NORM_PREFIX bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_TOKEN add HASH_SYS bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_TOKEN add HASH_SYS_AND_VALUE bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_TOKEN add HASH_VALUE bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.HFJ_SPIDX_URI add HASH_URI bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.SEQ_CNCPT_MAP_GRP_ELM_TGT_PID (next_val bigint null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.SEQ_CONCEPT_DESIG_PID (next_val bigint null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.SEQ_CONCEPT_MAP_GROUP_PID (next_val bigint null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.SEQ_CONCEPT_MAP_GRP_ELM_PID (next_val bigint null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.SEQ_CONCEPT_MAP_PID (next_val bigint null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CODESYSTEM add CS_NAME varchar(255) null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CODESYSTEM_VER add CS_VERSION_ID varchar(255) null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CODESYSTEM_VER add CODESYSTEM_PID bigint null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_CODESYSVER_CS_ID on ', MYSCHEMANAME, '.TRM_CODESYSTEM_VER (CODESYSTEM_PID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_CODESYSVER_RES_ID on ', MYSCHEMANAME, '.TRM_CODESYSTEM_VER (RES_ID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CODESYSTEM_VER drop key IDX_CSV_RESOURCEPID_AND_VER');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CODESYSTEM_VER drop column RES_VERSION_ID');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CONCEPT add CODE_SEQUENCE int null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.TRM_CONCEPT_DESIG (PID bigint not null primary key, LANG varchar(500) null, USE_CODE varchar(500) null,
	                      USE_DISPLAY varchar(500) null, USE_SYSTEM varchar(500) null, VAL varchar(500) not null, CONCEPT_PID bigint null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_CONCEPTDESIG_CONCEPT on ', MYSCHEMANAME, '.TRM_CONCEPT_DESIG (CONCEPT_PID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.TRM_CONCEPT_MAP (PID bigint not null primary key, RES_ID bigint null, SOURCE_URL varchar(200) null,
	TARGET_URL varchar(200) null, URL varchar(200) not null, constraint IDX_CONCEPT_MAP_URL unique (URL)) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_TRMCONCEPTMAP_RES on ', MYSCHEMANAME, '.TRM_CONCEPT_MAP (RES_ID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GROUP (PID bigint not null primary key, myConceptMapUrl varchar(255) null,
	SOURCE_URL varchar(200) not null, mySourceValueSet varchar(255) null, SOURCE_VERSION varchar(100) null, TARGET_URL varchar(200) not null,
	myTargetValueSet varchar(255) null, TARGET_VERSION varchar(100) null, CONCEPT_MAP_PID bigint not null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_TCMGROUP_CONCEPTMAP on ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GROUP (CONCEPT_MAP_PID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GRP_ELEMENT (PID bigint not null primary key, SOURCE_CODE varchar(100) not null,
	myConceptMapUrl varchar(255) null, SOURCE_DISPLAY varchar(400) null, mySystem varchar(255) null, mySystemVersion varchar(255) null,
	myValueSet varchar(255) null, CONCEPT_MAP_GROUP_PID bigint not null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_TCMGELEMENT_GROUP on ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GRP_ELEMENT (CONCEPT_MAP_GROUP_PID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index IDX_CNCPT_MAP_GRP_CD on ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GRP_ELEMENT (SOURCE_CODE)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create table ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GRP_ELM_TGT (PID bigint not null primary key, TARGET_CODE varchar(50) not null,
	myConceptMapUrl varchar(255) null, TARGET_DISPLAY varchar(400) null, TARGET_EQUIVALENCE varchar(50) null, mySystem varchar(255) null,
	mySystemVersion varchar(255) null, myValueSet varchar(255) null, CONCEPT_MAP_GRP_ELM_PID bigint not null) engine=MyISAM');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index FK_TCMGETARGET_ELEMENT on ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GRP_ELM_TGT (CONCEPT_MAP_GRP_ELM_PID)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('create index IDX_CNCPT_MP_GRP_ELM_TGT_CD on ', MYSCHEMANAME, '.TRM_CONCEPT_MAP_GRP_ELM_TGT (TARGET_CODE)');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CONCEPT_PROPERTY add PROP_CODESYSTEM varchar(500) null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CONCEPT_PROPERTY modify PROP_KEY varchar(500) not null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CONCEPT_PROPERTY add PROP_DISPLAY varchar(500) null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CONCEPT_PROPERTY modify PROP_VAL varchar(500) null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('alter table ', MYSCHEMANAME, '.TRM_CONCEPT_PROPERTY add PROP_TYPE int not null');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SELECT concat('Migrating schema: ', MYSCHEMANAME, ' complete');

END //
DELIMITER ;


DROP PROCEDURE IF EXISTS DO_MIGRATE_HAPI_33_TO_HAPI_34;

DELIMITER //
CREATE PROCEDURE DO_MIGRATE_HAPI_33_TO_HAPI_34() BEGIN
  DECLARE done BOOLEAN DEFAULT FALSE;
  DECLARE current_schema_name VARCHAR(255);
  DECLARE cur CURSOR FOR SELECT schema_name FROM information_schema.SCHEMATA WHERE UPPER(schema_name) LIKE 'HSPC_5_%';
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;
  DECLARE CONTINUE HANDLER FOR SQLSTATE '42S22' BEGIN END;
  DECLARE CONTINUE HANDLER FOR SQLSTATE '42000' BEGIN END;

  OPEN cur;

  testLoop: LOOP
    FETCH cur INTO current_schema_name;
    IF done THEN
      LEAVE testLoop;
    END IF;
    CALL MIGRATE_HAPI_33_TO_HAPI_34(current_schema_name);
  END LOOP testLoop;

  CLOSE cur;
END //
DELIMITER ;

CALL DO_MIGRATE_HAPI_33_TO_HAPI_34();