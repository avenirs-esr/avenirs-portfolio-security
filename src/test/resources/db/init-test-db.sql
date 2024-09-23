CREATE ROLE avenirs_security_admin_role_test SUPERUSER; 
CREATE ROLE avenirs_security_admin_test PASSWORD 'S33c@DM4avn' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_security_admin_role_test to avenirs_security_admin_test;

CREATE DATABASE avenirs_access_control_test OWNER avenirs_security_admin_test;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control_test TO avenirs_security_admin_role_test;

\c avenirs_access_control_test
CREATE SCHEMA IF NOT EXISTS test AUTHORIZATION avenirs_security_admin;
