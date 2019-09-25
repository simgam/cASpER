package it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.PackageSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.util.HashMap;
import java.util.List;

public class StructuralPromiscuousPackageStrategy implements PackageSmellDetectionStrategy {

    private static List<PackageBean> projectPackages;
    private static double MIntraC;
    private static double MInterC;

    public StructuralPromiscuousPackageStrategy(List<PackageBean> projectPackages, double MIntraC, double MInterC) {
        this.projectPackages = projectPackages;
        this.MIntraC = MIntraC;
        this.MInterC = MInterC;
    }

    public boolean isSmelly(PackageBean pPackage) {

        if ((CKMetrics.computeMediumInterConnectivity(pPackage, projectPackages) > MInterC) || (CKMetrics.computeMediumIntraConnectivity(pPackage) > MIntraC)) {
            return true;
        }
        return false;
    }

    public HashMap<String, Double> getThresold(PackageBean pPackage) {
        HashMap<String, Double> list = new HashMap<String, Double>();

        list.put("MInterC", CKMetrics.computeMediumInterConnectivity(pPackage, projectPackages));
        list.put("MIntraC", CKMetrics.computeMediumIntraConnectivity(pPackage));
        return list;
    }
}
