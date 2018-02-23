#!/usr/bin/env bash

profile="stu3"
sandbox_name="hspc6"

if [ $# -gt 0 ]; then
    case "$1" in
        dstu2)
            profile=$1
            sandbox_name="hspc5"
            ;;
        stu3)
            profile=$1
            sandbox_name="hspc6"
            ;;
        r4)
            profile=$1
            sandbox_name="hspc7"
            ;;
        *)
            echo "Usage: $0 {dstu2|stu3|r4} {default|multitenant}"
            exit 1
    esac
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
  -Dhspc.platform.api.sandbox.name=${sandbox_name} \
  -jar ${jar}
