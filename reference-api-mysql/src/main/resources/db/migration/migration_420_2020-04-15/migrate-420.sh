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