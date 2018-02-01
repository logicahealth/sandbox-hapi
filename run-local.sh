#!/usr/bin/env bash

profile="stu3"

if [ $# -gt 0 ]; then
    if [ "$1" == "dstu2" ] || [ "$1" == "stu3" ] || [ "$1" == "r4" ]; then
        profile=$1
    else
        echo "Invalid argument: $profile.  Use \"dstu2\", \"stu3\", or \"r4\""
        exit 1;
    fi
fi

echo "using $profile profile..."

if [ $# -gt 1 ]; then

    tenant=$2
    jar="reference-api-webapp/target/hspc-reference-api-webapp-*.jar"
else
    tenant="multitenant"
    jar="reference-api-webapp-multitenant/target/hspc-reference-api-webapp-multitenant-*.jar"
fi

set -x

echo "running..."
java \
  -Dspring.profiles.active=${profile},${tenant} \
  -Xms256M \
  -Xmx512M \
  -Dhspc.platform.api.sandbox.name=${profile} \
  -jar ${jar}
