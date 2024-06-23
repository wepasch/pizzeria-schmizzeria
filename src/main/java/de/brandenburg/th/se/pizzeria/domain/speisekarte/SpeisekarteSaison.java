package de.brandenburg.th.se.pizzeria.domain.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.gericht.GerichtTyp;

public enum SpeisekarteSaison {
    STANDARD("Standard"),
    SOMMER("Sommer");

    public final String label;

    SpeisekarteSaison(String label) {
        this.label = label;
    }

    public static SpeisekarteSaison ausLabel(String label) {
        for (SpeisekarteSaison type : SpeisekarteSaison.values()) {
            if (type.label.equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("keine Saison '" + label + "' für Speisekarten");
    }
}
