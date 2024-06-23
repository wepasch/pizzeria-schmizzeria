package de.brandenburg.th.se.pizzeria.infrastructure;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.infrastructure.zutat.ZutatRepoH2;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ZutatRepoH2Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZutatRepoH2Test.class);
    private static final ZutatRepoH2 zutatRepo = new ZutatRepoH2(Enviroment.TEST);

    @BeforeAll
    static void setUpDb() {
        DBReset.resetFromFile(RepoH2.DB_URL_TEST, RepoH2.DB_USER, RepoH2.DB_PW, DBReset.INIT_TEST);
    }

    @AfterAll
    static void tearDownDb() {
        try {
            Files.delete(Paths.get("db", "pizzeriaTest.mv.db"));
        } catch (IOException e) {
            LOGGER.error("ZutatRepoDb(Test) nach Test nicht gelöscht: {}", e.getMessage());
        }
    }

    @Test
    @Order(1)
    void getAlleZutatenTest() {
        List<Zutat> expected = Arrays.asList(
                new Zutat("Ananas", new Preis(4.99)),
                new Zutat("Tomate", new Preis(1.23)));
        List<Zutat> actual = zutatRepo.getAlleZutaten();
        assertEquals(expected, actual, "Es werden nicht alle vorhandenen Zutaten gefunden");
    }

    @Test
    @Order(2)
    void getMitNamenTest() {
        Zutat expected = new Zutat("Tomate", new Preis(1.23));
        try {
            Zutat actual = zutatRepo.getMitNamen("Tomate");
            assertEquals(expected, actual, "Vorhandene Zutat wird nicht gefunden");
        } catch (Exception e) {
            fail("Es sollte keine Exception geworfen werfen.");
        }
        try {
            zutatRepo.getMitNamen("Fingerhut");
            fail("Zutat sollte nicht gefunden werden");
        } catch (IllegalArgumentException e) {
            fail("Es sollte keine IllegalArgument Exception geworfen werden.");
        } catch (RepositoryException e) {
            assertTrue(true);
        }

    }

    @Test
    @Order(3)
    void aktualisiereZutatTest() {
        try {
            zutatRepo.aktualisiereZutat(new Zutat("Tomate", new Preis(1000)));
        } catch (Exception e) {
            fail("Aktualisierung sollte keine Exception werfen.");
        }
        try {
            assertEquals(new Zutat("Tomate", new Preis(1000)), zutatRepo.getMitNamen("Tomate"),
                    "Zutat mit selben Namen und neuem Preis muss nach Aktualisierung vorhanden sein");
        } catch (Exception e) {
            fail("Anfrage mit Namen sollte keine Exception werfen.");
        }
    }

    @Test
    @Order(4)
    void addZutatTest() {
        try {
            zutatRepo.addZutat(new Zutat("Käse", new Preis(3.99)));
        } catch (RepositoryException e) {
            fail("Hinzufügen neuer Zutat sollte keine Exception werfen.");
        }
        try {
            zutatRepo.addZutat(new Zutat("Käse", new Preis(3.99)));
            fail("Hinzufügen vorhandener Zutat sollte Exception werfen.");
        } catch (RepositoryException e) {
            assertTrue(true);
        }
        try {
            assertEquals(new Zutat("Käse", new Preis(3.99)),
                    zutatRepo.getMitNamen("Käse"),
                    "Neue Zutat muss nach Hinzufügen gefunden werden");
        } catch (Exception e) {
            fail("Anfrage vorhandener Zutat sollte keine Exception werfen.");
        }
    }

    @Test
    @Order(5)
    void entferneZutatTest() {
        try {
            zutatRepo.entferneZutat(new Zutat("Tomate", new Preis(99)));
            fail("Entfernung muss für existierende Zutat aber ungültigen Preis eine Exception werfen.");
        } catch (RepositoryException e) {
            assertTrue(true);
        }
        try {
            zutatRepo.entferneZutat(new Zutat("Tomate", new Preis(1000)));
        } catch (RepositoryException e) {
            fail("Entfernung muss für existierende Zutat und gültigen Preis darf keine Exception werfen.");
        }
        try {
            zutatRepo.getMitNamen("Tomate");
            fail("Entfernte Zutat darf nicht mehr gefunden werden");
        } catch (RepositoryException e) {
            assertTrue(true);
        }
        try {
            zutatRepo.entferneZutat(new Zutat("Fingerhut", new Preis(1.23)));
            assertTrue(true);
        } catch (RepositoryException e) {
            fail("Entfernen nicht vorhandener Zutat kann ruhi klappen");
        }
    }
}
