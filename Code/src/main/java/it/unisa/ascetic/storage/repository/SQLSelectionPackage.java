package it.unisa.ascetic.storage.repository;

/**
 * oggetto che istanzia la query da eseguire per la selezione dei package
 */
public class SQLSelectionPackage implements SQLiteCriterion {
    /**
     * @return stringa per selezionare tutti i package nel db
     */
    public String toSQLquery() {
        return "SELECT * FROM PackageBean";
    }
}
