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

DB_STARTS_WITH="hspc_5"

echo "Sandbox List" > sandboxes_done.txt

SQL_STRING="SELECT SCHEMA_NAME AS db FROM information_schema.SCHEMATA WHERE SCHEMA_NAME NOT IN ('mysql', 'information_schema') AND SCHEMA_NAME LIKE 'hspc_5%';"
# Pipe the SQL into mysql
DBS=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)
set -f                      # avoid globbing (expansion of *).
array=($(echo "$DBS" | tr ',' '\n'))

# Display your result
for FULL_NAME in "${array[@]}"
do
    SANDBOX_NAME=${FULL_NAME:7}
	echo "$SANDBOX_NAME" >> sandboxes_done.txt
	STARTED=0
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
        R4)
            FHIR_VERSION="r4"
            ;;
        *)
            FHIR_VERSION="stu3"
            ;;
    esac
    mysql --user="$MYSQL_USER" --password="$MYSQL_PASS" --database="$FULL_NAME" < preReindexing.sql
	SQL_STRING="SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA ='$FULL_NAME' and TABLE_NAME='HFJ_SPIDX_TOKEN' and COLUMN_NAME='HASH_IDENTITY'"
	SQL_STRING2="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RESOURCE;"
	if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" != "0" ]]; then
        SQL_STRING3="SELECT COUNT(*) FROM $FULL_NAME.HFJ_SPIDX_TOKEN WHERE HASH_IDENTITY IS NULL;"
        if [[ "$(echo $SQL_STRING3 | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" != "0" ]]; then
           ./migrate-hapi-370.sh $MYSQL_USER $MYSQL_PASS $ENVIRONMENT $FULL_NAME $FHIR_VERSION $BEARER_TOKEN $JASYPT_PASSWORD
        fi
    elif [[ "$(echo $SQL_STRING2 | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs)" != "0" ]]; then
        echo "Running Pre-Reindexing sql scripts for $SANDBOX_NAME"
        echo "USE $FULL_NAME" | mysql -u$MYSQL_USER -p$MYSQL_PASS -Bs
        ./migrate-hapi-370.sh $MYSQL_USER $MYSQL_PASS $ENVIRONMENT $SANDBOX_NAME $FHIR_VERSION $BEARER_TOKEN $JASYPT_PASSWORD
    fi
done