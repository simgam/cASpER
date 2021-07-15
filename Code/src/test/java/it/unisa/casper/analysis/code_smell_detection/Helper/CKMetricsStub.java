package it.unisa.casper.analysis.code_smell_detection.Helper;

import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.MethodBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.util.Collection;

public class CKMetricsStub {

    public static double computeMediumIntraConnectivity(PackageBean pPackage) {
        return 1.0;
    }

    public static double computeMediumInterConnectivity(PackageBean pPackage, Collection<PackageBean> pPackages) {
        return 0.0;
    }

    public static int getFeatureSum(ClassBean pClass) {
        return 22;
    }

    public static int getLCOM(ClassBean cb) {
        return 26;
    }

    public static int getELOC(ClassBean cb) {
        return 44;
    }

    public static double getNumberOfDependencies(MethodBean pMethod, ClassBean pClass) {
        return 4.0;
    }

    public static double getNumberOfDependencies(ClassBean pClass, PackageBean pPackage) {
        return 2.0;
    }

}
