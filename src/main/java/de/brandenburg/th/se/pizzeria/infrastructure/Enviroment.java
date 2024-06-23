package de.brandenburg.th.se.pizzeria.infrastructure;

public enum Enviroment {
    TEST("testing"),
    DEV("development");

    public final String label;
    Enviroment(String label) {
        this.label = label;
    }
}
