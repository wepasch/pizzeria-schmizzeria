package de.brandenburg.th.se.pizzeria.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static de.brandenburg.th.se.pizzeria.domain.Pizzeria.CHAR_DEZIMAL_SEP;
import static de.brandenburg.th.se.pizzeria.domain.Pizzeria.CHAR_WAEHRUNG;

public class Preis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Preis.class);
    private BigDecimal wert;

    public Preis(double wert) {
        setWert(wert);
    }

    private void setWert(double wert) {
        if (!istValide(wert)) throw new IllegalArgumentException("Preis invalide");
        BigDecimal bd = BigDecimal.valueOf(wert);
        this.wert = bd.setScale(2, RoundingMode.CEILING);

    }

    public BigDecimal getWert() {
        return wert;
    }

    public Preis malFaktor(double faktor) {
        if (faktor <= 0) throw new IllegalArgumentException("Faktor muss größer als 0 sein");
        return new Preis(wert.doubleValue() * faktor);
    }

    @Override
    public String toString() {
        return getWert().toString().replace('.', CHAR_DEZIMAL_SEP) + CHAR_WAEHRUNG;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Preis p)) return false;
        return getWert().equals(p.getWert());
    }

    @Override
    public int hashCode() {
        return getWert().hashCode();
    }

    public static boolean istValide(double preis) {
        if (preis <= 0) {
            LOGGER.error("Preis muss größer als 0 sein, war aber: {}", preis);
            return false;
        }
        return true;
    }

    public static Preis summiere(List<Preis> preise) {
        BigDecimal summe  = preise.stream().map(Preis::getWert).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Preis(summe.doubleValue());
    }
}
