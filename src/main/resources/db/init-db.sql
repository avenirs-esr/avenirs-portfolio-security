CREATE ROLE avenirs_security_admin_role SUPERUSER; 
CREATE ROLE avenirs_security_admin PASSWORD 'S33c@DM4avn' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_security_admin_role to avenirs_security_admin;

CREATE DATABASE avenirs_access_control OWNER avenirs_security_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control TO avenirs_security_admin_role;
\c avenirs_access_control
CREATE SCHEMA IF NOT EXISTS dev AUTHORIZATION avenirs_security_admin;
CREATE EXTENSION IF NOT EXISTS citext;


