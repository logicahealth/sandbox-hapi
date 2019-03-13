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
INSERT INTO client_details (client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES
	('hspc_resource_server', 'secret', 'HSPC Resource Server', false, null, 3600, 600, true);

INSERT INTO client_scope (owner_id, scope) VALUES
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'openid'),
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'launch'),
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'patient/*.read'),
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'patient/*.*'),
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'user/*.*'),
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'smart/orchestrate_launch');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
	((SELECT id from client_details where client_id = 'hspc_resource_server'), 'authorization_code');

COMMIT;

SET AUTOCOMMIT = 1;
