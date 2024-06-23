package de.brandenburg.th.se.pizzeria.domain.gericht;

import de.brandenburg.th.se.pizzeria.domain.Preis;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LeerGerichtFactoryTest {
    AbstractGerichtFactory gerichtFactory = new LeerGerichtFactory();

    @Test
    void erstellenTest() {
        assertInstanceOf(Pizza.class, gerichtFactory.erstellePizza("Pizza", new Preis(1.23)));
        assertInstanceOf(Pasta.class, gerichtFactory.erstellePasta("Pasta", new Preis(1.23)));
        assertInstanceOf(Dessert.class, gerichtFactory.erstelleDessert("Dessert", new Preis(1.23)));
    }
}
