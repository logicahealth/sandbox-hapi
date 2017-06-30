#!/usr/bin/env bash

echo "running..."
java \
  -Dspring.profiles.active=stu3,multitenant \
  -Xms256M \
  -Xmx512M \
  -Dserver.port=8074 \
  -jar reference-api-webapp-multitenant/target/hspc-reference-api-webapp-multitenant-*.jar
