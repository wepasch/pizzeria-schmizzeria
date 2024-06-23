package de.brandenburg.th.se.pizzeria.domain.gericht;

import java.util.ArrayList;
import java.util.List;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import de.brandenburg.th.se.pizzeria.domain.Produkt;
import de.brandenburg.th.se.pizzeria.domain.Zutat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gericht extends Produkt {
    private static final Logger LOGGER = LoggerFactory.getLogger(Gericht.class);

    private final GerichtTyp gerichtTyp;
    private final List<Zutat> zutaten;

    protected Gericht(String name, Preis basisPreis, GerichtTyp gerichtTyp) {
        super(name, basisPreis);
        this.gerichtTyp = gerichtTyp;
        this.zutaten = new ArrayList<>();
    }

    public List<Zutat> getZutaten() {
        return zutaten;
    }

    public List<String> getZutatenNamen() {
        return zutaten.stream()
                .map(Produkt::getName)
                .toList();
    }

    public GerichtTyp getGerichtTyp() {return gerichtTyp;}
    public String getTyp() {return gerichtTyp.label;}

    public boolean enthaelt(Zutat zutat) {
        return zutaten.stream().map(Zutat::getName).anyMatch(n -> n.equals(zutat.getName()));
    }

    public void addZutat(Zutat neueZutat) {
        if (enthaelt(neueZutat)) {
            LOGGER.warn("Gericht '{}' enthält bereits Zutat '{}'.", getName(), neueZutat.getName());
            return;
        }
        zutaten.add(neueZutat);
    }

    public void entferneZutat(Zutat zutat) {
        if (!enthaelt(zutat)) {
            LOGGER.warn("Gericht '{}' enthält Zutat '{}' nicht.", getName(), zutat.getName());
            return;
        }
        if (!zutaten.remove(zutat)) {
            LOGGER.warn("Zutat '{}' konnte nicht aus Gericht '{}' entfernt werden.", getName(), zutat.getName());
        }
    }

    public Preis getPreis(GerichtGroesse groesse) {
        List<Preis> basisPreise = new ArrayList<>(zutaten.stream().map(Produkt::getBasisPreis).toList());
        basisPreise.add(getBasisPreis());
        Preis preisSumme = Preis.summiere(basisPreise);
        return preisSumme.malFaktor(groesse.preisFaktor);
    }

    public String toJsonString() {
        JSONObject jo = new JSONObject();
        jo.put("name", super.getName());
        jo.put("typ", gerichtTyp.label);
        JSONObject preiseJson = new JSONObject();
        preiseJson.put(GerichtGroesse.KLEIN.name(), getPreis(GerichtGroesse.KLEIN));
        preiseJson.put(GerichtGroesse.MITTEL.name(), getPreis(GerichtGroesse.MITTEL));
        preiseJson.put(GerichtGroesse.GROSS.name(), getPreis(GerichtGroesse.GROSS));
        jo.put("preise", preiseJson);
        JSONArray zutatenJson = new JSONArray(getZutatenNamen());
        jo.put("zutaten", zutatenJson);
        return jo.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gericht oGericht)) return false;
        if (!gerichtTyp.equals(oGericht.gerichtTyp)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int klassenHash = gerichtTyp.hashCode();
        klassenHash += zutaten.stream()
                .mapToInt(Produkt::hashCode)
                .sum();
        return klassenHash + super.hashCode();
    }


}
