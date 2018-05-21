FROM openjdk:8-jdk-alpine
ADD reference-api-webapp/target/hspc-reference-api-webapp*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]