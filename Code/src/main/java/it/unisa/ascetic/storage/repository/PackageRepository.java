package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;
import it.unisa.ascetic.analysis.code_smell.PromiscuousPackageCodeSmell;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * repository dedicata ai package
 */
public class PackageRepository implements PackageBeanRepository {

    private Connection con = null; // oggetto connessione per stabilire la connessione col db
    private PreparedStatement stat = null;// variabile per creare statement da eseguire
    private ResultSet res = null;// resultset per memorizzare risultati della query

    /**
     * aggiunge un package al db e a cascata tutte le classi,metodi e variabili di istanza
     * contenuti nel package
     *
     * @param aPackage package da aggiungere
     * @throws RepositoryException
     */
    @Override
    public void add(PackageBean aPackage) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "INSERT OR REPLACE INTO PackageBean(fullQualifiedName,textContent) VALUES(?,?)";
        List<InstanceVariableBean> instanceList = new ArrayList<InstanceVariableBean>();
        List<MethodBean> methodList = new ArrayList<MethodBean>();
        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, aPackage.getFullQualifiedName());
            stat.setString(2, aPackage.getTextContent());
            stat.executeUpdate();

            List<ClassBean> classeList = new ArrayList<ClassBean>();
            if (aPackage.getClassList() != null) classeList = aPackage.getClassList();

            for (ClassBean classe : classeList) {
                addClass(classe, con);

                if (classe.getInstanceVariablesList() != null) {
                    instanceList = classe.getInstanceVariablesList();
                    for (InstanceVariableBean instance : instanceList) {
                        addInstanceVariable(instance, con);
                    }

                }

                if (classe.getMethodList() != null) {
                    methodList = classe.getMethodList();
                    for (MethodBean method : methodList) {
                        addMethod(method, con);
                    }

                }
            }
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
     * metodo privato che aggiunge una singola classe contenuta dal package al db,
     * contestualmente riempie anche le tabelle contenenti le relazioni con metodi
     * e variabili di istanza contenuti dalla classe
     *
     * @param toAdd classe da aggiungere
     * @param con   oggetto connessione
     * @throws RepositoryException
     */
    private void addClass(ClassBean toAdd, Connection con) throws RepositoryException {
        String sql = "INSERT OR REPLACE INTO ClassBean(fullQualifiedName,textContent,belongingPackage,LOC,superclass,entityClassUsage,pathToFile) VALUES(?,?,?,?,?,?,?)";
        List<InstanceVariableBean> instanceList = new ArrayList<InstanceVariableBean>();
        if (toAdd.getInstanceVariablesList() != null) instanceList = toAdd.getInstanceVariablesList();
        List<MethodBean> methodList = new ArrayList<MethodBean>();
        if (toAdd.getMethodList() != null) methodList = toAdd.getMethodList();

        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, toAdd.getFullQualifiedName());
            stat.setString(2, toAdd.getTextContent());
            stat.setString(3, toAdd.getBelongingPackage().getFullQualifiedName());
            stat.setInt(4, toAdd.getLOC());
            if (toAdd.getSuperclass() != null) {
                stat.setString(5, toAdd.getSuperclass());
            } else {
                stat.setString(5, "");
            }
            ;
            stat.setInt(6, toAdd.getEntityClassUsage());
            stat.setString(7, toAdd.getPathToFile());
            stat.executeUpdate();

            for (InstanceVariableBean instance : instanceList) {
                sql = "INSERT OR REPLACE INTO Classe_VariabileIstanza(classBeanFullQualifiedName,instanceVariableBeanFullQualifiedName) VALUES (?,?)";
                stat = (PreparedStatement) con.prepareStatement(sql);
                stat.setString(1, toAdd.getFullQualifiedName());
                stat.setString(2, instance.getFullQualifiedName());
                stat.executeUpdate();
            }
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
     * metodo privato che aggiunge un singolo metodo contenuto da una classe nel package,
     * riempie successivamente le relazioni delle variabili di istanza utilizzate e dei parametri utilizzati
     *
     * @param toAdd metodo da aggiungere
     * @param con   oggetto connessione
     * @throws RepositoryException
     */
    private void addMethod(MethodBean toAdd, Connection con) throws RepositoryException {
        String sql = "INSERT OR REPLACE INTO MethodBean(fullQualifiedName,textContent,return_type,staticMethod,isDefaultConstructor,belongingClass,visibility) VALUES (?,?,?,?,?,?,?)";

        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, toAdd.getFullQualifiedName());
            stat.setString(2, toAdd.getTextContent());
            if (toAdd.getReturnType() != null) {
                stat.setString(3, toAdd.getReturnType().getFullQualifiedName());
            } else {
                stat.setString(3, "");
            }
            ;
            stat.setBoolean(4, toAdd.getStaticMethod());
            stat.setBoolean(5, toAdd.getDefaultCostructor());
            stat.setString(6, toAdd.getBelongingClass().getFullQualifiedName());
            stat.setString(7, toAdd.getVisibility());
            stat.executeUpdate();

            List<InstanceVariableBean> list = new ArrayList<InstanceVariableBean>();
            if (toAdd.getInstanceVariableList() != null) {
                list = toAdd.getInstanceVariableList();
                for (InstanceVariableBean instance : list) {
                    sql = "INSERT OR REPLACE INTO Instance_Variable_Used(methodBeanFullQualifiedName,instanceVariableBeanFullQualifiedName) VALUES (?,?)";
                    stat = (PreparedStatement) con.prepareStatement(sql);
                    stat.setString(1, toAdd.getFullQualifiedName());
                    stat.setString(2, instance.getFullQualifiedName());
                    stat.executeUpdate();
                }
            }

            HashMap<String, ClassBean> hash = toAdd.getParameters();
            if (hash != null) {
                Set<String> set = hash.keySet();
                for (String key : set) {
                    sql = "INSERT OR REPLACE INTO Parameter_Used(methodBeanFullQualifiedName,parameterClassFullQualifiedName,typeParameter,classBeanFullQualifiedName) VALUES (?,?,?,?)";
                    stat = (PreparedStatement) con.prepareStatement(sql);
                    stat.setString(1, toAdd.getFullQualifiedName());
                    stat.setString(2, key);
                    stat.setString(3, hash.get(key).getFullQualifiedName());
                    stat.setString(4, toAdd.getBelongingClass().getFullQualifiedName());
                    stat.executeUpdate();
                }
            }
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RepositoryException(ex.getMessage());
            }
            throw new RepositoryException(e.getMessage());
        }
        ;
    }

    /**
     * metodo privato che aggiunge una singola  variabile di istanza contenuta da una classe nel package
     *
     * @param toAdd variabile da aggiungere
     * @param con   oggetto connessione
     * @throws RepositoryException
     */
    private void addInstanceVariable(InstanceVariableBean toAdd, Connection con) throws RepositoryException {
        String sql = "INSERT OR REPLACE INTO InstanceVariableBean(fullQualifiedName,tipo,initialization,visibility) VALUES(?,?,?,?)";
        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, toAdd.getFullQualifiedName());
            stat.setString(2, toAdd.getType());
            stat.setString(3, toAdd.getInitialization());
            stat.setString(4, toAdd.getVisibility());
            stat.executeUpdate();
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
     * aggiorna un package con nuovi valori nel db
     *
     * @param aPackage package aggiornato da reinserire
     * @throws RepositoryException
     */
    @Override
    public void update(PackageBean aPackage) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "UPDATE PackageBean SET textContent=? " +
                "WHERE fullQualifiedName=?";
        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, aPackage.getTextContent());
            stat.setString(2, aPackage.getFullQualifiedName());
            stat.executeUpdate();

            List<CodeSmell> list = aPackage.getAffectedSmell();
            if (list != null) {
                sql = "INSERT OR REPLACE INTO Index_CodeSmell (indexId,indice,name) VALUES (?,?,?);";
                String sql2 = "INSERT OR REPLACE INTO Package_SmellType (packageBeanFullQualifiedName,codeSmellFullQualifiedName,algorithmUsed,indice) VALUES (?,?,?,?);";

                String key = null;
                HashMap<String, Double> index = null;
                for (CodeSmell smell : list) {
                    key = smell.getSmellName() + "-" + aPackage.getFullQualifiedName();
                    stat = (PreparedStatement) con.prepareStatement(sql);

                    index = smell.getIndex();
                    for (String s : index.keySet()) {
                        stat.setString(1, key);
                        stat.setString(2, String.valueOf(index.get(s)));
                        stat.setString(3, s);

                        stat.executeUpdate();
                    }

                    stat = (PreparedStatement) con.prepareStatement(sql2);

                    stat.setString(1, aPackage.getFullQualifiedName());
                    stat.setString(2, smell.getSmellName());
                    stat.setString(3, smell.getAlgoritmsUsed());
                    stat.setString(4, key);
                    stat.executeUpdate();

                }
            }
            con.commit();
            stat.close();
            SQLiteConnector.releaseConnection(con);

        } catch (
                SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new RepositoryException(ex.getMessage());
            }
            throw new RepositoryException(e.getMessage());
        }


    }

    /**
     * rimuove un package dal db
     *
     * @param aPackage package da rimuovere
     * @throws RepositoryException
     */
    @Override
    public void remove(PackageBean aPackage) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "DELETE FROM PackageBean WHERE fullQualifiedName='" + aPackage.getFullQualifiedName() + "'";

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
     * seleziona una lista di package dal db
     *
     * @param criterion query usata per la selezione
     * @return lista di package selezionati
     * @throws RepositoryException
     */
    @Override
    public List<PackageBean> select(Criterion criterion) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final List<PackageBean> packages = new ArrayList<PackageBean>();
        final SQLiteCriterion sqlCriterion = (SQLiteCriterion) criterion;//oggetto che istanzia la query per la selezione
        try {
            String key = null;
            String sql = sqlCriterion.toSQLquery();
            Statement selection = con.createStatement();
            res = selection.executeQuery(sql);
            PackageBean p = null;
            while (res.next()) {
                p = new PackageBean.Builder(res.getString("fullQualifiedName"), res.getString("textContent"))
                        .setClassList(new ClassListProxy(res.getString("fullQualifiedName")))
                        .build();

                packages.add(p);
            }

            for (PackageBean pack : packages) {
                sql = "SELECT packageBeanFullQualifiedName, algorithmUsed, indice FROM Package_SmellType WHERE packageBeanFullQualifiedName='" + pack.getFullQualifiedName() + "'";
                res = selection.executeQuery(sql);
                while (res.next()) {
                    CodeSmell smell = new PromiscuousPackageCodeSmell(null, res.getString("algorithmUsed"));
                    pack.addSmell(smell);
                }
            }

            for (PackageBean pack : packages) {
                for (CodeSmell smell : pack.getAffectedSmell()) {
                    key = smell.getSmellName() + "-" + pack.getFullQualifiedName();
                    sql = "SELECT indice, name FROM Index_CodeSmell WHERE indexId='" + key + "'";
                    res = selection.executeQuery(sql);
                    while (res.next()) {
                        smell.addIndex(res.getString("name"), Double.parseDouble(res.getString("indice")));
                    }
                }
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

        return packages;
    }

}






