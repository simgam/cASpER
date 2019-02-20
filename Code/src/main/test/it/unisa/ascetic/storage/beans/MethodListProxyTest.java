package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class MethodListProxyTest {

    private Connection con;
    private Statement selection;

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
            con.commit();
            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (SQLException e) {
            try {
                tearDown();
            } catch (Exception ex) {
            }
            e.printStackTrace();
        }
        ;
    }

    @After
    public void tearDown() throws Exception {
        con = SQLiteConnector.connect();
        selection = con.createStatement();
        String sql = "DELETE FROM PackageBean; DELETE FROM ClassBean; DELETE FROM MethodBean;";
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
    public void getList() {

        MethodListProxy proxy=new MethodListProxy("classe");
        ClassBean classe = new ClassBean.Builder("classe","contenuto")
                .setMethods(proxy)
                .build();
        Logger log=Logger.getLogger(getClass().getName());
        log.info("\n"+classe.getMethodList());
        if(classe.getMethodList()!=null){log.info("true");}
        else {log.info("false");}
        assertNotNull(classe.getMethodList());
    }
}