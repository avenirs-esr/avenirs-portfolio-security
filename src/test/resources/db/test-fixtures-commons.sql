INSERT INTO principal (id, login)
VALUES 
('00000000-0000-0000-0000-000000000001', 'gribonvald'),
('00000000-0000-0000-0000-000000000002', 'deman'),
('00000000-0000-0000-0000-000000000003', 'dugat'),
('00000000-0000-0000-0000-000000000004', 'patterson');

INSERT INTO structure (id, name, description)
VALUES 
('00000000-0000-0000-0000-000000000000', 'ANY', 'Toutes les structures'),
('00000000-0000-0000-0000-000000000001', 'RECIA', 'Groupement d''Intérêt Public Région Centre Interactive'),
('00000000-0000-0000-0000-000000000002', 'Univ. Toulon', 'Université de Toulon'),
('00000000-0000-0000-0000-000000000003', 'AMU', 'Université d''Aix-Marseille'),
('00000000-0000-0000-0000-000000000004', 'MIT', 'Massachusetts Institute of Technology');

INSERT INTO principal_structure (id_principal, id_structure)
VALUES 
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001'),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002'),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003'),
('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004');

INSERT INTO role (id, name, description) 
VALUES 
('00000000-0000-0000-0000-000000000001', 'ROLE_GUEST', 'Default role'),
('00000000-0000-0000-0000-000000000002', 'ROLE_OWNER', 'Owner of the resource'),
('00000000-0000-0000-0000-000000000003', 'ROLE_TEACHER', 'Teacher for training'),
('00000000-0000-0000-0000-000000000004', 'ROLE_CONTRIBUTOR', 'Contributor for the resource'),
('00000000-0000-0000-0000-000000000005', 'ROLE_PAIR', 'Can give feedback');

INSERT INTO permission (id, name, description)
VALUES
('00000000-0000-0000-0000-000000000001', 'PERM_READ', 'Read permission'),
('00000000-0000-0000-0000-000000000002', 'PERM_WRITE', 'Write permission'),
('00000000-0000-0000-0000-000000000003', 'PERM_COMMENT', 'Comments and feedbacks'),
('00000000-0000-0000-0000-000000000004', 'PERM_SHARE', 'Share permission'),
('00000000-0000-0000-0000-000000000005', 'PERM_DELETE', 'Delete permission');

INSERT INTO action (id, name, description)
VALUES
('00000000-0000-0000-0000-000000000001', 'ACT_SHARE_READ_RESOURCE', 'Share a resource readonly'),
('00000000-0000-0000-0000-000000000002', 'ACT_SHARE_WRITE_RESOURCE', 'Share a resource read and write'),
('00000000-0000-0000-0000-000000000003', 'ACT_DISPLAY', 'Visualize a resource'),
('00000000-0000-0000-0000-000000000004', 'ACT_EDIT', 'Edit a resource'),
('00000000-0000-0000-0000-000000000005', 'ACT_DO_FEEDBACK', 'Do a feedback'),
('00000000-0000-0000-0000-000000000006', 'ACT_DELETE', 'Delete a resource');

INSERT INTO action_route (id, id_action, uri, method)
VALUES
('00000000-0000-0000-0000-000000000001', (SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), '/share/read', 'post'),
('00000000-0000-0000-0000-000000000002', (SELECT id FROM action WHERE action.name = 'ACT_SHARE_READ_RESOURCE'), '/share/read', 'put'),
('00000000-0000-0000-0000-000000000003', (SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), '/share/write', 'post'),
('00000000-0000-0000-0000-000000000004', (SELECT id FROM action WHERE action.name = 'ACT_SHARE_WRITE_RESOURCE'), '/share/write', 'put'),
('00000000-0000-0000-0000-000000000005', (SELECT id FROM action WHERE action.name = 'ACT_DISPLAY'), '/display', 'get'),
('00000000-0000-0000-0000-000000000006', (SELECT id FROM action WHERE action.name = 'ACT_EDIT'), '/edit', 'post'),
('00000000-0000-0000-0000-000000000007', (SELECT id FROM action WHERE action.name = 'ACT_EDIT'), '/edit', 'put'),
('00000000-0000-0000-0000-000000000008', (SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), '/feedback', 'post'),
('00000000-0000-0000-0000-000000000009', (SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), '/feedback', 'put'),
('00000000-0000-0000-0000-000000000010', (SELECT id FROM action WHERE action.name = 'ACT_DO_FEEDBACK'), '/feedback', 'get'),
('00000000-0000-0000-0000-000000000011', (SELECT id FROM action WHERE action.name = 'ACT_DELETE'), '/delete', 'delete');

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
('00000000-0000-0000-0000-000000000001', 'PORTFOLIO', 'Resource of type portfolio'),
('00000000-0000-0000-0000-000000000002', 'MES', 'Resource of type MES');

INSERT INTO resource (id, selector, id_resource_type)
VALUES 
('00000000-0000-0000-0000-000000000001', 'ptf_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
('00000000-0000-0000-0000-000000000002', 'ptf_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'PORTFOLIO')),
('00000000-0000-0000-0000-000000000003', 'mes_0000', (SELECT id FROM resource_type WHERE resource_type.name = 'MES')),
('00000000-0000-0000-0000-000000000004', 'mes_0001', (SELECT id FROM resource_type WHERE resource_type.name = 'MES'));

INSERT INTO scope (id, name)
VALUES 
('00000000-0000-0000-0000-000000000001', 'scope_0000'),
('00000000-0000-0000-0000-000000000002', 'scope_0001'),
('00000000-0000-0000-0000-000000000003', 'scope_0002'),
('00000000-0000-0000-0000-000000000004', 'scope_0003');

INSERT INTO scope_resource (id_scope, id_resource)
VALUES 
((SELECT id FROM scope WHERE name = 'scope_0000'), (SELECT id FROM resource WHERE selector = 'ptf_0000')),
((SELECT id FROM scope WHERE name = 'scope_0001'), (SELECT id FROM resource WHERE selector = 'ptf_0001')),
((SELECT id FROM scope WHERE name = 'scope_0002'), (SELECT id FROM resource WHERE selector = 'mes_0000')),
((SELECT id FROM scope WHERE name = 'scope_0003'), (SELECT id FROM resource WHERE selector = 'ptf_0000')),
((SELECT id FROM scope WHERE name = 'scope_0003'), (SELECT id FROM resource WHERE selector = 'ptf_0001'));

