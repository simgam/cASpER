package it.unisa.ascetic.analysis.code_smell_detection;

import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.util.ArrayList;

public class ComponentMutation {

    public String alterClass(ClassBean pClass) {
        String mutatedClass = "public class " + pClass.getFullQualifiedName() + " {\n";

        for (MethodBean methodBean : pClass.getMethodList()) {
            mutatedClass += methodBean.getTextContent();
            mutatedClass += "_____\n";
        }

        mutatedClass += "}\n}";

        return mutatedClass;
    }

    public String alterPackage(PackageBean pPackage) {
        String mutatedPackage = "";

        for (ClassBean classBean : pPackage.getClassList()) {
            mutatedPackage += classBean.getTextContent();
            mutatedPackage += "_____\n";
        }

        return mutatedPackage;
    }

    public String alterMethod(ArrayList<String> pMethodBlocks) {
        String mutatedMethod = "";

        for (String block : pMethodBlocks) {
            mutatedMethod += block;
            mutatedMethod += "_____\n";
        }

        return mutatedMethod;
    }

}
