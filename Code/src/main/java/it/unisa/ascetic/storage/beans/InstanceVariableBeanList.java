package it.unisa.ascetic.storage.beans;

import java.util.List;

/**
 * interfaccia che dichiara il metodo da implementare in instancevariablelist e instancevariableproxy
 */
public interface InstanceVariableBeanList {
    /**
     * getter
     *
     * @return lista di variabili di istanza
     */
    public List<InstanceVariableBean> getList();

}
