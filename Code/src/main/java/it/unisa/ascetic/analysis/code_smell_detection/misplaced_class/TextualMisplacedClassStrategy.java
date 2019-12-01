package it.unisa.ascetic.analysis.code_smell_detection.misplaced_class;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/*

Misplaced Class: quando una classe si trova in un pacchetto con altre classi non collegate ad essa.
Il refactoring da applicare è il Move Class capace di spostare la classe in un pacchetto più adatto.

 */

public class TextualMisplacedClassStrategy implements ClassSmellDetectionStrategy {

    private List<PackageBean> systemPackages;
    private double soglia;

    public TextualMisplacedClassStrategy(List<PackageBean> systemPackages, double soglia) {
        this.systemPackages = systemPackages;
        this.soglia = soglia;
    }

    public boolean isSmelly(ClassBean pClass) {
        SortedMap<PackageBean, Double> similaritiesWithClass = new TreeMap<PackageBean, Double>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = pClass.getTextContent();
        try {
            for (PackageBean packageBean : systemPackages) {

                String[] document2 = new String[2];
                document2[0] = "package";
                document2[1] = packageBean.getTextContent();

                similaritiesWithClass.put(packageBean, cosineSimilarity.computeSimilarity(document1, document2));
            }
        } catch (IOException e) {
            return false;
        }
        Entry<PackageBean, Double> firstRankedPackage = similaritiesWithClass.entrySet().iterator().next();
        if (firstRankedPackage.getKey().getFullQualifiedName().equals(pClass.getBelongingPackage().getFullQualifiedName()) || (firstRankedPackage.getValue() <= soglia)) {
            return false;
        }
        pClass.setEnviedPackage(firstRankedPackage.getKey());
        pClass.setSimilarity(firstRankedPackage.getValue());
        return true;
    }

    public HashMap<String, Double> getThresold(ClassBean pClass) {
        HashMap<String, Double> list = new HashMap<String, Double>();

        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = pClass.getTextContent();

        String[] document2 = new String[2];
        document2[0] = "package";
        document2[1] = pClass.getEnviedPackage().getTextContent();
        try {
            CosineSimilarity cosineSimilarity = new CosineSimilarity();
            list.put("coseno", cosineSimilarity.computeSimilarity(document1, document2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}