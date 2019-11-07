package it.unisa.ascetic.analysis.code_smell_detection;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MisplacedComponentsUtilities {

    public static ArrayList<ClassBean> getCandidates(MethodBean pMethod, List<PackageBean> pSystemPackages, double soglia) {
        ArrayList<ClassBean> candidates = new ArrayList<ClassBean>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        String[] document1 = new String[2];
        document1[0] = "method";
        document1[1] = pMethod.getTextContent();

        for (PackageBean packageBean : pSystemPackages) {
            for (ClassBean classBean : packageBean.getClassList()) {

                String[] document2 = new String[2];
                document2[0] = "class";
                document2[1] = classBean.getTextContent();

                try {
                    double cosine = cosineSimilarity.computeSimilarity(document1, document2);
                    classBean.setSimilarity(cosine);
                    if (cosine > soglia) {
                        candidates.add(classBean);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return candidates;
    }

}