package de.brandenburg.th.se.pizzeria.domain.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SpeisekarteBuilder implements SpeisekarteBuilderInterface{
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeisekarteBuilder.class);

    private String nameSpeisekarte;
    private SpeisekarteSaison saisonSpeisekarte;
    private final List<Gericht> gerichteSpeisekarte = new ArrayList<>();


    @Override
    public SpeisekarteBuilderInterface benennen(String name) {
        nameSpeisekarte = name;
        return this;
    }

    @Override
    public SpeisekarteBuilderInterface kategorisieren(SpeisekarteSaison saison) {
        saisonSpeisekarte = saison;
        return this;
    }

    @Override
    public SpeisekarteBuilderInterface befuellen(Gericht gericht) {
        gerichteSpeisekarte.add(gericht);
        return this;
    }

    @Override
    public SpeisekarteBuilderInterface befuellen(List<Gericht> gerichte) {
        gerichteSpeisekarte.addAll(gerichte);
        return this;
    }

    @Override
    public Speisekarte anlegen() {
        Speisekarte speisekarte = new Speisekarte(nameSpeisekarte, saisonSpeisekarte);
        for (Gericht gericht: gerichteSpeisekarte) speisekarte.addGericht(gericht);
        return speisekarte;
    }
}
