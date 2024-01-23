FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get -y dist-upgrade
RUN apt-get install -y maven default-mysql-client

RUN mkdir reference-api-webapp
COPY pom.xml pom.xml
COPY reference-api-webapp/pom.xml reference-api-webapp/pom.xml
RUN mvn verify --fail-never

COPY . .
RUN mvn package
# ADD reference-api-webapp/target/hspc-reference-api-webapp-*.jar app.jar
RUN mv reference-api-webapp/target/hspc-reference-api-webapp-*.jar app.jar
RUN rm -rf reference-api-webapp
ENV JAVA_OPTS="-Xmx1536m -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]