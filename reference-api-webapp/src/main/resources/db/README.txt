Don't have extra SQL files here as they add a lot to the maven repository, artifact size, and runtime deployment speed.

A dump file is collected from the target system:

mysqldump -u root -p hspc_5_dstu2 --hex-blob > dump.sql

