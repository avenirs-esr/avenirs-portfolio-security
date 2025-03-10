CREATE ROLE avenirs_security_admin_role SUPERUSER;
CREATE ROLE avenirs_security_admin PASSWORD 'ENC(nrhrW8giUqCjQzWRBDVj/XYVStp8Tgxs)' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_security_admin_role to avenirs_security_admin;

CREATE DATABASE avenirs_access_control OWNER avenirs_security_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control TO avenirs_security_admin_role;
\c avenirs_access_control
CREATE SCHEMA IF NOT EXISTS dev AUTHORIZATION avenirs_security_admin;
ALTER USER avenirs_security_admin SET search_path TO dev, public;
CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
