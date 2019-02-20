package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.ClassBean;

/**
 * oggetto che istanzia la query da eseguire per la selezione delle variabili di istanza
 */
public class SQLSelectionInstance implements SQLiteCriterion {

    private final String belongingClass;

    public SQLSelectionInstance(String aClass) {
        this.belongingClass = aClass;
    }

    /**
     * @return stringa per selezionare tutte le variabili di istanza di una data classe nel db
     */
    public String toSQLquery() {
        return "SELECT * " +
                "FROM InstanceVariableBean INNER JOIN Classe_VariabileIstanza ON fullQualifiedName=instanceVariableBeanFullQualifiedName " +
                "WHERE classBeanFullQualifiedName = '" +
                ""+ belongingClass+"'";
    }
}
