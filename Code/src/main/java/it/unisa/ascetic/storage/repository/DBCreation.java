package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * classe addetta alla creazione del db
 */
public class DBCreation {
    /**
     * metodo che costruisce il db tramite le apposite query
     *
     * @param conn
     * @return true se il db è stato creato correttamente, false altrimenti
     */
    public static boolean createSQL(Connection conn) {

        StringBuilder sql = new StringBuilder();

        sql.append(//PackageBean
                "CREATE TABLE IF NOT EXISTS PackageBean ("
                        + "fullQualifiedName VARCHAR(255) PRIMARY KEY, "
                        + "textContent TEXT NOT NULL"
                        + ");");
        sql.append(//ClassBean
                "CREATE TABLE IF NOT EXISTS ClassBean ("
                        + "fullQualifiedName VARCHAR(255) PRIMARY KEY, "
                        + "textContent TEXT NOT NULL, "
                        + "belongingPackage VARCHAR(255) NOT NULL, "
                        + "LOC INTEGER(10) NOT NULL, "
                        + "superclass VARCHAR(255), "
                        + "entityClassUsage INTEGER(10), "
                        + "pathToFile VARCHAR(255),"
                        + "FOREIGN KEY (superclass) REFERENCES ClassBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (belongingPackage) REFERENCES PackageBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//MethodBean
                "CREATE TABLE IF NOT EXISTS MethodBean ("
                        + "fullQualifiedName VARCHAR(255) PRIMARY KEY, "
                        + "textContent TEXT NOT NULL, "
                        + "return_type VARCHAR(255) , "
                        + "staticMethod BINARY(1) NOT NULL, "
                        + "isDefaultConstructor BINARY(1) NOT NULL, "
                        + "belongingClass VARCHAR(255) NOT NULL, "
                        + "visibility VARCHAR(255) NOT NULL, "
                        + "FOREIGN KEY (belongingClass) REFERENCES ClassBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//InstanceVariableBean
                "CREATE TABLE IF NOT EXISTS InstanceVariableBean ("
                        + "fullQualifiedName VARCHAR(255) PRIMARY KEY, "
                        + "tipo VARCHAR(255) NOT NULL, "
                        + "initialization VARCHAR(255) NOT NULL, "
                        + "visibility VARCHAR(255) NOT NULL"
                        + ");");
        sql.append(//CodeSmell
                "CREATE TABLE IF NOT EXISTS CodeSmell ("
                        + "fullQualifiedName VARCHAR(255) PRIMARY KEY, "
                        + "detectionStrategy VARCHAR(255) NOT NULL"
                        + ");");
        sql.append(//Classe_VariabileIstanza
                "CREATE TABLE IF NOT EXISTS Classe_VariabileIstanza ("
                        + "classBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "instanceVariableBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "PRIMARY KEY(classBeanFullQualifiedName,instanceVariableBeanFullQualifiedName),"
                        + "FOREIGN KEY (instanceVariableBeanFullQualifiedName) REFERENCES InstanceVariableBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (classBeanFullQualifiedName) REFERENCES ClassBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//Instance_Variable_Used
                "CREATE TABLE IF NOT EXISTS Instance_Variable_Used ("
                        + "methodBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "instanceVariableBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "PRIMARY KEY(methodBeanFullQualifiedName,instanceVariableBeanFullQualifiedName),"
                        + "FOREIGN KEY (methodBeanFullQualifiedName) REFERENCES MethodBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (instanceVariableBeanFullQualifiedName) REFERENCES InstanceVariableBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//Index_CodeSmell
                "CREATE TABLE IF NOT EXISTS Index_CodeSmell ("
                        + "indexId VARCHAR(255), "
                        + "indice INTEGER(5), "
                        + "name VARCHAR(255), "
                        + "PRIMARY KEY(indexId,indice)"
                        + ");");
        sql.append(//Package_SmellType
                "CREATE TABLE IF NOT EXISTS Package_SmellType ("
                        + "packageBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "codeSmellFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "algorithmUsed VARCHAR(255), "
                        + "indice VARCHAR(255), "
                        + "PRIMARY KEY(packageBeanFullQualifiedName,codeSmellFullQualifiedName,algorithmUsed),"
                        + "FOREIGN KEY (packageBeanFullQualifiedName) REFERENCES PackageBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (codeSmellFullQualifiedName) REFERENCES CodeSmell (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (indice) REFERENCES Index_CodeSmell (indexId) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//Classe_SmellType
                "CREATE TABLE IF NOT EXISTS Classe_SmellType ("
                        + "classBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "codeSmellFullQualifiedName VARCHAR(255) NOT NULL , "
                        + "fqn_envied_package VARCHAR(255), "
                        + "algorithmUsed VARCHAR(255), "
                        + "indice VARCHAR(255), "
                        + "PRIMARY KEY(classBeanFullQualifiedName,codeSmellFullQualifiedName,algorithmUsed),"
                        + "FOREIGN KEY (classBeanFullQualifiedName) REFERENCES ClassBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (fqn_envied_package) REFERENCES PackageBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (codeSmellFullQualifiedName) REFERENCES CodeSmell (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (indice) REFERENCES Index_CodeSmell (indexId) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//Metodo_SmellType
                "CREATE TABLE IF NOT EXISTS Metodo_SmellType ("
                        + "methodBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "codeSmellFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "fqn_envied_class VARCHAR(255), "
                        + "algorithmUsed VARCHAR(255), "
                        + "indice VARCHAR(255), "
                        + "PRIMARY KEY(methodBeanFullQualifiedName,codeSmellFullQualifiedName,algorithmUsed),"
                        + "FOREIGN KEY (methodBeanFullQualifiedName) REFERENCES MethodBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (fqn_envied_class) REFERENCES ClassBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (codeSmellFullQualifiedName) REFERENCES CodeSmell (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (indice) REFERENCES Index_CodeSmell (indexId) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//Methods_Calls
                "CREATE TABLE IF NOT EXISTS Methods_Calls ("
                        + "methodCallerFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "methodCalledFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "PRIMARY KEY(methodCallerFullQualifiedName,methodCalledFullQualifiedName),"
                        + "FOREIGN KEY (methodCallerFullQualifiedName) REFERENCES MethodBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (methodCalledFullQualifiedName) REFERENCES MethodBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append(//Parameter_Used
                "CREATE TABLE IF NOT EXISTS Parameter_Used ("
                        + "methodBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "parameterClassFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "typeParameter VARCHAR(255) NOT NULL,"
                        + "classBeanFullQualifiedName VARCHAR(255) NOT NULL, "
                        + "PRIMARY KEY(methodBeanFullQualifiedName,parameterClassFullQualifiedName,classBeanFullQualifiedName),"
                        + "FOREIGN KEY (methodBeanFullQualifiedName) REFERENCES MethodBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "FOREIGN KEY (classBeanFullQualifiedName) REFERENCES ClassBean (fullQualifiedName) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.MISPLACED_CLASS + "','TextualMisplacedClassStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.FEATURE_ENVY + "','TextualFeatureEnvyStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.BLOB + "','TextualBlobStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.PROMISCUOUS_PACKAGE + "','TextualPromiscuousPackageStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.MISPLACED_CLASS + "','StructuralMisplacedClassStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.FEATURE_ENVY + "','StructuralFeatureEnvyStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.BLOB + "','StructuralBlobStrategy');");
        sql.append("INSERT OR REPLACE INTO CodeSmell (fullQualifiedName,detectionStrategy) VALUES ('" + CodeSmell.PROMISCUOUS_PACKAGE + "','StructuralPromiscuousPackageStrategy');");

        try (Statement cmd = conn.createStatement()) {
            cmd.executeUpdate(sql.toString());
            conn.commit();
            cmd.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
