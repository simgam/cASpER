package it.unisa.ascetic.analysis.code_smell_detection.blob;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.ClassSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;

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
        if (!isBean(pClass)) {

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

    private boolean isBean(ClassBean pClass) {

        boolean bean = false;
        for (MethodBean pMethod : pClass.getMethodList()) {
            if (!isGetter(pMethod) && !isSetter(pMethod) && !(pMethod.getFullQualifiedName().toLowerCase()).contains("main") && !isConstructor(pMethod)) {
                bean = true;
            }
        }
        return bean;
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
}
