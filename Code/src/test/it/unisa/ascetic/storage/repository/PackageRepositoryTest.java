package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.PromiscuousPackageCodeSmell;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.TextualPromiscuousPackageStrategy;
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

public class PackageRepositoryTest {

    private Connection con;
    private Statement selection;
    private PackageRepository repo = new PackageRepository();
    private PackageBean pack;
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
            sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES ('classe','contenuto','package','1000','','','C:\\Users')";
            selection.executeUpdate(sql);
            sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES ('classe2','contenuto','package','800','','','C:\\Users')";
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
            sql = "INSERT OR REPLACE INTO Package_SmellType(packageBeanFullQualifiedName, codeSmellFullQualifiedName) VALUES ('package','promiscuous package')";
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
        String sql = "DELETE FROM PackageBean; DELETE FROM ClassBean; DELETE FROM MethodBean; DELETE FROM InstanceVariableBean; DELETE FROM Instance_Variable_Used; DELETE FROM Methods_Calls; DELETE FROM Parameter_Used; DELETE FROM Package_SmellType";
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
        instances.getList().add(new InstanceVariableBean("instance", "string", "pippo", "private"));
        HashMap<String, ClassBean> hash = new HashMap<String, ClassBean>();
        hash.put("name", new ClassBean.Builder("String", "").build());
        MethodBeanList methods = new MethodList();
        methods.getList().add(new MethodBean.Builder("metodo2", "")
                .setBelongingClass(new ClassBean.Builder("prova", "").build())
                .setReturnType(new ClassBean.Builder("String", "").build())
                .setInstanceVariableList(instances)
                .setVisibility("public")
                .setParameters(hash)
                .build());
        ClassBeanList classes = new ClassList();
        classes.getList().add(new ClassBean.Builder("prova", "contenuto")
                .setMethods(methods)
                .setInstanceVariables(instances)
                .setBelongingPackage(new PackageBean.Builder("package", "").build())
                .build());

        pack = new PackageBean.Builder("new", "contenuto")
                .setClassList(classes)
                .build();

        PackageBean oracolo = new PackageBean.Builder("new", "contenuto")
                .setClassList(classes)
                .build();
        try {
            selection = con.createStatement();
            repo.add(pack);

            String sql = "SELECT * FROM ClassBean INNER JOIN PackageBean ON belongingPackage='package'";
            res = selection.executeQuery(sql);
            classes = new ClassList();
            while (res.next()) {
                classes.getList().add(new ClassBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent")).build());
            }
            sql = "SELECT * FROM PackageBean WHERE fullQualifiedName='new'";
            res = selection.executeQuery(sql);
            pack = new PackageBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent"))
                    .setClassList(classes)
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
        log.info("\n" + pack + "\n\n" + oracolo);
        assertEquals(pack, oracolo);
    }

    @Test
    public void update() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pack = new PackageBean.Builder("package", "test").build();
        PackageBean oracolo = new PackageBean.Builder("package", "test").build();
        try {
            selection = con.createStatement();
            repo.update(pack);
            String sql = "SELECT * FROM PackageBean WHERE fullQualifiedName='package'";
            ResultSet res = selection.executeQuery(sql);

            pack = new PackageBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent")).build();

            List<PackageBean> systemPackage = new ArrayList<PackageBean>();
            systemPackage.add(pack);
            pack.addSmell(new PromiscuousPackageCodeSmell(new TextualPromiscuousPackageStrategy(0.5), "Testuale"));

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
        log.info("\n\n" + pack + "\n" + oracolo);
        assertEquals(pack, oracolo);
    }

    @Test
    public void remove() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pack = new PackageBean.Builder("package", "contenuto").build();
        try {
            selection = con.createStatement();
            repo.remove(pack);
            String sql = "SELECT * FROM PackageBean WHERE fullQualifiedName='package'";

            res = selection.executeQuery(sql);
            selection.close();
            SQLiteConnector.releaseConnection(con);
            boolean risultato = res.next();
            Logger log = Logger.getLogger(getClass().getName());
            log.info("\n\n" + String.valueOf(risultato));
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
        List<PackageBean> list = null;
        ClassBeanList classes = new ClassList();
        classes.getList().add(new ClassBean.Builder("classe", "contenuto").setBelongingPackage(new PackageBean.Builder("package", "").build()).build());

        ClassBeanList classes2 = new ClassList();
        classes.getList().add(new ClassBean.Builder("classe2", "contenuto").setBelongingPackage(new PackageBean.Builder("package2", "").build()).build());

        List<PackageBean> oracolo = new ArrayList<PackageBean>();
        oracolo.add(new PackageBean.Builder("package", "contenuto")
                .setClassList(classes)
                .build());
        oracolo.add(new PackageBean.Builder("package2", "contenuto")
                .setClassList(classes2)
                .build());
        oracolo.get(0).addSmell(new PromiscuousPackageCodeSmell(new TextualPromiscuousPackageStrategy(0.5), "Testuale"));
        try {
            list = repo.select(new SQLSelectionPackage());


        } catch (Exception e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
        Logger log = Logger.getLogger(getClass().getName());
        log.info("\n" + list + "\n\n" + oracolo);
        assertEquals(list, oracolo);
    }
}