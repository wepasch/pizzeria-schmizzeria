package de.brandenburg.th.se.pizzeria.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ZutatTest {
    @Test
    void erstellenTest() {
        Zutat a = new Zutat("Tomate", new Preis(0.99));
        assertEquals("Tomate", a.getName(), "Name wurde bei der Erstellung nicht übernommen.");
        assertEquals(new Preis(0.99), a.getBasisPreis(), "Preis wurde bei der Erstellung nicht übernommen.");
    }

    @Test
    void gleichheitTest() {
        assertEquals(new Zutat("Tomate", new Preis(0.99)),
                new Zutat("Tomate", new Preis(0.99)),
                "Identischer Konstruktoraufruf muss zu gleichen Zutaten führen.");
        assertNotEquals(new Zutat("Tomate", new Preis(0.99)),
                new Zutat("Ananas", new Preis(0.99)),
                "Konstruktoraufruf mit verschiedenen Namen muss zu nicht gleichen Zutaten führen.");
        assertNotEquals(new Zutat("Tomate", new Preis(0.99)),
                new Zutat("Tomate", new Preis(0.98)),
                "Konstruktoraufruf mit verschiedenen Preisen muss zu nicht gleichen Zutaten führen.");
    }

    @Test
    void hashTest() {
        assertEquals(
                new Zutat("Tomate", new Preis(1.23)),
                new Zutat("Tomate", new Preis(1.23)),
                "Zutaten mit selben Namen und Preis müssen selben Hash haben."
        );
        assertNotEquals(
                new Zutat("Tomate", new Preis(1.23)),
                new Zutat("Ananas", new Preis(1.23)),
                "Zutaten mit verschiedenen Namen müssen verschiedene Hashes haben."
        );
        assertNotEquals(
                new Zutat("Tomate", new Preis(1.23)),
                new Zutat("Tomate", new Preis(1.24)),
                "Zutaten mit verschiedenen Preisen müssen verschiedene Hashes haben."
        );
    }
}
