package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.InstanceVariableBean;

import java.util.List;

/**
 * interfaccia che dichiara i metodi da implementare nella repository dedicata alle variabili d'istanza
 */
public interface InstanceVariableBeanRepository extends Repository<InstanceVariableBean> {
    /**
     * aggiunge una variabile d'istanza al db
     *
     * @param aInstance variabile da aggiungere al db
     * @throws RepositoryException
     */
    public void add(InstanceVariableBean aInstance) throws RepositoryException;

    /**
     * rimuove una variabile d'istanza dal db
     *
     * @param aInstance variabile da rimuovere dal db
     * @throws RepositoryException
     */
    public void remove(InstanceVariableBean aInstance) throws RepositoryException;

    /**
     * aggiorna i dati di una variabile d'istanza contenuta nel db
     *
     * @param aInstance variabile aggiornata da reinserire
     * @throws RepositoryException
     */
    public void update(InstanceVariableBean aInstance) throws RepositoryException;

    /**
     * seleziona una lista di variabili di istanza dal db
     *
     * @param criterion generica query utilizzata per la selezione
     * @return lista di variabili selezionate
     * @throws RepositoryException
     */
    public List<InstanceVariableBean> select(Criterion criterion) throws RepositoryException;

}