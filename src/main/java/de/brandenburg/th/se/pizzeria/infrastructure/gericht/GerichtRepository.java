package de.brandenburg.th.se.pizzeria.infrastructure.gericht;

import de.brandenburg.th.se.pizzeria.domain.gericht.Gericht;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;

import java.util.List;

public interface GerichtRepository {

    Gericht getMitNamen(String name) throws RepositoryException;
    List<Gericht> getAlleGerichte() throws RepositoryException;
    void addGericht(Gericht gericht) throws RepositoryException;
    void aktualisiereGericht(Gericht gericht) throws RepositoryException;
    void entferneGericht(Gericht gericht) throws RepositoryException;
}
