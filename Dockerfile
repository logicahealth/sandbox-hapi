FROM openjdk:11-jdk
ADD reference-api-webapp/target/hspc-reference-api-webapp*.jar app.jar
RUN apk add --update mysql mysql-client
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]