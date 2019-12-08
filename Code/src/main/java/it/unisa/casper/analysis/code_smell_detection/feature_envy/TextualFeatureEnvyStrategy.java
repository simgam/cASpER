package it.unisa.casper.analysis.code_smell_detection.feature_envy;

import it.unisa.casper.analysis.code_smell_detection.BeanDetection;
import it.unisa.casper.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.casper.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.MethodBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/*

Feature Envy: Si ha quando un metodo è più usato in un'altra classe rispetto a quella in cui si trova.
Quindi questo metodo non è correttamente posizionato ed aumenta l'accoppiamento con classi differenti da quella in cui si trova.
Il refactoring per rimuovere questo smell è il Move Method, che sposta il metodo nella classe adatta.
Un metodo è affetto da questo smell quando è molto più simile ai concetti implementati nella classe esterna rispetto a quella in cui si trova.

 */

public class TextualFeatureEnvyStrategy implements MethodSmellDetectionStrategy {

    private List<PackageBean> systemPackages;
    private double soglia;

    public TextualFeatureEnvyStrategy(List<PackageBean> systemPackages, double soglia) {
        this.systemPackages = systemPackages;
        this.soglia = soglia;
    }

    public boolean isSmelly(MethodBean pMethod) {
        double cosine;

        if (!BeanDetection.detection(pMethod)) {

            SortedMap<Double, ClassBean> similaritiesWithMethod = new TreeMap<Double, ClassBean>();
            CosineSimilarity cosineSimilarity = new CosineSimilarity();
            Double maxCosine, belongingCosine;
            String[] document1 = new String[2];
            document1[0] = "method";
            document1[1] = pMethod.getTextContent();

            String[] document2 = new String[2];
            document2[0] = "class";
            document2[1] = pMethod.getBelongingClass().getTextContent();

            try {
                belongingCosine = cosineSimilarity.computeSimilarity(document1, document2);

                for (PackageBean packageBean : systemPackages) {
                    for (ClassBean classBean : packageBean.getClassList()) {
                        document2 = new String[2];
                        document2[0] = "class";
                        document2[1] = classBean.getTextContent();
                        cosine = cosineSimilarity.computeSimilarity(document1, document2);

                        similaritiesWithMethod.put(cosine, classBean);

                    }
                }
            } catch (IOException e) {
                e.getMessage();
                return false;
            }

            if (!similaritiesWithMethod.entrySet().iterator().hasNext()) {
                return false;
            }

            maxCosine = similaritiesWithMethod.lastKey();
            ClassBean firstRankedClass = similaritiesWithMethod.get(maxCosine);

            if (!firstRankedClass.getFullQualifiedName().equals(pMethod.getBelongingClass().getFullQualifiedName()) && (maxCosine - belongingCosine > soglia)) {
                pMethod.setEnviedClass(firstRankedClass);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public HashMap<String, Double> getThresold(MethodBean pMethod) {
        HashMap<String, Double> list = new HashMap<String, Double>();

        String[] document1 = new String[2];
        document1[0] = "method";
        document1[1] = pMethod.getTextContent();

        String[] document2 = new String[2];
        document2[0] = "class";
        document2[1] = pMethod.getEnviedClass().getTextContent();

        String[] document3 = new String[2];
        document3[0] = "class";
        document3[1] = pMethod.getBelongingClass().getTextContent();
        try {
            CosineSimilarity cosineSimilarity = new CosineSimilarity();
            list.put("coseno", cosineSimilarity.computeSimilarity(document1, document2) - cosineSimilarity.computeSimilarity(document1, document3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}