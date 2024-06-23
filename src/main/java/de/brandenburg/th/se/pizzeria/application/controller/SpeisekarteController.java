package de.brandenburg.th.se.pizzeria.application.controller;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.domain.gericht.GerichtGroesse;
import de.brandenburg.th.se.pizzeria.domain.gericht.GerichtTyp;
import de.brandenburg.th.se.pizzeria.domain.gericht.LeerGerichtFactory;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.Speisekarte;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.SpeisekarteBuilder;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.SpeisekarteSaison;
import de.brandenburg.th.se.pizzeria.infrastructure.Enviroment;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepository;
import de.brandenburg.th.se.pizzeria.infrastructure.speisekarte.SpeisekarteRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.speisekarte.SpeisekarteRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.brandenburg.th.se.pizzeria.application.controller.ControllerUtils.istPreisEingabe;
import static de.brandenburg.th.se.pizzeria.domain.Pizzeria.CHAR_DEZIMAL_SEP;
import static de.brandenburg.th.se.pizzeria.domain.gericht.GerichtTyp.*;

public class SpeisekarteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeisekarteController.class);
    private final SpeisekarteRepository speisekarteRepo = new SpeisekarteRepoH2(Enviroment.DEV);
    private final GerichtRepository gerichtRepo = new GerichtRepoH2(Enviroment.DEV);
    private Map<Speisekarte, Boolean> selectionMapSpeisekarten = new HashMap<>();
    private Map<Gericht, Boolean> selectionMapGerichte = new HashMap<>();

    @FXML
    private ListView<Speisekarte> auswahlSpeisekarten;
    @FXML
    private ListView<Gericht> auswahlGerichte;
    @FXML
    private Text txtSpeisekarteName;
    @FXML
    private Text txtSpeisekartePizza;
    @FXML
    private Text txtSpeisekarteSaison;
    @FXML
    private Text txtSpeisekartePasta;
    @FXML
    private Text txtSpeisekarteDessert;
    @FXML
    private TextField inputSpeisekarteName;
    @FXML
    private TextField inputSpeisekarteSaison;


    @FXML
    private void initialize() {
        ladeSpeisekarten();
        ladeGerichte();
    }

    @FXML
    private void ladeSpeisekarten() {
        auswahlSpeisekarten.setCellFactory(z -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Speisekarte speisekarte, boolean isEmpty) {
                super.updateItem(speisekarte, isEmpty);
                if (isEmpty || speisekarte == null) {
                    setGraphic(null);
                    setText(null);
                    checkBox.setOnAction(null);
                } else {
                    checkBox.setText(speisekarte.getName() + " (" + speisekarte.getSaison().label + ")");
                    checkBox.setSelected(selectionMapSpeisekarten.getOrDefault(speisekarte, false));
                    checkBox.setOnAction(e -> selectionMapSpeisekarten.put(speisekarte, checkBox.isSelected()));
                    setGraphic(checkBox);
                }
            }
        });
        List<Speisekarte> gladSpeisekarten = List.of();
        try {
            gladSpeisekarten = speisekarteRepo.getAlleSpeisekarten();
        } catch (RepositoryException e) {
            new Alert(Alert.AlertType.ERROR, "Speisekarten können nicht geladen werden.").showAndWait();
        }
        auswahlSpeisekarten.setItems(FXCollections.observableList(gladSpeisekarten));
        gladSpeisekarten.forEach(speisekarte -> selectionMapSpeisekarten.put(speisekarte, false));
        selectionMapGerichte = new HashMap<>();

    }

    @FXML
    private void ladeGerichte() {
        auswahlGerichte.setCellFactory(z -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Gericht gericht, boolean isEmpty) {
                super.updateItem(gericht, isEmpty);
                if (isEmpty || gericht == null) {
                    setGraphic(null);
                    setText(null);
                    checkBox.setOnAction(null);
                } else {
                    checkBox.setText(gericht.getName() + " (" + gericht.getBasisPreis() + ")");
                    checkBox.setSelected(selectionMapGerichte.getOrDefault(gericht, false));
                    checkBox.setOnAction(e -> selectionMapGerichte.put(gericht, checkBox.isSelected()));
                    setGraphic(checkBox);
                }
            }
        });
        List<Gericht> gladGerichte = List.of();
        try {
            gladGerichte = gerichtRepo.getAlleGerichte();
        } catch (RepositoryException e) {
            new Alert(Alert.AlertType.ERROR, "Gerichte können nicht geladen werden.").showAndWait();
        }
        auswahlGerichte.setItems(FXCollections.observableList(gladGerichte));
        gladGerichte.forEach(gericht -> selectionMapGerichte.put(gericht, false));
        selectionMapGerichte = new HashMap<>();
    }

    @FXML
    private void loescheSpeisekarten() {
        List<Speisekarte> zuLoeschSpeisekarten = new ArrayList<>();
        selectionMapSpeisekarten.forEach((speisekarte, check) -> {
            if (Boolean.TRUE.equals(check)) zuLoeschSpeisekarten.add(speisekarte);
        });
        for (Speisekarte speisekarte : zuLoeschSpeisekarten) {
            try {
                speisekarteRepo.entferneSpeisekarte(speisekarte);
            } catch (RepositoryException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        }
        selectionMapSpeisekarten = new HashMap<>();
        ladeSpeisekarten();
    }

    @FXML
    private void zeigeSpeisekarte() {
        List<Speisekarte> gewSpeisekarte = new ArrayList<>();
        selectionMapSpeisekarten.forEach((gericht, check) -> {
            if (Boolean.TRUE.equals(check)) gewSpeisekarte.add(gericht);
        });
        if (gewSpeisekarte.size() != 1) {
            new Alert(Alert.AlertType.INFORMATION, "Zum Anzeigen eine Speisekarte auswählen").showAndWait();
            return;
        }
        Map<GerichtTyp, List<String>> gerichte = new EnumMap<>(GerichtTyp.class);
        for (GerichtTyp typ: GerichtTyp.values()) gerichte.put(typ, new ArrayList<>());
        Speisekarte speisekarte = gewSpeisekarte.getFirst();
        for (Gericht gericht: speisekarte.getGerichte()) gerichte.get(gericht.getGerichtTyp()).add(gericht.getName());
        txtSpeisekarteName.setText(speisekarte.getName());
        txtSpeisekarteSaison.setText(speisekarte.getSaison().label);
        txtSpeisekartePizza.setText(String.join(", ", gerichte.get(GerichtTyp.PIZZA)));
        txtSpeisekartePasta.setText(String.join(", ", gerichte.get(GerichtTyp.PASTA)));
        txtSpeisekarteDessert.setText(String.join(", ", gerichte.get(GerichtTyp.DESSERT)));
    }

    @FXML
    private void addSpeisekarte() {
        Speisekarte speisekarte = getInputSpeisekarte();
        if (speisekarte == null) return;
        try {
            speisekarteRepo.addSpeisekarte(speisekarte);
            inputSpeisekarteName.setText("");
            inputSpeisekarteSaison.setText("");
            ladeSpeisekarten();
        } catch (RepositoryException e) {
            new Alert(Alert.AlertType.ERROR, "Speisekarte konnte nicht hinzugefügt werden.").showAndWait();
        }
    }

    private Speisekarte getInputSpeisekarte() {
        String nameNeu = inputSpeisekarteName.getText().trim();
        if (nameNeu.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Kein Speisekartentname angegeben").showAndWait();
            return null;
        }
        String saisonNeuString = inputSpeisekarteSaison.getText().trim();
        SpeisekarteSaison saisonNeu;
        try {
            saisonNeu = SpeisekarteSaison.ausLabel(saisonNeuString);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Saison '{}' nicht verfügbar, da {}", saisonNeuString, e.getMessage());
            String verfuegbareSaisons = String.join(", ", Arrays.stream(SpeisekarteSaison
                    .values())
                    .map(s -> s.label)
                    .toList());
            new Alert(Alert.AlertType.ERROR, "'" + saisonNeuString + "' ist keine Saison (verfügbar sind: " +
                    verfuegbareSaisons+ ")").showAndWait();
            return null;
        }
        List<Gericht> gerichteNeu = new ArrayList<>();
        selectionMapGerichte.forEach((gericht, check) -> {
            if (Boolean.TRUE.equals(check)) gerichteNeu.add(gericht);
        });
        return new SpeisekarteBuilder()
                .benennen(nameNeu)
                .kategorisieren(saisonNeu)
                .befuellen(gerichteNeu)
                .anlegen();
    }


}
