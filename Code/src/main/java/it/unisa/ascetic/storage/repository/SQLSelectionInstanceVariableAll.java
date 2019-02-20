package it.unisa.ascetic.storage.repository;

public class SQLSelectionInstanceVariableAll implements SQLiteCriterion {

    /**
     * @return stringa per selezionare tutte le variabili di istanza nel db
     */
    public String toSQLquery() {
        return "SELECT * " +
                "FROM InstanceVariableBean";
    }
}
