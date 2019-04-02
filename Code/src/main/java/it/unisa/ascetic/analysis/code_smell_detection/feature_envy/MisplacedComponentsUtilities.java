package it.unisa.ascetic.analysis.code_smell_detection.feature_envy;


import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.*;


public class MisplacedComponentsUtilities {

    public static ArrayList<PackageBean> getCandidates(ClassBean pClass, List<PackageBean> pSystemPackages) {
        ArrayList<PackageBean> candidates = new ArrayList<PackageBean>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        String[] document1 = new String[2];
        document1[0] = "class";
        document1[1] = pClass.getTextContent();

        for (PackageBean packageBean : pSystemPackages) {

            String[] document2 = new String[2];
            document2[0] = "package";
            document2[1] = packageBean.getTextContent();

            try {
                pClass.setSimilarity(cosineSimilarity.computeSimilarity(document1, document2));
                if (pClass.getSimilarity() > 0.5) {
                    candidates.add(packageBean);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return candidates;
    }

    public static ArrayList<ClassBean> getCandidates(MethodBean pMethod, List<PackageBean> pSystemPackages) {
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
                    if (cosine > 0.5) {
                        candidates.add(classBean);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return candidates;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Double) val);
                    break;
                }

            }

        }
        return sortedMap;
    }

}