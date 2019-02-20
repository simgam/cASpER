package it.unisa.ascetic.storage.repository;

/**
 * eccezione lanciata dalle repository
 */
public class RepositoryException extends Exception {
    /**
     * costruttore
     *
     * @param message messaggio da far visualizzare in caso d'eccezione
     */
    public RepositoryException(String message) {
        super(message);

    }

}


