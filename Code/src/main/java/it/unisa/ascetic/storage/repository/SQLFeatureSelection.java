package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;

public class SQLFeatureSelection implements SQLiteCriterion {
    @Override
    public String toSQLquery() {
        return "SELECT DISTINCT MethodBean.fullQualifiedName AS MfullQualifiedName, Metodo_SmellType.methodBeanFullQualifiedName, Metodo_SmellType.codeSmellFullQualifiedName, belongingClass, fqn_envied_class, ClassBean.textContent AS CtextContent, MethodBean.textContent AS MtextContent," +
                "return_type,staticMethod,isDefaultConstructor,visibility " +
                "FROM (Metodo_SmellType INNER JOIN MethodBean ON methodBeanFullQualifiedName=MethodBean.fullQualifiedName) INNER JOIN ClassBean ON ClassBean.fullQualifiedName=MethodBean.belongingClass " +
                "WHERE codeSmellFullQualifiedName='"+CodeSmell.FEATURE_ENVY +"'";
    }
}
