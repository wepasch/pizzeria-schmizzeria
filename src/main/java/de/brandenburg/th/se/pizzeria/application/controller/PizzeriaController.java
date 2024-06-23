package de.brandenburg.th.se.pizzeria.application.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.hibernate.hql.internal.ast.InvalidWithClauseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PizzeriaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PizzeriaController.class);

    @FXML
    private Tab tabSpeisekarte;

    @FXML
    private Tab tabGericht;

    @FXML
    public void initialize() {
        tabSpeisekarte.setText("Speisekarten");
        tabGericht.setText("Gerichte");
        try {
            FXMLLoader speisekarteLoader = new FXMLLoader(getClass().getResource("/de/brandenburg/th/se/pizzeria/application/speisekarte.fxml"));
            AnchorPane speisekarteContent = speisekarteLoader.load();
            tabSpeisekarte.setContent(speisekarteContent);

            FXMLLoader gerichtLoader = new FXMLLoader(getClass().getResource("/de/brandenburg/th/se/pizzeria/application/gericht.fxml"));
            AnchorPane gerichtContent = gerichtLoader.load();
            tabGericht.setContent(gerichtContent);
        } catch (IOException e) {
            LOGGER.error("Beim Laden der fxml-Dateien", e);
        }
    }
}
