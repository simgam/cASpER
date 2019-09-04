package it.unisa.ascetic.analysis.code_smell_detection.misplaced_class;

import it.unisa.ascetic.analysis.code_smell_detection.comparator.BeanComparator;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class StructuralMisplacedClassStrategy implements ClassSmellDetectionStrategy {

    private List<PackageBean> systemPackages;
    private int soglia;

    public StructuralMisplacedClassStrategy(List<PackageBean> systemPackages, int soglia) {
        this.systemPackages = systemPackages;
        this.soglia = soglia;
    }

    public boolean isSmelly(ClassBean pClass) {
        ArrayList<PackageBean> packages = new ArrayList<PackageBean>();
        PackageBean actualPackage = null;

        for (PackageBean packageBean : systemPackages) {
            if (packageBean.getFullQualifiedName().equals(pClass.getBelongingPackage().getFullQualifiedName())) {
                actualPackage = packageBean;
            } else {
                packages.add(packageBean);
            }
        }

        double numberOfDependenciesWithActualPackage = CKMetrics.getNumberOfDependencies(pClass, actualPackage);

        ArrayList<PackageBean> dependenciesWithClass = new ArrayList<PackageBean>();

        PackageBean comparablePackageBean = null;
        double numberOfDependenciesWithCandidateEnviedPackage = 0.0;

        for (PackageBean packageBean : packages) {

            numberOfDependenciesWithCandidateEnviedPackage = CKMetrics.getNumberOfDependencies(pClass, packageBean);

            comparablePackageBean = new PackageBean.Builder(packageBean.getFullQualifiedName(), packageBean.getTextContent())
                    .setClassList(packageBean.classes)
                    .setSimilarity(numberOfDependenciesWithCandidateEnviedPackage)
                    .build();

            dependenciesWithClass.add(comparablePackageBean);

        }

        BeanComparator comparator = new BeanComparator();
        Collections.sort(dependenciesWithClass, comparator);

        PackageBean firstRankedPackage = dependenciesWithClass.get(dependenciesWithClass.size() - 1);
        pClass.setSimilarity(numberOfDependenciesWithActualPackage);
        if (numberOfDependenciesWithActualPackage <= firstRankedPackage.getSimilarity() && firstRankedPackage.getSimilarity() != 0 && firstRankedPackage.getSimilarity() >= soglia) {
            pClass.setEnviedPackage(firstRankedPackage);
            return true;
        } else {
            return false;
        }
    }

    public HashMap<String, Double> getThresold(ClassBean pClass) {
        HashMap<String, Double> list = new HashMap<String, Double>();
        list.put("dipendenza", CKMetrics.getNumberOfDependencies(pClass, pClass.getEnviedPackage()));

        return list;
    }
}