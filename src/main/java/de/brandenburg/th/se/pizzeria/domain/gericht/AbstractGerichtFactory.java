package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;

public interface AbstractGerichtFactory {
    Pizza erstellePizza(String name, Preis basisPreis);
    Pasta erstellePasta(String name, Preis basisPreis);
    Dessert erstelleDessert(String name, Preis basisPreis);
    Gericht erstelleGerichtVon(String name, Preis basisPreis, GerichtTyp typ);
}
