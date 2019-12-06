package it.unisa.ascetic.analysis.code_smell_detection;

import it.unisa.ascetic.storage.beans.MethodBean;

import java.util.regex.Pattern;

public class BeanDetection {

    public static boolean detection(MethodBean pMethod) {
        if (!isGetter(pMethod) && !isSetter(pMethod) && !isToString(pMethod) && !isEquals(pMethod) && !isHashCode(pMethod) && !(pMethod.getFullQualifiedName().toLowerCase().contains("main")) && !isConstructor(pMethod)) {
            return false;
        }
        return true;
    }

    private static boolean isGetter(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().contains("get") && pMethodBean.getParameters().isEmpty() &&
                !(pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void") &&
                pMethodBean.getTextContent().length() < 100) {
            return true;
        }
        return false;
    }

    private static boolean isSetter(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().contains("set") && pMethodBean.getParameters().size() == 1 &&
                (pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private static boolean isToString(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().equals("toString") && pMethodBean.getParameters().isEmpty() &&
                (pMethodBean.getReturnType()).getFullQualifiedName().equals("String")) {
            return true;
        }
        return false;
    }

    private static boolean isHashCode(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().equals("hasCode") && pMethodBean.getParameters().isEmpty() &&
                (pMethodBean.getReturnType()).getFullQualifiedName().equals("int")) {
            return true;
        }
        return false;
    }

    private static boolean isEquals(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().equals("equals") && pMethodBean.getParameters().size() == 1 &&
                (pMethodBean.getReturnType()).getFullQualifiedName().equals("boolean")) {
            return true;
        }
        return false;
    }

    private static boolean isConstructor(MethodBean pMethodBean) {
        String[] fullClass = (pMethodBean.getBelongingClass()).getFullQualifiedName().split(Pattern.quote("."));
        String[] fullMethod = pMethodBean.getFullQualifiedName().split(Pattern.quote("."));
        String nameClass = fullClass[fullClass.length - 1], nameMethod = fullMethod[fullMethod.length - 1];

        if (nameMethod.equalsIgnoreCase(nameClass) && (pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

}
