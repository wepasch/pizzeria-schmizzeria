package de.brandenburg.th.se.pizzeria.infrastructure.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.domain.gericht.AbstractGerichtFactory;
import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.domain.gericht.GerichtTyp;
import de.brandenburg.th.se.pizzeria.domain.gericht.LeerGerichtFactory;
import de.brandenburg.th.se.pizzeria.domain.speisekarte.Speisekarte;
import de.brandenburg.th.se.pizzeria.infrastructure.Enviroment;
import de.brandenburg.th.se.pizzeria.infrastructure.RepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;
import de.brandenburg.th.se.pizzeria.infrastructure.speisekarte.SpeisekarteRepoH2;
import de.brandenburg.th.se.pizzeria.infrastructure.speisekarte.SpeisekarteRepository;
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

public class GerichtRepoH2 extends RepoH2 implements GerichtRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(GerichtRepoH2.class);
    private static final String SPALTE_NAME = "Name";
    private static final String SPALTE_PREIS = "Preis";
    private static final String SPALTE_TYP = "Typ";
    private static final String SPALTE_ZUTATEN = "Zutaten";
    private static final int COL_NAME = 1;
    private static final int COL_PREIS = 2;
    private static final int COL_TYP = 3;
    private static final int COL_ZUTATEN = 4;

    public GerichtRepoH2(Enviroment enviroment) {
        super(enviroment);
    }

    @Override
    public Gericht getMitNamen(String name) throws RepositoryException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name für Gericht darf nicht leer sein");
        }
        List<Gericht> ergebnis;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + TABELLE_GERICHT + " WHERE " + SPALTE_NAME + "=?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            ergebnis = resultSetZuListe(rs);
        } catch (SQLException e) {
            LOGGER.error("Gericht mit Namen '{}' kann nicht gefunden werden, da {}", name, e.getMessage());
            return null;
        }
        if (ergebnis.isEmpty()) {
            throw new RepositoryException("Gericht mit Namen '" + name + "' existiert nicht");
        }
        return ergebnis.getFirst();
    }

    @Override
    public List<Gericht> getAlleGerichte() throws RepositoryException {
        List<Gericht> ergebnis;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + TABELLE_GERICHT)) {
            ResultSet rs = ps.executeQuery();
            ergebnis = resultSetZuListe(rs);
        } catch (SQLException e) {
            LOGGER.error("bei Anfrage nach allen Gerichten: {}", e.getMessage());
            return new ArrayList<>();
        }
        return ergebnis;
    }

    @Override
    public void addGericht(Gericht gericht) throws RepositoryException {
        Gericht altesGericht = null;
        try {
            altesGericht = getMitNamen(gericht.getName());
        } catch (RepositoryException e) {
            LOGGER.info("Gericht '{}' kann hinzugefügt werden, da es noch nicht existiert", gericht.getName());
        }
        if (altesGericht != null) {
            throw new RepositoryException("Gericht mit Namen '" + gericht.getName() + "' existiert bereits");
        }
        List<Zutat> zutaten = gericht.getZutaten();
        try {
            macheZutatenVerfuegbar(zutaten);
        } catch (RepositoryException e) {
            throw new RepositoryException("Gericht '" + gericht.getName() + "' kann nicht gespeichert werden, da eine " +
                    "Zutat nicht gespeichert werden kann");
        }

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO " + TABELLE_GERICHT + " (" + SPALTE_NAME + ", " + SPALTE_PREIS + ", " + SPALTE_TYP +
                             ", " + SPALTE_ZUTATEN + ") " +
                             "VALUES (?, ?, ?, ?)")) {
            ps.setString(1, gericht.getName());
            ps.setDouble(2, gericht.getBasisPreis().getWert().doubleValue());
            ps.setString(3, gericht.getTyp());
            ps.setString(4, String.join(SEPARATOR_STRINGS, gericht.getZutatenNamen()));
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Gericht '{} gespeichert", gericht.getName());
            } else {
                LOGGER.error("Hinzufügen von Gericht '{}' resultiert in Antwortcode von {}.", gericht.getName(), ret);
                throw new RepositoryException("Gericht '" + gericht.getName() + "' nicht hinzugefügt");
            }
        } catch (SQLException e) {
            LOGGER.error("bei Hinzufügen von '{}': {}", gericht, e.getMessage());
            throw new RepositoryException("Gericht '" + gericht.getName() + "' kann nicht hinzugefügt werden");
        }

    }

    @Override
    public void aktualisiereGericht(Gericht gericht) throws RepositoryException {
        try {
            getMitNamen(gericht.getName());
        } catch (RepositoryException e) {
            throw new RepositoryException("Gericht mit Namen '" + gericht.getName() + "' existiert nicht");
        }
        try {
            macheZutatenVerfuegbar(gericht.getZutaten());
        } catch (RepositoryException e) {
            LOGGER.error("Zutaten für Gericht '{}' können nicht gespeichert werden", gericht.getName());
            throw new RepositoryException("Zutaten für Gericht '" + gericht.getName() +
                    "' können nicht gespeichert werden");
        }
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + TABELLE_GERICHT + " SET " + SPALTE_PREIS + "=?, " + SPALTE_TYP + "=?, " +
                             SPALTE_ZUTATEN + "=? WHERE " + SPALTE_NAME + "=?")) {
            ps.setDouble(1, gericht.getBasisPreis().getWert().doubleValue());
            ps.setString(2, gericht.getTyp());
            ps.setString(3, String.join(SEPARATOR_STRINGS, gericht.getZutatenNamen()));
            ps.setString(4, gericht.getName());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Gericht '{} aktualisiert", gericht.getName());
            } else {
                LOGGER.error("Änderung von Gericht '{}' resultiert in Antwortcode von {}.", gericht.getName(), ret);
                throw new RepositoryException("Gericht '" + gericht.getName() + "' nicht geändert");
            }
        } catch (SQLException e) {
            LOGGER.error("bei Aktualisierung von '{}': {}", gericht, e.getMessage());
            throw new RepositoryException("Gericht '" + gericht.getName() + "' kann nicht verändert werden");
        }

    }

    @Override
    public void entferneGericht(Gericht gericht) throws RepositoryException {
        if (istAufSpeisekarte(gericht)) {
            LOGGER.error("Gericht '{}' wird nicht entfernt, da es auf einer Speisekarte steht.", gericht.getName());
            throw new RepositoryException("Gericht '" + gericht.getName() +"' wird nicht entfernt, " +
                    "da es auf einer Speisekarte steht.");
        }
        Gericht altesGericht;
        try {
            altesGericht = getMitNamen(gericht.getName());
        } catch (RepositoryException e) {
            LOGGER.info("Gericht mit Namen '{}' existiert nicht", gericht.getName());
            return;
        }
        if (altesGericht == null) {return;}
        if (!altesGericht.equals(gericht)) {
            LOGGER.error("Gericht '{}' nicht entfernt, da das gespeicherte Gericht " +
                    "ihm nicht gleich ist", gericht.getName());
            throw new RepositoryException("Gericht '" + gericht.getName() + "' nicht entfernt, da es als mit anderen " +
                    "Eigenschaften gespeichert ist");
        }
        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TABELLE_GERICHT + " WHERE "
                + SPALTE_NAME + "=? ")) {
            ps.setString(1, gericht.getName());
            int ret = ps.executeUpdate();
            if (ret == 1) {
                LOGGER.debug("Gericht '{}' entfernt", gericht.getName());
            } else {
                LOGGER.error("Entfernung von Gericht '{}' resultiert in Antwortcode von {}.", gericht.getName(), ret);
                throw new RepositoryException("Gericht mit Namen '" + gericht.getName() + "' nicht entfernt");
            }
        } catch (SQLException e) {
            LOGGER.error("Entfernung von Gericht '{}' nicht möglich, da {}", gericht.getName(), e.getMessage());
            throw new RepositoryException("Gericht mit Namen '" + gericht.getName() + "' nicht entfernt");
        }
    }

    public void macheZutatenVerfuegbar(List<Zutat> zutaten) throws RepositoryException {
        ZutatRepository zutatRepo = new ZutatRepoH2(Enviroment.DEV);
        for (Zutat zutat: zutaten) {
            try {
                zutatRepo.getMitNamen(zutat.getName());
            } catch (RepositoryException eV) {
                LOGGER.info("Zutat '{}' ist nicht verfügbar und wird ins Repo hinzugefügt", zutat.getName());
                try {
                    zutatRepo.addZutat(zutat);
                } catch (RepositoryException eN) {
                    LOGGER.error("Zutat '{}' kann nicht ins Repo hinzugefügt werden.", zutat.getName());
                    throw new RepositoryException("Zutat '" + zutat.getName() + "' kann nicht gespeichert werden");
                }
            }
        }
    }

    private static List<Gericht> resultSetZuListe(ResultSet rs) throws SQLException, RepositoryException {
        List<Gericht> liste = new ArrayList<>();
        AbstractGerichtFactory gerichtFactory = new LeerGerichtFactory();
        while (rs.next()) {
            Gericht gericht = gerichtFactory.erstelleGerichtVon(rs.getString(COL_NAME),
                    new Preis(rs.getDouble(COL_PREIS)), GerichtTyp.ausLabel(rs.getString(COL_TYP)));
            try {
                List<Zutat> zutaten = sammelZutaten(rs.getString(COL_ZUTATEN));
                for (Zutat zutat : zutaten) gericht.addZutat(zutat);
            } catch (RepositoryException e) {
                LOGGER.error("Erstellung der Zutaten von Gericht {} nicht möglich: {}", gericht.getName(), e.getMessage());
                throw new RepositoryException("für Gericht '" + gericht.getName() + "' sind nicht alle Zutaten gespeichert");
            }
            liste.add(gericht);
        }
        return liste;
    }

    private static List<Zutat> sammelZutaten(String zutatenString) throws RepositoryException {
        if (zutatenString == null || zutatenString.isEmpty()) {
            return new ArrayList<>();
        }
        String[] zutatStrings = zutatenString.split("\\s*" + SEPARATOR_STRINGS + "\\s*");
        ZutatRepository zutatRepo = new ZutatRepoH2(Enviroment.DEV);
        List<Zutat> zutaten = new ArrayList<>();
        for (String zutatString : zutatStrings) {
            Zutat zutat = zutatRepo.getMitNamen(zutatString);
            zutaten.add(zutat);
        }
        return zutaten;
    }

    private boolean istAufSpeisekarte(Gericht gericht) {
        SpeisekarteRepository speisekarteRepo = new SpeisekarteRepoH2(Enviroment.DEV);
        try {
            List<Speisekarte> speisekarten = speisekarteRepo.getAlleSpeisekarten();
            for (Speisekarte s : speisekarten) {
                if (s.entferne(gericht.getName())) {
                    LOGGER.info("'{}' ist in '{}' enthalten.", gericht.getName(), s.getName());
                    return true;
                }
            }

        } catch (RepositoryException e) {
            LOGGER.warn("Speisekarten können nicht geladen werden. Gericht wird als relevant eingestuft");
            return true;
        }
        return false;
    }
}
