package it.unisa.casper.analysis.code_smell_detection.strategy;

import it.unisa.casper.storage.beans.ClassBean;

import java.util.HashMap;

/**
 * Interfaccia che dichiara i metodi da implementare negli Strategy addetti al rilevamento
 * di smell nelle classi
 */
public interface ClassSmellDetectionStrategy extends CodeSmellDetectionStrategy<ClassBean> {

    /**
     * Metodo che analizza una classe per verificare se è affetta da smell
     *
     * @param aClass Classe da passare allo strategy per effettuare l'analisi
     * @return true se la classe è affetta da smell, false altrimenti
     */
    boolean isSmelly(ClassBean aClass);

    /**
     * metodo che ritorna la lista di soglie individuate durante l'analisi
     *
     * @param aClass Classe da dare in input allo strategy per l'analisi
     * @return List<Double> lista delle soglie individuate durante l'analisi
     */
    HashMap<String, Double> getThresold(ClassBean aClass);
}
