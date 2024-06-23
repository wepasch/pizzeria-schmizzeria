package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;

public class Pizza extends Gericht{
    protected Pizza(String name, Preis basisPreis) {
        super(name, basisPreis, GerichtTyp.PIZZA);
    }
}
