package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GerichtTest {
    Zutat zutatTomate = new Zutat("Tomaten", new Preis(1.99));
    Zutat zutatAnanas = new Zutat("Ananas", new Preis(2.49));

    @Test
    void erstellenTest() {
        Gericht gericht = new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA);
        assertEquals("Gericht", gericht.getName(), "Gericht Name wurde nicht erhalten");
        assertEquals(new Preis(1.23), gericht.getBasisPreis(),
                "Gericht Basispreis wurde nicht erhalten");
        assertTrue(gericht.getZutatenNamen().isEmpty(), "Neues Gericht darf keine Zutaten enhalten.");
    }

    @Test
    void enthaeltZutatTest() {
        Gericht gericht = new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA);
        gericht.addZutat(zutatTomate);

        assertTrue(gericht.enthaelt(zutatTomate), "Enthaltene Zutat wird nicht erkannt.");
        assertFalse(gericht.enthaelt(zutatAnanas), "Nicht enthaltene Zutat wird nicht erkannt.");
    }

    @Test
    void addZutatenTest() {
        Gericht gericht = new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA);

        gericht.addZutat(zutatTomate);
        assertEquals(List.of(new String[]{zutatTomate.getName()}), gericht.getZutatenNamen(),
                "Zutat nicht aufgeführt");

        gericht.addZutat(zutatTomate);
        assertEquals(List.of(new String[]{zutatTomate.getName()}), gericht.getZutatenNamen(),
                "Zutat darf nicht doppelt hinzugefügt werden können");
    }

    @Test
    void entferneZutatTest() {
        Gericht gericht = new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA);
        gericht.addZutat(zutatTomate);
        gericht.addZutat(zutatAnanas);

        gericht.entferneZutat(zutatAnanas);
        assertEquals(List.of(new String[]{zutatTomate.getName()}), gericht.getZutatenNamen(),
                "Zutat wird nicht entfernt");
    }

    @Test
    void bepreisungTest() {
        Gericht gericht = new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA);

        assertEquals(gericht.getBasisPreis(), gericht.getPreis(GerichtGroesse.KLEIN),
                "Kleiner Preis muss gleich dem Basispreis sein (keine Zutaten).");
        assertEquals(new Preis(1.85), gericht.getPreis(GerichtGroesse.MITTEL),
                "Mittlerer Preis muss 1,5-faches (aufgerundet) des Basispreis sein (keine Zutaten).");
        assertEquals(new Preis(2.46), gericht.getPreis(GerichtGroesse.GROSS),
                "Großer Preis muss 2-faches des Basispreis sein (keine Zutaten).");

        gericht.addZutat(zutatTomate);
        assertEquals(new Preis(3.22), gericht.getPreis(GerichtGroesse.KLEIN),
                "Kleiner Preis muss gleich dem Basispreis + Zutatenpreis sein.");
        assertEquals(new Preis(4.83), gericht.getPreis(GerichtGroesse.MITTEL),
                "Mittlerer Preis muss 1,5-faches (aufgerundet) des Basispreis + Zutatenpreis sein.");
        assertEquals(new Preis(6.44), gericht.getPreis(GerichtGroesse.GROSS),
                "Großer Preis muss 2-faches des Basispreis + Zutatenpreis sein.");
    }

    @Test
    void gleichheitTest() {
        assertEquals(new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA),
                new Gericht("Gericht", new Preis(1.23), GerichtTyp.PIZZA),
                "Gerichte mit gleichen Namen, Preis und Typ müssen gleich sein.");
        assertNotEquals(new Gericht("GerichtA", new Preis(1.23), GerichtTyp.PIZZA),
                new Gericht("GerichtB", new Preis(1.23), GerichtTyp.PIZZA),
                "Gerichte mit verschiedenen Namen dürfen nicht gleich sein.");
        assertNotEquals(new Gericht("GerichtA", new Preis(1.23), GerichtTyp.PIZZA),
                new Gericht("GerichtA", new Preis(1.24), GerichtTyp.PIZZA),
                "Gerichte mit verschiedenen Preisen dürfen nicht gleich sein.");
        assertNotEquals(new Gericht("GerichtA", new Preis(1.23), GerichtTyp.PIZZA),
                new Gericht("GerichtA", new Preis(1.23), GerichtTyp.PASTA),
                "Gerichte mit verschiedenen Typen dürfen nicht gleich sein.");
    }

    @Test
    void toJsonTest() {
        Gericht g = new LeerGerichtFactory().erstellePasta("Pasta", new Preis(1.23));
        g.addZutat(new Zutat("Käse", new Preis(7.99)));
        g.addZutat(new Zutat("Tomate", new Preis(2.45)));

        assertEquals("{" +
                "\"name\":\"Pasta\"," +
                "\"typ\":\"Pasta\"," +
                "\"zutaten\":[" +
                    "\"Käse\"," +
                    "\"Tomate\"" +
                "]," +
                "\"preise\":{" +
                    "\"MITTEL\":\"17,51\\u20ac\"," +
                    "\"GROSS\":\"23,34\\u20ac\"," +
                    "\"KLEIN\":\"11,67\\u20ac\"" +
                    "}" +
                "}", g.toJsonString());
    }
}
