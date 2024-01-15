INSERT IGNORE INTO role (name) VALUES ('SUPER');
INSERT IGNORE INTO role (name) VALUES ('ADMIN');
INSERT IGNORE INTO role (name) VALUES ('USER');

INSERT IGNORE INTO user
    (id, email, firstname, lastname, password)
VALUES
    (1, 'robin.foutel@sii.fr', 'Robin', 'Foutel', '$2y$10$Rd4/r3gRbLsZ8ju.Zx.Hm.ZFvaklg2ziJ6G3RMvrelGMgW1dL4AI6');

INSERT IGNORE INTO user_role
    (role_id, user_id)
VALUES (
        (SELECT id FROM role WHERE name LIKE 'SUPER'), (SELECT id FROM user WHERE email LIKE 'robin.foutel@sii.fr')
);


INSERT IGNORE INTO user
(id, email, firstname, lastname, password)
VALUES
    (2, 'test.test@test.com', 'John', 'Doe', '$2y$10$kUeYm4IN38ALaBL5vxZKPO9/2VdSmm4eT3MZySLWenuLyhLWyzS0e');

INSERT IGNORE INTO user_role
(role_id, user_id)
VALUES (
           (SELECT id FROM role WHERE name LIKE 'USER'), (SELECT id FROM user WHERE email LIKE 'test.test@test.com')
       );

INSERT IGNORE INTO address
(id, name, zipcode)
VALUES
    (1, 'Paris', '75000'),
    (2, 'Marseille', '13000'),
    (3, 'Grenoble', '38000'),
    (4, 'Bordeau', '30072'),
    (5, 'Rouen', '76000'),
    (6, 'Lyon', '69000'),
    (7, 'Caen', '14000'),
    (8, 'Carhaix', '29024');


