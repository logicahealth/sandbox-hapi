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

TEMP_SCHEMA="hspc_8_370MigrationSchema"

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

	STARTED=0
	SQL_STRING="SELECT RES_VERSION FROM $FULL_NAME.HFJ_RESOURCE WHERE RES_ID=1;"
    # Pipe the SQL into mysql
    FHIR_VERSION=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs)
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
    esac
    if [[ $FHIR_VERSION != "r4" && $FULL_VERSION != 'hspc_5_fhirnpi' && $FULL_VERSION != 'hspc_5_hspc5' && $FULL_VERSION != 'hspc_5_hspc6' && $FULL_VERSION != 'hspc_5_hspc7' ]]; then

        SQL_STRING="SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA ='$FULL_NAME' and TABLE_NAME='HFJ_SPIDX_TOKEN' and COLUMN_NAME='HASH_IDENTITY'"
        SQL_STRING2="SELECT COUNT(*) FROM $FULL_NAME.HFJ_RESOURCE;"
        if [[ "$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs)" != "0" ]]; then
            SQL_STRING3="SELECT COUNT(*) FROM $FULL_NAME.HFJ_SPIDX_TOKEN WHERE HASH_IDENTITY IS NULL;"
            if [[ "$(echo $SQL_STRING3 | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs)" != "0" ]]; then
                SQL_STRING="UPDATE sandman.sandbox SET expiration_message='For the next several minutes, your sandbox will be undergoing an upgrade. Any changes made to the FHIR data of this sandbox may not be saved while this banner is showing.', expiration_date='2019-04-01' WHERE sandbox_id='$SANDBOX_NAME';"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
                SQL_STRING="DROP DATABASE IF EXISTS $TEMP_SCHEMA;"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
                SQL_STRING="CREATE SCHEMA $TEMP_SCHEMA;"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
                echo "$SANDBOX_NAME" >> sandboxes_done.txt
                mysqldump --host=$HOST --protocol=tcp --user=$MYSQL_USER --password=$MYSQL_PASS --hex-blob=TRUE --port=3306 --default-character-set=utf8 --skip-triggers "$FULL_NAME" > temp-dump.sql
                mysql --user=$MYSQL_USER --password=$MYSQL_PASS --host=$HOST --port=3306 $TEMP_SCHEMA < temp-dump.sql
                echo "Running Pre-Reindexing sql scripts for $SANDBOX_NAME"
                mysql --user=$MYSQL_USER --password=$MYSQL_PASS --host=$HOST --port=3306 $TEMP_SCHEMA < preReindexing.sql
               ./migrate-hapi-370.sh $MYSQL_USER $MYSQL_PASS $ENVIRONMENT $TEMP_SCHEMA $FHIR_VERSION $BEARER_TOKEN $JASYPT_PASSWORD

               mysqldump --host=$HOST --protocol=tcp --user=$MYSQL_USER --password=$MYSQL_PASS --hex-blob=TRUE --port=3306 --default-character-set=utf8 --skip-triggers "$TEMP_SCHEMA" > temp-dump.sql
                SQL_STRING="DROP DATABASE $FULL_NAME; CREATE SCHEMA hspc_8_$SANDBOX_NAME;"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

                mysql --user=$MYSQL_USER --password=$MYSQL_PASS --host=$HOST --port=3306 "hspc_8_$SANDBOX_NAME" < temp-dump.sql

                SQL_STRING="UPDATE hspc_8_$SANDBOX_NAME.hspc_tenant_info SET hspc_schema_version='8' WHERE tenant_id='$SANDBOX_NAME';"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

                SQL_STRING="SELECT api_endpoint_index FROM sandman.sandbox WHERE sandbox_id='$SANDBOX_NAME';"
                FHIR_VERSION_NUMBER=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs)
                case "${FHIR_VERSION_NUMBER}" in
                5)
                    FHIR_VERSION_NUMBER="8"
                    ;;
                6)
                    FHIR_VERSION_NUMBER="9"
                    ;;
                7)
                    FHIR_VERSION_NUMBER="10"
                    ;;
                esac

                SQL_STRING="UPDATE sandman.sandbox SET api_endpoint_index='$FHIR_VERSION_NUMBER' WHERE sandbox_id='$SANDBOX_NAME';"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

                SQL_STRING="DROP DATABASE $TEMP_SCHEMA;"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

               SQL_STRING="UPDATE sandman.sandbox SET expiration_message=NULL, expiration_date=NULL WHERE sandbox_id='$SANDBOX_NAME';"
                echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
            fi
        elif [[ "$(echo $SQL_STRING2 | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs)" != "0" ]]; then
            echo "USE $FULL_NAME" | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
            SQL_STRING="UPDATE sandman.sandbox SET expiration_message='For the next several minutes, your sandbox will be undergoing an upgrade. Any changes made to the FHIR data of this sandbox may not be saved while this banner is showing.', expiration_date='2019-04-01' WHERE sandbox_id='$SANDBOX_NAME';"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
            SQL_STRING="DROP DATABASE IF EXISTS $TEMP_SCHEMA;"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
            SQL_STRING="CREATE SCHEMA $TEMP_SCHEMA;"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
            echo "$SANDBOX_NAME" >> sandboxes_done.txt
            mysqldump --host=$HOST --protocol=tcp --user=$MYSQL_USER --password=$MYSQL_PASS --hex-blob=TRUE --port=3306 --default-character-set=utf8 --skip-triggers "$FULL_NAME" > temp-dump.sql
            mysql --user=$MYSQL_USER --password=$MYSQL_PASS --host=$HOST --port=3306 $TEMP_SCHEMA < temp-dump.sql

            echo "Running Pre-Reindexing sql scripts for $SANDBOX_NAME"
            mysql --user=$MYSQL_USER --password=$MYSQL_PASS --host=$HOST --port=3306 $TEMP_SCHEMA < preReindexing.sql
            ./migrate-hapi-370.sh $MYSQL_USER $MYSQL_PASS $ENVIRONMENT $TEMP_SCHEMA $FHIR_VERSION $BEARER_TOKEN $JASYPT_PASSWORD

            mysqldump --host=$HOST --protocol=tcp --user=$MYSQL_USER --password=$MYSQL_PASS --hex-blob=TRUE --port=3306 --default-character-set=utf8 --skip-triggers "$TEMP_SCHEMA" > temp-dump.sql
            SQL_STRING="DROP DATABASE $FULL_NAME; CREATE SCHEMA hspc_8_$SANDBOX_NAME;"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

            mysql --user=$MYSQL_USER --password=$MYSQL_PASS --host=$HOST --port=3306 "hspc_8_$SANDBOX_NAME" < temp-dump.sql

            SQL_STRING="UPDATE hspc_8_$SANDBOX_NAME.hspc_tenant_info SET hspc_schema_version='8' WHERE tenant_id='$SANDBOX_NAME';"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

            SQL_STRING="SELECT api_endpoint_index FROM sandman.sandbox WHERE sandbox_id='$SANDBOX_NAME';"
            FHIR_VERSION_NUMBER=$(echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs)
            case "${FHIR_VERSION_NUMBER}" in
            5)
                FHIR_VERSION_NUMBER="8"
                ;;
            6)
                FHIR_VERSION_NUMBER="9"
                ;;
            7)
                FHIR_VERSION_NUMBER="10"
                ;;
            esac

            SQL_STRING="UPDATE sandman.sandbox SET api_endpoint_index='$FHIR_VERSION_NUMBER' WHERE sandbox_id='$SANDBOX_NAME';"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

            SQL_STRING="DROP DATABASE $TEMP_SCHEMA;"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs

            SQL_STRING="UPDATE sandman.sandbox SET expiration_message=NULL, expiration_date=NULL WHERE sandbox_id='$SANDBOX_NAME';"
            echo $SQL_STRING | mysql -u$MYSQL_USER -p$MYSQL_PASS -h$HOST --port=3306 -Bs
        fi


    fi
done