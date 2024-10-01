INSERT INTO principal (id, login)
VALUES 
(1, 'gribonvald'),
(2, 'deman'),
(3, 'dugat'),
(4, 'patterson');

INSERT INTO structure (id, name, description)
VALUES 
(0, 'ANY', 'Toutes les structures'),
(1, 'RECIA', 'Groupement d''Intérêt Public Région Centre Interactive'),
(2, 'Univ. Toulon', 'Université de Toulon'),
(3, 'AMU', 'Université d''Aix-Marseille'),
(4, 'MIT', 'Massachusetts Institute of Technology');

INSERT INTO principal_structure (id_principal, id_structure)
VALUES 
(1, 1),
(2, 2),
(3, 3),
(4, 4);

INSERT INTO role (id, name, description) 
VALUES 
(1, 'ROLE_GUEST', 'Default role'),
(2, 'ROLE_OWNER', 'Owner of the resource'),
(3, 'ROLE_TEACHER', 'Teacher for training'),
(4, 'ROLE_CONTRIBUTOR', 'Contributor for the resource'),
(5, 'ROLE_PAIR', 'Can give feedback');


INSERT INTO permission (id, name, description) 
VALUES
(1, 'PERM_READ', 'Read permission'),
(2, 'PERM_WRITE', 'Write permission'),
(3, 'PERM_COMMENT', 'Comments and feedbacks'),
(4, 'PERM_SHARE', 'Share permission'),
(5, 'PERM_DELETE', 'Delete permission');

INSERT INTO action (id, name, description) 
VALUES
(1, 'ACT_SHARE_READ_RESOURCE', 'Share a resource readonly'),
(2, 'ACT_SHARE_WRITE_RESOURCE', 'Share a resource read and write'),
(3, 'ACT_DISPLAY', 'Visualize a resource'),
(4, 'ACT_EDIT', 'Edit a resource'),
(5, 'ACT_DO_FEEDBACK', 'Do a feedback'),
(6, 'ACT_DELETE', 'Delete a resource');

INSERT INTO action_route (id, id_action, uri, method) 
VALUES
(1, (SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), '/share/read', 'post'),
(2, (SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), '/share/read', 'put'),
(3, (SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), '/share/write', 'post'),
(4, (SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), '/share/write', 'put'),
(5, (SELECT id FROM action WHERE action.name = 'ACT_DISPLAY'), '/display', 'get'),
(6, (SELECT id FROM action WHERE action.name = 'ACT_EDIT'), '/edit', 'post'),
(7, (SELECT id FROM action WHERE action.name = 'ACT_EDIT'), '/edit', 'put'),
(8, (SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), '/feedback', 'post'),
(9, (SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), '/feedback', 'put'),
(10, (SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), '/feedback', 'get'),
(11, (SELECT id FROM action WHERE action.name = 'ACT_DELETE'), '/delete', 'delete');



INSERT INTO action_permission (id_action, id_permission) 
VALUES 
((SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM action WHERE action.name = 'ACT_DISPLAY'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM action WHERE action.name = 'ACT_EDIT'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM action WHERE action.name = 'ACT_EDIT'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), (SELECT id FROM permission WHERE permission.name = 'PERM_COMMENT')),
((SELECT id FROM action WHERE action.name = 'ACT_DELETE'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM action WHERE action.name = 'ACT_DELETE'), (SELECT id FROM permission WHERE permission.name = 'PERM_DELETE'));



INSERT INTO role_permission (id_role, id_permission) 
VALUES 
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_COMMENT')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_SHARE')),
((SELECT id FROM role WHERE role.name = 'ROLE_OWNER'), (SELECT id FROM permission WHERE permission.name = 'PERM_DELETE')),
((SELECT id FROM role WHERE role.name = 'ROLE_PAIR'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM role WHERE role.name = 'ROLE_PAIR'), (SELECT id FROM permission WHERE permission.name = 'PERM_COMMENT')),
((SELECT id FROM role WHERE role.name = 'ROLE_CONTRIBUTOR'), (SELECT id FROM permission WHERE permission.name = 'PERM_READ')),
((SELECT id FROM role WHERE role.name = 'ROLE_CONTRIBUTOR'), (SELECT id FROM permission WHERE permission.name = 'PERM_WRITE'));




INSERT INTO resource_type (id, name, description)
VALUES 
(1, 'PORTFOLIO', 'Resource of type portfolio'),
(2, 'MES', 'Resource of type MES');





INSERT INTO resource (id, selector, id_resource_type)
VALUES 
(1, 'ptf_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
(2, 'ptf_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
(3, 'mes_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'MES')),
(4, 'mes_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'MES'));



INSERT INTO scope (id, name)
VALUES 
(1, 'scope_0000'),
(2, 'scope_0001'),
(3, 'scope_0002'),
(4, 'scope_0003');


INSERT INTO scope_resource (id_scope, id_resource)
VALUES 
((SELECT id FROM scope WHERE name = 'scope_0000'), (SELECT id FROM resource WHERE selector = 'ptf_0000')),
((SELECT id FROM scope WHERE name = 'scope_0001'), (SELECT id FROM resource WHERE selector = 'ptf_0001')),
((SELECT id FROM scope WHERE name = 'scope_0002'), (SELECT id FROM resource WHERE selector = 'mes_0000')),
((SELECT id FROM scope WHERE name = 'scope_0003'), (SELECT id FROM resource WHERE selector = 'ptf_0000')),
((SELECT id FROM scope WHERE name = 'scope_0003'), (SELECT id FROM resource WHERE selector = 'ptf_0001'));


INSERT INTO context (id, validity_start, validity_end)
VALUES 
(0, null, null);

