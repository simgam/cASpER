package it.unisa.casper.analysis.code_smell_detection.misplaced_class;

import it.unisa.casper.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.casper.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        double cosine;
        SortedMap<Double, PackageBean> similaritiesWithClass = new TreeMap<Double, PackageBean>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        Double maxCosine, belongingCosine;

        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = pClass.getTextContent();
        String[] document2 = new String[2];
        document2[0] = "package";
        document2[1] = pClass.getBelongingPackage().getTextContent();

        try {
            belongingCosine = cosineSimilarity.computeSimilarity(document1, document2);

            for (PackageBean packageBean : systemPackages) {
                document2 = new String[2];
                document2[0] = "package";
                document2[1] = packageBean.getTextContent();
                cosine = cosineSimilarity.computeSimilarity(document1, document2);

                similaritiesWithClass.put(cosine, packageBean);
            }
        } catch (IOException e) {
            return false;
        }

        if (!similaritiesWithClass.entrySet().iterator().hasNext()) {
            return false;
        }

        maxCosine = similaritiesWithClass.lastKey();
        PackageBean firstRankedPackage = similaritiesWithClass.get(maxCosine);

        for (PackageBean p : similaritiesWithClass.values()) {
            p.setSimilarity(0);
        }

        if (!firstRankedPackage.getFullQualifiedName().equals(pClass.getBelongingPackage().getFullQualifiedName()) && (maxCosine - belongingCosine > soglia)) {
            pClass.setEnviedPackage(similaritiesWithClass.get(maxCosine));
            return true;
        }
        System.out.println();
        return false;
    }

    public HashMap<String, Double> getThresold(ClassBean pClass) {
        HashMap<String, Double> list = new HashMap<String, Double>();

        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = pClass.getTextContent();

        String[] document2 = new String[2];
        document2[0] = "package";
        document2[1] = pClass.getEnviedPackage().getTextContent();

        String[] document3 = new String[2];
        document3[0] = "class";
        document3[1] = pClass.getBelongingPackage().getTextContent();

        try {
            CosineSimilarity cosineSimilarity = new CosineSimilarity();
            list.put("coseno", cosineSimilarity.computeSimilarity(document1, document2) - cosineSimilarity.computeSimilarity(document1, document3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}