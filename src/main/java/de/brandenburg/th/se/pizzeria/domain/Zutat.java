package de.brandenburg.th.se.pizzeria.domain;

public class Zutat extends Produkt{
    public Zutat(String name, Preis basisPreis) {
        super(name, basisPreis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zutat)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
