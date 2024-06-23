package de.brandenburg.th.se.pizzeria.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBReset {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBReset.class);

    public static final String INIT_DEV = "db/sql/init_dev.sql";
    public static final String INIT_TEST = "db/sql/init_test.sql";

    public static void main(String[] args) {
        resetFromFile(RepoH2.DB_URL_DEV, RepoH2.DB_USER, RepoH2.DB_PW, INIT_DEV);
        resetFromFile(RepoH2.DB_URL_TEST, RepoH2.DB_USER, RepoH2.DB_PW, INIT_TEST);
    }

    public static void resetFromFile(String url, String user, String pw, String path) {
        try (Connection connection = DriverManager.getConnection(url, user, pw);
             Statement statement = connection.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get(path)));
            statement.execute(sql);
        } catch (SQLException | IOException e) {
            LOGGER.error("Beim db Reset mit {}: {}", path, e.getMessage());
        }
    }
}
