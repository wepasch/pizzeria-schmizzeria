package de.brandenburg.th.se.pizzeria.infrastructure.speisekarte;

import de.brandenburg.th.se.pizzeria.domain.speisekarte.Speisekarte;
import de.brandenburg.th.se.pizzeria.infrastructure.RepositoryException;

import java.util.List;

public interface SpeisekarteRepository {
    Speisekarte mitNamen(String name) throws RepositoryException;
    List<Speisekarte> getAlleSpeisekarten() throws RepositoryException;
    void addSpeisekarte(Speisekarte speisekarte) throws RepositoryException;
    void aktualisiereSpeisekarte(Speisekarte speisekarte) throws RepositoryException;
    void entferneSpeisekarte(Speisekarte speisekarte) throws RepositoryException;
}
