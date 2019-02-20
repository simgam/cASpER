package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;

/**
 * Classe astratta figlia di CodeSmell, estendendo questa classe verranno istanziati tutti
 * gli oggetti CodeSmell che riguardano l'ambito delle classi
 */
public abstract class ClassLevelCodeSmell extends CodeSmell<ClassBean> {

    /**
     * Costruttore
     *
     * @param name              rappresenta nome dello mell
     * @param detectionStrategy Strategy associato ai code smell che interessano le classi
     */
    public ClassLevelCodeSmell(String name, ClassSmellDetectionStrategy detectionStrategy) {
        super(name, detectionStrategy);
    }

    /**
     * Metodo che stabilisce la presenza di un code smell in una classe
     *
     * @param aClass ClassBean da passare allo Strategy per effettuare l'analisi
     * @return true se la classe è affetta da smell, false altrimenti
     */
    public abstract boolean affects(ClassBean aClass);

}
