DROP TABLE IF EXISTS GERICHT;

CREATE TABLE GERICHT
(
    NAME  VARCHAR(255) PRIMARY KEY,
    PREIS DECIMAL(10, 2),
    TYP   VARCHAR(255),
    ZUTATEN VARCHAR(255)
);

INSERT INTO GERICHT (NAME, PREIS, TYP, ZUTATEN)
VALUES ('Pizza Margherita', 6.79, 'Pizza', 'Tomatensoße;Mozzarella;Basilikum'),
       ('Pizza Hawaii', 7.19, 'Pizza', 'Tomatensoße;Käse;Ananas;Schinken'),
       ('Pasta Alfredo', 5.49, 'Pasta', 'Fettuccine;Butter;Parmesan'),
       ('Spaghetti Pesto', 5.79, 'Pasta', 'Spaghetti;Pesto;Parmesan'),
       ('Cola', 0.29, 'Dessert', 'Cola'),
       ('Cuba Libre', 0.49, 'Dessert', 'Cola;Rum');