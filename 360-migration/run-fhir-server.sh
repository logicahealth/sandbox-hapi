#!/usr/bin/env bash

profile="dstu2"
sandbox_name="hspc5"
profile=$1
environment=$2
sandbox_name=$3
jasypt_password=$4

echo "using $1 profile..."

jar="../reference-api-webapp/target/hspc-reference-api-webapp-*.jar"

set -x

echo "running..."
#-Djasypt.encryptor.password=${jasypt_password} \
java \
  -Dspring.profiles.active=${profile},multitenant,${environment} \
  -Xms256M \
  -Xmx512M \
  -Dhspc.platform.api.sandbox.name=${sandbox_name} \
  -jar ${jar} &> output.log &
