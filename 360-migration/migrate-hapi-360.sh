#!/usr/bin/env bash

MYSQL_USER=$1
MYSQL_PASS=$2
ENVIRONMENT=$3
JASYPT_PASSWORD=$4 || ""
HOST="127.0.0.1:3306"

case "${ENVIRONMENT}" in
    local)
        HOST="127.0.0.1:3306"
        ;;
    test)
        HOST="sandboxdb-test.hspconsortium.org"
        ;;
    prod)
        HOST="sandboxdb.hspconsortium.org"
        ;;
esac

#DB_STARTS_WITH="hspc_5"
#
#SQL_STRING="SELECT SCHEMA_NAME AS db FROM information_schema.SCHEMATA WHERE SCHEMA_NAME NOT IN ('mysql', 'information_schema') AND SCHEMA_NAME LIKE 'hspc_5%';"
## Pipe the SQL into mysql
#DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
#set -f                      # avoid globbing (expansion of *).
#array=($(echo "$DBS" | tr ',' '\n'))
## Display your result
#for FULL_NAME in "${array[@]}"
#do
#    SANDBOX_NAME=${FULL_NAME:7}
#	echo "$SANDBOX_NAME"
#done
FULL_NAME="hspc_5_3606"
SANDBOX_NAME="3606"
echo "Running Pre-Reindexing sql scripts"
mysql --user="$MYSQL_USER" --password="$MYSQL_PASS" --database="$FULL_NAME" < preReindexing.sql

hapi-fhir-3.6.0-cli/hapi-fhir-cli migrate-database -d MYSQL_5_7 -u "jdbc:mysql://$HOST/$FULL_NAME?serverTimezone=America/Denver" -n "$MYSQL_USER" -p "$MYSQL_PASS" -f V3_4_0 -t V3_6_0 -x no-migrate-350-hashes

fhirVersion="stu3"
PORT="8075"
case "${fhirVersion}" in
    dstu2)
        PORT="8075"
        ;;
    stu3)
        PORT="8076"
        ;;
    r4)
        PORT="8077"
        ;;
esac
FHIR_HOST="http://127.0.0.1:$PORT"
case "${ENVIRONMENT}" in
    local)
        FHIR_HOST="http://127.0.0.1:$PORT"
        ;;
    test)
        FHIR_HOST="https://api-v5-$fhirVersion-test.hspconsortium.org"
        ;;
    prod)
        FHIR_HOST="https://api-v5-$fhirVersion.hspconsortium.org"
        ;;
esac

echo "Killing port $PORT if already running."

if [[ ! -z "$(lsof -t -i:$PORT)" ]]; then
        kill "$(lsof -t -i:${PORT})"
    fi

./run-fhir-server.sh ${fhirVersion} $ENVIRONMENT $SANDBOX_NAME $JASYPT_PASSWORD

STARTED=0
sleep 20

OUTPUT=""
until [  $STARTED -eq 1 ]; do
    if [[ ! -z "$(lsof -t -i:$PORT)" ]]; then
        let STARTED=1
        echo "$(lsof -t -i:$PORT)"
    fi
    sleep 3
done

echo "Running server on port $PORT."

echo "$(lsof -t -i:$PORT)"
echo "$FHIR_HOST/$SANDBOX_NAME/open/\$mark-all-resources-for-reindexing"

curl -v "$FHIR_HOST/$SANDBOX_NAME/open/\$mark-all-resources-for-reindexing"
sleep 30

STARTED=0
SQL_STRING="SELECT SP_INDEX_STATUS FROM $FULL_NAME.HFJ_RESOURCE WHERE RES_ID = (SELECT MAX(RES_ID) FROM $FULL_NAME.HFJ_RESOURCE);"

until [  $STARTED -eq 1 ]; do
    if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" != "NULL" ]]; then
        let STARTED=1
    fi
    echo "Attempting..."
    sleep 5
done

echo "Killing port $PORT"
kill "$(lsof -t -i:${PORT})"

mysql --user="$MYSQL_USER" --password="$MYSQL_PASS" --database="$FULL_NAME" < postReindexing.sql

hapi-fhir-3.6.0-cli/hapi-fhir-cli migrate-database -d MYSQL_5_7 -u "jdbc:mysql://$HOST/$FULL_NAME?serverTimezone=America/Denver" -n "$MYSQL_USER" -p "$MYSQL_PASS" -f V3_4_0 -t V3_6_0

