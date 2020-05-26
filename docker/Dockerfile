FROM openjdk:11.0.7-jdk-slim

# mapped to host for filesystem mounting
VOLUME /tmp

ADD target/hspc-reference-api-webapp*.jar app.jar

#RUN apk add --update mysql mysql-client

ENV JAVA_OPTS="-Xmx1536m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]
