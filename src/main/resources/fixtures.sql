
\connect avenirs_access_control

DELETE FROM role;
DELETE FROM permission;
DELETE FROM action;
DELETE FROM resource_type;
DELETE FROM resource;
DELETE FROM scope;
DELETE FROM context;
DELETE FROM principal;


INSERT INTO role (name, description) 
VALUES 
('ROLE_GUEST', 'Default role'),
('ROLE_OWNER', 'Owner of the resource'),
('ROLE_TEACHER', 'Teacher for training'),
('ROLE_CONTRIBUTOR', 'Contributor for the resource'),
('ROLE_PAIR', 'Can give feedback');


INSERT INTO permission (name, description) 
VALUES
('PERM_SEE', 'See permission'),
('PERM_READ', 'Read permission'),
('PERM_WRITE', 'Write permission'),
('PERM_COMMENT', 'Comments and feedbacks'),
('PERM_SHARE', 'Share permission'),
('PERM_DELETE', 'Delete permission');

INSERT INTO action (name, description) 
VALUES
('ACT_SHARE_READ_RESOURCE', 'Share a resource readonly'),
('ACT_SHARE_WRITE_RESOURCE', 'Share a resource read and write'),
('ACT_DISPLAY', 'Visualize a resource'),
('ACT_EDIT', 'Edit a resource'),
('ACT_DO_FEEDBACK', 'Do a feedback'),
('ACT_DELETE', 'Delete a resource');

INSERT INTO action_permission (id_action, id_permission) 
VALUES 
((SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM action WHERE action.name = 'ACT_DISPLAY'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM action WHERE action.name = 'ACT_EDIT'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), (SELECT id FROM permission WHERE permission.name = 'PERM_DELETE'));



INSERT INTO role_permission (id_role, id_permission) 
VALUES 
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_DELETE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM role WHERE role.name = 'ROLE_PAIR'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM role WHERE role.name = 'ROLE_PAIR'), (SELECT id FROM permission WHERE permission.name = 'PERM_COMMENT')),
((SELECT id FROM role WHERE role.name = 'ROLE_CONTRIBUTOR'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE'));




INSERT INTO resource_type (name, description)
VALUES 
('PORTFOLIO', 'Resource of type portfolio'),
('SAE', 'Resource of type SAE');





INSERT INTO resource (selector, id_resource_type)
VALUES 
('ptf_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
('ptf_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
('sae_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'SAE')),
('sae_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'SAE'));



INSERT INTO scope (name)
VALUES 
('scope_00000'),
('scope_00001'),
('scope_00002'),
('scope_00003');


INSERT INTO scope_resource (id_scope, id_resource)
VALUES 
((SELECT id FROM scope WHERE name = 'scope_00000'), (SELECT id FROM resource WHERE selector = 'ptf_0000')),
((SELECT id FROM scope WHERE name = 'scope_00001'), (SELECT id FROM resource WHERE selector = 'ptf_0001')),
((SELECT id FROM scope WHERE name = 'scope_00002'), (SELECT id FROM resource WHERE selector = 'sae_0001')),
((SELECT id FROM scope WHERE name = 'scope_00003'), (SELECT id FROM resource WHERE selector = 'ptf_0000')),
((SELECT id FROM scope WHERE name = 'scope_00003'), (SELECT id FROM resource WHERE selector = 'ptf_0001'));


INSERT INTO principal (login)
VALUES 
('gribonvald'),
('deman'),
('dugat');


INSERT INTO context (name, description)
VALUES ('empty', 'Default empty context');


INSERT INTO assignment (id_role, id_principal, id_scope, id_context)
VALUES 
((SELECT id FROM role WHERE name = 'ROLE_OWNER'), (SELECT id FROM principal WHERE login = 'deman'), (SELECT id FROM scope WHERE name = 'scope_00000'), (SELECT id FROM context WHERE name = 'empty')),
((SELECT id FROM role WHERE name = 'ROLE_PAIR'), (SELECT id FROM principal WHERE login = 'deman'), (SELECT id FROM scope WHERE name = 'scope_00002'), (SELECT id FROM context WHERE name = 'empty')),
((SELECT id FROM role WHERE name = 'ROLE_OWNER'), (SELECT id FROM principal WHERE login = 'gribonvald'), (SELECT id FROM scope WHERE name = 'scope_00001'), (SELECT id FROM context WHERE name = 'empty')),
((SELECT id FROM role WHERE name = 'ROLE_PAIR'), (SELECT id FROM principal WHERE login = 'dugat'), (SELECT id FROM scope WHERE name = 'scope_00002'), (SELECT id FROM context WHERE name = 'empty'));







