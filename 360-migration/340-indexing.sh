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

DB_STARTS_WITH="hspc_5"

SQL_STRING="SELECT SCHEMA_NAME AS db FROM information_schema.SCHEMATA WHERE SCHEMA_NAME NOT IN ('mysql', 'information_schema') AND SCHEMA_NAME LIKE 'hspc_5%';"
# Pipe the SQL into mysql
#DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=sandman -Bs)
set -f                      # avoid globbing (expansion of *).
array=($(echo "$DBS" | tr ',' '\n'))
# Display your result
for FULL_NAME in "${array[@]}"
do
    SANDBOX_NAME=${FULL_NAME:7}
#    SANDBOX_NAME="OtherDstu3"
#    FULL_NAME="hspc_5_OtherDstu3"
	echo "$SANDBOX_NAME"
	FINISHED=0
	SQL_STRING="UPDATE $FULL_NAME.HFJ_SPIDX_QUANTITY SET SP_SYSTEM='http://unitsofmeasure.org' WHERE SP_SYSTEM IS NULL;"
#	echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs
	echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs

    SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RESOURCE WHERE SP_INDEX_STATUS IS NULL;"
#    if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" != "0" ]]; then
    if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs)" != "0" ]]; then

        # Need to make sure the resource versions are in sync
        IFS="$( echo -e '\t' )"
#        mysql -u$MYSQL_USER -p$MYSQL_PASS -e "SELECT * FROM $FULL_NAME.HFJ_RES_VER;" |
        mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -e "SELECT * FROM $FULL_NAME.HFJ_RES_VER;" |
            while read P_ID RES_DELETED_AT RES_ENCODING RES_VERSION HAS_TAGS RES_PUBLISHED RES_TEXT RES_TITLE RES_UPDATED RES_ID RES_TYPE RES_VER FORCED_ID_PID; do
            SQL_STRING="SELECT RES_VER FROM $FULL_NAME.HFJ_RESOURCE WHERE RES_ID=$RES_ID;"
#            if "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" -lt "$RES_VER" 2>/dev/null; then
            if [[ $(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs) -lt $RES_VER ]] 2>/dev/null; then
                SQL_STRING="UPDATE $FULL_NAME.HFJ_RESOURCE SET RES_VER=$RES_VER WHERE RES_ID=$RES_ID;"
#                $(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
                $(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs)
            fi
        done

        SQL_STRING="SELECT RES_VERSION FROM $FULL_NAME.HFJ_RESOURCE WHERE RES_ID=1;"
        # Pipe the SQL into mysql
#        FHIR_VERSION=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
        FHIR_VERSION=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST -Bs)
        case "${FHIR_VERSION}" in
            DSTU2)
                FHIR_VERSION="dstu2"
                ;;
            DSTU3)
                FHIR_VERSION="stu3"
                ;;
            R4)
                FHIR_VERSION="r4"
                ;;
            *)
                FHIR_VERSION="stu3"
                ;;
        esac
        ./run-fhir-server.sh $FHIR_VERSION $ENVIRONMENT $SANDBOX_NAME $JASYPT_PASSWORD
        sleep 240

        SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RESOURCE WHERE SP_INDEX_STATUS IS NULL;"
        SQL_STRING2="SELECT COUNT(*) FROM $FULL_NAME.HFJ_SPIDX_TOKEN WHERE HASH_VALUE IS NULL;"

        until [  $FINISHED -eq 1 ]; do
#            if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" == "0" ]]; then
            if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs)" == "0" && "$(echo $SQL_STRING2 | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs)" == "0" ]]; then
                let FINISHED=1
            fi
            sleep 15
        done

        ps ax | grep nameForShutdown | grep -v grep | awk '{print $1}' | xargs kill
    fi

done

