package de.brandenburg.th.se.pizzeria.application.controller;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.domain.gericht.GerichtGroesse;
import de.brandenburg.th.se.pizzeria.domain.gericht.GerichtTyp;
import de.brandenburg.th.se.pizzeria.domain.gericht.LeerGerichtFactory;
import de.brandenburg.th.se.pizzeria.infrastructure.Enviroment;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepository;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;
import de.brandenburg.th.se.pizzeria.infrastructure.zutat.ZutatRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.zutat.ZutatRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.brandenburg.th.se.pizzeria.application.controller.ControllerUtils.istPreisEingabe;
import static de.brandenburg.th.se.pizzeria.domain.Pizzeria.CHAR_DEZIMAL_SEP;
import static de.brandenburg.th.se.pizzeria.domain.gericht.GerichtTyp.*;

public class GerichtController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GerichtController.class);

    private final ZutatRepository zutatRepo = new ZutatRepoH2(Enviroment.DEV);
    private final GerichtRepository gerichtRepo = new GerichtRepoH2(Enviroment.DEV);

    private Map<Gericht, Boolean> selectionMapGerichte = new HashMap<>();
    private Map<Zutat, Boolean> selectionMapZutaten = new HashMap<>();
    private List<Button> controllerBtns;
    private ToggleGroup typToggle;

    @FXML
    private ListView<Gericht> auswahlGerichte;
    @FXML
    private RadioButton typPizza;
    @FXML
    private RadioButton typPasta;
    @FXML
    private RadioButton typDessert;
    @FXML
    private ListView<Zutat> auswahlZutaten;
    @FXML
    private Button btnLadeGerichte;
    @FXML
    private Button btnLoeschGerichte;
    @FXML
    private Button btnGerichtAnzeigen;
    @FXML
    private Button btnAddGericht;
    @FXML
    private Button btnLadeZutaten;
    @FXML
    private Button btnLoeschZutaten;
    @FXML
    private Button btnAddZutat;
    @FXML
    private Button btnEditZutat;
    @FXML
    private Button btnAuswahlZutat;
    @FXML
    private TextField inputGerichtName;
    @FXML
    private TextField inputGerichtPreis;
    @FXML
    private TextField inputZutatName;
    @FXML
    private TextField inputZutatPreis;
    @FXML
    private TextField inputNeuerPreis;
    @FXML
    private Text txtEditZutat;
    @FXML
    private Text txtGerichtName;
    @FXML
    private Text txtGerichtTyp;
    @FXML
    private Text txtGerichtPreis;
    @FXML
    private Text txtGerichtZutaten;


    @FXML
    private void initialize() {
        controllerBtns = List.of(btnLadeGerichte, btnLoeschGerichte, btnGerichtAnzeigen, btnAddGericht, btnAddZutat,
                btnLadeZutaten, btnLoeschZutaten, btnEditZutat, btnAuswahlZutat);
        disableBtns(false);
        initializeGerichte();
        initializeZutaten();
    }

    @FXML
    public void initializeGerichte() {
        typToggle = new ToggleGroup();
        typPizza.setText(PIZZA.label);
        typPizza.setToggleGroup(typToggle);
        typPasta.setText(PASTA.label);
        typPasta.setToggleGroup(typToggle);
        typDessert.setText(DESSERT.label);
        typDessert.setToggleGroup(typToggle);
        typPizza.setSelected(true);
        ladeGerichte();
    }

    @FXML
    private void initializeZutaten() {
        inputNeuerPreis.setDisable(true);
        btnEditZutat.setDisable(true);
        inputNeuerPreis.setText("");
        txtEditZutat.setText("");
        ladeZutaten();
    }

    @FXML
    private void addGericht() {
        Gericht gerichtNeu = getInputGericht();
        if (gerichtNeu == null) return;
        try {
            gerichtRepo.addGericht(gerichtNeu);
        } catch (RepositoryException e) {
            LOGGER.error("Bei Hinzufügen neues Gerichts: {}", e.getMessage());
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        initialize();
    }

    @FXML
    private void ladeGerichte() {
        disableBtns(true);
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
        disableBtns(false);

    }


    @FXML
    private void ladeZutaten() {
        disableBtns(true);
        auswahlZutaten.setCellFactory(z -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Zutat zutat, boolean isEmpty) {
                super.updateItem(zutat, isEmpty);
                if (isEmpty || zutat == null) {
                    setGraphic(null);
                    setText(null);
                    checkBox.setOnAction(null);
                } else {
                    checkBox.setText(zutat.getName() + " (" + zutat.getBasisPreis() + ")");
                    checkBox.setSelected(selectionMapZutaten.getOrDefault(zutat, false));
                    checkBox.setOnAction(e -> selectionMapZutaten.put(zutat, checkBox.isSelected()));
                    setGraphic(checkBox);
                }
            }
        });
        List<Zutat> gladZutaten = zutatRepo.getAlleZutaten();
        auswahlZutaten.setItems(FXCollections.observableList(gladZutaten));
        gladZutaten.forEach(zutat -> selectionMapZutaten.put(zutat, false));
        selectionMapZutaten = new HashMap<>();
        disableBtns(false);
    }

    @FXML
    private void zeigeGericht() {
        List<Gericht> gewGericht = new ArrayList<>();
        selectionMapGerichte.forEach((gericht, check) -> {
            if (Boolean.TRUE.equals(check)) gewGericht.add(gericht);
        });
        if (gewGericht.size() != 1) {
            new Alert(Alert.AlertType.INFORMATION, "Zum Anzeigen ein Gericht auswählen").showAndWait();
            return;
        }
        Gericht gericht = gewGericht.getFirst();
        txtGerichtName.setText(gericht.getName());
        txtGerichtTyp.setText(gericht.getTyp());
        txtGerichtPreis.setText("klein: " + gericht.getPreis(GerichtGroesse.KLEIN) +
                "\nmittel: " + gericht.getPreis(GerichtGroesse.MITTEL) +
                "\ngroß: " + gericht.getPreis(GerichtGroesse.GROSS) +
                "\nBasis: " + gericht.getBasisPreis());
        txtGerichtZutaten.setText(String.join(", ", gericht.getZutatenNamen()));
    }

    @FXML
    public void loescheGerichte() {
        btnLoeschGerichte.setDisable(true);
        List<Gericht> zuLoeschGerichte = new ArrayList<>();
        selectionMapGerichte.forEach((gericht, check) -> {
            if (Boolean.TRUE.equals(check)) zuLoeschGerichte.add(gericht);
        });
        for (Gericht gericht : zuLoeschGerichte) {
            try {
                gerichtRepo.entferneGericht(gericht);
            } catch (RepositoryException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        }
        ladeGerichte();
        btnLoeschGerichte.setDisable(false);

    }

    @FXML
    public void loeschZutaten() {
        btnLoeschZutaten.setDisable(true);
        List<Zutat> zuLoeschZutaten = new ArrayList<>();
        selectionMapZutaten.forEach((zutat, check) -> {
            if (Boolean.TRUE.equals(check)) zuLoeschZutaten.add(zutat);
        });
        for (Zutat zutat : zuLoeschZutaten) {
            try {
                zutatRepo.entferneZutat(zutat);
            } catch (RepositoryException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        }
        ladeZutaten();
        btnLoeschZutaten.setDisable(false);
    }

    public void addZutaten() {
        disableBtns(true);
        Zutat zutatNeu = getInputZutat();
        if (zutatNeu == null) {
            disableBtns(false);
            return;
        }
        try {
            zutatRepo.addZutat(zutatNeu);
        } catch (RepositoryException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        inputZutatName.setText("");
        inputZutatPreis.setText("");
        ladeZutaten();
        disableBtns(false);
    }

    @FXML
    public void editZutatA() {
        List<Zutat> gewZutaten = new ArrayList<>();
        selectionMapZutaten.forEach((zutat, check) -> {
            if (Boolean.TRUE.equals(check)) gewZutaten.add(zutat);
        });
        if (gewZutaten.size() != 1) {
            new Alert(Alert.AlertType.INFORMATION, "Zum Bearbeiten eine Zutat auswählen").showAndWait();
            return;
        }
        Zutat zutatAuswahl = gewZutaten.getFirst();
        txtEditZutat.setText(zutatAuswahl.getName());
        inputNeuerPreis.setText(zutatAuswahl.getBasisPreis().getWert().toString());
        inputNeuerPreis.setDisable(false);
        ladeZutaten();
        disableBtns(true);
        btnEditZutat.setDisable(false);
    }

    @FXML
    public void editZutatB() {
        String preisNeuStr = inputNeuerPreis.getCharacters().toString().replace(CHAR_DEZIMAL_SEP, '.');
        if (istPreisEingabe(preisNeuStr)) {
            try {
                double preisNeu = Double.parseDouble(preisNeuStr);
                Zutat neueZutat = new Zutat(txtEditZutat.getText(), new Preis(preisNeu));
                zutatRepo.aktualisiereZutat(neueZutat);
            } catch (RepositoryException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        }
        initializeZutaten();

    }

    private Gericht getInputGericht() {
        String nameNeu = inputGerichtName.getText().trim();
        if (nameNeu.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Kein Gerichtname angegeben").showAndWait();
            return null;
        }
        String preisNeu = inputGerichtPreis.getText().replace(CHAR_DEZIMAL_SEP, '.');
        if (!istPreisEingabe(preisNeu)) {
            return null;
        }
        GerichtTyp typNeu;
        RadioButton ausgTyp = (RadioButton) typToggle.getSelectedToggle();
        String typAuswahl = ausgTyp.getText();
        switch (typAuswahl) {
            case "Pizza" -> typNeu = PIZZA;
            case "Pasta" -> typNeu = PASTA;
            case "Dessert" -> typNeu = DESSERT;
            default -> {
                new Alert(Alert.AlertType.ERROR, "Gerichttyp Auswahl überprüfen").showAndWait();
                return null;
            }
        }
        Gericht gericht = new LeerGerichtFactory().erstelleGerichtVon(nameNeu, new Preis(Double.parseDouble(preisNeu)),
                typNeu);
        List<Zutat> zutatenNeu = new ArrayList<>();
        selectionMapZutaten.forEach((zutat, check) -> {
            if (Boolean.TRUE.equals(check)) zutatenNeu.add(zutat);
        });
        for (Zutat zutat : zutatenNeu) gericht.addZutat(zutat);
        return gericht;
    }

    private Zutat getInputZutat() {
        String nameNeu = inputZutatName.getCharacters().toString();
        nameNeu = nameNeu.trim();
        String preisNeuStr = inputZutatPreis.getCharacters().toString();
        preisNeuStr = preisNeuStr.replace(CHAR_DEZIMAL_SEP, '.');
        if (nameNeu.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Name für Zutat eingeben").showAndWait();
            disableBtns(false);
            return null;
        }
        if (!istPreisEingabe(preisNeuStr)) {
            disableBtns(false);
            return null;
        }
        double preisNeu = Double.parseDouble(preisNeuStr);
        Zutat zutatNeu;
        try {
            zutatNeu = new Zutat(nameNeu, new Preis(preisNeu));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            disableBtns(false);
            return null;
        }
        return zutatNeu;
    }

    private void disableBtns(boolean wert) {
        controllerBtns.forEach(b -> b.setDisable(wert));
    }
}
