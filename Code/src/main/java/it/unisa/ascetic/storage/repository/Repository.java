package it.unisa.ascetic.storage.repository;

import java.util.List;

/**
 * interfaccia generale implementata da tutte le altre interfacce dedicate alle repository,
 * che dichiara i metodi da implementare in queste ultime
 *
 * @param <T>
 */
public interface Repository<T> {
    /**
     * aggiunge un elemento al db
     *
     * @param toAdd elemento da aggiungere
     * @throws RepositoryException
     */
    public void add(T toAdd) throws RepositoryException;

    /**
     * rimuove un elemento dal db
     *
     * @param toRemove elemento da rimuovere
     * @throws RepositoryException
     */
    public void remove(T toRemove) throws RepositoryException;

    /**
     * aggiorna un elemento nel db
     *
     * @param toUpdate
     * @throws RepositoryException
     */
    public void update(T toUpdate) throws RepositoryException;

    /**
     * seleziona un insieme di elementi dal db
     *
     * @param criterion query generica per eseguire la selezione
     * @return lista di elementi da selezionare
     * @throws RepositoryException
     */
    public List<T> select(Criterion criterion) throws RepositoryException;

}