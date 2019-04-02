package it.unisa.ascetic.storage.sqlite_jdbc_driver_connection;

import it.unisa.ascetic.storage.repository.DBCreation;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * oggetto che istanzia la connessione col database
 */
public class SQLiteConnector {

    private static String nameDB = "db";

    /**
     * settere
     *
     * @param name nome del db da settare
     */
    public static synchronized void setNameDB(String name) {
        nameDB = name;
    }

    /**
     * stabilisce una connessione col db
     *
     * @return oggetto connessione
     */
    public static synchronized Connection connect() throws SQLException {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String dir = System.getProperty("user.home") + File.separator +".ascetic";
            if (!(new File(dir)).exists()) {
                new File(dir).mkdir();
            }
            String url = "jdbc:sqlite:" + dir;
            // create a connection to the database

            if(!DBexists()) {
                conn = DriverManager.getConnection(url + File.separator + nameDB + ".db");
                conn.setAutoCommit(false);
                DBCreation.createSQL(conn);
            } else {
                conn = DriverManager.getConnection(url + File.separator + nameDB + ".db");
                conn.setAutoCommit(false);
            }
        } catch (SQLException e) {
            Logger logger= Logger.getLogger("global");
            logger.severe(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * verifica l'esistenza del db
     *
     * @return boolean true se il db esiste, false altrimenti
     */
    public static boolean DBexists() {
        String dir = System.getProperty("user.home") + File.separator +".ascetic"+File.separator+nameDB+".db";
        return (new File(dir)).exists();
    }

    public static boolean PrepareDB(){
        String dir = System.getProperty("user.home") + File.separator +".ascetic"+File.separator+nameDB+".db";
        File dbFile = new File(dir);
        dbFile.delete();
        return true;
    }

    /**
     * rilascia la connessione col db
     *
     * @param connection oggetto connessione
     * @throws SQLException
     */
    public static synchronized void releaseConnection(Connection connection) throws SQLException {
        if (connection != null) connection.close();
    }
}