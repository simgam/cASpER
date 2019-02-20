package it.unisa.ascetic.analysis.code_smell_detection.feature_envy;

import it.unisa.ascetic.analysis.code_smell_detection.comparator.BeanComparator;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StructuralFeatureEnvyStrategy implements MethodSmellDetectionStrategy {

    private List<PackageBean> systemPackages;

    public StructuralFeatureEnvyStrategy(List<PackageBean> systemPackages) {
        this.systemPackages = systemPackages;
    }

    /*public boolean isSmelly(MethodBean pMethod) {
        SortedMap<ClassBean, Double> similaritiesWithMethod = new TreeMap<ClassBean, Double>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        ArrayList<ClassBean> candidateEnviedClasses = MisplacedComponentsUtilities.getCandidates(pMethod, systemPackages);

        for (ClassBean classBean : candidateEnviedClasses) {
            Logger.getGlobal().info(classBean.getFullQualifiedName());
        }

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

        Map.Entry<ClassBean, Double> firstRankedClass = similaritiesWithMethod.entrySet().iterator().next();
        Logger.getGlobal().info("First ranked class: " + firstRankedClass);

        if (!firstRankedClass.getKey().getFullQualifiedName().equals(pMethod.getBelongingClass().getFullQualifiedName())) {
            return false;
        }

    }*/

    public boolean isSmelly(MethodBean pMethod) {
        ArrayList<ClassBean> classes = new ArrayList<ClassBean>();
        ClassBean actualClass = null;

        if (pMethod.getBelongingClass() != null) {
            for (PackageBean packageBean : systemPackages) {
                for (ClassBean classBean : packageBean.getClassList()) {

                    if (classBean.getFullQualifiedName().equals(pMethod.getBelongingClass().getFullQualifiedName())) {
                        actualClass = classBean;
                    } else {
                        classes.add(classBean);
                    }
                }
            }
        }

        double numberOfDependenciesWithActualClass = CKMetrics.getNumberOfDependencies(pMethod, actualClass);

        ArrayList<ClassBean> dependenciesWithMethod = new ArrayList<ClassBean>();

        ClassBean comparableClassBean = null;
        for (ClassBean classBean : classes) {

            double numberOfDependenciesWithCandidateEnviedClass = CKMetrics.getNumberOfDependencies(pMethod, classBean);

            comparableClassBean = new ClassBean.Builder(classBean.getFullQualifiedName(), classBean.getTextContent())
                    .setInstanceVariables(classBean.instanceVariables)
                    .setMethods(classBean.methods)
                    .setImports(classBean.getImports())
                    .setLOC(classBean.getLOC())
                    .setSuperclass(classBean.getSuperclass())
                    .setBelongingPackage(classBean.getBelongingPackage())
                    .setPathToFile(classBean.getPathToFile())
                    .setSimilarity(numberOfDependenciesWithCandidateEnviedClass)
                    .build();

            dependenciesWithMethod.add(comparableClassBean);

        }

        BeanComparator comparator = new BeanComparator();
        Collections.sort(dependenciesWithMethod, comparator);

        ClassBean firstRankedClass = dependenciesWithMethod.get(dependenciesWithMethod.size() - 1);

        if (numberOfDependenciesWithActualClass <= firstRankedClass.getSimilarity()) {
            pMethod.setEnviedClass(firstRankedClass);
            return true;
        } else {
            return false;
        }
    }

}
