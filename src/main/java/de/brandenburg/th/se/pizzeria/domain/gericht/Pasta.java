package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;

public class Pasta extends Gericht{
    protected Pasta(String name, Preis basisPreis) {
        super(name, basisPreis, GerichtTyp.PASTA);
    }
}
