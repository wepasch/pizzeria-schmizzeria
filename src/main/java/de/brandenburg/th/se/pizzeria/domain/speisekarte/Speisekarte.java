package de.brandenburg.th.se.pizzeria.domain.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Speisekarte {
    private static final Logger LOGGER = LoggerFactory.getLogger(Speisekarte.class);

    private String name;
    private SpeisekarteSaison typ;
    private final List<Gericht> gerichte;

    protected Speisekarte(String name, SpeisekarteSaison speisekarteSaison) {
        setName(name);
        setSaison(speisekarteSaison);
        this.gerichte = new ArrayList<>();
    }

    protected Speisekarte() {
        this.gerichte = new ArrayList<>();
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Speisekartenname darf nicht leer sein.");
        }
        this.name = name;
    }

    public void setSaison(SpeisekarteSaison typ) {
        if (typ == null){
            throw new IllegalArgumentException("Speisekarte muss Saison enthalten");
        }
        this.typ = typ;
    }

    public String getName() {
        return name;
    }

    public SpeisekarteSaison getSaison() {
        return this.typ;
    }

    public List<Gericht> getGerichte() {
        return this.gerichte;
    }

    public boolean enthaelt(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return getGerichte().stream().anyMatch(g -> g.getName().equals(name));
    }

    public boolean addGericht(Gericht gericht) {
        if (gericht == null){
            return false;
        }
        if (enthaelt(gericht.getName())) {
            LOGGER.warn("Gericht bereits in Speisekarte enthalten.");
            return false;
        }

        this.getGerichte().add(gericht);
        return true;
    }


    public boolean entferne(String name) {
        if (!enthaelt(name)) {
            LOGGER.warn("Gericht befindet sich nicht in Speisekarte.");
            return true;
        }
        List<Gericht> gefundeneGerichte = getGerichte().stream().filter(g -> g.getName().equals(name)).toList();
        if (gefundeneGerichte.isEmpty()) {
            LOGGER.error("Keine Gerichte mit Namen '{}' in Speisekarte.", name);
            return false;
        }
        return getGerichte().remove(gefundeneGerichte.getFirst());

    }
}
