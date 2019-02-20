package it.unisa.ascetic.storage.repository;

/**
 * interfaccia che estende criterion, dichiara il metodo da implementare
 * nelle classi che istanziano le query per la selezione nel db
 */
public interface SQLiteCriterion extends Criterion {
    /**
     * metodo che restituisce la query da eseguire nel db per la selezione
     *
     * @return stringa contenente la query da eseguire
     */
    public String toSQLquery();

}