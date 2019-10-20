package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.FeatureEnvyCodeSmell;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.TextualFeatureEnvyStrategy;
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

import static org.junit.Assert.*;

public class MethodRepositoryTest {

    private Connection con;
    private Statement selection;
    private MethodRepository repo = new MethodRepository();
    private MethodBean method;
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
            sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES ('classe','contenuto','package','1000','','','C:\\Users')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES ('classe2','contenuto','package','800','classe','','C:\\Users')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO MethodBean(fullQualifiedName,return_type,textContent,staticMethod,isDefaultConstructor,belongingClass,visibility) VALUES ('metodo','void','contenuto','false','false','classe','public')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO MethodBean(fullQualifiedName,return_type,textContent,staticMethod,isDefaultConstructor,belongingClass,visibility) VALUES ('metodo2','String','contenuto','false','true','classe2','public')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO InstanceVariableBean(fullQualifiedName,tipo,initialization,visibility) VALUES('fullQualifiedName','string','user','public')";
            selection.executeUpdate(sql);

            sql = "INSERT OR REPLACE INTO Instance_Variable_Used(methodBeanFullQualifiedName,instanceVariableBeanFullQualifiedName) VALUES ('metodo','fullQualifiedName')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Methods_Calls(methodCallerFullQualifiedName,methodCalledFullQualifiedName) VALUES ('metodo','metodo2')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Parameter_Used(methodBeanFullQualifiedName,parameterClassFullQualifiedName,typeParameter,classBeanFullQualifiedName) VALUES ('metodo','name','String','classe')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO Metodo_SmellType(methodBeanFullQualifiedName, codeSmellFullQualifiedName, fqn_envied_class) VALUES ('metodo','feature envy','classe2')";
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
        String sql = "DELETE FROM PackageBean; DELETE FROM ClassBean; DELETE FROM InstanceVariableBean; DELETE FROM MethodBean; DELETE FROM Instance_Variable_Used; DELETE FROM Methods_Calls; DELETE FROM Parameter_Used; DELETE FROM Metodo_SmellType;";
        try {
            selection.executeUpdate(sql);
            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ;
    }


    @Test
    public void add() throws SQLException {
        con = SQLiteConnector.connect();

        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("variabile", "string", "testo", "private"));
        MethodBeanList methods = new MethodList();
        methods.getList().add(new MethodBean.Builder("metodo_chiamato", "contenuto").build());
        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());

        method = new MethodBean.Builder("new", "contenuto")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setParameters(hash)
                .setInstanceVariableList(instances)
                .setMethodsCalls(methods)
                .setBelongingClass(new ClassBean.Builder("classe", "").build())
                .setEnviedClass(null)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setAffectedSmell()
                .setVisibility("public")
                .build();

        MethodBean oracolo = new MethodBean.Builder("new", "contenuto")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setParameters(hash)
                .setInstanceVariableList(instances)
                .setMethodsCalls(methods)
                .setBelongingClass(new ClassBean.Builder("classe", "").build())
                .setEnviedClass(null)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setAffectedSmell()
                .setVisibility("public")
                .build();
        try {
            selection = con.createStatement();
            repo.add(method);

            String sql = "SELECT * FROM Instance_Variable_Used INNER JOIN InstanceVariableBean ON methodBeanFullQualifiedName='new'";
            res = selection.executeQuery(sql);
            instances = new InstanceVariableList();
            while (res.next()) {
                instances.getList().add(new InstanceVariableBean(res.getString("fullQualifiedName"), res.getString("tipo"), res.getString("initialization"), res.getString("visibility")));
            }
            sql = "SELECT * FROM Methods_Calls INNER JOIN MethodBean ON methodCallerFullQualifiedName='new'";
            res = selection.executeQuery(sql);
            methods = new MethodList();
            while (res.next()) {
                methods.getList().add(new MethodBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent")).build());
            }
            hash = new HashMap<String, ClassBean>();
            sql = "SELECT parameterClassFullQualifiedName,typeParameter FROM Parameter_Used WHERE methodBeanFullQualifiedName='new' AND classBeanFullQualifiedName='classe'";
            res = selection.executeQuery(sql);
            while (res.next()) {
                hash.put(res.getString("parameterClassFullQualifiedName"), new ClassBean.Builder(res.getString("typeParameter"), "").build());
            }

            sql = "SELECT * FROM MethodBean WHERE fullQualifiedName='new'";
            res = selection.executeQuery(sql);
            method = new MethodBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent"))
                    .setReturnType(new ClassBean.Builder(res.getString("return_type"), "").build())
                    .setParameters(hash)
                    .setInstanceVariableList(instances)
                    .setMethodsCalls(methods)
                    .setBelongingClass(new ClassBean.Builder(res.getString("belongingClass"), "").build())
                    .setEnviedClass(null)
                    .setStaticMethod(false)
                    .setDefaultCostructor(false)
                    .setAffectedSmell()
                    .setVisibility(res.getString("visibility"))
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
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + method + "\n\n" + oracolo);
        assertEquals(method, oracolo);
    }

    @Test
    public void update() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        MethodBeanList methods = new MethodList();
        methods.getList().add(new MethodBean.Builder("metodo2", "").build());
        methods.getList().add(new MethodBean.Builder("metodo_chiamato", "").build());
        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("fullQualifiedName", "string", "user", "public"));

        method = new MethodBean.Builder("metodo", "prova")
                .setInstanceVariableList(instances)
                .setMethodsCalls(methods)
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setBelongingClass(new ClassBean.Builder("classe2", "").build())
                .setEnviedClass(new ClassBean.Builder("classe", "contenuto").build())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setVisibility("public")
                .setAffectedSmell()
                .build();

        ClassBean classe = new ClassBean.Builder("classe2", "contenuto")
                .setInstanceVariables(instances)
                .setMethods(new MethodList())
                .setLOC(500)
                .setSuperclass("")
                .setBelongingPackage(new PackageBean.Builder("package", "").build())
                .setEnviedPackage(null)
                .setEntityClassUsage(20)
                .setPathToFile("C:\\Users")
                .build();
        classe.addMethodBeanList(method);
        List<PackageBean> systemPackage = new ArrayList<PackageBean>();
        systemPackage.add(new PackageBean.Builder("package", "contenuto").setClassList(new ClassList()).build());
        systemPackage.get(0).addClassList(classe);
        method.addSmell(new FeatureEnvyCodeSmell(new TextualFeatureEnvyStrategy(systemPackage, 0.5), "textual"));

        MethodBean oracolo = new MethodBean.Builder("metodo", "prova")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setBelongingClass(new ClassBean.Builder("classe2", "").build())
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setVisibility("public")
                .setAffectedSmell()
                .build();
        oracolo.addSmell(new FeatureEnvyCodeSmell(new TextualFeatureEnvyStrategy(systemPackage, 0.5), "textual"));
        try {
            selection = con.createStatement();
            repo.update(method);
            String sql = "SELECT * FROM MethodBean WHERE fullQualifiedName='metodo'";
            ResultSet res = selection.executeQuery(sql);
            while (res.next()) {
                method = new MethodBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent"))
                        .setReturnType(new ClassBean.Builder(res.getString("return_type"), "").build())
                        .setBelongingClass(new ClassBean.Builder(res.getString("belongingClass"), "").build())
                        .setStaticMethod(res.getBoolean("staticMethod"))
                        .setDefaultCostructor(res.getBoolean("isDefaultConstructor"))
                        .setVisibility(res.getString("visibility"))
                        .setAffectedSmell()
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
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + method + "\n\n" + oracolo);
        assertEquals(method, oracolo);
    }

    @Test
    public void remove() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        method = new MethodBean.Builder("metodo", "contenuto").build();
        try {
            selection = con.createStatement();
            repo.remove(method);
            String sql = "SELECT * FROM MethodBean WHERE fullQualifiedName='metodo'";

            res = selection.executeQuery(sql);
            selection.close();
            SQLiteConnector.releaseConnection(con);
            boolean risultato = res.next();
            Logger log = Logger.getLogger(getClass().getName());
            log.info("\n" + String.valueOf(risultato));
            assertFalse(risultato);
        } catch (Exception e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
    }

    @Test
    public void select() {
        List<MethodBean> list = null;
        InstanceVariableBeanList instances = new InstanceVariableList();
        instances.getList().add(new InstanceVariableBean("variabile", "string", "testo", "private"));
        MethodBeanList methods = new MethodList();
        methods.getList().add(new MethodBean.Builder("metodo2", "contenuto")
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setParameters(new HashMap<String, ClassBean>())
                .setInstanceVariableList(new UsedInstanceVariableListProxy("fullQualifiedName"))
                .setMethodsCalls(new CalledMethodsListProxy("fullQualifiedName"))
                .setBelongingClass(new ClassBean.Builder("classe2", "").build())
                .setEnviedClass(null)
                .setStaticMethod(false)
                .setDefaultCostructor(false)
                .setAffectedSmell()
                .setVisibility("public")
                .build());
        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());

        try {
            list = repo.select(new SQLSelectionMethod("classe"));

        } catch (Exception e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + list + "\n");
        assertNotNull(list);
    }
}