# HSPC Reference API Parent

Welcome to the HSPC Reference API!  The HSPC Reference API server contains a FHIR resource server.  The project is a composition of servers and libraries that are available in this repository.

# HSPC Sandbox

*Note:* If you are wanting to build and test SMART on FHIR Apps, it is recommended that you use the free cloud-hosted version of the HSPC Sandbox.

[HSPC Sandbox](https://sandbox.hspconsortium.org)

# Servers

## reference-api-webapp-multitenant
A deployable multitenant, web application that includes configuration of a FHIR server (reference-api-fhir, reference-api-mysql) for OAuth2 (reference-api-oauth2) and SMART launch (reference-api-smart-support).  The reference-api-webapp may be used as an example for a custom HSPC FHIR Resource server.

## reference-api-webapp
A example of a deployable web application that includes configuration of a FHIR server (reference-api-fhir, reference-api-mysql) for OAuth2 (reference-api-oauth2) and SMART launch (reference-api-smart-support).  The reference-api-webapp may be used as an example for a custom HSPC FHIR Resource server.

# Default Ports
The following default port assignments exist

| Port        | Version             | Schema Version |
|------------:| ------------------- | -------------- |
| 8070        | Current Dev Version | ?              |
| 8071        | DSTU2 1.0.2         | HSPC_1         |
| 8072        | STU3 1.6            | HSPC_2         |
| 8073        | STU3 1.8            | HSPC_3         |
| 8074        | STU3 3.0.1          | HSPC_4         |

# Libraries

## reference-api-smart-support
The reference-api-smart-support library adds SMART launch endpoints to a FHIR resource server conformance statement.

## reference-api-oauth2
The reference-api-oauth2 library configures OAuth2/OpenID Connect security for a FHIR resource server.

## reference-api-mysql
The reference-api-mysql library configures a MySQL FHIR resource repository to be used by the reference-api-fhir library.

## reference-api-fhir
The reference-api-fhir-library is an extension of [HAPI FHIR](http://jamesagnew.github.io/hapi-fhir/) that includes support for SMART launch.

## How do I get set up? ##

### Preconditions ###
    For secured configuration, the reference-api server must register a client with the reference-authorization server.
    From MySQL
    mysql> use oic;
    mysql> source {install path}/reference-api-mysql/src/main/resources/db/openidconnect/mysql/resource-server-client.sql;
    * note this script is included with the complete installation of the reference-impl (optional)

### Build and Run ###
    mvn clean install
    java -jar reference-api-webapp-multitenant/target/hspc-reference-api-webapp-multitenant-*.war

### Verify ###
* http://localhost:8070/hspc4/data/metadata

## Open Mode ##
When the HSPC Reference API server is run in open mode, no security is applied.  This is very convenient for development, allowing resources to be read and written without authentication.  See reference-api-webapp/src/main/resources/application.yml.
* hsp.platform.api.security.mode=open

### Sample Operations ###
* http://localhost:8070/hspc4/open/Patient
* http://localhost:8070/hspc4/open/Observation

## Secured Mode ##
When the HSPC Reference API server is run in secured mode, authentication is required for most endpoints with the exception of the conformance statement.  See reference-api-webapp/src/main/resources/application.yml.
* hsp.platform.api.security.mode=secured

## Configuration ##

See reference-api-webapp/src/main/resources/application.yml for an initial listing of properties that may be overridden.

## Where to go from here ##
https://healthservices.atlassian.net/wiki/display/HSPC/Healthcare+Services+Platform+Consortium