SET AUTOCOMMIT = 0;

START TRANSACTION;

-- HSPC Resource Server
DELETE FROM client_grant_type WHERE owner_id = (SELECT id from client_details where client_id = 'hspc_resource_server');
DELETE FROM client_scope WHERE owner_id = (SELECT id from client_details where client_id = 'hspc_resource_server');
DELETE FROM client_details WHERE client_id = 'hspc_resource_server';

COMMIT;

SET AUTOCOMMIT = 1;
