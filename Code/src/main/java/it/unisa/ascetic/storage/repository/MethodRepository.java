package it.unisa.ascetic.storage.repository;

import it.unisa.ascetic.analysis.code_smell.CodeSmell;
import it.unisa.ascetic.analysis.code_smell.FeatureEnvyCodeSmell;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.CodeSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * repository dedicata ai metodi
 */
public class MethodRepository implements MethodBeanRepository {

    private Connection con = null;          // oggetto connessione per stabilire la connessione col db
    private PreparedStatement stat = null;  // variabile per creare statement da eseguire
    private ResultSet res = null;           // resultset per memorizzare risultati della query

    /**
     * aggiunge un metodo al db, riempie anche le tabelle contenenti le variabili d'istanza usate e parametri usati
     *
     * @param aMethod metodo da aggiungere
     * @throws RepositoryException
     */
    @Override
    public void add(MethodBean aMethod) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "INSERT OR REPLACE INTO MethodBean(fullQualifiedName,textContent,return_type,staticMethod,isDefaultConstructor,belongingClass,visibility) VALUES (?,?,?,?,?,?,?)";
        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, aMethod.getFullQualifiedName());
            stat.setString(2, aMethod.getTextContent());
            if (aMethod.getReturnType() != null) {
                stat.setString(3, aMethod.getReturnType().getFullQualifiedName());
            } else {
                stat.setString(3, "");
            }
            ;
            stat.setBoolean(4, aMethod.getStaticMethod());
            stat.setBoolean(5, aMethod.getDefaultCostructor());
            stat.setString(6, aMethod.getBelongingClass().getFullQualifiedName());
            stat.setString(7, aMethod.getVisibility());
            stat.executeUpdate();

            if (aMethod.getInstanceVariableList() != null) {
                List<InstanceVariableBean> list = new ArrayList<InstanceVariableBean>();
                list = aMethod.getInstanceVariableList();
                for (InstanceVariableBean instance : list) {
                    sql = "INSERT OR REPLACE INTO Instance_Variable_Used(methodBeanFullQualifiedName,instanceVariableBeanFullQualifiedName) VALUES (?,?)";
                    stat = (PreparedStatement) con.prepareStatement(sql);
                    stat.setString(1, aMethod.getFullQualifiedName());
                    stat.setString(2, instance.getFullQualifiedName());
                    stat.executeUpdate();
                }
            }

            HashMap<String, ClassBean> hash = aMethod.getParameters();
            if (hash != null) {
                Set<String> set = hash.keySet();
                for (String key : set) {
                    sql = "INSERT OR REPLACE INTO Parameter_Used(methodBeanFullQualifiedName,parameterClassFullQualifiedName,typeParameter,classBeanFullQualifiedName) VALUES (?,?,?,?)";
                    stat = (PreparedStatement) con.prepareStatement(sql);
                    stat.setString(1, aMethod.getFullQualifiedName());
                    stat.setString(2, key);
                    stat.setString(3, hash.get(key).getFullQualifiedName());
                    stat.setString(4, aMethod.getBelongingClass().getFullQualifiedName());
                    stat.executeUpdate();
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
        ;
    }

    /**
     * aggiorna un metodo contenuto nel db con nuovi valori, aggiornando anche le tabelle contenenti le informazioni sui metodi chiamati e sui code smell presenti in esso
     *
     * @param aMethod metodo aggiornato da reinserire
     * @throws RepositoryException
     */
    @Override
    public void update(MethodBean aMethod) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "UPDATE MethodBean SET textContent=?, return_type=?, staticMethod=?, isDefaultConstructor=? ,belongingClass=? ,visibility=?" +
                "WHERE fullQualifiedName=?";
        try {

            stat = (PreparedStatement) con.prepareStatement(sql);
            stat.setString(1, aMethod.getTextContent());
            if (aMethod.getReturnType() != null) {
                stat.setString(2, aMethod.getReturnType().getFullQualifiedName());
            } else {
                stat.setString(2, "");
            }
            ;
            stat.setBoolean(3, aMethod.getStaticMethod());
            stat.setBoolean(4, aMethod.getDefaultCostructor());
            stat.setString(5, aMethod.getBelongingClass().getFullQualifiedName());
            stat.setString(6, aMethod.getVisibility());
            stat.setString(7, aMethod.getFullQualifiedName());
            stat.executeUpdate();

            if (aMethod.getMethodsCalls() != null) {
                List<MethodBean> listCalls = new ArrayList<MethodBean>();
                listCalls = aMethod.getMethodsCalls();
                for (MethodBean method : listCalls) {
                    sql = "INSERT OR REPLACE INTO Methods_Calls(methodCallerFullQualifiedName,methodCalledFullQualifiedName) VALUES (?,?)";
                    stat = (PreparedStatement) con.prepareStatement(sql);
                    stat.setString(1, aMethod.getFullQualifiedName());
                    stat.setString(2, method.getFullQualifiedName());
                    stat.executeUpdate();
                }
            }
            String enviedClass = null;
            if (aMethod.getEnviedClass() != null) {
                enviedClass = aMethod.getEnviedClass().getFullQualifiedName();

                if (aMethod.getAffectedSmell() != null) {
                    List<CodeSmell> listSmell = new ArrayList<CodeSmell>();
                    listSmell = aMethod.getAffectedSmell();
                    for (CodeSmell smell : listSmell) {
                        sql = "INSERT OR REPLACE INTO Metodo_SmellType (methodBeanFullQualifiedName,codeSmellFullQualifiedName,fqn_envied_class) VALUES (?,?,?);";
                        stat = (PreparedStatement) con.prepareStatement(sql);
                        stat.setString(1, aMethod.getFullQualifiedName());
                        stat.setString(2, smell.getSmellName());
                        stat.setString(3, enviedClass);
                        stat.executeUpdate();
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
        ;
    }

    /**
     * rimuove un metodo dal db
     *
     * @param aMethod metodo da rimuovere
     * @throws RepositoryException
     */
    @Override
    public void remove(MethodBean aMethod) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "DELETE FROM MethodBean WHERE fullQualifiedName='" + aMethod.getFullQualifiedName() + "'";
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
        ;
    }

    /**
     * seleziona una lista di metodi dal db
     *
     * @param criterion query utilizzata per la selezione dei metodi
     * @return lista dei metodi selezionati
     * @throws RepositoryException
     */
    @Override
    public List<MethodBean> select(Criterion criterion) throws RepositoryException {
        try {
            con = SQLiteConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final List<MethodBean> methods = new ArrayList<MethodBean>();
        final SQLiteCriterion sqlCriterion = (SQLiteCriterion) criterion; //oggetto che istanzia la query per la selezione

        try {
            String sql = sqlCriterion.toSQLquery();
            Statement selection = con.createStatement();
            res = selection.executeQuery(sql);
            MethodBean m = null;
            HashMap<String, ClassBean> hash = null;
            while (res.next()) {
                hash = new HashMap<String, ClassBean>();
                if (res.getString("return_type").equals("")) {
                                m = new MethodBean.Builder(res.getString("MfullQualifiedName"), res.getString("MtextContent"))
                                        .setReturnType(null)
                                        .setParameters(hash)
                                        .setInstanceVariableList(new UsedInstanceVariableListProxy(res.getString("MfullQualifiedName")))
                                        .setMethodsCalls(new CalledMethodsListProxy(res.getString("MfullQualifiedName")))
                                        .setBelongingClass(new ClassBean.Builder(res.getString("belongingClass"), res.getString("ClassBean.textContent")).build())
                                        .setEnviedClass(null)
                                        .setStaticMethod(res.getBoolean("staticMethod"))
                                        .setDefaultCostructor(res.getBoolean("isDefaultConstructor"))
                                        .setAffectedSmell()
                                        .setVisibility(res.getString("visibility"))
                                        .build();
                } else {
                    m = new MethodBean.Builder(res.getString("MfullQualifiedName"), res.getString("MtextContent"))
                            .setReturnType(new ClassBean.Builder(res.getString("return_type"), "").build())
                            .setParameters(hash)
                            .setInstanceVariableList(new UsedInstanceVariableListProxy(res.getString("MfullQualifiedName")))
                            .setMethodsCalls(new CalledMethodsListProxy(res.getString("MfullQualifiedName")))
                            .setBelongingClass(new ClassBean.Builder(res.getString("belongingClass"), res.getString("CtextContent")).build())
                            .setEnviedClass(null)
                            .setStaticMethod(res.getBoolean("staticMethod"))
                            .setDefaultCostructor(res.getBoolean("isDefaultConstructor"))
                            .setAffectedSmell()
                            .setVisibility(res.getString("visibility"))
                            .build();
                }
                methods.add(m);
            }

            for (MethodBean method : methods) {
                sql = "SELECT parameterClassFullQualifiedName,typeParameter FROM Parameter_Used WHERE methodBeanFullQualifiedName='" + method.getFullQualifiedName() + "' AND classBeanFullQualifiedName='" + method.getBelongingClass().getFullQualifiedName() + "'";
                res = selection.executeQuery(sql);
                while (res.next()) {
                    method.addParameters(res.getString("parameterClassFullQualifiedName"), new ClassBean.Builder(res.getString("typeParameter"), "").build());
                }
                sql = "SELECT fqn_envied_class FROM Metodo_SmellType INNER JOIN CodeSmell ON codeSmellFullQualifiedName=CodeSmell.fullQualifiedName WHERE methodBeanFullQualifiedName='" + method.getFullQualifiedName() + "'";
                res = selection.executeQuery(sql);
                String envied = null;
                while (res.next()) {
                    method.addSmell(new FeatureEnvyCodeSmell(null));
                    if (!res.getString("fqn_envied_class").equals(""))
                        envied = res.getString("fqn_envied_class");
                }
                if (envied != null) method.setEnviedClass(new ClassBean.Builder(envied, "").build());
            }

            for(MethodBean methodBean: methods){
                if(methodBean.getEnviedClass() != null){
                    String enviedClassQuery = "SELECT * FROM ClassBean WHERE fullQualifiedName = '"+methodBean.getEnviedClass().getFullQualifiedName()+"'";
                    res = selection.executeQuery(enviedClassQuery);
                    while (res.next()){
                        methodBean.setEnviedClass(new ClassBean.Builder(res.getString("fullQualifiedName"),res.getString("textContent")).build());
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

        return methods;
    }
}