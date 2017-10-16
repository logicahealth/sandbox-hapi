#!/usr/bin/env bash

echo "running..."
java \
  -Dspring.profiles.active=dstu2,multitenant \
  -Xms256M \
  -Xmx512M \
  -Dserver.port=8071 \
  -Dhspc.platform.api.sandbox.name=hspc \
  -jar reference-api-webapp-multitenant/target/hspc-reference-api-webapp-multitenant-*.jar
