CREATE ROLE avenirs_security_admin_role_test SUPERUSER;
CREATE ROLE avenirs_security_admin_test PASSWORD 's4dfHty@DHTpa' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_security_admin_role_test to avenirs_security_admin_test;

CREATE DATABASE avenirs_access_control_test OWNER avenirs_security_admin_test;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control_test TO avenirs_security_admin_role_test;

\c avenirs_access_control_test
CREATE SCHEMA IF NOT EXISTS test AUTHORIZATION avenirs_security_admin_test;
ALTER USER avenirs_security_admin_test SET search_path TO test, public;
CREATE EXTENSION IF NOT EXISTS citext;
