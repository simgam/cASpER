package it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.PackageSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.util.HashMap;

public class StructuralPromiscuousPackageStrategy implements PackageSmellDetectionStrategy {

    public boolean isSmelly(PackageBean pPackage) {

        // to do

        return false;
    }

    public HashMap<String, Double> getThresold(PackageBean pPackage) {
        HashMap<String, Double> list = new HashMap<String, Double>();

        //to do

        return list;
    }
}
