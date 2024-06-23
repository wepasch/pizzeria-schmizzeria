package de.brandenburg.th.se.pizzeria.domain.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;

import java.util.List;

public interface SpeisekarteBuilderInterface {
    SpeisekarteBuilderInterface benennen(String name);
    SpeisekarteBuilderInterface kategorisieren(SpeisekarteSaison saison);
    SpeisekarteBuilderInterface befuellen(Gericht gericht);
    SpeisekarteBuilderInterface befuellen(List<Gericht> gerichte);
    Speisekarte anlegen();
}
