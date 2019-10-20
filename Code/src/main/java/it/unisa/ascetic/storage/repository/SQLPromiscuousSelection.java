package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;

public class SQLPromiscuousSelection implements SQLiteCriterion {
    @Override
    public String toSQLquery() {
        return "SELECT DISTINCT PackageBean.fullQualifiedName, textContent " +
                "FROM Package_SmellType INNER JOIN PackageBean ON packageBeanFullQualifiedName=PackageBean.fullQualifiedName " +
                "WHERE codeSmellFullQualifiedName='" + CodeSmell.PROMISCUOUS_PACKAGE + "'";
    }
}
