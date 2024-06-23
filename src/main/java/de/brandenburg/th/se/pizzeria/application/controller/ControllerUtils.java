package de.brandenburg.th.se.pizzeria.application.controller;

import javafx.scene.control.Alert;

public class ControllerUtils {
    private ControllerUtils(){}


    public static boolean istPreisEingabe(String s) {
        if (s.isEmpty() || !s.matches("\\d*\\.?\\d{0,2}")) {
            new Alert(Alert.AlertType.ERROR, "Preis eingeben (z.B. 0,99)").showAndWait();
            return false;
        }
        return true;
    }
}
