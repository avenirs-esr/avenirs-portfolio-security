
INSERT INTO context (id, validity_start, validity_end)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    DATEADD('DAY', -1, CURRENT_TIMESTAMP),
    DATEADD('DAY', 1, CURRENT_TIMESTAMP)
);

INSERT INTO assignment (id, id_role, id_principal, id_scope, id_context)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    (SELECT id FROM role WHERE name = 'ROLE_PAIR'),
    (SELECT id FROM principal WHERE login = 'gribonvald'),
    (SELECT id FROM scope WHERE name = 'scope_0001'),
    '00000000-0000-0000-0000-000000000002'
);



