package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.CodeSmellDetectionStrategy;

/**
 * Classe astratta dell'oggetto CodeSmell, necessario per tenere traccia nel database dei code smell
 * individuati durante l'analisi del codice
 *
 * @param <T> rappresenta un generico detection strategy, specificato caso per caso nelle classi figlie.
 */

public abstract class CodeSmell<T> {
    /**
     * Le stringhe sottostanti dichiarano i nomi (invariabili) degli smell presi in esame
     * da questo plug-in.
     */
    public static final String MISPLACED_CLASS = "Misplaced Class";
    public static final String FEATURE_ENVY = "Feature Envy";
    public static final String BLOB = "Blob";
    public static final String PROMISCUOUS_PACKAGE = "Promiscuous Package";

    protected String smellName;
    protected CodeSmellDetectionStrategy detectionStrategy;
    protected String algoritmsUsed;
    protected double index;

    /**
     * Costruttore
     *
     * @param name              Stringa che rappresenta il nome del code smell istanziato
     * @param detectionStrategy Strategy associato al code smell, da utilizzare per il refactoring
     * @param algoritmsUsed     Stringa che rappresenta il tipo di algoritmo che ha rilevato tale smell
     */
    public CodeSmell(String name, CodeSmellDetectionStrategy detectionStrategy, String algoritmsUsed) {
        this.smellName = name;
        this.detectionStrategy = detectionStrategy;
        this.algoritmsUsed = algoritmsUsed;
    }

    /**
     * Metodo che stabilisce la presenza di uno smell in un component
     *
     * @param component generico componente da analizzare
     * @return true se il component è affetto da smell,false altrimenti
     */
    public abstract boolean affects(T component);

    /**
     * override del toString per consentire il ritorno del nome dello smell
     *
     * @return
     */
    public String toString() {
        return this.smellName;
    }

    /**
     * getter
     *
     * @return stringa corrispondente al nome dello smell
     */
    public String getSmellName() {
        return smellName;
    }

    /**
     * getter
     *
     * @return stringa corrispondente al tipo di algoritmo usato
     */
    public String getAlgoritmsUsed() {
        return algoritmsUsed;
    }

    /**
     * getter
     *
     * @return double corrispondente all'index dell'algoritmo usato
     */
    public double getIndex() {
        return index;
    }

    /**
     * setter
     *
     * @param double corrispondente all'index dell'algoritmo usato
     */
    public void setIndex(double index) {
        this.index = index;
    }

}
