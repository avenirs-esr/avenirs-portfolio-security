

INSERT INTO role (id, name, description) 
VALUES 
(1, 'ROLE_GUEST', 'Default role'),
(2, 'ROLE_OWNER', 'Owner of the resource'),
(3, 'ROLE_TEACHER', 'Teacher for training'),
(4, 'ROLE_CONTRIBUTOR', 'Contributor for the resource'),
(5, 'ROLE_PAIR', 'Can give feedback');


INSERT INTO permission (id, name, description) 
VALUES
(1, 'PERM_SEE', 'See permission'),
(2, 'PERM_READ', 'Read permission'),
(3, 'PERM_WRITE', 'Write permission'),
(4, 'PERM_COMMENT', 'Comments and feedbacks'),
(5, 'PERM_SHARE', 'Share permission'),
(6, 'PERM_DELETE', 'Delete permission');

INSERT INTO action (id, name, description) 
VALUES
(1, 'ACT_SHARE_READ_RESOURCE', 'Share a resource readonly'),
(2, 'ACT_SHARE_WRITE_RESOURCE', 'Share a resource read and write'),
(3, 'ACT_DISPLAY', 'Visualize a resource'),
(4, 'ACT_EDIT', 'Edit a resource'),
(5, 'ACT_DO_FEEDBACK', 'Do a feedback'),
(6, 'ACT_DELETE', 'Delete a resource');

INSERT INTO action_permission (id_action, id_permission) 
VALUES 
((SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM action WHERE action.name = 'ACT_DISPLAY'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM action WHERE action.name = 'ACT_EDIT'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM action WHERE action.name = 'ACT_DELETE'), (SELECT id FROM permission WHERE permission.name = 'PERM_DELETE'));



INSERT INTO role_permission (id_role, id_permission) 
VALUES 
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_SEE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_DELETE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM role WHERE role.name = 'ROLE_PAIR'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM role WHERE role.name = 'ROLE_PAIR'), (SELECT id FROM permission WHERE permission.name = 'PERM_COMMENT')),
((SELECT id FROM role WHERE role.name = 'ROLE_CONTRIBUTOR'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE'));
((SELECT id FROM role WHERE role.name = 'ROLE_CONTRIBUTOR'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE'));




INSERT INTO resource_type (id, name, description)
VALUES 
(1, 'PORTFOLIO', 'Resource of type portfolio'),
(2, 'SAE', 'Resource of type SAE');





INSERT INTO resource (id, selector, id_resource_type)
VALUES 
(1, 'ptf_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
(2, 'ptf_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
(3, 'sae_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'SAE')),
(4, 'sae_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'SAE'));



INSERT INTO scope (id, name)
VALUES 
(1, 'scope_00000'),
(2, 'scope_00001'),
(3, 'scope_00002'),
(4, 'scope_00003');


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



