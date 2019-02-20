package it.unisa.ascetic.storage.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * oggetto che istanzia una lista di classi
 */
public class ClassList implements ClassBeanList {

    private List<ClassBean> classes; // lista nella quale inserire le classi

    /**
     * costruttore
     */
    public ClassList() {
        this.classes = new ArrayList<ClassBean>();
    }

    /**
     * setter
     *
     * @param classes lista da settare nella lista
     */
    public void setList(List<ClassBean> classes) {
        this.classes = classes;
    }

    /**
     * getter
     *
     * @return lista di classi
     */
    public List<ClassBean> getList() {
        return classes;
    }

}
