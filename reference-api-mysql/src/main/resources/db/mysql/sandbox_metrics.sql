--
--  * #%L
--  *
--  * %%
--  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
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

-- determine the size (MB)
SELECT sub.table_schema, sub.table_schema_size_mb, SUBSTRING(sub.table_schema, 6 + POSITION('_' IN SUBSTRING(sub.table_schema, 6))) AS "sandbox_name"
FROM (
  SELECT table_schema AS "table_schema",
  (SUM(data_length + index_length) / 1024 / 1024) AS "table_schema_size_mb"
  FROM information_schema.TABLES
  WHERE UPPER(table_schema) like 'HSPC_%'
  GROUP BY table_schema
) AS sub
ORDER BY sub.table_schema_size_mb;

-- include the creator, high usage
SELECT sub_metadata.user_name, sub_metadata.email, sub_metadata.name, sub_data.sandbox, sub_data.size
FROM (
  SELECT SUBSTRING(table_schema, 8) AS "sandbox",
  ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "size"
  FROM information_schema.TABLES
  WHERE UPPER(table_schema) like 'HSPC_%'
  GROUP BY table_schema
  HAVING ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) > 300
) as sub_data
LEFT OUTER JOIN
(
  SELECT s.id, s.name, s.sandbox_id, s.created_by_id, u.name as 'user_name', u.email
  FROM sandbox s JOIN user u ON (s.created_by_id = u.id)
) as sub_metadata
ON UPPER(sub_data.sandbox)=UPPER(sub_metadata.sandbox_id)
;


-- determine the aggregates
SELECT MIN(sub.size) min_size, AVG(sub.size) avg_size, MAX(sub.size) max_size
FROM (
  SELECT table_schema AS "database",
  ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "size"
  FROM information_schema.TABLES
  WHERE UPPER(table_schema) like 'HSPC_5%'
  GROUP BY table_schema
) as sub;