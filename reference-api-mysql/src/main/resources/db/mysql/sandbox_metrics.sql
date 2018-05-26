-- determine the size (MB)
SELECT table_schema AS "database",
ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "size"
FROM information_schema.TABLES
WHERE UPPER(table_schema) like 'HSPC_5%'
GROUP BY table_schema;

-- include the creator, high usage
SELECT sub_metadata.user_name, sub_metadata.email, sub_metadata.name, sub_data.sandbox, sub_data.size
FROM (
  SELECT SUBSTRING(table_schema, 8) AS "sandbox",
  ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "size"
  FROM information_schema.TABLES
  WHERE UPPER(table_schema) like 'HSPC_%'
  GROUP BY table_schema
  HAVING ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) > 300
) as sub_data
LEFT OUTER JOIN
(
  SELECT s.id, s.name, s.sandbox_id, s.created_by_id, u.name as 'user_name', u.email
  FROM sandbox s JOIN user u ON (s.created_by_id = u.id)
) as sub_metadata
ON UPPER(sub_data.sandbox)=UPPER(sub_metadata.sandbox_id)
;


-- determine the aggregates
SELECT MIN(sub.size) min_size, AVG(sub.size) avg_size, MAX(sub.size) max_size
FROM (
  SELECT table_schema AS "database",
  ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS "size"
  FROM information_schema.TABLES
  WHERE UPPER(table_schema) like 'HSPC_5%'
  GROUP BY table_schema
) as sub;