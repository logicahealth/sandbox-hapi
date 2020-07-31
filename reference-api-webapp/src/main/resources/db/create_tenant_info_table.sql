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

DROP TABLE hspc_tenant_info;

CREATE TABLE hspc_tenant_info (
    tenant_id VARCHAR(255) NOT NULL PRIMARY KEY,
    hspc_schema_version VARCHAR(10) NOT NULL,
    allow_open_endpoint VARCHAR(1) NOT NULL,
    baseline_date DATE NULL
);

INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
VALUES (?, '8', 'F');

*** Notes ***
To roll this out, create a temp table that has the following columns:
schema, team id, hspc_schema_version

Use a query similar to this to populate the table.
select
	CONCAT(
		CONCAT('INSERT INTO ', sub1.table_schema),
		'.hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
        VALUES (?, 8,
		hspc_schema_version VARCHAR(10) NOT NULL,
		allow_open_endpoint VARCHAR(1) NOT NULL
		);')
from
(select distinct table_schema from tables where table_schema like 'hapi_%' or table_schema like 'hspc_%') sub1;

-- add baseline date
alter table hspc_tenant_info add baseline_date DATE;
