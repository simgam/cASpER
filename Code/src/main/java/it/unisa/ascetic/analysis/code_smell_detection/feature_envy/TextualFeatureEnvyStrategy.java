package it.unisa.ascetic.analysis.code_smell_detection.feature_envy;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.*;

/*

Feature Envy: Si ha quando un metodo è più usato in un'altra classe rispetto a quella in cui si trova.
Quindi questo metodo non è correttamente posizionato ed aumenta l'accoppiamento con classi differenti da quella in cui si trova.
Il refactoring per rimuovere questo smell è il Move Method, che sposta il metodo nella classe adatta.
Un metodo è affetto da questo smell quando è molto più simile ai concetti implementati nella classe esterna rispetto a quella in cui si trova.

 */

public class TextualFeatureEnvyStrategy implements MethodSmellDetectionStrategy {

    private List<PackageBean> systemPackages;

    public TextualFeatureEnvyStrategy(List<PackageBean> systemPackages) {
        this.systemPackages = systemPackages;
    }

    public boolean isSmelly(MethodBean pMethod) {
        SortedMap<ClassBean, Double> similaritiesWithMethod = new TreeMap<ClassBean, Double>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        ArrayList<ClassBean> candidateEnviedClasses = MisplacedComponentsUtilities.getCandidates(pMethod, systemPackages);

        String[] document1 = new String[2];
        document1[0] = "method";
        document1[1] = pMethod.getTextContent();

        for (ClassBean classBean : candidateEnviedClasses) {

            String[] document2 = new String[2];
            document2[0] = "class";
            document2[1] = classBean.getTextContent();

            try {
                similaritiesWithMethod.put(classBean, cosineSimilarity.computeSimilarity(document1, document2));

            } catch (IOException e) {
                return false;
            }
        }

        if (!similaritiesWithMethod.entrySet().iterator().hasNext()) {
            return false;
        }

        Set<ClassBean> list = similaritiesWithMethod.keySet();
        ClassBean firstRankedClass = list.iterator().next();

        for (ClassBean classBean : list) {
            if (classBean.getSimilarity() > firstRankedClass.getSimilarity()) {
                firstRankedClass = classBean;
            }
        }

        if (firstRankedClass.getFullQualifiedName().equals(pMethod.getBelongingClass().getFullQualifiedName())) {
            return false;
        } else {
            pMethod.setEnviedClass(firstRankedClass);
            System.out.println("Found FE:"+pMethod.getFullQualifiedName()+" Envied Class:"+firstRankedClass.getFullQualifiedName());
            return true;
        }
    }

    /*public ClassBean getEnviedClass(MethodBean pMethod) {
        List<ClassBean> similaritiesWithMethod = new ArrayList<ClassBean>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        ClassBean actualClass = null;

        String[] document1 = new String[2];
        document1[0] = "method";
        document1[1] = pMethod.getTextContent();
        try {
            for (PackageBean packageBean : systemPackages) {
                for (ClassBean classBean : packageBean.getClassList()) {
                    if (classBean.getFullQualifiedName().equals(pMethod.getBelongingClass().getFullQualifiedName())) {

                        String[] actualClassDocument = new String[2];
                        actualClassDocument[0] = "class";
                        actualClassDocument[1] = classBean.getTextContent();

                        actualClass = new ClassBean.Builder(classBean.getFullQualifiedName(), classBean.getTextContent())
                                .setInstanceVariables(classBean.instanceVariables)
                                .setMethods(classBean.methods)
                                .setImports(classBean.getImports())
                                .setLOC(classBean.getLOC())
                                .setSuperclass(classBean.getSuperclass())
                                .setBelongingPackage(classBean.getBelongingPackage())
                                .setPathToFile(classBean.getPathToFile())
                                .setSimilarity(cosineSimilarity.computeSimilarity(document1, actualClassDocument))
                                .build();

                        if (actualClass.getSimilarity() >= 0.4)
                            return actualClass;

                    }
                }
            }
        } catch (IOException e) {
        }
        if (actualClass.getSimilarity() < 0.25) {
            ArrayList<ClassBean> candidateEnviedClasses = MisplacedComponentsUtilities.getCandidates(pMethod, systemPackages);
            ClassBean comparableClassBean = null;
            try {
                for (ClassBean classBean : candidateEnviedClasses) {

                    String[] document2 = new String[2];
                    document2[0] = "class";
                    document2[1] = classBean.getTextContent();

                    comparableClassBean = new ClassBean.Builder(classBean.getFullQualifiedName(), classBean.getTextContent())
                            .setInstanceVariables(classBean.instanceVariables)
                            .setMethods(classBean.methods)
                            .setImports(classBean.getImports())
                            .setLOC(classBean.getLOC())
                            .setSuperclass(classBean.getSuperclass())
                            .setBelongingPackage(classBean.getBelongingPackage())
                            .setPathToFile(classBean.getPathToFile())
                            .setSimilarity(cosineSimilarity.computeSimilarity(document1, document2))
                            .build();

                    similaritiesWithMethod.add(comparableClassBean);

                }
            } catch (IOException e) {
            }
            if (similaritiesWithMethod.isEmpty()) {
                return null;
            }

            BeanComparator comparator = new BeanComparator();
            Collections.sort(similaritiesWithMethod, comparator);
            ClassBean firstRankedClass = similaritiesWithMethod.get(similaritiesWithMethod.size() - 1);

            return firstRankedClass;
        }

        return actualClass;
    }*/

}