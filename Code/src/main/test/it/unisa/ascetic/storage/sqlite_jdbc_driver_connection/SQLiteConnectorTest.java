package it.unisa.ascetic.storage.sqlite_jdbc_driver_connection;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

public class SQLiteConnectorTest {

    @Test
    public void testConnect() {
        Connection conn = null;
        SQLiteConnector connector = null;
        try {
            conn = connector.connect();
            String risultato="Connessione non riuscita";
            if(conn!=null){risultato="Connessione riuscita";};
            Logger log=Logger.getLogger(getClass().getName());
            log.info("\n"+String.valueOf(risultato));
            assertNotNull(conn);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}