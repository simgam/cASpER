package it.unisa.ascetic.storage.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * oggetto che istanzia una lista di variabili di istanza
 */
public class InstanceVariableList implements InstanceVariableBeanList {

    private List<InstanceVariableBean> instances; //lista effettiva delle variabili

    /**
     * costruttore
     */
    public InstanceVariableList() {
        instances = new ArrayList<InstanceVariableBean>();
    }

    /**
     * setter
     *
     * @param instances lista di variabili da settare
     */
    public void setList(List<InstanceVariableBean> instances) {
        this.instances = instances;
    }

    /**
     * getter
     *
     * @return lista di variabili di istanza
     */
    public List<InstanceVariableBean> getList() {
        return this.instances;
    }

}
