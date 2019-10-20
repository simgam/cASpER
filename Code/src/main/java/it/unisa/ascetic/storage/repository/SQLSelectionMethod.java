package it.unisa.ascetic.storage.repository;

/**
 * oggetto che istanzia la query da eseguire per la selezione dei metodi
 */
public class SQLSelectionMethod implements SQLiteCriterion {

    private final String belongingClass;

    /**
     * Costruttore
     */
    public SQLSelectionMethod(String aClass) {
        belongingClass = aClass;
    }

    /**
     * @return stringa per selezionare tutti i metodi nel db
     */
    public String toSQLquery() {
        return "SELECT MethodBean.fullQualifiedName AS MfullQualifiedName, belongingClass, ClassBean.textContent AS CtextContent, MethodBean.textContent AS MtextContent," +
                "isDefaultConstructor, staticMethod, return_type, visibility " +
                "FROM MethodBean INNER JOIN ClassBean ON belongingClass=ClassBean.fullQualifiedName " +
                "WHERE MethodBean.BelongingClass = " +
                "'" + belongingClass + "'";
    }
}
