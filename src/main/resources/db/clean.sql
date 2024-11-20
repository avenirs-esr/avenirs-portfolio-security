\connect template1;
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'avenirs_access_control' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS avenirs_access_control;
DROP ROLE IF EXISTS avenirs_security_admin;
DROP ROLE IF EXISTS avenirs_security_admin_role;