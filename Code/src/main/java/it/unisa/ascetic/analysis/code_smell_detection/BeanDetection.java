package it.unisa.ascetic.analysis.code_smell_detection;

import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;

import java.util.regex.Pattern;

public class BeanDetection {

    public boolean detection(MethodBean pMethod) {
        if (!isGetter(pMethod) && !isSetter(pMethod) && !isToString(pMethod) && !isEquals(pMethod) && !isHashCode(pMethod) && !(pMethod.getFullQualifiedName().toLowerCase().contains("main")) && !isConstructor(pMethod)) {
            return false;
        }
        return true;
    }

    private boolean isGetter(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().contains("get") && pMethodBean.getParameters().isEmpty() &&
                !((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private boolean isSetter(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().contains("set") && pMethodBean.getParameters().size() == 1 &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private boolean isToString(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().equals("toString") && pMethodBean.getParameters().isEmpty() &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equals("String")) {
            return true;
        }
        return false;
    }

    private boolean isHashCode(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().equals("hasCode") && pMethodBean.getParameters().isEmpty() &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equals("int")) {
            return true;
        }
        return false;
    }

    private boolean isEquals(MethodBean pMethodBean) {
        if (pMethodBean.getFullQualifiedName().toLowerCase().equals("equals") && pMethodBean.getParameters().size() == 1 &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equals("boolean")) {
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

    public void stampa(MethodBean pMethod){
        System.out.println(pMethod.getFullQualifiedName());
        System.out.println(!isGetter(pMethod));
        System.out.println(!isSetter(pMethod));
        System.out.println(!isToString(pMethod));
        System.out.println(!isEquals(pMethod));
        System.out.println(!isHashCode(pMethod));
        System.out.println(!(pMethod.getFullQualifiedName().toLowerCase().contains("main")));
        System.out.println(!isConstructor(pMethod));
    }
}
