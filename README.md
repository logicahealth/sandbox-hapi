# Logica Reference API Parent

Welcome to the Logica Reference API!  The Logica Reference API server contains a FHIR resource server.  The project is a composition of servers and libraries that are available in this repository.

# Logica Sandbox

*Note:* If you are wanting to build and test SMART on FHIR Apps, it is recommended that you use the free cloud-hosted version of the Logica Sandbox.

[Logica Sandbox](https://sandbox.logicahealth.org)

# Servers

## reference-api-webapp
A deployable multitenant web application.  The reference-api-webapp may be used as an example for a custom Logica FHIR Resource server.

# Default Ports
The following default port assignments exist

| Port        | Version             | Schema Version | Status |
|------------:| ------------------- | -------------- | ------ |
| 8070        | Current Dev Version | ?              | legacy |
| 8071        | DSTU2 1.0.2         | HSPC_1         | legacy |
| 8072        | STU3 1.6            | HSPC_2         | legacy |
| 8073        | STU3 1.8            | HSPC_3         | legacy |
| 8074        | STU3 3.0.1          | HSPC_4         | legacy |
| 8075        | DSTU2               | HSPC_5         | legacy |
| 8076        | STU3                | HSPC_5         | legacy |
| 8077        | R4                  | HSPC_5         | legacy |
| 8078        | DSTU2               | HSPC_8         | active |
| 8079        | STU3                | HSPC_8         | active |
| 8070        | R4                  | HSPC_8         | active |


### How do I set up?
This project uses Java 11.  Please make sure that your Project SDK is set to use Java 11.

#### Step 1: Preconditions
1. For secured configuration, the reference-api server must register a client with the reference-authorization server.  This can only be done after setting up the reference-auth server with it's "oic" schema. 
   From MySQL
    
        mysql> use oic;
        mysql> source {install path}/reference-api-webapp/src/main/resources/db/openidconnect/mysql/resource-server-client.sql;
    
    * note this script is included with the complete installation of the reference-impl (optional)

2. Create the following schemas hspc_8_hspc8, hspc_8_hspc9, hspc_8_hspc10 and then run the SQL script reference-api-webapp/src/main/resources/db/empty_schema.sql.
3. Create the following schemas hspc_8_MasterDstu2Empty, hspc_8_MasterR4Empty, hspc_8_MasterStu3Empty and then run the SQL script reference-api-webapp/src/main/resources/db/empty_schema.sql.
    Then edit the reference-api-webapp/src/main/resources/db/create_tenant_info_table.sql to insert INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
                                                                                                    VALUES (MasterDstu2Empty, '8', 'F');
    Then edit the reference-api-webapp/src/main/resources/db/create_tenant_info_table.sql to insert INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
                                                                                                    VALUES (MasterR4Empty, '8', 'F'); 
    Then edit the reference-api-webapp/src/main/resources/db/create_tenant_info_table.sql to insert INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
                                                                                                     VALUES (MasterStu3Empty, '8', 'F');                                                
4. Create the following schema hspc_8_MasterDstu2Smart and then run the SQL script reference-api-webapp/src/main/resources/db/mysql/hspc_8_dstu2_default_dataset.sql
    Then edit the reference-api-webapp/src/main/resources/db/create_tenant_info_table.sql to insert INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
                                                                                                    VALUES (MasterDstu2Smart, '8', 'F');
5. Create the following schema hspc_8_MasterR4Smart and then run the SQL script reference-api-webapp/src/main/resources/db/mysql/hspc_8_r4_default_dataset.sql
    Then edit the reference-api-webapp/src/main/resources/db/create_tenant_info_table.sql to insert INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
                                                                                                    VALUES (MasterR4Smart, '8', 'F');
6. Create the following schema hspc_8_MasterStu3Smart and then run the SQL script reference-api-webapp/src/main/resources/db/mysql/hspc_8_stu3_default_dataset.sql
    Then edit the reference-api-webapp/src/main/resources/db/create_tenant_info_table.sql to insert INSERT INTO hspc_tenant_info (tenant_id, hspc_schema_version, allow_open_endpoint)
                                                                                                    VALUES (MasterStu3Smart, '8', 'F');
7. After the above schemas are created, run the appropriate HAPI migration script on the all the schemas reference-api-webapp/src/main/resources/db/migration  
#### Step 2: Maven Build
In the terminal, run the following command:

    mvn package
    
#### Step 3: Run locally or Run on docker
###### Local installation

    ./run-local.sh

###### OR Docker Installation

Run the following commands one line at a time:

    cd docker/nginx
    ./build.sh
    cd ..
    ./build.sh
    docker-compose up
  
The set up process is complete and your project is running now. 
The service is available at (see default ports): 
    http://localhost:8078/
    
#### Datatbase Migration (optional)
If you wish to migrate sandboxes from previous HAPI versions, see /reference-api-webapp/src/main/resources/db/migration for the appropriate migration scripts.

## Open Mode ##
When the Logica Reference API server is run in open mode, no security is applied.  This is very convenient for development, allowing resources to be read and written without authentication.  See reference-api-webapp/src/main/resources/application.yml.
* hspc.platform.api.security.mode=open

### Sample Operations ###
* http://localhost:8078/hspc8/open/Patient
* http://localhost:8078/hspc8/open/Observation

## Secured Mode ##
When the Logica Reference API server is run in secured mode, authentication is required for most endpoints with the exception of the conformance statement.  See reference-api-webapp/src/main/resources/application.yml.
* hspc.platform.api.security.mode=secured

## Configuration ##

See reference-api-webapp/src/main/resources/application.yml for an initial listing of properties that may be overridden.

## Where to go from here ##
https://logica.atlassian.net/wiki/spaces/HSPC/overview