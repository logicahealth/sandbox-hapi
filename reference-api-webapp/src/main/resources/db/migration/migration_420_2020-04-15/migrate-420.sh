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

# Requires the user name, password and environment as follows:
# ./migrate-380.sh root password local

MYSQL_USER=$1
MYSQL_PASS=$2
ENVIRONMENT=$3

case "${ENVIRONMENT}" in
local)
  HOST="127.0.0.1"
  ;;
dev)
  HOST="mysqldb.interopio-dev.com"
  ;;
test)
  HOST="mysqldb.interopio-test.com"
  ;;
prod)
  HOST="db.interopio.com"
  ;;
esac

echo $HOST

schemas=$(echo "SELECT schema_name FROM information_schema.SCHEMATA WHERE UPPER(schema_name) LIKE 'iocdr_%'" | mysql -h "$HOST" -u $MYSQL_USER -p$MYSQL_PASS)
printf "%s\n" "${schemas[@]}" >temp-migration-text.txt
getArray() {
  schemas=() # Create array
  while IFS= read -r line; do # Read a line
    schemas+=("$line") # Append line to the array
  done <"$1"
}
getArray "temp-migration-text.txt"

for i in "${schemas[@]:1}"; do
  echo $i;
  mysql -h "$HOST" -u "$MYSQL_USER" "-p$MYSQL_PASS" "$i" < test.sql # Change test.sql to migrate-420.sql for actual migration
  #mysql -h "$HOST" -u "$MYSQL_USER" "-p$MYSQL_PASS" "$i" -e "select count(*) from HFJ_SEARCH;"
done

rm temp-migration-text.txt