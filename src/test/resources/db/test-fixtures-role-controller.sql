
INSERT INTO context (id, validity_start, validity_end)
VALUES 
(1, to_timestamp('01/09/2024', 'DD/MM/YYYY'),null);


SELECT setval((SELECT pg_get_serial_sequence('context', 'id')), (SELECT MAX(id) FROM context));



INSERT INTO assignment (id_role, id_principal, id_scope, id_context)
VALUES 
((SELECT id FROM role WHERE name = 'ROLE_OWNER'), (SELECT id FROM principal WHERE login = 'deman'), (SELECT id FROM scope WHERE name = 'scope_0000'), 1),
((SELECT id FROM role WHERE name = 'ROLE_PAIR'), (SELECT id FROM principal WHERE login = 'deman'), (SELECT id FROM scope WHERE name = 'scope_0001'), 1);



