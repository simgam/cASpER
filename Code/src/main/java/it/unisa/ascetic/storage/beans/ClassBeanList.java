package it.unisa.ascetic.storage.beans;

import java.util.List;

/**
 * interfaccia che dichiara il metodo da implementare in classlist e classlistproxy
 */
public interface ClassBeanList {
    /**
     * getter
     *
     * @return una lista di classi
     */
    List<ClassBean> getList();

}
