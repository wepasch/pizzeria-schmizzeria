package de.brandenburg.th.se.pizzeria.domain.gericht;

public enum GerichtGroesse {
    KLEIN("klein", 1),
    MITTEL("mittel", 1.5f),
    GROSS("groß", 2);

    public final String typ;
    public final float preisFaktor;

    GerichtGroesse(String typ, float preisFaktor) {
        this.typ = typ;
        this.preisFaktor = preisFaktor;
    }

}
