FROM openjdk:11.0.7-jdk-slim
ADD reference-api-webapp/target/hspc-reference-api-webapp-*-SNAPSHOT.jar app.jar
#RUN apk add --update mysql mysql-client
ENV JAVA_OPTS="-Xmx1536m -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]