package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.storage.beans.InstanceVariableBean;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * repository dedicata alle variabili d'istanza
 */
public class InstanceVariableRepository implements InstanceVariableBeanRepository {

    private Connection con = null; // oggetto connessione per stabilire connessione col db
    private PreparedStatement stat = null;// variabile per creare statement da eseguire
    private ResultSet res = null;// resultset per memorizzare risultati della query

    /**
     * aggiunge una variabile di istanza al db
     *
     * @param aInstance variabile da aggiungere al db
     * @throws RepositoryException
     */
    @Override
    public void add(InstanceVariableBean aInstance) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "INSERT OR REPLACE INTO InstanceVariableBean(fullQualifiedName,tipo,initialization,visibility) VALUES(?,?,?,?)";

        try {
            con.setAutoCommit(false);
            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, aInstance.getFullQualifiedName());
            stat.setString(2, aInstance.getType());
            stat.setString(3, aInstance.getInitialization());
            stat.setString(4, aInstance.getVisibility());
            stat.executeUpdate();
            con.commit();
            stat.close();
            SQLiteConnector.releaseConnection(con);

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RepositoryException(ex.getMessage());
            }
            throw new RepositoryException(e.getMessage());
        }

    }

    /**
     * aggiorna i valori di una variabile di istanza nel db
     *
     * @param aInstance variabile da aggiornare
     * @throws RepositoryException
     */
    @Override
    public void update(InstanceVariableBean aInstance) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "UPDATE InstanceVariableBean SET tipo=?, initialization=?, visibility=? " +
                "WHERE fullQualifiedName=?";
        try {
            con.setAutoCommit(false);
            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, aInstance.getType());
            stat.setString(2, aInstance.getInitialization());
            stat.setString(3, aInstance.getVisibility());
            stat.setString(4, aInstance.getFullQualifiedName());
            stat.executeUpdate();
            con.commit();
            stat.close();
            SQLiteConnector.releaseConnection(con);

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RepositoryException(ex.getMessage());
            }
            throw new RepositoryException(e.getMessage());
        }

    }

    /**
     * rimuove una variabile di istanza dal db
     *
     * @param aInstance variabile da rimuovere dal db
     * @throws RepositoryException
     */
    @Override
    public void remove(InstanceVariableBean aInstance) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "DELETE FROM InstanceVariableBean WHERE fullQualifiedName=" + aInstance.getFullQualifiedName() + "";

        try {
            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.executeUpdate();
            con.commit();
            stat.close();
            SQLiteConnector.releaseConnection(con);

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RepositoryException(ex.getMessage());
            }
            throw new RepositoryException(e.getMessage());
        }

    }

    /**
     * seleziona una lista di variabili di istanza dal db
     *
     * @param criterion generica query utilizzata per la selezione
     * @return lista di variabili selezionate
     * @throws RepositoryException
     */
    @Override
    public List<InstanceVariableBean> select(Criterion criterion) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final List<InstanceVariableBean> instances = new ArrayList<InstanceVariableBean>();
        final SQLiteCriterion sqlCriterion = (SQLiteCriterion) criterion; // oggetto che istanzia la query usata per la selezione delle variabili

        try {
            String sql = sqlCriterion.toSQLquery();
            Statement selection = con.createStatement();
            res = selection.executeQuery(sql);
            InstanceVariableBean ib = null;
            while (res.next()) {
                ib = new InstanceVariableBean(res.getString("fullQualifiedName"), res.getString("tipo"), res.getString("initialization"), res.getString("visibility"));
                instances.add(ib);
            }
            con.commit();
            res.close();
            selection.close();
            SQLiteConnector.releaseConnection(con);
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RepositoryException(ex.getMessage());
            }
            throw new RepositoryException(e.getMessage());
        }

        return instances;
    }
}
