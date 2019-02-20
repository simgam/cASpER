package it.unisa.ascetic.storage.beans;

import it.unisa.ascetic.storage.repository.InstanceVariableRepository;
import it.unisa.ascetic.storage.repository.RepositoryException;
import it.unisa.ascetic.storage.repository.SQLUsedInstanceSelection;

import java.util.ArrayList;
import java.util.List;

public class UsedInstanceVariableListProxy implements InstanceVariableBeanList {

    private List<InstanceVariableBean> lista;
    private final String usingMethod;

    /**
     * costruttore
     *
     * @param methodCaller full qualified name del metodo che utilizza la lista di
     *                     variabili di istanza prese in esame
     */
    public UsedInstanceVariableListProxy(String methodCaller) {
        usingMethod = methodCaller;
    }

    /**
     * seleziona una lista di variabili di istanza ni base al criterio
     *
     * @return lista di variabili di istanza utilizzate dal metodo avente
     * come full qualified name il parametr "usingMethod"
     */
    @Override
    public List<InstanceVariableBean> getList() {
        if (lista == null || lista.isEmpty()) {
            InstanceVariableRepository repo = new InstanceVariableRepository();
            lista = new ArrayList<InstanceVariableBean>();
            try {
                lista = repo.select(new SQLUsedInstanceSelection(usingMethod));
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}
