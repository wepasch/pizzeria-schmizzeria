DROP TABLE IF EXISTS SPEISEKARTE;

CREATE TABLE SPEISEKARTE
(
    NAME  VARCHAR(255) PRIMARY KEY,
    SAISON VARCHAR(255),
    GERICHTE VARCHAR(255)
);

INSERT INTO SPEISEKARTE (NAME, SAISON, GERICHTE)
VALUES ('Haupt 2024', 'Standard', 'Pizza Margherita;Pizza Hawaii;Pasta Alfredo;Spaghetti Pesto;Cola;Cuba Libre');