package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.storage.repository.ClassRepository;
import it.unisa.ascetic.storage.repository.RepositoryException;
import it.unisa.ascetic.storage.repository.SQLSelectionClass;

import java.util.ArrayList;
import java.util.List;

/**
 * proxy addetto al lazy loading per le classi
 */
public class ClassListProxy implements ClassBeanList {

    private List<ClassBean> lista;//lista di classi
    private String packageFullQualifiedName;

    public ClassListProxy(String packFullQualified) {
        this.packageFullQualifiedName = packFullQualified;
    }

    /**
     * getter
     *
     * @return ritorna una lista di classi effettuando una query mediante repository
     */
    public List<ClassBean> getList() {
        if (lista == null || lista.isEmpty()) {
            ClassRepository repo = new ClassRepository();
            lista = new ArrayList<ClassBean>();
            try {
                lista = repo.select(new SQLSelectionClass(packageFullQualifiedName));
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

}
