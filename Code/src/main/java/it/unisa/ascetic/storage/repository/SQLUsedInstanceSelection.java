package it.unisa.ascetic.storage.repository;

public class SQLUsedInstanceSelection implements SQLiteCriterion {

    private final String methodCaller;

    /**
     * costruttore
     *
     * @param callerMethod FQN del metodo che utilizza le variabili di istanza
     */
    public SQLUsedInstanceSelection(String callerMethod) {
        this.methodCaller = callerMethod;
    }

    /**
     * @return query per selezionare la lista di variabili di istanza utilizzate dal metodo
     * avente come FQN "methodCaller"
     */
    @Override
    public String toSQLquery() {
        return "SELECT *" +
                "FROM Instance_Variable_Used INNER JOIN InstanceVariableBean ON instanceVariableBeanFullQualifiedName=fullQualifiedName " +
                "WHERE methodBeanFullQualifiedName ='" +
                "" + this.methodCaller + "'";
    }
}
