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
