package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.storage.repository.MethodRepository;
import it.unisa.ascetic.storage.repository.RepositoryException;
import it.unisa.ascetic.storage.repository.SQLSelectionMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * proxy addetto al lazy loading dei metodi
 */
public class MethodListProxy implements MethodBeanList {

    private String belongingClass;
    private List<MethodBean> lista;//lista di metodi

    public MethodListProxy(String aClass) {
        belongingClass = aClass;
    }

    /**
     * getter
     *
     * @return lista di metodi ottenuti mediante una query effettuata con la repository
     */
    public List<MethodBean> getList() {
        if (lista == null || lista.isEmpty()) {
            lista = new ArrayList<MethodBean>();
            MethodRepository repo = new MethodRepository();
            try {
                lista = repo.select(new SQLSelectionMethod(belongingClass));

            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

}
