USE bookmyroom;
-- Ajout des roles 
Insert into role (name) values ('manager');
Insert into role (name) values ('receptionist');
Insert into role (name) values ('tenant');

-- Ajout adresse
Insert into address (city_id, address_line) values (1,'Rue du testing 1');

-- Ajout User

INSERT INTO user
(`role_id`,`address_id`,`username`,`password`,`firstname`,`lastname`,`phone_number`,`email`)
VALUES
(1,1,'lahcen','$shiro1$SHA-256$500000$SsOV1X29o20w3aWrVHBYMg==$aVCAylWzc1rgCbu/CjZ5zZQgEkSUoVi9vHNdmV1UuKo=','Lahcen','Boukhoubza','0489812322','mymail@nomail.com');

INSERT INTO user
(`role_id`,`address_id`,`username`,`password`,`firstname`,`lastname`,`phone_number`,`email`)
VALUES
    (2,1,'receptionist','$shiro1$SHA-256$500000$SsOV1X29o20w3aWrVHBYMg==$aVCAylWzc1rgCbu/CjZ5zZQgEkSUoVi9vHNdmV1UuKo=','Julien','Jules','071559899','receptionist@nomail.com');