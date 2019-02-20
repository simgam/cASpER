package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;

/**
 * Classe concreta che istanzia lo smell "Blob"
 */
public class BlobCodeSmell extends ClassLevelCodeSmell {

    /**
     * Costruttore
     *
     * @param detectionStrategy Strategy associato al rilevamento di smell che
     *                          riguardano le classi. Il nome viene istanziato nella classe padre
     */
    public BlobCodeSmell(ClassSmellDetectionStrategy detectionStrategy) {
        super(BLOB, detectionStrategy);
    }

    /**
     * Metodo che sancisce la presenza di blob in una classe
     *
     * @param aClass ClassBean da passare allo Strategy per effettuare l'analisi
     * @return true se la classe è affetta da blob, false altrimenti
     */
    public boolean affects(ClassBean aClass) {
        if (detectionStrategy.isSmelly(aClass)) {
            aClass.addSmell(this);
            return true;
        }
        return false;
    }
}
