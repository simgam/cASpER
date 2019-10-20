package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.InstanceVariableBean;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InstanceVariableRepositoryTest {

    private Connection con;
    private Statement selection;
    private InstanceVariableRepository repo = new InstanceVariableRepository();
    private InstanceVariableBean ib;
    private ResultSet res = null;

    @Before
    public void setUp() throws Exception {

        con = SQLiteConnector.connect();
        selection = con.createStatement();
        String sql = "INSERT OR REPLACE INTO InstanceVariableBean(fullQualifiedName,tipo,initialization,visibility) VALUES('fullQualifiedName','string','admin','public')";
        try {
            selection.executeUpdate(sql);
            selection.close();
            con.commit();
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
        String sql = "DELETE FROM InstanceVariableBean;";
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
    public void add() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ib = new InstanceVariableBean("prova", "integer", "10", "private");
        InstanceVariableBean oracolo = new InstanceVariableBean("prova", "integer", "10", "private");

        try {
            selection = con.createStatement();
            repo.add(ib);
            String sql = "SELECT * FROM InstanceVariableBean WHERE fullQualifiedName='prova'";
            res = selection.executeQuery(sql);
            ib.setFullQualifiedName(res.getString("fullQualifiedName"));
            ib.setType(res.getString("tipo"));
            ib.setInitialization(res.getString("initialization"));
            ib.setVisibility(res.getString("visibility"));

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
        log.info("\n" + ib + "\n\n" + oracolo);
        assertEquals(ib, oracolo);
    }

    @Test
    public void update() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ib = new InstanceVariableBean("fullQualifiedName", "integer", "10", "private");
        InstanceVariableBean oracolo = new InstanceVariableBean("fullQualifiedName", "integer", "10", "private");

        try {
            selection = con.createStatement();
            repo.update(ib);
            String sql = "SELECT * FROM InstanceVariableBean WHERE fullQualifiedName='fullQualifiedName'";
            res = selection.executeQuery(sql);
            ib.setFullQualifiedName(res.getString("fullQualifiedName"));
            ib.setType(res.getString("tipo"));
            ib.setInitialization(res.getString("initialization"));
            ib.setVisibility(res.getString("visibility"));

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
        log.info("\n" + ib + "\n\n" + oracolo);
        assertEquals(ib, oracolo);
    }

    @Test
    public void remove() {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ib = new InstanceVariableBean("fullQualifiedName", "", "", "");
        try {
            selection = con.createStatement();
            repo.remove(ib);
            String sql = "SELECT * FROM InstanceVariableBean WHERE fullQualifiedName='fullQualifiedName'";
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
        List<InstanceVariableBean> list = null;
        List<InstanceVariableBean> oracolo = new ArrayList<InstanceVariableBean>();
        oracolo.add(new InstanceVariableBean("fullQualifiedName", "string", "admin", "public"));
        try {
            list = repo.select(new SQLSelectionInstanceVariableAll());
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