Don't have extra SQL files here as they add a lot to the maven repository, artifact size, and runtime deployment speed.

mysqldump -u root -p hspc_4_hspc4 --hex-blob > dump_hspc.sql

A dump file is loaded into a new database within mysql:

create database hspc_4_hspc4;
use hspc_4_hspc4;
source dump.sql
