package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;

public class Dessert extends Gericht{
    protected Dessert(String name, Preis basisPreis) {
        super(name, basisPreis, GerichtTyp.DESSERT);
    }
}
