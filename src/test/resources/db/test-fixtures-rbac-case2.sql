
INSERT INTO context (id, validity_start, validity_end)
VALUES 
(2, to_timestamp('01/04/2025', 'DD/MM/YYYY'),to_timestamp('15/04/2025', 'DD/MM/YYYY'));


SELECT setval((SELECT pg_get_serial_sequence('context', 'id')), (SELECT MAX(id) FROM context));

INSERT INTO assignment (id_role, id_principal, id_scope, id_context)
VALUES 
((SELECT id FROM role WHERE name = 'ROLE_PAIR'), (SELECT id FROM principal WHERE login = 'gribonvald'), (SELECT id FROM scope WHERE name = 'scope_0001'), 2);



