package de.brandenburg.th.se.pizzeria.application;

import de.brandenburg.th.se.pizzeria.domain.Pizzeria;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class JFXMain extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(JFXMain.class);
    private static final String FXML_NAME = "de/brandenburg/th/se/pizzeria/application/pizzeria.fxml";

    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = getClass().getResource("/" + FXML_NAME);
        if (fxmlUrl == null) {
            LOGGER.error("Keine FXML-Datei unter '{}'.", "/" + FXML_NAME);
            return;
        } else {
            LOGGER.info("FXML-Datei '{}'.", fxmlUrl);
        }

        Parent page = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.setTitle(Pizzeria.NAME);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
