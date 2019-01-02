#!/usr/bin/env bash

MYSQL_USER=$1
MYSQL_PASS=$2
ENVIRONMENT=$3
BEARER_TOKEN=$4
JASYPT_PASSWORD=$5 || ""
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
FULL_NAME="hspc_5_360"
SANDBOX_NAME="360"
echo "Running Pre-Reindexing sql scripts"
mysql --user="$MYSQL_USER" --password="$MYSQL_PASS" --database="$FULL_NAME" < preReindexing.sql
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

curl -v "$FHIR_HOST/$SANDBOX_NAME/data/\$mark-all-resources-for-reindexing" --header "Authorization: BEARER ${BEARER_TOKEN}"
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

mysql --user="$MYSQL_USER" --password="$MYSQL_PASS" --database="$FULL_NAME" < postReindexing.sql

hapi-fhir-3.6.0-cli/hapi-fhir-cli migrate-database -d MYSQL_5_7 -u "jdbc:mysql://$HOST/$FULL_NAME?serverTimezone=America/Denver" -n "$MYSQL_USER" -p "$MYSQL_PASS" -f V3_4_0 -t V3_6_0

declare -A my_dict
FOUND=0
IFS="$( echo -e '\t' )"
mysql -u$MYSQL_USER -p$MYSQL_PASS 2>/dev/null -e "SELECT * FROM $FULL_NAME.HFJ_SPIDX_STRING WHERE HASH_IDENTITY is null;" |
    while read SP_ID SP_MISSING SP_NAME RES_ID RES_TYPE SP_UPDATED SP_VALUE_EXACT SP_VALUE_NORMALIZED HASH_EXACT HASH_NORM_PREFIX HASH_IDENTITY; do

    for key in ${!my_dict[@]}; do
        if [[ ${key} == $RES_TYPE && ${my_dict[${key}]} == *"$SP_NAME,"* ]]; then
            let FOUND=1
        fi
    done

    if [[ $FOUND -eq 0 ]]; then
        my_dict[$RES_TYPE]+="$SP_NAME,"
        HASH=$(curl --silent "http://localhost:8076/$SANDBOX_NAME/sandbox/hash/$RES_TYPE,$SP_NAME" --header "Authorization: BEARER ${BEARER_TOKEN}")
        mysql -u$MYSQL_USER -p$MYSQL_PASS -e "UPDATE $FULL_NAME.HFJ_SPIDX_STRING SET HASH_IDENTITY='$HASH' WHERE RES_TYPE='$RES_TYPE' AND SP_NAME='$SP_NAME';"
    fi
    let FOUND=0
done

echo "Killing port $PORT"
kill "$(lsof -t -i:${PORT})"

