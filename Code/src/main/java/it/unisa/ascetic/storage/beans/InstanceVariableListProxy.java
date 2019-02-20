package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.storage.repository.InstanceVariableRepository;
import it.unisa.ascetic.storage.repository.RepositoryException;
import it.unisa.ascetic.storage.repository.SQLSelectionInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * proxy addetto al lazy loading delle variabili di istanza
 */
public class InstanceVariableListProxy implements InstanceVariableBeanList {

    private List<InstanceVariableBean> lista;// lista di variabili di istanza
    private final String belongingClass;

    public InstanceVariableListProxy(String aClass) {
        this.belongingClass = aClass;
    }

    /**
     * getter
     *
     * @return lista di variabili di istanza ottenuta eseguendo una query tramite repository
     */
    public List<InstanceVariableBean> getList() {
        if (lista == null || lista.isEmpty()) {
            InstanceVariableRepository repo = new InstanceVariableRepository();
            lista = new ArrayList<InstanceVariableBean>();
            try {
                lista = repo.select(new SQLSelectionInstance(belongingClass));
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

}
