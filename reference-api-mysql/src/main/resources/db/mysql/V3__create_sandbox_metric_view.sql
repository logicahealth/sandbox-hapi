-- determine the size (MB)

SELECT sub.table_schema, sub.table_schema_size_mb, SUBSTRING(sub.table_schema, 6 + POSITION('_' IN SUBSTRING(sub.table_schema, 6))) AS "sandbox_name"
FROM (
  SELECT table_schema AS "table_schema",
  (SUM(data_length + index_length) / 1024 / 1024) AS "table_schema_size_mb"
  FROM information_schema.TABLES
  WHERE UPPER(table_schema) like 'HSPC_%'
  GROUP BY table_schema
) AS sub
ORDER BY sub.table_schema_size_mb;
