#!/usr/bin/env bash
#
#  * #%L
#  *
#  * %%
#  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
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
FULL_NAME=$4
FHIR_VERSION=$5
BEARER_TOKEN=$6
JASYPT_PASSWORD=$7 || ""
MYSQL_URL="127.0.0.1:3306"

case "${ENVIRONMENT}" in
    local)
        MYSQL_URL="127.0.0.1:3306"
        ;;
    test)
        MYSQL_URL="sandboxdb-test.hspconsortium.org"
        ;;
    prod)
        MYSQL_URL="sandboxdb.hspconsortium.org"
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

STARTED=0

OUTPUT=""
until [  $STARTED -eq 1 ]; do
sleep 60
    if [[ ! -z "$(lsof -t -i:$PORT)" ]]; then
        let STARTED=1
    else
        ps ax | grep nameForShutdown | grep -v grep | awk '{print $1}' | xargs kill
        ./run-fhir-server.sh $FHIR_VERSION $ENVIRONMENT $SANDBOX_NAME $JASYPT_PASSWORD
    fi

done

echo "Running server on port $PORT."

echo "curl --header \"Authorization: BEARER ${BEARER_TOKEN}\" \"$FHIR_HOST/$SANDBOX_NAME/data/\$mark-all-resources-for-reindexing\""
STARTED=0
until [  $STARTED -eq 1 ]; do
    if [[ "$(curl -X GET --header "Authorization: BEARER $BEARER_TOKEN" "$FHIR_HOST/$SANDBOX_NAME/data/\$mark-all-resources-for-reindexing")" != *"NullPointerException"* ]]; then
        let STARTED=1
        echo "Successful reindexing connection!"
    fi
    sleep 1
done

FINISHED=0
SQL_STRING="SELECT COUNT(*) FROM $FULL_NAME.HFJ_SPIDX_TOKEN WHERE HASH_IDENTITY IS NULL;"
SQL_STRING2="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RES_REINDEX_JOB;"

until [  $FINISHED -eq 1 ]; do
    if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$MYSQL_URL --port=3306 -Bs)" != "0" && "$(echo $SQL_STRING2 | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$MYSQL_URL --port=3306 -Bs)" == "0" ]]; then
        curl -X GET --header "Authorization: BEARER ${BEARER_TOKEN}" "$FHIR_HOST/$SANDBOX_NAME/data/\$mark-all-resources-for-reindexing"

    elif [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$MYSQL_URL --port=3306 -Bs)" == "0" && "$(echo $SQL_STRING2 | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$MYSQL_URL --port=3306 -Bs)" == "0" ]]; then
        let FINISHED=1
    fi
    sleep 15
done

mysql --user="$MYSQL_USER" --password="$MYSQL_PASS" -h$MYSQL_URL --port=3306 --database="$FULL_NAME" < postReindexing.sql

hapi-fhir-3.7.0-cli/hapi-fhir-cli migrate-database -d MYSQL_5_7 -u "jdbc:mysql://$MYSQL_URL/$FULL_NAME?serverTimezone=America/Denver" -n "$MYSQL_USER" -p "$MYSQL_PASS" -f V3_4_0 -t V3_7_0

SQL_STRING="UPDATE sandman.sandbox SET api_endpoint_index='$INDEX' WHERE sandbox_id='$SANDBOX_NAME'";
echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$MYSQL_URL --port=3306 -Bs

echo "Shutting down sever"
ps ax | grep nameForShutdown | grep -v grep | awk '{print $1}' | xargs kill



