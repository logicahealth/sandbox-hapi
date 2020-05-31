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

-- pick some schema for the temp table
use hspc_5_hspc6;

-- create a table of all the schema to migrate
CREATE TEMPORARY TABLE IF NOT EXISTS schema_to_migrate AS (
SELECT schema_name FROM information_schema.SCHEMATA WHERE UPPER(schema_name) LIKE 'HSPC_5_%'
);

DROP PROCEDURE IF EXISTS FIX_CONCEPT_MAP;

DELIMITER //
CREATE PROCEDURE FIX_CONCEPT_MAP
(IN MYSCHEMANAME VARCHAR(255))
BEGIN
  SELECT CONCAT('Migrating schema: ', MYSCHEMANAME, '...') as status;
  CALL add_pid(MYSCHEMANAME);
END //
DELIMITER ;


DROP PROCEDURE IF EXISTS DO_FIX_CONCEPT_MAP;

DELIMITER //
CREATE PROCEDURE DO_FIX_CONCEPT_MAP() BEGIN
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
    CALL FIX_CONCEPT_MAP(current_schema_name);
  END LOOP testLoop;

  CLOSE cur;
END //
DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS add_pid $$
CREATE PROCEDURE add_pid( in MYSCHEMANAME varchar(256) )
BEGIN
SET @s = CONCAT('SELECT COUNT(*) FROM ' , MYSCHEMANAME, '.SEQ_CONCEPT_MAP_PID into @outvar');
PREPARE stmt FROM @s;
set @invar = 1;
execute stmt;
DEALLOCATE PREPARE stmt;
 IF(@outvar = '0') THEN
   SET @s = CONCAT('INSERT INTO ' , MYSCHEMANAME, '.SEQ_CONCEPT_MAP_PID(next_val) VALUES (1)');
   PREPARE stmt FROM @s;
   EXECUTE stmt;
   DEALLOCATE PREPARE Stmt;
 END IF;
 SET @s = CONCAT('SELECT COUNT(*) FROM ' , MYSCHEMANAME, '.SEQ_CONCEPT_MAP_GROUP_PID into @outvar');
PREPARE stmt FROM @s;
set @invar = 1;
execute stmt;
DEALLOCATE PREPARE stmt;
 IF(@outvar = '0') THEN
   SET @s = CONCAT('INSERT INTO ' , MYSCHEMANAME, '.SEQ_CONCEPT_MAP_GROUP_PID(next_val) VALUES (1)');
   PREPARE stmt FROM @s;
   EXECUTE stmt;
   DEALLOCATE PREPARE Stmt;
 END IF;
 SET @s = CONCAT('SELECT COUNT(*) FROM ' , MYSCHEMANAME, '.SEQ_CONCEPT_MAP_GRP_ELM_PID into @outvar');
PREPARE stmt FROM @s;
set @invar = 1;
execute stmt;
DEALLOCATE PREPARE stmt;
 IF(@outvar = '0') THEN
   SET @s = CONCAT('INSERT INTO ' , MYSCHEMANAME, '.SEQ_CONCEPT_MAP_GRP_ELM_PID(next_val) VALUES (1)');
   PREPARE stmt FROM @s;
   EXECUTE stmt;
   DEALLOCATE PREPARE Stmt;
 END IF;
 SET @s = CONCAT('SELECT COUNT(*) FROM ' , MYSCHEMANAME, '.SEQ_CNCPT_MAP_GRP_ELM_TGT_PID into @outvar');
PREPARE stmt FROM @s;
set @invar = 1;
execute stmt;
DEALLOCATE PREPARE stmt;
 IF(@outvar = '0') THEN
   SET @s = CONCAT('INSERT INTO ' , MYSCHEMANAME, '.SEQ_CNCPT_MAP_GRP_ELM_TGT_PID(next_val) VALUES (1)');
   PREPARE stmt FROM @s;
   EXECUTE stmt;
   DEALLOCATE PREPARE Stmt;
 END IF;
END $$
DELIMITER ;

INSERT INTO SEQ_CONCEPT_MAP_PID(next_val) VALUES (1);
INSERT INTO SEQ_CONCEPT_MAP_GROUP_PID(next_val) VALUES (1);
INSERT INTO SEQ_CONCEPT_MAP_GRP_ELM_PID(next_val) VALUES (1);
INSERT INTO SEQ_CNCPT_MAP_GRP_ELM_TGT_PID(next_val) VALUES (1);

CALL DO_FIX_CONCEPT_MAP();



