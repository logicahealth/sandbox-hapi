SELECT distinct SUBSTRING(schema_name, 0, 5) FROM information_schema.SCHEMATA WHERE UPPER(schema_name) LIKE 'HSPC_%'
