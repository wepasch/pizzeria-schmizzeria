package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;

public class LeerGerichtFactory implements AbstractGerichtFactory{
    @Override
    public Pizza erstellePizza(String name, Preis basisPreis) {
        return new Pizza(name, basisPreis);
    }

    @Override
    public Pasta erstellePasta(String name, Preis basisPreis) {
        return new Pasta(name, basisPreis);
    }

    @Override
    public Dessert erstelleDessert(String name, Preis basisPreis) {
        return new Dessert(name, basisPreis);
    }

    @Override
    public Gericht erstelleGerichtVon(String name, Preis basisPreis, GerichtTyp typ) {
        switch (typ) {
            case PIZZA -> {
                return erstellePizza(name, basisPreis);
            }
            case PASTA -> {
                return erstellePasta(name, basisPreis);
            }
            case DESSERT -> {
                return erstelleDessert(name, basisPreis);
            }
        }
        throw new IllegalArgumentException("unbekannter GerichtTyp " + typ);
    }
}
