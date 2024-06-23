package de.brandenburg.th.se.pizzeria.infrastructure.zutat;

import de.brandenburg.th.se.pizzeria.domain.Zutat;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;

import java.util.List;

public interface ZutatRepository {

    Zutat getMitNamen(String name) throws RepositoryException;
    List<Zutat> getAlleZutaten();
    void addZutat(Zutat zutat) throws RepositoryException;
    void aktualisiereZutat(Zutat zutat) throws RepositoryException;
    void entferneZutat(Zutat zutat) throws RepositoryException;


}
