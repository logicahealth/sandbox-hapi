#!/usr/bin/env bash

echo "running..."
java \
  -Dspring.profiles.active=r4,multitenant \
  -Xms256M \
  -Xmx512M \
  -Dserver.port=8071 \
  -jar reference-api-webapp-multitenant/target/hspc-reference-api-webapp-multitenant-*.jar
