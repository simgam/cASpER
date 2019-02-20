package it.unisa.ascetic.storage.repository;

public class SQLCalledMethodsSelection implements SQLiteCriterion {

    private final String methodCaller;

    /**
     * costruttore
     *
     * @param callerMethod FQn del metodo chiamante
     */
    public SQLCalledMethodsSelection(String callerMethod) {
        this.methodCaller = callerMethod;
    }

    /**
     * @return query che seleziona la lista di metodi chiamati dal metodo avente come fqn
     * "methodCaller"
     */
    @Override
    public String toSQLquery() {
        return "SELECT MethodBean.fullQualifiedName AS MfullQualifiedName, belongingClass, ClassBean.textContent AS CtextContent, MethodBean.textContent AS MtextContent," +
                "isDefaultConstructor, staticMethod, return_type, visibility " +
                "FROM (Methods_Calls INNER JOIN MethodBean ON methodCalledFullQualifiedName=MethodBean.fullQualifiedName) INNER JOIN ClassBean ON ClassBean.fullQualifiedName=MethodBean.belongingClass " +
                "WHERE methodCallerFullQualifiedName = '" +
                "" + this.methodCaller+"'";
    }
}
