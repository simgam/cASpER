package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.MethodBean;

import java.util.List;

/**
 * interfaccia che dichiara i metodi da implementare nella repository dedicata ai metodi
 */
public interface MethodBeanRepository extends Repository<MethodBean> {
    /**
     * aggiunge un metodo al db
     *
     * @param aMethod metodo da aggiungere
     * @throws RepositoryException
     */
    public void add(MethodBean aMethod) throws RepositoryException;

    /**
     * rimuove un metodo dal db
     *
     * @param aMethod metodo da rimuovere
     * @throws RepositoryException
     */
    public void remove(MethodBean aMethod) throws RepositoryException;

    /**
     * aggiorna un metodo contenuto nel db con nuovi valori
     *
     * @param aMethod metodo aggiornato da reinserire
     * @throws RepositoryException
     */
    public void update(MethodBean aMethod) throws RepositoryException;

    /**
     * seleziona una lista di metodi dal db
     *
     * @param criterion generica query utilizzata per la selezione dei metodi
     * @return lista di metodi selezionati
     * @throws RepositoryException
     */
    public List<MethodBean> select(Criterion criterion) throws RepositoryException;

}