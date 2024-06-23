package de.brandenburg.th.se.pizzeria.domain.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.domain.gericht.LeerGerichtFactory;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpeisekarteTest {
    @Test
    void erstellenTest() {
        Speisekarte speisekarte =  new Speisekarte("Name", SpeisekarteSaison.STANDARD);
        assertEquals("Name", speisekarte.getName(), "Name muss übernommen werden");
        assertEquals(SpeisekarteSaison.STANDARD, speisekarte.getSaison(), "Saison muss übernommen werden");
        assertTrue(speisekarte.getGerichte().isEmpty(), "Neu erstellte Speisekarte hat keine Gereichte");

        try {
            new Speisekarte(null, SpeisekarteSaison.STANDARD);
            fail("Keine Erstellung mit null");
        } catch (Exception e) {
            assertTrue(true);
        }
        try {
            new Speisekarte("", SpeisekarteSaison.STANDARD);
            fail("Keine Erstellung mit leerem Namen");
        } catch (Exception e) {
            assertTrue(true);
        }
        try {
            new Speisekarte("Name", null);
            fail("Keine Erstellung mit null");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void gerichtAddTest() {
        Speisekarte speisekarte = new Speisekarte("Name", SpeisekarteSaison.STANDARD);
        LeerGerichtFactory factory = new LeerGerichtFactory();
        Gericht gerichtA = factory.erstellePasta("Pizza", new Preis(1.23));
        Gericht gerichtB = factory.erstellePasta("Pizza", new Preis(1.23));
        Gericht gerichtC = factory.erstellePasta("Pasta", new Preis(1.23));


        assertTrue(speisekarte.addGericht(gerichtA), "Leere Speisekarte muss alle Gerichte annehmen");
        assertTrue(speisekarte.addGericht(gerichtC), "Speisekarte muss nicht vorhandenes Gericht annehmen");
        assertFalse(speisekarte.addGericht(gerichtB), "Speisekarte darf Gericht mit vorhandenem Namen nicht annehmen");
        assertFalse(speisekarte.addGericht(null), "Speisekarte darf null nicht annehmen");
    }

    @Test
    void entferneGerichtTest() {
        Speisekarte speisekarte = new Speisekarte("Name", SpeisekarteSaison.STANDARD);
        LeerGerichtFactory factory = new LeerGerichtFactory();
        Gericht gerichtA = factory.erstellePasta("PizzaA", new Preis(1.23));
        Gericht gerichtC = factory.erstellePasta("Pasta", new Preis(1.23));
        speisekarte.addGericht(gerichtA);
        speisekarte.addGericht(gerichtC);

        assertTrue(speisekarte.entferne("PizzaA"), "Gerichentfernen mit gültigen Namen muss gut gehen");
        assertTrue(speisekarte.entferne("PizzaA"), "Gerichentfernen mit nicht vorhandenem Namen muss scheitern");
        assertTrue(speisekarte.entferne(null), "Gerichentfernen mit null muss scheitern");
        assertTrue(speisekarte.entferne(""), "Gerichentfernen mit leerem Namen muss scheitern");
    }

    @Test
    void toJsonTest() {
        JSONObject expected = new JSONObject();
        expected.put("name", "Speisekarte");
        expected.put("saison", SpeisekarteSaison.SOMMER);
        JSONObject expectedGerichte = new JSONObject();
    }
}
