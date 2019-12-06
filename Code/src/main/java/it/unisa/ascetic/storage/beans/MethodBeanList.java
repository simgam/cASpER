package it.unisa.ascetic.storage.beans;

import java.util.List;

/**
 * interfaccia che dichiara il metodo da implementare in methodlist e methodlistproxy
 */
public interface MethodBeanList {

    /**
     * getter
     *
     * @return lista di metodi
     */
    List<MethodBean> getList();

}

