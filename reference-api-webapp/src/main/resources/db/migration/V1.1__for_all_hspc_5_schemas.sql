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

DROP PROCEDURE MIGRATE_HAPI_32_TO_HAPI_33;

DELIMITER //
CREATE PROCEDURE MIGRATE_HAPI_32_TO_HAPI_33
(IN MYSCHEMANAME VARCHAR(255))
BEGIN
  DECLARE SQLStmt TEXT;

  SELECT CONCAT('Migrating schema: ', MYSCHEMANAME, '...') as status;

  SET @SQLStmt = CONCAT('ALTER TABLE ', MYSCHEMANAME, '.HFJ_RESOURCE DROP COLUMN RES_TEXT');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('ALTER TABLE ', MYSCHEMANAME, '.HFJ_RESOURCE DROP COLUMN RES_ENCODING');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('ALTER TABLE ', MYSCHEMANAME, '.HFJ_RES_VER MODIFY RES_ENCODING VARCHAR(5) NULL');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  SET @SQLStmt = CONCAT('ALTER TABLE ', MYSCHEMANAME, '.HFJ_RES_VER MODIFY COLUMN RES_TEXT LONGBLOB NULL');
  PREPARE Stmt FROM @SQLStmt;
  EXECUTE Stmt;
  DEALLOCATE PREPARE Stmt;

  CREATE TABLE TEMP_IDS
  SELECT RES.RES_ID, SUB.MAX_RES_DELETED_AT_VER AS RES_DELETED_AT
  FROM HFJ_RESOURCE AS RES
  INNER JOIN
  (
    SELECT R.RES_ID, R.RES_DELETED_AT, RV.RES_ID AS RES_ID_VER, RV.MAX_RES_DELETED_AT AS MAX_RES_DELETED_AT_VER
    FROM
      (SELECT RES_ID, MAX(RES_DELETED_AT) AS MAX_RES_DELETED_AT FROM HFJ_RES_VER WHERE RES_DELETED_AT IS NOT NULL GROUP BY RES_ID) AS RV
      LEFT OUTER JOIN
      HFJ_RESOURCE AS R ON RV.RES_ID=R.RES_ID
    WHERE RV.MAX_RES_DELETED_AT IS NOT NULL AND R.RES_DELETED_AT IS NULL
  ) SUB
  ON RES.RES_ID=SUB.RES_ID
  WHERE RES.RES_DELETED_AT IS NULL;

  UPDATE HFJ_RESOURCE AS R INNER JOIN TEMP_IDS AS T ON R.RES_ID=T.RES_ID
  SET R.RES_DELETED_AT=T.RES_DELETED_AT;

  COMMIT;

  DROP TABLE TEMP_IDS;

  SELECT concat('Migrating schema: ', MYSCHEMANAME, ' complete');

END //
DELIMITER ;


DROP PROCEDURE DO_MIGRATE_HAPI_32_TO_HAPI_33;

DELIMITER //
CREATE PROCEDURE DO_MIGRATE_HAPI_32_TO_HAPI_33() BEGIN
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
    CALL MIGRATE_HAPI_32_TO_HAPI_33(current_schema_name);
  END LOOP testLoop;

  CLOSE cur;
END //
DELIMITER ;

CALL DO_MIGRATE_HAPI_32_TO_HAPI_33();