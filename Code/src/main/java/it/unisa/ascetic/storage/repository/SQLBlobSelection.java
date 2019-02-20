package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;

public class SQLBlobSelection implements SQLiteCriterion {
    @Override
    public String toSQLquery() {
        return "SELECT ClassBean.fullQualifiedName AS CfullQualifiedName, ClassBean.textContent AS CtextContent, belongingPackage, PackageBean.textContent AS PtextContent," +
                "LOC,superclass,entityClassUsage,pathToFile " +
                "FROM (Classe_SmellType INNER JOIN ClassBean ON classBeanFullQualifiedName=ClassBean.fullQualifiedName) INNER JOIN PackageBean ON ClassBean.belongingPackage=PackageBean.fullQualifiedName " +
                "WHERE codeSmellFullQualifiedName='"+CodeSmell.BLOB +"'";
    }
}
