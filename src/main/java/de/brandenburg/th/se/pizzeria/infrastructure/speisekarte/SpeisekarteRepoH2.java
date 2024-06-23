package de.brandenburg.th.se.pizzeria.infrastructure.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.Speisekarte;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.SpeisekarteBuilder;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.SpeisekarteBuilderInterface;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.SpeisekarteSaison;
import de.brandenburg.th.se.pizzeria.infrastructure.Enviroment;
import de.brandenburg.th.se.pizzeria.infrastructure.RepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepository;
import de.brandenburg.th.se.pizzeria.infrastructure.zutat.ZutatRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.zutat.ZutatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpeisekarteRepoH2 extends RepoH2 implements SpeisekarteRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeisekarteRepoH2.class);
    private static final String SPALTE_NAME = "Name";
    private static final String SPALTE_SAISON = "Saison";
    private static final String SPALTE_GERICHTE = "Gerichte";
    private static final int COL_NAME = 1;
    private static final int COL_SAISON = 2;
    private static final int COL_GERICHTE = 3;

    public SpeisekarteRepoH2(Enviroment enviroment) {
        super(enviroment);
    }

    @Override
    public Speisekarte mitNamen(String name) throws RepositoryException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name für Speisekarte darf nicht leer sein");
        }
        List<Speisekarte> ergebnis;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + TABELLE_SPEISEKARTE + " WHERE " + SPALTE_NAME + "=?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            ergebnis = resultSetZuListe(rs);
        } catch (SQLException e) {
            LOGGER.error("Speisekarte mit Namen '{}' kann nicht gefunden werden, da {}", name, e.getMessage());
            return null;
        }
        if (ergebnis.isEmpty()) {
            throw new RepositoryException("Speisekarte mit Namen '" + name + "' existiert nicht");
        }
        return ergebnis.getFirst();
    }

    @Override
    public List<Speisekarte> getAlleSpeisekarten() throws RepositoryException {
        List<Speisekarte> ergebnis;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + TABELLE_SPEISEKARTE)) {
            ResultSet rs = ps.executeQuery();
            ergebnis = resultSetZuListe(rs);
        } catch (SQLException e) {
            LOGGER.error("Speisekarten können nicht geladen werden, da {}", e.getMessage());
            throw new RepositoryException("Speisekarten können nicht geladen");
        }
        return ergebnis;
    }

    @Override
    public void addSpeisekarte(Speisekarte speisekarte) throws RepositoryException {
        Speisekarte speisekarteAlt = null;
        try {
            speisekarteAlt = mitNamen(speisekarte.getName());
        } catch (RepositoryException e) {
            LOGGER.info("Speisekarte mit Namen '{}' existiert noch nicht.", speisekarte.getName());
        }
        if (speisekarteAlt != null) {
            LOGGER.info("Speisekarte mit Namen '{}' existiert bereits und kann nicht neu angelegt werden.",
                    speisekarte.getName());
            throw new RepositoryException("Speisekarte mit Name '" + speisekarte.getName() + "' existiert bereits.");
        }
        String gerichteStrings = String.join(SEPARATOR_STRINGS, speisekarte.getGerichte().stream()
                .map(Gericht::getName).toList());
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABELLE_SPEISEKARTE +
                     " (" + SPALTE_NAME + ", " + SPALTE_SAISON + ", " + SPALTE_GERICHTE + ") " +
                     "VALUES (?, ?, ?)")) {
            ps.setString(1, speisekarte.getName());
            ps.setString(2, speisekarte.getSaison().label);
            ps.setString(3, gerichteStrings);
            int ret = ps.executeUpdate();
            if (ret != 1) {
                LOGGER.error("Speisekarte '{}' kann nicht hinzugefügt werden, Returncode ist {}.",
                        speisekarte.getName(), ret);
                throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' nicht hinzugefügt");
            }
            LOGGER.info("Speisekarte '{}' hinzugefügt.", speisekarte.getName());

        } catch (SQLException e) {
            LOGGER.error("Speisekarte '{}' nicht hinzugefügt, da {}", speisekarte.getName(), e.getMessage());
            throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' nicht hinzugefügt");

        }

    }

    @Override
    public void aktualisiereSpeisekarte(Speisekarte speisekarte) throws RepositoryException {
        Speisekarte speisekarteAlt;
        try {
            speisekarteAlt = mitNamen(speisekarte.getName());
        } catch (RepositoryException e) {
            LOGGER.error("Speisekarte mit Namen '{}' existiert nicht.", speisekarte.getName());
            throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' existiert nicht " +
                    "und kann nicht geändert werden");
        }
        if (speisekarteAlt == null) {
            LOGGER.error("Speisekarte mit Namen '{}' existiert nicht.", speisekarte.getName());
            throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' existiert nicht " +
                    "und kann nicht geändert werden");
        }
        try {
            macheGerichteVerfuegbar(speisekarte.getGerichte());
        } catch (RepositoryException e) {
            LOGGER.error("Gerichte für Speisekarte '{}' können nicht gespeichert werden", speisekarte.getName());
            throw new RepositoryException("Gerichte für Speisekarte '" + speisekarte.getName() +
                    "' können nicht gespeichert werden");
        }
        String gerichteString = String.join(SEPARATOR_STRINGS,
                speisekarte.getGerichte().stream().map(Gericht::getName).toList());
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + TABELLE_SPEISEKARTE + " SET " + SPALTE_SAISON + "=?, " + SPALTE_GERICHTE + "=? " +
                             "WHERE " + SPALTE_NAME + "=?")) {
            ps.setString(1, speisekarte.getSaison().label);
            ps.setString(2, String.join(SEPARATOR_STRINGS, gerichteString));
            ps.setString(3, speisekarte.getName());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Speisekarte '{} aktualisiert", speisekarte.getName());
            } else {
                LOGGER.error("Änderung von Speisekarte '{}' resultiert in Antwortcode von {}.",
                        speisekarte.getName(), ret);
                throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' nicht geändert");
            }
        } catch (SQLException e) {
            LOGGER.error("bei Aktualisierung von '{}': {}", speisekarte.getName(), e.getMessage());
            throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' kann nicht verändert werden");
        }

    }

    @Override
    public void entferneSpeisekarte(Speisekarte speisekarte) throws RepositoryException {
        Speisekarte speisekarteAlt;
        try {
            speisekarteAlt = mitNamen(speisekarte.getName());
        } catch (RepositoryException e) {
            LOGGER.info("Speisekarte existiert nicht.");
            return;
        }
        if (speisekarteAlt == null) {
            LOGGER.info("Speisekarte existiert nicht.");
            return;
        }
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABELLE_SPEISEKARTE +
                     " WHERE " + SPALTE_NAME + "=?")) {
            ps.setString(1, speisekarte.getName());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.info("Speisekarte '{} entfernt", speisekarte.getName());
            } else {
                throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' nicht entfernt");
            }

        } catch (SQLException e) {
            LOGGER.error("Bei Entfernen von Speisekarte '{}': {}.", speisekarte.getName(), e.getMessage());
            throw new RepositoryException("Speisekarte '" + speisekarte.getName() + "' nicht entfernt");
        }

    }

    private static List<Speisekarte> resultSetZuListe(ResultSet rs) throws RepositoryException, SQLException {
        List<Speisekarte> liste = new ArrayList<>();

        while (rs.next()) {
            SpeisekarteBuilderInterface bauer = new SpeisekarteBuilder();
            bauer.benennen(rs.getString(COL_NAME));
            bauer.kategorisieren(SpeisekarteSaison.ausLabel(rs.getString(COL_SAISON)));
            List<Gericht> gerichte = sammelGerichte(rs.getString(COL_GERICHTE));
            bauer.befuellen(gerichte);
            Speisekarte speisekarte = bauer.anlegen();
            liste.add(speisekarte);
        }
        return liste;
    }

    private static List<Gericht> sammelGerichte(String gerichtStrings) throws RepositoryException {
        if (gerichtStrings == null || gerichtStrings.isEmpty()) {
            return new ArrayList<>();
        }
        String[] zutatStrings = gerichtStrings.split("\\s*" + SEPARATOR_STRINGS + "\\s*");
        GerichtRepository gerichtRepo = new GerichtRepoH2(Enviroment.DEV);
        List<Gericht> gerichte = new ArrayList<>();
        for (String zutatString : zutatStrings) {
            Gericht gericht = gerichtRepo.getMitNamen(zutatString);
            gerichte.add(gericht);
        }
        return gerichte;
    }

    private static void macheGerichteVerfuegbar(List<Gericht> gerichte) throws RepositoryException {
        GerichtRepository gerichtRepo = new GerichtRepoH2(Enviroment.DEV);
        for (Gericht gericht : gerichte) {
            try {
                gerichtRepo.getMitNamen(gericht.getName());
            } catch (RepositoryException eV) {
                LOGGER.info("Gericht '{}' ist nicht verfügbar und wird ins Repo hinzugefügt", gericht.getName());
                try {
                    gerichtRepo.addGericht(gericht);
                } catch (RepositoryException eN) {
                    LOGGER.error("Gericht '{}' kann nicht ins Repo hinzugefügt werden.", gericht.getName());
                    throw new RepositoryException("Gericht '" + gericht.getName() + "' kann nicht gespeichert werden");
                }
            }
        }
    }
}
