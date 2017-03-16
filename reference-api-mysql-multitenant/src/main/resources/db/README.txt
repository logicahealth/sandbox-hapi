A dump file is collected from the target system:

mysqldump -u root -p hspc_3_hspc3 --hex-blob > dump_hspc.sql

A dump file is loaded into a new database within mysql:

create database hspc_3_hspc3;
use hspc_3_hspc3;
source dump.sql
