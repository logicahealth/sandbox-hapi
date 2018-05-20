#!/bin/bash

# mysql credential
user="root"
pass="password"

# list of all databases
#all_dbs="$(mysql -u $user -p$pass -Bse 'show databases')"
all_dbs="$(mysql -u $user -p$pass -Bse 'SELECT DISTINCT SCHEMA_NAME AS `database` FROM information_schema.SCHEMATA WHERE SCHEMA_NAME LIKE ('hspc_5_%')2)"

for db in $all_dbs
     do
        echo "DB: " + for_db
        if test $db != "information_schema"
            then if test $db != "mysql"
            then mysql -u$user -p$pass $db -sN -e "source V1__for_all_hspc_5.sh"
        fi
    fi
done