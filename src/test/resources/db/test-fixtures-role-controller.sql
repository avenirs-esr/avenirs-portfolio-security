
INSERT INTO context (id, validity_start, validity_end)
VALUES
    (
    '00000000-0000-0000-0000-000000000001',
    to_timestamp('01/09/2024', 'DD/MM/YYYY'),
    null
    ),
    (
       '00000000-0000-0000-0000-000000000002',
       to_timestamp('01/09/2024', 'DD/MM/YYYY'),
       null
    );

INSERT INTO assignment (id, id_role, id_principal, id_scope, id_context)
VALUES
    (
        '00000000-0000-0000-0000-000000000001',
        (SELECT id FROM role WHERE name = 'ROLE_PAIR'),
        (SELECT id FROM principal WHERE login = 'deman'),
        '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001'
    ),
    (
        '00000000-0000-0000-0000-000000000002',
        (SELECT id FROM role WHERE name = 'ROLE_CONTRIBUTOR'),
        (SELECT id FROM principal WHERE login = 'deman'),
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000002'
    );



