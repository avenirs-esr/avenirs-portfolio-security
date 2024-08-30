\i clean-test-db.sql

CREATE ROLE avenirs_security_admin_role_test SUPERUSER; 
CREATE ROLE avenirs_security_admin_test PASSWORD 'S33c@DM4avn' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_security_admin_role_test to avenirs_security_admin;

CREATE DATABASE avenirs_access_control_test OWNER avenirs_security_admin_test;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control_test TO avenirs_security_admin_role_test;



-- \connect avenirs_access_control_test
-- \i ../../main/resources/init-tables.sql
-- \i test-fixtures.sql