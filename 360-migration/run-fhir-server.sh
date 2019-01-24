#!/usr/bin/env bash

profile="dstu2"
sandbox_name="hspc5"
profile=$1
environment=$2
sandbox_name=$3
jasypt_password=$4

jar="hspc-reference-api-webapp-*.jar"

set -x

java \
  -Dspring.profiles.active=${profile},multitenant,${environment} \
  -Xms256M \
  -Xmx512M \
  -DnameForShutdown=1 \
  -Dhspc.platform.api.sandbox.name=${sandbox_name} \
  -Djasypt.encryptor.password=${jasypt_password} \
  -jar ${jar} &> output.log &
