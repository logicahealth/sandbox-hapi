#!/usr/bin/env bash
#
#  * #%L
#  *
#  * %%
#  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
#  * %%
#  * Licensed under the Apache License, Version 2.0 (the "License");
#  * you may not use this file except in compliance with the License.
#  * You may obtain a copy of the License at
#  *
#  *      http://www.apache.org/licenses/LICENSE-2.0
#  *
#  * Unless required by applicable law or agreed to in writing, software
#  * distributed under the License is distributed on an "AS IS" BASIS,
#  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  * See the License for the specific language governing permissions and
#  * limitations under the License.
#  * #L%
#


MYSQL_USER=$1
MYSQL_PASS=$2
ENVIRONMENT=$3
BEARER_TOKEN=$4
JASYPT_PASSWORD=$5 || ""

case "${ENVIRONMENT}" in
    local)
        HOST="127.0.0.1"
        ;;
    test)
        HOST="sandboxdb-test.hspconsortium.org"
        ;;
    prod)
        HOST="sandboxdb.hspconsortium.org"
        ;;
esac

SQL_STRING="SELECT SCHEMA_NAME AS db FROM information_schema.SCHEMATA WHERE SCHEMA_NAME NOT IN ('mysql', 'information_schema') AND SCHEMA_NAME LIKE 'hspc_8%';"
# Pipe the SQL into mysql
#DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=sandman -Bs)
set -f                      # avoid globbing (expansion of *).
array=($(echo "$DBS" | tr ',' '\n'))
# Display your result
for FULL_NAME in "${array[@]}"
do
    SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RESOURCE;"
    if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs)" != "0" ]]; then
#    if [[ $FULL_NAME == "hspc_8_TwoUsers" ]]; then
        SQL_STRING="UPDATE $FULL_NAME.HFJ_RESOURCE SET SP_INDEX_STATUS = NULL"
        echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

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
       SANDBOX_NAME=${FULL_NAME:7}

       case "$FHIR_VERSION" in
            dstu2)
                PORT="8078"
                INDEX="8"
                ;;
            stu3)
                PORT="8079"
                INDEX="9"
                ;;
            r4)
                PORT="8070"
                INDEX="10"
                ;;
        esac

        FHIR_HOST="http://127.0.0.1:$PORT"

        case "${ENVIRONMENT}" in
            local)
                FHIR_HOST="http://127.0.0.1:$PORT"
                ;;
            test)
                FHIR_HOST="https://api-v8-$FHIR_VERSION-test.hspconsortium.org"
                ;;
            prod)
                FHIR_HOST="https://api-v8-$FHIR_VERSION.hspconsortium.org"
                ;;
        esac

       ./run-fhir-server.sh $FHIR_VERSION $ENVIRONMENT $SANDBOX_NAME $JASYPT_PASSWORD

       sleep 60
        if [[ ! -z "$(lsof -t -i:$PORT)" ]]; then
            let STARTED=1
        else
            ps ax | grep nameForShutdown | grep -v grep | awk '{print $1}' | xargs kill
            ./run-fhir-server.sh $FHIR_VERSION $ENVIRONMENT $SANDBOX_NAME $JASYPT_PASSWORD
        fi


        echo "Running server on port $PORT."

        echo "curl --header \"Authorization: BEARER ${BEARER_TOKEN}\" \"http://localhost:$PORT/$SANDBOX_NAME/data/\$mark-all-resources-for-reindexing\""
        STARTED=0
        until [  $STARTED -eq 1 ]; do
            if [[ "$(curl -X GET --header "Authorization: BEARER $BEARER_TOKEN" "$FHIR_HOST/$SANDBOX_NAME/data/\$mark-all-resources-for-reindexing")" != *"NullPointerException"* ]]; then
                let STARTED=1
                echo "Successful reindexing connection!"
            fi
            sleep 1
        done
        FINISHED=0
       SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RESOURCE WHERE SP_INDEX_STATUS IS NULL"
       until [  $FINISHED -eq 1 ]; do
    #            if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" == "0" ]]; then
            if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 --database=$FULL_NAME -Bs)" == "0" ]]; then
                let FINISHED=1
            fi
            sleep 15
        done

        ps ax | grep nameForShutdown | grep -v grep | awk '{print $1}' | xargs kill
    fi

done