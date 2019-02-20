package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.MethodBean;

/**
 * Classe astratta figlia di CodeSmell, estendendo questa classe verranno istanziati tutti
 * gli oggetti CodeSmell che riguardano l'ambito dei metodi
 */
public abstract class MethodLevelCodeSmell extends CodeSmell<MethodBean> {

    /**
     * Costruttore
     *
     * @param name              rappresenta nome dello smell
     * @param detectionStrategy Strategy associato ai code smell che riguardano le classi
     */
    public MethodLevelCodeSmell(String name, MethodSmellDetectionStrategy detectionStrategy) {
        super(name, detectionStrategy);
    }

    /**
     * Metodo che stabilisce la presenza di uno smell in un metodo
     *
     * @param aMethod MethodBean da passare allo strategy per effettuare l'analisi
     * @return true se il metodo è affetto da smell, false altrimenti
     */
    public abstract boolean affects(MethodBean aMethod);

}
