package it.unisa.ascetic.analysis.code_smell_detection.blob;

import it.unisa.ascetic.analysis.code_smell_detection.BeanDetection;
import it.unisa.ascetic.analysis.code_smell_detection.ComponentMutation;
import it.unisa.ascetic.analysis.code_smell_detection.smellynessMetricProcessing.SmellynessMetric;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;

import java.io.IOException;
import java.util.HashMap;

/*

Blob: Classe caratterizzata da una taglia molto grande, alto numero di attributi e metodi
Il refactoring Extract Class prevede molte operazioni che possono essere applicate per rimuovere questo tipo di smell. 
Esso permette di dividere la classe originale creando nuove classi e ridistribuendo le responsabilità.
Le classi Blob sono caratterizzate da una dispersione semantica dei contenuti.

 */

public class TextualBlobStrategy implements ClassSmellDetectionStrategy {

    private double soglia;

    public TextualBlobStrategy(double soglia) {
        this.soglia = soglia;
    }

    public boolean isSmelly(ClassBean pClass) {
        BeanDetection control = new BeanDetection();
        boolean bean = true;
        for (MethodBean method : pClass.getMethodList()) {
            if (!control.detection(method)) {
                bean = false;
            }
        }

        if (!bean) {
            SmellynessMetric smellyness = new SmellynessMetric();
            ComponentMutation componentMutation = new ComponentMutation();

            String mutatedClass = componentMutation.alterClass(pClass);
            double smellynessIndex = 0;
            try {
                smellynessIndex = smellyness.computeSmellyness(mutatedClass);
                if (smellynessIndex > soglia)
                    return true;

            } catch (IOException e) {
                e.getMessage();
                return false;
            }
        }
        return false;
    }

    public HashMap<String, Double> getThresold(ClassBean pClass) {
        SmellynessMetric smellyness = new SmellynessMetric();
        ComponentMutation componentMutation = new ComponentMutation();
        HashMap<String, Double> list = new HashMap<String, Double>();

        String mutatedClass = componentMutation.alterClass(pClass);

        try {
            list.put("coseno", smellyness.computeSmellyness(mutatedClass));
            return list;

        } catch (IOException e) {
            return list;
        }
    }

}
