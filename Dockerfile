FROM openjdk:8-jdk-alpine
ADD reference-api-webapp-multitenant/target/hspc-reference-api-webapp-multitenant*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]