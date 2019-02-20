package it.unisa.ascetic.analysis.code_smell_detection.blob;

import it.unisa.ascetic.analysis.code_smell_detection.ComponentMutation;
import it.unisa.ascetic.analysis.code_smell_detection.smellynessMetricProcessing.SmellynessMetric;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.io.IOException;
import java.util.List;

/*

Blob: Classe caratterizzata da una taglia molto grande, alto numero di attributi e metodi
Il refactoring Extract Class prevede molte operazioni che possono essere applicate per rimuovere questo tipo di smell. 
Esso permette di dividere la classe originale creando nuove classi e ridistribuendo le responsabilità.
Le classi Blob sono caratterizzate da una dispersione semantica dei contenuti.

 */

public class TextualBlobStrategy implements ClassSmellDetectionStrategy {

    public boolean isSmelly(ClassBean pClass) {
        SmellynessMetric smellyness = new SmellynessMetric();
        ComponentMutation componentMutation = new ComponentMutation();

        String mutatedClass = componentMutation.alterClass(pClass);
        double smellynessIndex =0;
        try {
            smellynessIndex=smellyness.computeSmellyness(mutatedClass);
            System.out.println("DBG->"+pClass.getFullQualifiedName()+"smelliness index = "+smellynessIndex);
            if (smellynessIndex > 0.55)
                return true;

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    public double getBlobProbability(ClassBean pClass) {
        SmellynessMetric smellyness = new SmellynessMetric();
        ComponentMutation componentMutation = new ComponentMutation();

        String mutatedClass = componentMutation.alterClass(pClass);

        try {
            double similarity = smellyness.computeSmellyness(mutatedClass);

            if (CKMetrics.getLOC(pClass) < 500) {
                similarity -= ((similarity * 85) / 100);
            }

            return similarity;

        } catch (IOException e) {
            return 0.0;
        }
    }

}
