SELECT 'CREATE DATABASE eastbill_identity'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'eastbill_identity')
\gexec
