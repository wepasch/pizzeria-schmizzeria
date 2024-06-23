package de.brandenburg.th.se.pizzeria.infrastructure.zutat;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.infrastructure.*;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.gericht.GerichtRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ZutatRepoH2 extends RepoH2 implements ZutatRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZutatRepoH2.class);

    private static final String SPALTE_NAME = "Name";
    private static final String SPALTE_PREIS = "Preis";
    private static final int COL_NAME = 1;
    private static final int COL_PREIS = 2;

    public ZutatRepoH2(Enviroment enviroment) {
        super(enviroment);
    }

    @Override
    public Zutat getMitNamen(String name) throws IllegalArgumentException, RepositoryException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name für Zutat darf nicht leer sein");
        }
        List<Zutat> ergebnis;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + TABELLE_ZUTAT + " WHERE " + SPALTE_NAME + "=?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            ergebnis = resultSetZuListe(rs);
        } catch (SQLException e) {
            LOGGER.error("Zutat mit Namen '{}' kann nicht gefunden werden, da {}", name, e.getMessage());
            return null;
        }
        if (ergebnis.isEmpty()) {
            throw new RepositoryException("Zutat mit Namen '" + name + "' existiert nicht");
        }
        return ergebnis.getFirst();
    }

    @Override
    public List<Zutat> getAlleZutaten() {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + TABELLE_ZUTAT)) {
            ResultSet rs = ps.executeQuery();
            List<Zutat> ergebnis = resultSetZuListe(rs);
            ergebnis.sort(Comparator.comparing(Zutat::getName));
            return ergebnis;
        } catch (SQLException e) {
            LOGGER.error("Anfrage nach allen Zutaten nicht möglich, da {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void addZutat(Zutat zutat) throws RepositoryException {
        Zutat alteZutat = null;
        try {
            alteZutat = getMitNamen(zutat.getName());
        } catch (RepositoryException e) {
            LOGGER.info("Zutat '{}' kann hinzugefügt werden, da sie noch nicht existiert", zutat.getName());
        }
        if (alteZutat != null) {
            throw new RepositoryException("Zutat mit Namen '" + zutat.getName() + "' existiert bereits");
        }
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO " + TABELLE_ZUTAT + " (" + SPALTE_NAME + ", " + SPALTE_PREIS + ") " +
                             "VALUES (?, ?)")) {
            ps.setString(1, zutat.getName());
            ps.setDouble(2, zutat.getBasisPreis().getWert().doubleValue());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Zutat '{} gespeichert", zutat);
            } else {
                LOGGER.error("Hinzufügen von Zutat '{}' resultiert in Antwortcode von {}.", zutat, ret);
                throw new RepositoryException("Zutat '" + zutat.getName() + "' nicht hinzugefügt");
            }
        } catch (SQLException e) {
            LOGGER.error("Hinzufügen von Zutat '{}' nicht möglich, da {}", zutat, e.getMessage());
            throw new RepositoryException("Zutat '" + zutat.getName() + "' nicht hinzugefügt");
        }
    }

    @Override
    public void aktualisiereZutat(Zutat zutat) throws RepositoryException {
        Zutat alteZutat;
        try {
            alteZutat = getMitNamen(zutat.getName());
        } catch (RepositoryException e) {
            throw new RepositoryException("Zutat '" + zutat.getName() + "' kann nicht verändert werden");
        }
        if (alteZutat.equals(zutat)) {
            LOGGER.info("Zutat '{}' bereits gespeichert.", zutat);
            return;
        }
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + TABELLE_ZUTAT + " SET " + SPALTE_PREIS + "=? " +
                             "WHERE " + SPALTE_NAME + "=?")) {
            ps.setDouble(1, zutat.getBasisPreis().getWert().doubleValue());
            ps.setString(2, zutat.getName());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Zutat auf '{}' aktualisiert", zutat);
            } else {
                LOGGER.error("Aktualisierung auf Zutat '{}' resultiert in Antwortcode von {}.", zutat, ret);
                throw new RepositoryException("Zutat '" + zutat + "' nicht aktualisiert");
            }
        } catch (SQLException e) {
            LOGGER.error("Aktualisierung auf Zutat '{}' nicht möglich, da {}", zutat, e.getMessage());
            throw new RepositoryException("Zutat '" + zutat + "' nicht aktualisiert");
        }
    }

    @Override
    public void entferneZutat(Zutat zutat) throws RepositoryException {
        Zutat alteZutat;
        try {
            alteZutat = getMitNamen(zutat.getName());
        } catch (RepositoryException e) {
            return;
        }
        if (!alteZutat.equals(zutat)) {
            LOGGER.error("Zutat '{}' nicht entfernt, da die gespeicherte Zutat " +
                    "ihr nicht gleich ist: {}", zutat.getName(), alteZutat.getBasisPreis());
            throw new RepositoryException("Zutat '" + zutat + "' nicht entfernt, da sie als '" +
                    alteZutat + "' gespeichert ist");
        }
        if (istInGericht(zutat)) {
            LOGGER.error("Zutat '{}' wird nicht gelöscht, da sie in Gerichten enthalten ist.", zutat);
            throw new RepositoryException(zutat.getName() + " ist in Gerichten enthalten");
        }
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM " + TABELLE_ZUTAT + " WHERE " + SPALTE_NAME + "=? ")) {
            ps.setString(1, zutat.getName());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Zutat '{}' entfernt", zutat);
            } else {
                LOGGER.error("Entfernung von Zutat '{}' resultiert in Antwortcode von {}.", zutat, ret);
                throw new RepositoryException("Zutat mit Namen '" + zutat.getName() +
                        "' nicht entfernt");
            }
        } catch (SQLException e) {
            LOGGER.error("Entfernung von Zutat '{}' nicht möglich, da {}", zutat, e.getMessage());
            throw new RepositoryException("Zutat mit Namen '" + zutat.getName() +
                    "' nicht entfernt");
        }
    }

    private boolean istInGericht(Zutat zutat) {
        GerichtRepository gerichtRepo = new GerichtRepoH2(Enviroment.DEV);
        try {
            List<Gericht> gerichte = gerichtRepo.getAlleGerichte();
            for (Gericht g : gerichte) {
                if (g.getZutatenNamen().contains(zutat.getName())) {
                    LOGGER.info("'{}' ist in '{}' enthalten.", zutat.getName(), g.getName());
                    return true;
                }
            }

        } catch (RepositoryException e) {
            LOGGER.warn("Gerichte können nicht geladen werden. Zutat wird als relevant eingestuft");
            return true;
        }
        return false;
    }

    private static List<Zutat> resultSetZuListe(ResultSet rs) throws SQLException {
        List<Zutat> liste = new ArrayList<>();
        while (rs.next()) {
            liste.add(new Zutat(rs.getString(COL_NAME), new Preis(rs.getDouble(COL_PREIS))));
        }
        return liste;
    }
}
