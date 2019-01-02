#!/usr/bin/env bash

MYSQL_USER=$1
MYSQL_PASS=$2
ENVIRONMENT=$3
JASYPT_PASSWORD=$4 || ""

DB_STARTS_WITH="hspc_5"

SQL_STRING="SELECT SCHEMA_NAME AS db FROM information_schema.SCHEMATA WHERE SCHEMA_NAME NOT IN ('mysql', 'information_schema') AND SCHEMA_NAME LIKE 'hspc_5%';"
# Pipe the SQL into mysql
DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
set -f                      # avoid globbing (expansion of *).
array=($(echo "$DBS" | tr ',' '\n'))
# Display your result
for FULL_NAME in "${array[@]}"
do
    SANDBOX_NAME=${FULL_NAME:7}
	echo "$SANDBOX_NAME"
	STARTED=0
    SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_SPIDX_TOKEN WHERE HASH_VALUE IS NULL;"
    if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" != "0" ]]; then
        SQL_STRING="SELECT RES_VERSION FROM $FULL_NAME.HFJ_RESOURCE WHERE RES_ID=1;"
        # Pipe the SQL into mysql
        FHIR_VERSION=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
        case "${FHIR_VERSION}" in
            DSTU2)
                FHIR_VERSION="dstu2"
                ;;
            DSTU3)
                FHIR_VERSION="stu3"
                ;;
            R4) # TODO: check this
                FHIR_VERSION="r4"
                ;;
            *)
                FHIR_VERSION="stu3"
                ;;
        esac
        echo $FHIR_VERSION
        ./run-fhir-server.sh $FHIR_VERSION $ENVIRONMENT $SANDBOX_NAME $JASYPT_PASSWORD
        sleep 30

        SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_SPIDX_TOKEN WHERE HASH VALUE IS NULL;"

        until [  $STARTED -eq 1 ]; do
            if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" == "0" ]]; then
                let STARTED=1
            fi
            echo "Attempting..."
            sleep 5
        done

        echo "Killing port $PORT"
        kill "$(lsof -t -i:${PORT})"
        STARTED=0
    fi

done