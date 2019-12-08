package it.unisa.casper.structuralMetrics;

import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.InstanceVariableBean;
import it.unisa.casper.storage.beans.MethodBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CKMetrics {

    public static int getELOC(ClassBean cb) {
        return cb.getTextContent().split("\r\n|\r|\n").length;
    }

    public static int getLOC(ClassBean cb) {
        return cb.getLOC();
    }

    public static int getLOC(MethodBean mb) {
        return mb.getTextContent().split("\n").length;
    }

    public static int getWMC(ClassBean cb) {
        int WMC = 0;
        Vector<MethodBean> methods = new Vector<MethodBean>(cb.getMethodList());
        for (MethodBean m : methods) {
            WMC += getMcCabeCycloComplexity(m);
        }

        return WMC;
    }

    public static int getNumberOfClasses(PackageBean pb) {
        return pb.getClassList().size();
    }

    public static int getDIT(ClassBean cb, Vector<ClassBean> System, int inizialization) {

        int DIT = inizialization;

        if (DIT == 3)
            return DIT;
        else {
            if (cb.getSuperclass() != null) {
                DIT++;
                for (ClassBean cb2 : System) {
                    if (cb2.getFullQualifiedName().equals(cb.getSuperclass())) {
                        getDIT(cb2, System, DIT);
                    }
                }
            } else {
                return DIT;
            }
        }
        return DIT;
    }


    public static int getNOC(ClassBean cb, Vector<ClassBean> System) {

        int NOC = 0;

        for (ClassBean c : System) {
            if (c.getSuperclass() != null && c.getSuperclass().equals(cb.getFullQualifiedName())) {
                NOC++;
            }
        }
        return NOC;
    }

    public static int getRFC(ClassBean cb) {

        int RFC = 0;

        Vector<MethodBean> methods = new Vector<MethodBean>(cb.getMethodList());
        for (MethodBean m : methods) {
            RFC += m.getMethodsCalls().size();
        }
        return RFC;
    }

    public static int getCBO(ClassBean cb) {

        if (cb.getImports() != null) {
            Vector<String> imports = new Vector<String>(cb.getImports());
            return imports.size();
        }
        return 0;
    }

    /**
     * Lack of Cohesion in Methods
     * Una metrica che misura la coesione
     */
    public static int getLCOM(ClassBean cb) {

        int share = 0;
        int notShare = 0;

        Vector<MethodBean> methods = new Vector<MethodBean>(cb.getMethodList());
        for (int i = 0; i < methods.size(); i++) {
            for (int j = i + 1; j < methods.size(); j++) {
                if (shareAnInstanceVariable(methods.elementAt(i), methods.elementAt(j))) {
                    share++;
                } else {
                    notShare++;
                }
            }
        }

        if (share > notShare) {
            return 0;
        } else {
            return (notShare - share);
        }
    }

    public static int getNOM(ClassBean cb) {
        return cb.getMethodList().size();
    }

    public static int getNOA(ClassBean cb) {
        return cb.getInstanceVariablesList().size();
    }

    public static int getNOPA(ClassBean cb) {
        int publicVariable = 0;

        Collection<InstanceVariableBean> variables = cb.getInstanceVariablesList();

        for (InstanceVariableBean variable : variables) {
            if (variable.getVisibility() != null && variable.getVisibility().equals("public"))
                publicVariable++;
        }

        return publicVariable;
    }

    public static int getNOPrivateA(ClassBean cb) {
        int privateVariable = 0;

        Collection<InstanceVariableBean> variables = cb.getInstanceVariablesList();

        for (InstanceVariableBean variable : variables) {
            if (variable.getVisibility() == null || variable.getVisibility().equals("private"))
                privateVariable++;
        }
        return privateVariable;
    }

    //Number of operations added by a subclass
    public static int getNOA(ClassBean cb, Vector<ClassBean> System) {

        int NOA = 0;

        for (ClassBean c : System) {
            if (c.getFullQualifiedName().equals(cb.getSuperclass())) {
                Vector<MethodBean> subClassMethods = new Vector<MethodBean>(cb.getMethodList());
                Vector<MethodBean> superClassMethods = new Vector<MethodBean>(c.getMethodList());
                for (MethodBean m : subClassMethods) {
                    if (!superClassMethods.contains(m)) {
                        NOA++;
                    }
                }
                break;
            }
        }
        return NOA;
    }

    public static int getFeatureSum(ClassBean pClass) {
        return CKMetrics.getWMC(pClass) + CKMetrics.getNOA(pClass);
    }

    //Number of operations overridden by a subclass
    public static int getNOO(ClassBean cb, Vector<ClassBean> System) {

        int NOO = 0;

        if (cb.getSuperclass() != null) {
            for (ClassBean c : System) {
                if (c.getFullQualifiedName().equals(cb.getSuperclass())) {
                    Vector<MethodBean> subClassMethods = new Vector<MethodBean>(cb.getMethodList());
                    Vector<MethodBean> superClassMethods = new Vector<MethodBean>(cb.getMethodList());
                    for (MethodBean m : subClassMethods) {
                        if (superClassMethods.contains(m)) {
                            NOO++;
                        }
                    }
                    break;
                }
            }
        }
        return NOO;
    }

    public static double computeMediumIntraConnectivity(PackageBean pPackage) {
        if (pPackage.getClassList().size() > 1) {
            double packAllLinks = Math.pow(pPackage.getClassList().size() - 1, 2);
            double packIntraConnectivity = 0.0;
            for (ClassBean eClass : pPackage.getClassList()) {
                for (ClassBean current : pPackage.getClassList()) {
                    if (eClass != current) {
                        if (existsDependence(eClass, current)) {
                            packIntraConnectivity++;
                        }
                    }
                }
            }
            return 1 - (packIntraConnectivity / packAllLinks);
        }
        return 0.0;
    }

    public static double computeMediumInterConnectivity(PackageBean pPackage, Collection<PackageBean> pPackages) {
        double sumInterConnectivities = 0.0;
        double packsAllLinks, packsInterConnectivity;
        if (pPackages.size() > 1) {
            for (ClassBean eClass : pPackage.getClassList()) {
                for (PackageBean currentPack : pPackages) {
                    packsInterConnectivity = 0.0;
                    packsAllLinks = 2 * pPackage.getClassList().size() - 1 * currentPack.getClassList().size() - 1;
                    if (pPackage != currentPack) {
                        for (ClassBean currentClass : currentPack.getClassList()) {
                            if (existsDependence(eClass, currentClass)) {
                                packsInterConnectivity++;
                            }
                        }
                    }
                    if (packsAllLinks == 0.0) return 0.0;
                    sumInterConnectivities += ((packsInterConnectivity) / packsAllLinks);
                }

            }
            return ((1.0 / (pPackages.size() * (pPackages.size() - 1))) * sumInterConnectivities);
        }
        return 0.0;
    }

    public static int getMcCabeCycloComplexity(MethodBean mb) {

        int mcCabe = 0;
        String code = mb.getTextContent();

        String regex = "return";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "if";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "else";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "case";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "for";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "while";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "break";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "&&";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "||";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "catch";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }

        regex = "throw";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(code);

        if (matcher.find()) {
            mcCabe++;
        }
        return mcCabe;
    }

    public static double getNumberOfDependencies(ClassBean pClass, PackageBean pPackage) {
        double dependencies = 0.0;

        for (ClassBean classBean : pPackage.getClassList()) {
            if (existsDependence(pClass, classBean))
                dependencies++;
        }
        return dependencies;
    }

    public static double getNumberOfDependencies(MethodBean pMethod, ClassBean pClass) {
        double dependencies = 0.0;

        if (pClass != null && pMethod.getMethodsCalls() != null) {
            for (MethodBean call : pMethod.getMethodsCalls()) {
                for (MethodBean classMethod : pClass.getMethodList()) {
                    if (call.getFullQualifiedName().equals(classMethod.getFullQualifiedName()))
                        dependencies++;
                }
            }
        }
        return dependencies;
    }

    private static boolean existsDependence(ClassBean pClass1, ClassBean pClass2) {

        for (MethodBean methodClass1 : pClass1.getMethodList()) {
            for (MethodBean methodClass2 : pClass2.getMethodList()) {
                if (methodClass2.getMethodsCalls() != null)
                    for (MethodBean call : methodClass2.getMethodsCalls()) {
                        if (methodClass1.getFullQualifiedName().equalsIgnoreCase(call.getFullQualifiedName()))
                            return true;
                    }
            }
        }

        return false;
    }

    private static boolean shareAnInstanceVariable(MethodBean m1, MethodBean m2) {

        Vector<InstanceVariableBean> m1Variables = new Vector<InstanceVariableBean>(m1.getInstanceVariableList());
        Vector<InstanceVariableBean> m2Variables = new Vector<InstanceVariableBean>(m2.getInstanceVariableList());

        for (InstanceVariableBean i : m1Variables) {
            if (m2Variables.contains(i)) {
                return true;
            }
        }
        return false;
    }

    private static int getWeigthedNumberOfDependencies(ClassBean pFirstClass, ClassBean pSecondClass) {
        int dependencies = 0;
        int numberOfParameters = 0;

        for (MethodBean methodBean : pFirstClass.getMethodList()) {
            Collection<MethodBean> calls = methodBean.getMethodsCalls();

            for (MethodBean call : calls) {

                for (MethodBean methodBeanToCompare : pSecondClass.getMethodList()) {
                    if (call.getFullQualifiedName().equals(methodBeanToCompare.getFullQualifiedName())) {
                        numberOfParameters += call.getParameters().size();
                        dependencies++;

                    }
                }
            }
        }

        return (numberOfParameters * dependencies);
    }

}
