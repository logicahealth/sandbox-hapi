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

SET AUTOCOMMIT = 0;

START TRANSACTION;

-- HSPC Resource Server
DELETE FROM client_grant_type WHERE owner_id = (SELECT id from client_details where client_id = 'hspc_resource_server');
DELETE FROM client_scope WHERE owner_id = (SELECT id from client_details where client_id = 'hspc_resource_server');
DELETE FROM client_details WHERE client_id = 'hspc_resource_server';

COMMIT;

SET AUTOCOMMIT = 1;
