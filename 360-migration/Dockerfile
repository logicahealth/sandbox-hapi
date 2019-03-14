FROM openjdk:8-jdk-alpine
ADD hspc-reference-api-webapp*.jar hspc-reference-api-webapp-2.4.2-SNAPSHOT.jar
ADD all_8_sandboxes_reindexed.sh all_8_sandboxes_reindexed.sh
ADD run-fhir-server.sh run-fhir-server.sh
RUN chmod 755 all_8_sandboxes_reindexed.sh
RUN chmod 755 run-fhir-server.sh
RUN apk add --update mysql mysql-client
RUN apk update && apk add bash
ENV user user
ENV pass pass
ENV environment environment
ENV jasypt jasypt
CMD ./all_8_sandboxes_reindexed.sh $user $pass $environment $jasypt >> docker-output.log 2>&1