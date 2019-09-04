package it.unisa.ascetic.analysis.code_smell_detection.feature_envy;

import it.unisa.ascetic.analysis.code_smell_detection.comparator.BeanComparator;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class StructuralFeatureEnvyStrategy implements MethodSmellDetectionStrategy {

    private List<PackageBean> systemPackages;
    private int soglia;

    public StructuralFeatureEnvyStrategy(List<PackageBean> systemPackages, int soglia) {
        this.systemPackages = systemPackages;
        this.soglia = soglia;
    }

    public boolean isSmelly(MethodBean pMethod) {

        if (!isGetter(pMethod) && !isSetter(pMethod) && !(pMethod.getFullQualifiedName().toLowerCase()).contains("main") && !isConstructor(pMethod)) {

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
            double numberOfDependenciesWithCandidateEnviedClass = 0.0;

            ClassBean comparableClassBean = null;
            for (ClassBean classBean : classes) {

                numberOfDependenciesWithCandidateEnviedClass = CKMetrics.getNumberOfDependencies(pMethod, classBean);

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

            if (numberOfDependenciesWithActualClass <= firstRankedClass.getSimilarity() && firstRankedClass.getSimilarity() != 0 && firstRankedClass.getSimilarity() >= soglia) {
                pMethod.setEnviedClass(firstRankedClass);
                return true;
            }
        }
        return false;
    }

    private boolean isGetter(MethodBean pMethodBean) {
        if ((pMethodBean.getFullQualifiedName().toLowerCase()).contains("get") && pMethodBean.getParameters().isEmpty() &&
                !((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private boolean isSetter(MethodBean pMethodBean) {
        if ((pMethodBean.getFullQualifiedName().toLowerCase()).contains("set") && pMethodBean.getParameters().size() == 1 &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private boolean isConstructor(MethodBean pMethodBean) {
        String[] fullClass = ((ClassBean) pMethodBean.getBelongingClass()).getFullQualifiedName().split(Pattern.quote("."));
        String[] fullMethod = pMethodBean.getFullQualifiedName().split(Pattern.quote("."));

        if (fullMethod[fullMethod.length - 1].equalsIgnoreCase(fullClass[fullClass.length - 1]) &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    public HashMap<String, Double> getThresold(MethodBean pMethod) {
        HashMap<String, Double> list = new HashMap<String, Double>();
        list.put("dipendenza", CKMetrics.getNumberOfDependencies(pMethod, pMethod.getEnviedClass()));

        return list;
    }
}
