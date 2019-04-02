package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.BlobCodeSmell;
import it.unisa.ascetic.analysis.code_smell.MisplacedClassCodeSmell;
import it.unisa.ascetic.analysis.code_smell_detection.blob.TextualBlobStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.TextualMisplacedClassStrategy;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ClassRepositoryTest {

    private Connection con;
    private Statement selection;
    private ClassRepository repo = new ClassRepository();
    private ClassBean classe;
    private ResultSet res = null;

    @Before
    public void setUp() throws Exception {
        con = SQLiteConnector.connect();
        selection = con.createStatement();
        String sql;
        try {
            con.setAutoCommit(false);
            sql = "INSERT OR REPLACE INTO PackageBean(fullQualifiedName, textContent) VALUES ('package','contenuto')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO PackageBean(fullQualifiedName, textContent) VALUES ('package2','contenuto')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES ('classe','contenuto','package','1000','','10','C:\\Users')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES ('classe2','contenuto','package2','800','','20','C:\\Users')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO MethodBean(fullQualifiedName,return_type,textContent,staticMethod,isDefaultConstructor,belongingClass,visibility) VALUES ('metodo','void','contenuto','false','false','classe','public')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO InstanceVariableBean(fullQualifiedName,tipo,initialization,visibility) VALUES('fullQualifiedName','string','user','public')";
            selection.executeUpdate(sql);

            sql = "INSERT OR REPLACE INTO Instance_Variable_Used(methodBeanFullQualifiedName,instanceVariableBeanFullQualifiedName) VALUES ('metodo','fullQualifiedName')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Methods_Calls(methodCallerFullQualifiedName,methodCalledFullQualifiedName) VALUES ('metodo','metodo2')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Parameter_Used(methodBeanFullQualifiedName,parameterClassFullQualifiedName,typeParameter,classBeanFullQualifiedName) VALUES ('metodo','name','String','classe')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Classe_SmellType(classBeanFullQualifiedName, codeSmellFullQualifiedName, fqn_envied_package) VALUES ('classe','misplaced class','package2')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Classe_SmellType(classBeanFullQualifiedName, codeSmellFullQualifiedName, fqn_envied_package) VALUES ('classe','blob','package2')";
            selection.executeUpdate(sql);
            con.commit();
            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (SQLException e) {
            tearDown();
            e.printStackTrace();
        }
        ;
    }

    @After
    public void tearDown() throws Exception {
        con = SQLiteConnector.connect();
        selection = con.createStatement();
        String sql = "DELETE FROM PackageBean; DELETE FROM ClassBean; DELETE FROM MethodBean; DELETE FROM InstanceVariableBean; DELETE FROM Instance_Variable_Used; DELETE FROM Methods_Calls; DELETE FROM Parameter_Used; DELETE FROM Classe_SmellType";
        try {
            selection.executeUpdate(sql);
            con.commit();
            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ;
    }

    @Test
    public void add() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("variabile", "string", "testo", "private"));
        MethodBeanList methods = new MethodList();
        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());

        methods.getList().add(new MethodBean.Builder("metodo_chiamato", "contenuto")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setMethodsCalls(null)
                .setParameters(hash)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("classe", "").build())
                .setEnviedClass(null)
                .setVisibility("public")
                .build());

        classe = new ClassBean.Builder("new", "contenuto")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setLOC(500)
                .setSuperclass("")
                .setBelongingPackage(new PackageBean.Builder("package", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(20)
                .setPathToFile("C:\\Users")
                .build();

        ClassBean oracolo = new ClassBean.Builder("new", "contenuto")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setLOC(500)
                .setSuperclass("")
                .setBelongingPackage(new PackageBean.Builder("package", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(20)
                .setPathToFile("C:\\Users")
                .build();
        try {
            selection = con.createStatement();
            repo.add(classe);

            String sql = "SELECT * FROM Classe_VariabileIstanza INNER JOIN InstanceVariableBean ON classBeanFullQualifiedName='new'";
            res = selection.executeQuery(sql);
            instances = new InstanceVariableList();
            while (res.next()) {
                instances.getList().add(new InstanceVariableBean(res.getString("fullQualifiedName"), res.getString("tipo"), res.getString("initialization"), res.getString("visibility")));
            }
            sql = "SELECT * FROM ClassBean INNER JOIN MethodBean ON MethodBean.belongingClass='new'";
            res = selection.executeQuery(sql);
            methods = new MethodList();
            while (res.next()) {
                methods.getList().add(new MethodBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent")).build());
            }
            sql = "SELECT * FROM ClassBean WHERE fullQualifiedName='new'";
            ResultSet res = selection.executeQuery(sql);
            while (res.next()) {
                classe = new ClassBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent"))
                        .setInstanceVariables(instances)
                        .setMethods(methods)
                        .setLOC(Integer.parseInt(res.getString("LOC")))
                        .setSuperclass(res.getString("superclass"))
                        .setBelongingPackage(new PackageBean.Builder(res.getString("belongingPackage"), "").build())
                        .setEnviedPackage(null)
                        .setEntityClassUsage(Integer.parseInt(res.getString("entityClassUsage")))
                        .setPathToFile(res.getString("pathToFile"))
                        .build();
            }
            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (Exception e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
        Logger log=Logger.getLogger(getClass().getName());
        log.info("\n"+classe+"\n\n"+oracolo);
        assertEquals(classe, oracolo);
    }

    @Test
    public void update() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        classe = new ClassBean.Builder("classe", "contenuto")
                .setBelongingPackage(new PackageBean.Builder("package2", "").build())
                .setLOC(300)
                .setSuperclass("classe2")
                .setEntityClassUsage(20)
                .setPathToFile("D:\\Users")
                .setAffectedSmell()
                .build();

        List<PackageBean> systemPackage = new ArrayList<PackageBean>();
        systemPackage.add(new PackageBean.Builder("package", "contenuto").setClassList(new ClassList()).build());
        systemPackage.get(0).addClassList(classe);
        classe.addSmell(new MisplacedClassCodeSmell(new TextualMisplacedClassStrategy(systemPackage)));
        classe.addSmell(new BlobCodeSmell(new TextualBlobStrategy()));

        ClassBean oracolo = new ClassBean.Builder("classe", "contenuto")
                .setBelongingPackage(new PackageBean.Builder("package2", "").build())
                .setLOC(300)
                .setSuperclass("classe2")
                .setEnviedPackage(null)
                .setEntityClassUsage(20)
                .setPathToFile("D:\\Users")
                .build();
        try {
            selection = con.createStatement();
            repo.update(classe);
            String sql = "SELECT * FROM ClassBean WHERE fullQualifiedName='classe'";
            ResultSet res = selection.executeQuery(sql);

            classe = new ClassBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent"))
                    .setBelongingPackage(new PackageBean.Builder(res.getString("belongingPackage"), "").build())
                    .setLOC(Integer.parseInt(res.getString("LOC")))
                    .setSuperclass(res.getString("superclass"))
                    .setEnviedPackage(null)
                    .setEntityClassUsage(Integer.parseInt(res.getString("entityClassUsage")))
                    .setPathToFile(res.getString("pathToFile"))
                    .build();

            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (Exception e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
        Logger log=Logger.getLogger(getClass().getName());
        log.info("\n"+classe+"\n\n"+oracolo);
        assertEquals(classe, oracolo);
    }

    @Test
    public void remove() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        classe = new ClassBean.Builder("classe", "contenuto").build();
        try {
            selection = con.createStatement();
            repo.remove(classe);
            String sql = "SELECT * FROM ClassBean WHERE fullQualifiedName='classe'";

            res = selection.executeQuery(sql);
            selection.close();
            SQLiteConnector.releaseConnection(con);
            boolean risultato=res.next();
            Logger log=Logger.getLogger(getClass().getName());
            log.info("\n"+String.valueOf(risultato));
            assertFalse(risultato);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }

    @Test
    public void select() {

        List<ClassBean> list = null;
        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("variabile", "string", "testo", "private"));
        MethodBeanList methods = new MethodList();
        methods.getList().add(new MethodBean.Builder("metodo", "contenuto")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(null)
                .setMethodsCalls(null)
                .setParameters(null)
                .setStaticMethod(false)
                .setDefaultCostructor(true)
                .setBelongingClass(new ClassBean.Builder("classe", "").build())
                .setVisibility("public")
                .build());

        List<ClassBean> oracolo = new ArrayList<ClassBean>();
        oracolo.add(new ClassBean.Builder("classe", "contenuto")
                .setInstanceVariables(instances)
                .setMethods(methods)
                .setImports(new ArrayList<String>())
                .setLOC(1000)
                .setSuperclass(null)
                .setBelongingPackage(new PackageBean.Builder("package", "contenuto").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(10)
                .setPathToFile("C:\\Users")
                .setAffectedSmell()
                .build());

        List<PackageBean> systemPackage = new ArrayList<PackageBean>();
        systemPackage.add(new PackageBean.Builder("package", "contenuto").setClassList(new ClassList()).build());
        oracolo.get(0).addSmell(new MisplacedClassCodeSmell(new TextualMisplacedClassStrategy(systemPackage)));
        oracolo.get(0).addSmell(new BlobCodeSmell(new TextualBlobStrategy()));
        try {
            list = repo.select(new SQLSelectionClass("package"));

        } catch (Exception e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
        Logger log=Logger.getLogger(getClass().getName());
        log.info("\n"+list+"\n\n"+oracolo);
        assertEquals(list, oracolo);
    }
}