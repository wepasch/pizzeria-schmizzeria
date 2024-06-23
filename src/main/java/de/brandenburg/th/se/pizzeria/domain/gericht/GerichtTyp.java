package de.brandenburg.th.se.pizzeria.domain.gericht;

public enum GerichtTyp {
    PIZZA("Pizza"),
    PASTA("Pasta"),
    DESSERT("Dessert");

    public final String label;
    GerichtTyp(String label) {
        this.label = label;
    }

    public static GerichtTyp ausLabel(String label) {
        for (GerichtTyp type : GerichtTyp.values()) {
            if (type.label.equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("kein Gerichttyp '" + label + "'");
    }
}
