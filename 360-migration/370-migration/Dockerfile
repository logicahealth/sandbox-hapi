FROM openjdk:8-jdk-alpine
ADD hspc-reference-api-webapp-2.4.3-SNAPSHOT.jar hspc-reference-api-webapp-2.4.3-SNAPSHOT.jar
ADD migrate-hapi-370.sh migrate-hapi-370.sh
ADD all-sandboxes-to-370.sh all-sandboxes-to-370.sh
ADD run-fhir-server.sh run-fhir-server.sh
ADD preReindexing.sql preReindexing.sql
ADD postReindexing.sql postReindexing.sql
ADD hapi-fhir-3.7.0-cli hapi-fhir-3.7.0-cli
RUN chmod 755 migrate-hapi-370.sh
RUN chmod 755 run-fhir-server.sh
RUN chmod 755 all-sandboxes-to-370.sh
RUN apk add --update mysql mysql-client
RUN apk update && apk add bash && apk add curl
ENV user user
ENV pass pass
ENV environment environment
ENV bearer_token bearer_token
ENV jasypt jasypt
CMD ./all-sandboxes-to-370.sh $user $pass $environment $bearer_token $jasypt >> docker-output.log 2>&1