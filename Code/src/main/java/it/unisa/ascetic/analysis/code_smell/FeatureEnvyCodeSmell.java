package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.MethodBean;

/**
 * Classe concreta che istanzia lo smell "Feature Envy"
 */

public class FeatureEnvyCodeSmell extends MethodLevelCodeSmell {

    /**
     * Costruttore
     *
     * @param detectionStrategy Strategy associato al rilevamento di smell che
     *                          riguardano i metodi. Il nome viene istanziato nella classe padre
     */
    public FeatureEnvyCodeSmell(MethodSmellDetectionStrategy detectionStrategy) {
        super(FEATURE_ENVY, detectionStrategy);
    }

    /**
     * Metodo che sancisce la presenza di feature envy in una classe
     *
     * @param aMethod MethodBean da passare allo strategy per effettuare l'analisi
     * @return true se il metodo è affetto da feature envy, false altrimenti
     */
    public boolean affects(MethodBean aMethod) {
        if (detectionStrategy.isSmelly(aMethod)) {
            aMethod.addSmell(this);
            return true;
        }
        return false;
    }

}
