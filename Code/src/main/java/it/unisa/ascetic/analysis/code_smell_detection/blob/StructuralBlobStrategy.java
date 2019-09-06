package it.unisa.ascetic.analysis.code_smell_detection.blob;

import it.unisa.ascetic.analysis.code_smell_detection.BeanDetection;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

import java.util.HashMap;
import java.util.regex.Pattern;

public class StructuralBlobStrategy implements ClassSmellDetectionStrategy {

    private static int LCOM;
    private static int featureSum;
    private static int ELOC;

    public StructuralBlobStrategy(int LCOM, int featureSum, int ELOC) {
        this.LCOM = LCOM;
        this.featureSum = featureSum;
        this.ELOC = ELOC;
    }

    public boolean isSmelly(ClassBean pClass) {
        BeanDetection control = new BeanDetection();
        boolean bean = true;
        for (MethodBean method : pClass.getMethodList()) {
            if (!control.detection(method)) {
                bean = false;
            }
        }

        if (!bean) {

            if (isControllerClass(pClass) || isLargeClassLowCohesion(pClass)) {
                return true;
            }

        }
        return false;
    }

    private static boolean isLargeClassLowCohesion(ClassBean pClass) {
        int fSUM = CKMetrics.getWMC(pClass) + CKMetrics.getNOA(pClass);

        if ((CKMetrics.getLCOM(pClass) > LCOM) || (fSUM > featureSum)) {
            if (CKMetrics.getELOC(pClass) > ELOC) {
                return true;
            }
        }
        return false;
    }

    private static boolean isControllerClass(ClassBean pClass) {
        String pClassName = pClass.getFullQualifiedName().toLowerCase();

        if ((pClassName.contains("process")) || (pClassName.contains("control") || pClassName.contains("command")
                || pClassName.contains("manage") || pClassName.contains("drive") || pClassName.contains("system"))) {
            int fSUM = CKMetrics.getWMC(pClass) + CKMetrics.getNOA(pClass);

            if ((CKMetrics.getLCOM(pClass) > LCOM) || (fSUM > featureSum)) {
                if (CKMetrics.getELOC(pClass) > ELOC)
                    return true;
            }
        }
        return false;
    }

    public HashMap<String, Double> getThresold(ClassBean pClass) {
        HashMap<String, Double> list = new HashMap<String, Double>();

        list.put("featureSum", (double) (CKMetrics.getWMC(pClass) + CKMetrics.getNOA(pClass)));
        list.put("LCOM", (double) (CKMetrics.getLCOM(pClass)));
        list.put("ELOC", (double) (CKMetrics.getELOC(pClass)));
        return list;
    }
}
