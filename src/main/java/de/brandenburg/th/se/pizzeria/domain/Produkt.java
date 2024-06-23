package de.brandenburg.th.se.pizzeria.domain;

import java.util.Objects;

public class Produkt {
    private String name;
    private Preis baisisPreis;

    public Produkt(String name, Preis basisPreis) {
        setName(name);
        setBaisisPreis(basisPreis);
    }

    public void setBaisisPreis(Preis baisisPreis) {
        this.baisisPreis = baisisPreis;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Produktname darf nicht leer sein.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Preis getBasisPreis() {
        return baisisPreis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produkt oProdukt)) return false;
        return name.equals(oProdukt.getName())
                && baisisPreis.equals(oProdukt.getBasisPreis());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, baisisPreis);
    }

    @Override
    public String toString() {
        return getName() + " | " + getBasisPreis().toString().replace(".", ",");
    }

}
