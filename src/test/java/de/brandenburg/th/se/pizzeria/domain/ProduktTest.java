package de.brandenburg.th.se.pizzeria.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ProduktTest {
    static final String DEFAULT_NAME = "Name";
    static final Preis DEFAULT_PREIS = new Preis(1.23);

    @Test
    void erstellenTest() {
        Produkt a = new Produkt(DEFAULT_NAME, DEFAULT_PREIS);
        Preis pPreis = a.getBasisPreis();
        assertEquals(DEFAULT_PREIS, pPreis, "Preis wurde nicht erhalten.");
    }

    @Test
    void gleichheitsTest() {
        assertEquals(new Produkt("ProduktA", DEFAULT_PREIS),
                new Produkt("ProduktA", DEFAULT_PREIS),
                "Produkte mit selben Namen und Preisen müssen gleich sein.");
        assertNotEquals(new Produkt("ProduktA", DEFAULT_PREIS),
                new Produkt("ProduktB", DEFAULT_PREIS),
                "Produkte mit verschedenen Namen dürfen nicht gleich sein.");
        assertNotEquals(new Produkt("ProduktA", DEFAULT_PREIS),
                new Produkt("ProduktA", new Preis(1.24)),
                "Produkte mit verschedenen Preisen dürfen nicht gleich sein.");

    }

    @Test
    void toStringTest() {
        assertNotEquals("Produkt | 1.23", (new Produkt("Produkt", DEFAULT_PREIS)).toString(),
                "String soll Komma statt Punkt enthalten");
        assertEquals("Produkt | 1,23€", (new Produkt("Produkt", DEFAULT_PREIS)).toString());
    }
}
