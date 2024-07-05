\connect template1;

DROP DATABASE IF EXISTS avenirs_access_control;
DROP DATABASE IF EXISTS avenirs_access_control_test;
DROP ROLE  IF EXISTS  avenirs_security_admin;
DROP ROLE  IF EXISTS avenirs_security_admin_role;

CREATE ROLE avenirs_security_admin_role SUPERUSER; 
CREATE ROLE avenirs_security_admin PASSWORD 'S33c@DM4avn' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_security_admin_role to avenirs_security_admin;

CREATE DATABASE avenirs_access_control_test OWNER avenirs_security_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control_test TO avenirs_security_admin_role;



CREATE DATABASE avenirs_access_control OWNER avenirs_security_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_access_control TO avenirs_security_admin_role;

\connect avenirs_access_control

-- Mock table
CREATE TABLE principal(
	id serial,
	login VARCHAR(80) NOT NULL,
	CONSTRAINT user_pk PRIMARY KEY(id)
);
ALTER TABLE principal OWNER TO avenirs_security_admin_role;
-- End of mock





CREATE TABLE role(
	id serial,
	name VARCHAR(80) UNIQUE NOT NULL,
	description VARCHAR(255),
	
	CONSTRAINT role_pk PRIMARY KEY(id)
);
ALTER TABLE role OWNER TO avenirs_security_admin_role;

CREATE TABLE permission(
	id serial,
	name VARCHAR(80) UNIQUE NOT NULL,
	description VARCHAR(255),
	
	CONSTRAINT permission_pk PRIMARY KEY(id)
);
ALTER TABLE permission OWNER TO avenirs_security_admin_role;

CREATE TABLE action(
	id serial,
	name VARCHAR(80) NOT NULL,
	description VARCHAR(255),
	
	CONSTRAINT action_pk PRIMARY KEY(id)
);
ALTER TABLE action OWNER TO avenirs_security_admin_role;


CREATE TABLE resource_type(
	id serial,
	name VARCHAR(80) UNIQUE NOT NULL,
	description VARCHAR(255),
	CONSTRAINT resource_typ_pk PRIMARY KEY(id)
);
ALTER TABLE resource_type OWNER TO avenirs_security_admin_role;


CREATE TABLE resource(
	id serial,
	selector VARCHAR(255),
	id_resource_type INTEGER NOT NULL, 
	CONSTRAINT resource_pk PRIMARY KEY(id)
);
ALTER TABLE resource OWNER TO avenirs_security_admin_role;

ALTER TABLE resource ADD CONSTRAINT resource_id_resource_type_fk FOREIGN KEY(id_resource_type)
REFERENCES resource_type(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

CREATE TABLE scope(
	id serial,
	name VARCHAR(80),
	CONSTRAINT scope_pk PRIMARY KEY(id)
);
ALTER TABLE scope OWNER TO avenirs_security_admin_role;

CREATE TABLE scope_resource(
	id_scope INTEGER NOT NULL,
	id_resource INTEGER NOT NULL,
	CONSTRAINT scope_resource_pk PRIMARY KEY(id_scope, id_resource)
);
ALTER TABLE scope_resource OWNER TO avenirs_security_admin_role;

ALTER TABLE scope_resource ADD CONSTRAINT scope_resource_id_scope_fk FOREIGN KEY(id_scope)
REFERENCES scope(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

ALTER TABLE scope_resource ADD CONSTRAINT scope_resource_id_resource_fk FOREIGN KEY(id_resource)
REFERENCES resource(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

CREATE TABLE context(
	id serial,
	name VARCHAR(80),
	description VARCHAR(255),
	validityStart TIMESTAMP,
	validityEnd TIMESTAMP,
	establishment VARCHAR(255),
	CONSTRAINT context_pk PRIMARY KEY(id)
);
ALTER TABLE context OWNER TO avenirs_security_admin_role;

CREATE TABLE action_permission(
	id_action INTEGER,
	id_permission INTEGER,
	CONSTRAINT action_permission_pk PRIMARY KEY(id_permission, id_action)
);
ALTER TABLE action_permission OWNER TO avenirs_security_admin_role;

ALTER TABLE action_permission ADD CONSTRAINT action_permission_id_action_fk FOREIGN KEY(id_action)
REFERENCES action(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

ALTER TABLE action_permission ADD CONSTRAINT action_permission_id_permission_fk FOREIGN KEY(id_permission)
REFERENCES permission(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

CREATE TABLE role_permission(
	id_role INTEGER,
	id_permission INTEGER,
	CONSTRAINT role_permission_pk PRIMARY KEY(id_role,id_permission)
);
ALTER TABLE role_permission OWNER TO avenirs_security_admin_role;

ALTER TABLE role_permission ADD CONSTRAINT role_permission_id_role_fk FOREIGN KEY(id_role)
REFERENCES role(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

ALTER TABLE role_permission ADD CONSTRAINT role_permission_id_permission_fk FOREIGN KEY(id_permission)
REFERENCES permission(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

CREATE TABLE assignment(
	id_role INTEGER,
	id_principal INTEGER,
	id_scope INTEGER,
	id_context INTEGER DEFAULT NULL,
	CONSTRAINT assignment_pk PRIMARY KEY(id_role, id_principal, id_scope, id_context)
);
ALTER TABLE assignment OWNER TO avenirs_security_admin_role;

ALTER TABLE assignment ADD CONSTRAINT assignment_id_principal_fk FOREIGN KEY(id_principal)
REFERENCES principal(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

ALTER TABLE assignment ADD CONSTRAINT assignment_id_role_fk FOREIGN KEY(id_role)
REFERENCES role(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

ALTER TABLE assignment ADD CONSTRAINT assignment_id_scope_fk FOREIGN KEY(id_scope)
REFERENCES scope(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;

ALTER TABLE assignment ADD CONSTRAINT assignment_id_context_fk FOREIGN KEY(id_context)
REFERENCES context(id) MATCH FULL
ON DELETE CASCADE NOT DEFERRABLE;



