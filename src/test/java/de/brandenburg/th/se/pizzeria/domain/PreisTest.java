package de.brandenburg.th.se.pizzeria.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static de.brandenburg.th.se.pizzeria.domain.Preis.istValide;
import static de.brandenburg.th.se.pizzeria.domain.Preis.summiere;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PreisTest {

    @Test
    void ertsellenTest() {
        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.UNNECESSARY), new Preis(1).getWert(),
                "Fehlende Zehntel/Hunderstel müssen hinzugefügt werden");
        assertEquals(BigDecimal.valueOf(1.20).setScale(2, RoundingMode.UNNECESSARY), new Preis(1.2).getWert(),
                "Fehlende Hunderstel müssen hinzugefügt werden");
        assertEquals(BigDecimal.valueOf(1.23), new Preis(1.23).getWert(),
                "Preis muss mit zwei Nachkommastellen zurückgegeben werden");

        assertEquals(BigDecimal.valueOf(1.24), new Preis(1.2300001).getWert(),
                "Bei 100 * Preis % 100 != 0 ,uss zum nächsten Hundertstel aufgerundet werden");

        try {
            new Preis(0);
            fail("Konstruktion mit 0 als Preis muss scheitern");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            new Preis(-0.1);
            fail("Konstruktion mit negativem Preis muss scheitern");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void stringTest() {
        assertEquals("1,00€", new Preis(1).toString(),
                "Preisstring muss auch Hundertstel/Tausendstel mit 0 enhalten");
        assertEquals("1,20€", new Preis(1.2).toString(),
                "Preisstring muss auch Hundertstel/Tausendstel mit 0 enhalten");
        assertEquals("1,23€", new Preis(1.23).toString());
    }

    @Test
    void gleicheitTest() {
        assertEquals(new Preis(1.23), new Preis(1.23), "Preise mit selbem Bigdecimal müssen gleichen sein");
        assertNotEquals(new Preis(1.23), new Preis(1.24),
                "Preise mit verschiedenem Bigdecimal müssen gleichen sein");
    }

    @Test
    void multiplikationsTest() {
        Preis p = new Preis(1.23);
        assertEquals(new Preis(1.85), p.malFaktor(1.5));
        assertEquals(new Preis(2.46), p.malFaktor(2));
        try {
            p.malFaktor(0);
            fail("Keine Multiplikation mit 0 erlaubt");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            p.malFaktor(-0.01);
            fail("Keine Multiplikation mit negativen Faktoren erlaubt");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void validitaetsTest() {
        assertTrue(istValide(0.01));
        assertFalse(istValide(0), "Nullpreis nicht valide");
        assertFalse(istValide(-0.01), "negativer Preis nicht valide");
    }

    @Test
    void summierenTest() {
        List<Preis> emptyList = new ArrayList<>();
        try {
            Preis.summiere(emptyList);
            fail("Leere Preisliste führt zu Preis = 0, was kein valider Preis ist");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertEquals(new Preis(1.23), summiere(List.of(new Preis(1.23))));
        assertEquals(new Preis(3.54), summiere(List.of(new Preis(1.23), new Preis(2.31))));
        assertEquals(new Preis(18.66), summiere(List.of(new Preis(9.87), new Preis(8.79))));
    }
}
