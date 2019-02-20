package it.unisa.ascetic.analysis.code_smell_detection;

import it.unisa.ascetic.storage.beans.ClassBean;

/**
 * Interfaccia che dichiara i metodi da implementare negli Strategy addetti al rilevamento
 * di smell nelle classi
 */
public interface ClassSmellDetectionStrategy extends CodeSmellDetectionStrategy<ClassBean>{
    /**
     * Metodo che analizza una classe per verificare se è affetta da smell
     * @param aClass Classe da passare allo strategy per effettuare l'analisi
     * @return true se la classe è affetta da smell, false altrimenti
     */
    public boolean isSmelly(ClassBean aClass);

}
