package it.unisa.ascetic.analysis.code_smell_detection.blob;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

public class StructuralBlobStrategy implements ClassSmellDetectionStrategy {

    public boolean isSmelly(ClassBean pClass) {
        if (!isBean(pClass)) {

            if (isControllerClass(pClass) || isLargeClassLowCohesion(pClass)) return true;

        }
        return false;
    }

    private static boolean isLargeClassLowCohesion(ClassBean pClass) {
        int featureSum = CKMetrics.getWMC(pClass) + CKMetrics.getNOA(pClass);

        if ((CKMetrics.getLCOM(pClass) > 350) || (featureSum > 20)) {
            if (CKMetrics.getELOC(pClass) > 500)
                return true;
        }
        return false;
    }

    private static boolean isControllerClass(ClassBean pClass) {
        String pClassName = pClass.getFullQualifiedName().toLowerCase();

        if ((pClassName.contains("process")) || (pClassName.contains("control") || pClassName.contains("command")
                || pClassName.contains("manage") || pClassName.contains("drive") || pClassName.contains("system"))) {
            int featureSum = CKMetrics.getWMC(pClass) + CKMetrics.getNOA(pClass);

            if ((CKMetrics.getLCOM(pClass) > 350) || (featureSum > 20)) {
                if (CKMetrics.getELOC(pClass) > 500)
                    return true;
            }
        }

        return false;
    }

    private boolean isBean(ClassBean pClass) {
        //da fare
        return false;
    }
}
