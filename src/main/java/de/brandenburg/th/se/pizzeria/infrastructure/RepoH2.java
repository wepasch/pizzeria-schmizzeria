package de.brandenburg.th.se.pizzeria.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RepoH2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepoH2.class);

    public static final String DB_URL_DEV = "jdbc:h2:file:./db/pizzeria";
    public static final String DB_URL_TEST = "jdbc:h2:file:./db/pizzeriaTest";
    public static final String DB_USER = "user";
    public static final String DB_PW = "user";
    public static final String SEPARATOR_STRINGS = ";";

    public static final String TABELLE_ZUTAT = "Zutat";
    public static final String TABELLE_GERICHT = "Gericht";
    public static final String TABELLE_SPEISEKARTE = "Speisekarte";

    private final Enviroment enviroment;

    public RepoH2(Enviroment enviroment) {
        this.enviroment = enviroment;
    }

    public Connection getConnection() throws SQLException {
        LOGGER.info("Versuche Verbindung mit Environment {}.", enviroment.label);
        return switch (enviroment) {
            case Enviroment.DEV -> DriverManager.getConnection(DB_URL_DEV, DB_USER, DB_PW);
            case Enviroment.TEST -> DriverManager.getConnection(DB_URL_TEST, DB_USER, DB_PW);
        };
    }
}
