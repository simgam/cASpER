package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.storage.repository.MethodRepository;
import it.unisa.ascetic.storage.repository.RepositoryException;
import it.unisa.ascetic.storage.repository.SQLCalledMethodsSelection;

import java.util.ArrayList;
import java.util.List;

public class CalledMethodsListProxy implements MethodBeanList {

    private final String methodCaller;
    private List<MethodBean> lista;

    /**
     * costruttore
     *
     * @param callerMethod full qualified name del metodo che chiama altri metodi
     *                     del quale vogliamo tenere traccia
     */
    public CalledMethodsListProxy(String callerMethod) {
        methodCaller = callerMethod;
    }

    /**
     * seleziona in base al criterio una lista di metodi chiamati
     *
     * @return ritorna la lista di metodi chiamati dal metodo avente
     * come full qualified name il parametro "methodCaller"
     */
    @Override
    public List<MethodBean> getList() {
        if (lista == null || lista.isEmpty()) {
            MethodRepository repo = new MethodRepository();
            lista = new ArrayList<MethodBean>();
            try {
                lista = repo.select(new SQLCalledMethodsSelection(methodCaller));

            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}
