package it.unisa.casper.analysis.code_smell_detection.strategy;

import java.util.HashMap;

/**
 * Interfaccia che dichiara i metodi da implementare negli Strategy
 */
public interface CodeSmellDetectionStrategy<T> {

    /**
     * metodo che analizza un component per verificare se è affetto da smell
     *
     * @param component generico component da dare in input allo strategy per l'analisi
     * @return true se il component è affetto da smell, false altrimenti
     */
    boolean isSmelly(T component);

    /**
     * metodo che ritorna la lista di soglie individuate durante l'analisi
     *
     * @param component generico component da dare in input allo strategy per l'analisi
     * @return List<Double> lista delle soglie individuate durante l'analisi
     */
    HashMap<String, Double> getThresold(T component);
}
