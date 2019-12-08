package it.unisa.casper.storage.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * oggetto che istanzia una lista di metodi
 */
public class MethodList implements MethodBeanList {

    private List<MethodBean> methods; // lista di metodi

    /**
     * costruttore
     */
    public MethodList() {
        methods = new ArrayList<MethodBean>();
    }

    /**
     * setter
     *
     * @param methods lista di metodi da settare
     */
    public void setList(List<MethodBean> methods) {
        this.methods = methods;
    }

    /**
     * getter
     *
     * @return lista di metodi
     */
    public List<MethodBean> getList() {
        return this.methods;
    }

}
