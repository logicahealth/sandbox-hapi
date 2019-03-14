FROM openjdk:8-jdk-alpine
ADD hspc-reference-api-webapp*.jar hspc-reference-api-webapp-2.4.2-SNAPSHOT.jar
ADD 340-indexing.sh 340-indexing.sh
ADD run-fhir-server.sh run-fhir-server.sh
RUN chmod 755 340-indexing.sh
RUN chmod 755 run-fhir-server.sh
RUN apk add --update mysql mysql-client
RUN apk update && apk add bash
ENV user user
ENV pass pass
ENV environment environment
ENV jasypt jasypt
CMD ./340-indexing.sh $user $pass $environment $jasypt >> docker-output.log 2>&1