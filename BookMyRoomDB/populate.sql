USE bookmyroom;
-- Ajout des roles 
Insert into role (Name) values ('manager');
Insert into role (Name) values ('receptionist');
Insert into role (Name) values ('tenant');

-- Ajout adresse
Insert into addresse (CityID, AddressLine) values (1,'Rue du testing 1');

-- Ajout User

INSERT INTO user
(`RoleID`,`AdresseID`,`UserName`,`Password`,`FirstName`,`LastName`,`PhoneNumber`,`Email`)
VALUES
(1,1,'lahcen','test','Lahcen','Boukhoubza','0489812322','mymail@mail.com');