package it.unisa.ascetic.analysis.code_smell_detection.strategy;

import it.unisa.ascetic.storage.beans.MethodBean;

/**
 * Interfaccia che dichiara i metodi da implementare negli Strategy addetti al rilevamento
 * di smell nei metodi
 */
public interface MethodSmellDetectionStrategy extends CodeSmellDetectionStrategy<MethodBean> {
    /**
     * metodo che analizza un metodo per verificare la presenza di uno smell
     *
     * @param aMethod metodo da passare allo strategy per l'analisi
     * @return true se il metodo è affetto da smell,false altrimenti
     */
    public boolean isSmelly(MethodBean aMethod);

}
