package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.ClassBean;

import java.util.List;

/**
 * interfaccia che dichiara i metodi da implementare nella repository dedicata alle classi
 */
public interface ClassBeanRepository extends Repository<ClassBean> {

    /**
     * aggiunge una classe al db
     *
     * @param aClass classe da aggiungere
     * @throws RepositoryException
     */
    public void add(ClassBean aClass) throws RepositoryException;

    /**
     * rimuove una classe dal db
     *
     * @param aClass classe da rimuovere
     * @throws RepositoryException
     */
    public void remove(ClassBean aClass) throws RepositoryException;

    /**
     * aggiorna i dati di una classe
     *
     * @param aClass classe aggiornata da reinserire
     * @throws RepositoryException
     */
    public void update(ClassBean aClass) throws RepositoryException;

    /**
     * restituisce una lista di classi in base alla query specificata dal criterion
     *
     * @param criterion generica query per la selezione dal db
     * @return lista di classi ottenuta dalla query
     * @throws RepositoryException
     */
    public List<ClassBean> select(Criterion criterion) throws RepositoryException;

}