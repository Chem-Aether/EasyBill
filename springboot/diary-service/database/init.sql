SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'diary_database' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS diary_database;
CREATE DATABASE diary_database ENCODING 'UTF8';
