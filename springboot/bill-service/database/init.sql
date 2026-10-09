SELECT 'CREATE DATABASE eastbill_bill'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'eastbill_bill')
\gexec
