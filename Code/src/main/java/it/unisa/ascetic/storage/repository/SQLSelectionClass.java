package it.unisa.ascetic.storage.repository;

/**
 * oggetto che istanzia la query da eseguire per la selezione delle classi
 */
public class SQLSelectionClass implements SQLiteCriterion {

    private final String belongingPackage;

    /**
     * costruttore
     *
     * @param packageFullQualifiedName nome da settare
     */
    public SQLSelectionClass(String packageFullQualifiedName) {
        belongingPackage = packageFullQualifiedName;

    }

    /**
     * @return stringa per selezionare tutte le classi nel db
     */
    public String toSQLquery() {
        return "SELECT ClassBean.fullQualifiedName AS CfullQualifiedName, ClassBean.textContent AS CtextContent, belongingPackage, PackageBean.textContent AS PtextContent, LOC, superclass, entityClassUsage, pathToFile " +
                "FROM ClassBean INNER JOIN PackageBean ON belongingPackage=PackageBean.fullQualifiedName " +
                "WHERE belongingPackage = '" +
                "" + belongingPackage + "'";
    }
}
