SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'travel_database' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS travel_database;
CREATE DATABASE travel_database ENCODING 'UTF8';
