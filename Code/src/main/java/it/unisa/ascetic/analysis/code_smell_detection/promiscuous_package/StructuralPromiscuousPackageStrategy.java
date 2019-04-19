package it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.PackageSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.PackageBean;

public class StructuralPromiscuousPackageStrategy implements PackageSmellDetectionStrategy {

    public boolean isSmelly(PackageBean pPackage) {
        return false;
    }
}
