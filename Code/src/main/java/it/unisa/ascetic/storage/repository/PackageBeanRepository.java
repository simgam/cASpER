package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.PackageBean;

import java.util.List;

/**
 * interfaccia che dichiara i metodi da implementare nella repository dedicata ai package
 */
public interface PackageBeanRepository extends Repository<PackageBean> {
    /**
     * aggiunge un package al db
     *
     * @param aPackage package da aggiungere
     * @throws RepositoryException
     */
    public void add(PackageBean aPackage) throws RepositoryException;

    /**
     * rimuove un package dal db
     *
     * @param aPackage package da rimuovere
     * @throws RepositoryException
     */
    public void remove(PackageBean aPackage) throws RepositoryException;

    /**
     * aggiorna un package contenuto nel db con nuovi valori
     *
     * @param aPackage package aggiornato da reinserire
     * @throws RepositoryException
     */
    public void update(PackageBean aPackage) throws RepositoryException;

    /**
     * seleziona una lista di package dal db
     *
     * @param criterion generica query usata per la selezione
     * @return lista di package selezionati
     * @throws RepositoryException
     */
    public List<PackageBean> select(Criterion criterion) throws RepositoryException;

}