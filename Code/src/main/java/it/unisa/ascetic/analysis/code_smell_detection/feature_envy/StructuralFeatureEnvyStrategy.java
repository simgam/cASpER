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
