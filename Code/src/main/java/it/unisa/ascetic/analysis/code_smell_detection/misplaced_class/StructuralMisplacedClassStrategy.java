package it.unisa.ascetic.analysis.code_smell_detection.misplaced_class;

import it.unisa.ascetic.analysis.code_smell_detection.comparator.BeanComparator;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StructuralMisplacedClassStrategy implements ClassSmellDetectionStrategy {

    private List<PackageBean> systemPackages;

    public StructuralMisplacedClassStrategy(List<PackageBean> systemPackages) {
        this.systemPackages = systemPackages;
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
        double numberOfDependenciesWithCandidateEnviedPackage;

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

        if (numberOfDependenciesWithActualPackage <= firstRankedPackage.getSimilarity()) {
            pClass.setEnviedPackage(firstRankedPackage);
            return true;
        } else {
            return false;
        }
    }

}