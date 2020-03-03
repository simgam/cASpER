package it.unisa.casper.parser;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import it.unisa.casper.analysis.code_smell.BlobCodeSmell;
import it.unisa.casper.analysis.code_smell.FeatureEnvyCodeSmell;
import it.unisa.casper.analysis.code_smell.MisplacedClassCodeSmell;
import it.unisa.casper.analysis.code_smell.PromiscuousPackageCodeSmell;
import it.unisa.casper.analysis.code_smell_detection.blob.StructuralBlobStrategy;
import it.unisa.casper.analysis.code_smell_detection.blob.TextualBlobStrategy;
import it.unisa.casper.analysis.code_smell_detection.feature_envy.StructuralFeatureEnvyStrategy;
import it.unisa.casper.analysis.code_smell_detection.feature_envy.TextualFeatureEnvyStrategy;
import it.unisa.casper.analysis.code_smell_detection.misplaced_class.StructuralMisplacedClassStrategy;
import it.unisa.casper.analysis.code_smell_detection.misplaced_class.TextualMisplacedClassStrategy;
import it.unisa.casper.analysis.code_smell_detection.promiscuous_package.StructuralPromiscuousPackageStrategy;
import it.unisa.casper.analysis.code_smell_detection.promiscuous_package.TextualPromiscuousPackageStrategy;
import it.unisa.casper.storage.beans.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;

//import it.unisa.casper.storage.beans.*;

public class PsiParser implements Parser {
    private Project project;
    private final List<PackageBean> projectPackages;
    private static String path;

    public PsiParser(Project project) {
        this.project = project;
        path = project.getBasePath();
        projectPackages = new ArrayList<PackageBean>();
    }

    @Override
    public List<PackageBean> parse() throws ParsingException {
        try {
            PackageBean parsedPackageBean;
            Set<PsiPackage> listPackages = getAllPackagesBeans();
            for (PsiPackage psiPackage : listPackages) {
                parsedPackageBean = parse(psiPackage);
                projectPackages.add(parsedPackageBean);
            }

            HashMap<String, Double> coseno = new HashMap<String, Double>();
            HashMap<String, Integer> dipendence = new HashMap<String, Integer>();

            ArrayList<String> smell = new ArrayList<String>();
            smell.add("Feature");
            smell.add("Misplaced");
            smell.add("Blob");
            smell.add("Promiscuous");
            try {
                FileReader f = new FileReader(System.getProperty("user.home") + File.separator + ".casper" + File.separator + "threshold.txt");
                BufferedReader b = new BufferedReader(f);

                String[] list = null;
                for (String s : smell) {
                    list = b.readLine().split(",");
                    coseno.put("coseno" + s, Double.parseDouble(list[0]));
                    dipendence.put("dip" + s, Integer.parseInt(list[1]));
                    if (s.equalsIgnoreCase("promiscuous")) {
                        dipendence.put("dip" + s + "2", Integer.parseInt(list[2]));
                    }
                    if (s.equalsIgnoreCase("blob")) {
                        dipendence.put("dip" + s + "2", Integer.parseInt(list[2]));
                        dipendence.put("dip" + s + "3", Integer.parseInt(list[3]));
                    }
                }
            } catch (Exception e) {

            }

            for (PackageBean packageBean : projectPackages) {

                packageAnalysis(coseno, dipendence, packageBean);

                for (ClassBean classBean : packageBean.getClassList()) {

                    classAnalysis(coseno, dipendence, classBean);

                    for (MethodBean methodBean : classBean.getMethodList()) {

                        methosAnalysis(coseno, dipendence, methodBean);
                    }
                }
            }
            return projectPackages;
        } catch (Exception e) {
            throw new ParsingException();
        }
    }

    private void methosAnalysis(HashMap<String, Double> coseno, HashMap<String, Integer> dipendence, MethodBean methodBean) {
        TextualFeatureEnvyStrategy textualFeatureEnvyStrategy = new TextualFeatureEnvyStrategy(projectPackages, coseno.get("cosenoFeature"));
        FeatureEnvyCodeSmell tFeatureEnvyCodeSmell = new FeatureEnvyCodeSmell(textualFeatureEnvyStrategy, "Textual");
        methodBean.isAffected(tFeatureEnvyCodeSmell);

        StructuralFeatureEnvyStrategy structuralFeatureEnvyStrategy = new StructuralFeatureEnvyStrategy(projectPackages, dipendence.get("dipFeature"));
        FeatureEnvyCodeSmell sFeatureEnvyCodeSmell = new FeatureEnvyCodeSmell(structuralFeatureEnvyStrategy, "Structural");
        methodBean.isAffected(sFeatureEnvyCodeSmell);
    }

    private void classAnalysis(HashMap<String, Double> coseno, HashMap<String, Integer> dipendence, ClassBean classBean) {
        TextualBlobStrategy textualBlobStrategy = new TextualBlobStrategy(coseno.get("cosenoBlob"));
        BlobCodeSmell tBlobCodeSmell = new BlobCodeSmell(textualBlobStrategy, "Textual");
        TextualMisplacedClassStrategy textualMisplacedClassStrategy = new TextualMisplacedClassStrategy(projectPackages, coseno.get("cosenoMisplaced"));
        MisplacedClassCodeSmell tMisplacedClassCodeSmell = new MisplacedClassCodeSmell(textualMisplacedClassStrategy, "Textual");
        classBean.isAffected(tBlobCodeSmell);
        classBean.isAffected(tMisplacedClassCodeSmell);
        classBean.setSimilarity(0);

        StructuralBlobStrategy structuralBlobStrategy = new StructuralBlobStrategy(dipendence.get("dipBlob"), dipendence.get("dipBlob2"), dipendence.get("dipBlob3"));
        BlobCodeSmell sBlobCodeSmell = new BlobCodeSmell(structuralBlobStrategy, "Structural");
        StructuralMisplacedClassStrategy structuralMisplacedClassStrategy = new StructuralMisplacedClassStrategy(projectPackages, dipendence.get("dipMisplaced"));
        MisplacedClassCodeSmell sMisplacedClassCodeSmell = new MisplacedClassCodeSmell(structuralMisplacedClassStrategy, "Structural");
        classBean.isAffected(sBlobCodeSmell);
        classBean.isAffected(sMisplacedClassCodeSmell);
        classBean.setSimilarity(0);
    }

    private void packageAnalysis(HashMap<String, Double> coseno, HashMap<String, Integer> dipendence, PackageBean packageBean) {
        TextualPromiscuousPackageStrategy textualPromiscuousPackageStrategy = new TextualPromiscuousPackageStrategy(coseno.get("cosenoPromiscuous"));
        PromiscuousPackageCodeSmell tPromiscuousPackagecodeSmell = new PromiscuousPackageCodeSmell(textualPromiscuousPackageStrategy, "Textual");
        packageBean.isAffected(tPromiscuousPackagecodeSmell);
        packageBean.setSimilarity(0);

        StructuralPromiscuousPackageStrategy structuralPromiscuousPackageStrategy = new StructuralPromiscuousPackageStrategy(projectPackages, dipendence.get("dipPromiscuous") / 100, dipendence.get("dipPromiscuous2") / 100);
        PromiscuousPackageCodeSmell sPromiscuousPackagecodeSmell = new PromiscuousPackageCodeSmell(structuralPromiscuousPackageStrategy, "Structural");
        packageBean.isAffected(sPromiscuousPackagecodeSmell);
        packageBean.setSimilarity(0);
    }

    /**
     * Creates a PackageBean from an intellij PsiPackage.
     * The method obtains all the information from the
     * PsiPackage and uses them to create a PackageBean.
     *
     * @param psiPackage
     * @return
     */
    private PackageBean parse(PsiPackage psiPackage) {
        //Package da parsare
        StringBuilder textContent = new StringBuilder();
        String name;
        ArrayList<ClassBean> list = new ArrayList<>();
        name = psiPackage.getQualifiedName();

        for (PsiClass psiClass : psiPackage.getClasses()) {
            textContent.append(psiClass.getContext().getText());
        }

        PackageBean.Builder builder = new PackageBean.Builder(name, textContent.toString());
        PsiClass[] classes = psiPackage.getClasses();
        ClassList classBeanList = new ClassList();
        for (PsiClass psiClass : classes) {
            list.add(parse(psiClass, textContent.toString()));
        }
        classBeanList.setList(list);
        builder.setClassList(classBeanList);
        return builder.build();
    }

    /**
     * Creates an ClassBean from an intellij psiClass.
     *
     * @param psiClass
     * @return ClassBean
     */
    public ClassBean parse(PsiClass psiClass, String contentForPackage) {
        //Classe da parsare
        PsiJavaFile file = (PsiJavaFile) psiClass.getContainingFile();
        PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(file.getPackageName());
        String pkgName = psiPackage.getQualifiedName();
        //PackageBean per supporto
        PackageBean packageBean = new PackageBean.Builder(pkgName, contentForPackage).build();
        //analizzo la classe
        String name = psiClass.getQualifiedName();
        String text = psiClass.getScope().getText();

        //creo la lista delle variabili d'istanza
        ClassBean.Builder builder = new ClassBean.Builder(name, text);
        builder.setBelongingPackage(packageBean);

        //ottengo il path assoluto
        name = name.replace(".", "/");
        String[] list = name.split("/");
        String root = list[0];
        File source = new File(path);
        boolean controllo = false;
        int j = 0, i = 0;
        File[] all = source.listFiles();
        File[] in;
        while (j < all.length && !controllo) {
            in = all[j].listFiles();
            if (in != null) {
                while (i < in.length && !controllo) {
                    if (in[i].getName().equalsIgnoreCase(root)) {
                        controllo = true;
                        root = path + "/" + all[j].getName();
                    }
                    i++;
                }
            }
            i = 0;
            j++;
        }
        if (controllo) {
            for (j = 0; j < list.length - 1; j++) {
                root += "/" + list[j];
            }
            builder.setPathToFile(root);
        }

        //controllo se presenta superclasse
        if (psiClass.getSuperClass() != null) {
            builder.setSuperclass(psiClass.getSuperClass().getQualifiedName());
        }

        //calcolo LOC
        Pattern newLine = Pattern.compile("\n");
        String[] lines = newLine.split(psiClass.getText());
        builder.setLOC(lines.length);

        ArrayList<InstanceVariableBean> listVariabili = new ArrayList<>();
        PsiField[] fields = psiClass.getFields();
        for (PsiField field : fields) {
            listVariabili.add(parse(field));
        }
        InstanceVariableList instanceVariableList = new InstanceVariableList();
        instanceVariableList.setList(listVariabili);
        builder.setInstanceVariables(instanceVariableList);

        //creo la lista dei metodi
        ArrayList<MethodBean> listaMetodi = new ArrayList<>();
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            listaMetodi.add(parse(method, text));
        }
        MethodList methodList = new MethodList();
        methodList.setList(listaMetodi);
        builder.setMethods(methodList);

        List<String> imports = new ArrayList<>();
        for (PsiElement e : ((PsiJavaFile) psiClass.getContainingFile()).getImportList().getChildren()) {
            if (!e.getText().contains("\n")) {
                imports.add(e.getText());
            }
        }
        builder.setImports(imports);
        return builder.build();
    }

    /**
     * Creates a MethodBean from an intellij PsiMethod.
     * The method obtains all the information from the
     * PsiMethod and uses them to create a MethodBean.
     *
     * @param psiMethod the method to convert
     * @return MethodBean
     */
    public MethodBean parse(PsiMethod psiMethod, String textContent) {
        //MethodBan da Parsare
        MethodBean.Builder builder = new MethodBean.Builder(psiMethod.getContainingClass().getQualifiedName() + "." + psiMethod.getName(), psiMethod.getText());
        builder.setStaticMethod(psiMethod.getModifierList().hasExplicitModifier("static"));

        //Genero la Lista di Variabili d'istanza utilizzate
        ArrayList<InstanceVariableBean> list = new ArrayList<>();
        // controllo se nel metodo ci sono variabili d'istanza
        try {
            PsiField[] fields = psiMethod.getContainingClass().getFields();
            String methodBody;
            if (psiMethod.getBody() == null) {
                methodBody = "";
            } else {
                methodBody = psiMethod.getBody().getText();
            }
            for (PsiField field : fields) {
                if (methodBody.contains(field.getName()))
                    list.add(parse(field));
            }
        } catch (NullPointerException e) {

        }
        InstanceVariableList instanceVariableList = new InstanceVariableList();
        instanceVariableList.setList(list);

        //ClassBean Return Type
        String returnTypeName;
        try {
            returnTypeName = psiMethod.getReturnType().getInternalCanonicalText();
        } catch (NullPointerException e) {
            returnTypeName = "void";
        }
        ClassBean returnTypeBean = new ClassBean.Builder(returnTypeName, "").build();
        builder.setReturnType(returnTypeBean);
        builder.setInstanceVariableList(instanceVariableList);


        //ClassBean Belonging Class
        ClassBean belongingClass = new ClassBean.Builder(psiMethod.getContainingClass().getQualifiedName(), textContent).build();
        builder.setBelongingClass(belongingClass);
        //setto i parametri del metodo
        HashMap<String, ClassBean> map = new HashMap<>();
        for (PsiParameter parametri : psiMethod.getParameterList().getParameters()) {
            map.put(parametri.getName(), new ClassBean.Builder(parametri.getType().getInternalCanonicalText(), "").build());
        }
        builder.setParameters(map);

        builder.setVisibility(getFieldVisibilityString(psiMethod.getModifierList()));
        MethodBeanList methodCalls = new MethodList();
        List<MethodBean> methodCallList = new ArrayList<MethodBean>();

        for (MethodBean m : findMethodInvocations(psiMethod)) {
            methodCallList.add(m);
        }
        ((MethodList) methodCalls).setList(methodCallList);
        builder.setMethodsCalls(methodCalls);
        return builder.build();
    }

    /**
     * Creates an InstanceVariableBean from an intellij PsiField.
     * The method obtains the name,the visibility, the type and
     * the initialization value from the PsiField and uses them to
     * set InstanceVariableBean properties.
     *
     * @param psiField to convert.
     * @return the InstanceVariableBean corresponding to the given PsiField
     */
    public InstanceVariableBean parse(PsiField psiField) {
        //Variabili d'instanza da parsare
        String name = psiField.getName();
        String type = psiField.getType().getCanonicalText();
        String initialization = "";
        String visibility = getFieldVisibilityString(psiField.getModifierList());

        if (psiField.getInitializer() != null)
            initialization = psiField.getInitializer().getText();
        return new InstanceVariableBean(name, type, initialization, visibility);
    }

    /**
     * This method obtain the visibility for an Object of type PsiModifierList.
     * The method check if the parameter is a parameter of type "public" || "private"
     * || protected || null, and return the result.
     *
     * @param modifierList of a PsiField
     * @return "public" || "private" || "protected" || null.
     */
    private String getFieldVisibilityString(PsiModifierList modifierList) {
        final String PUBLIC_PROP = "public";
        final String PRIVATE_PROP = "private";
        final String PROTECTED_PROP = "protected";
        if (modifierList.hasModifierProperty("public")) {
            return PUBLIC_PROP;
        } else if (modifierList.hasModifierProperty("private")) {
            return PRIVATE_PROP;
        } else if (modifierList.hasModifierProperty("protected")) {
            return PROTECTED_PROP;
        } else {
            return null;
        }
    }

    /**
     * Restituisce una lista di method beans corrispondenti ai metodi invocati
     * dal metodo dato in input
     *
     * @param psiMethod il metodo in cui trovare le chiamate a metodi
     * @return
     */
    private List<MethodBean> findMethodInvocations(PsiMethod psiMethod) {
        List<MethodBean> invocations = new ArrayList<MethodBean>();

        String qualifiedName = "";

        Collection<PsiMethodCallExpression> psiMethodCallExpressions = PsiTreeUtil.findChildrenOfType(psiMethod, PsiMethodCallExpression.class);
        Collection<PsiConstructorCall> psiIstanceOfClass = PsiTreeUtil.findChildrenOfType(psiMethod, PsiConstructorCall.class);

        MethodBean bean;
        for (PsiMethodCallExpression call : psiMethodCallExpressions) {

            PsiElement elementFound = call.getMethodExpression().resolve();
            if (elementFound instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) elementFound;
                qualifiedName = method.getContainingClass().getQualifiedName();
                if (!qualifiedName.contains("java.util.") && !qualifiedName.contains("java.lang.") && !qualifiedName.contains("java.io.")) {

                    bean = new MethodBean.Builder(qualifiedName + "." + method.getName(), "").build();
                    if (!invocations.contains(bean)) {
                        invocations.add(bean);
                    }
                }
            }
        }
        qualifiedName = "";
        bean = null;

        for (PsiConstructorCall call2 : psiIstanceOfClass) {

            PsiMethod elementFound2 = call2.resolveMethod();
            if (elementFound2 != null) {
                qualifiedName = elementFound2.getContainingClass().getQualifiedName();
                if (!qualifiedName.contains("java.util.") && !qualifiedName.contains("java.lang.") && !qualifiedName.contains("java.io.")) {

                    bean = new MethodBean.Builder(qualifiedName + "." + elementFound2.getName(), "").build();
                    if (!invocations.contains(bean)) {
                        invocations.add(bean);
                    }
                }
            }
        }

        return invocations;
    }

    /**
     * CODICE DI MANUEL ATTENZIONE
     * non toccare qui sennò Gesù si arrabbia
     *
     * @return Restituisce la lista dei Package
     */
    private Set<PsiPackage> getAllPackagesBeans() {

        final Set<PsiPackage> foundPackages = new HashSet<>();

        AnalysisScope scope = new AnalysisScope(this.project);
        scope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {
                if (file instanceof PsiJavaFile) {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                    final PsiPackage aPackage = JavaPsiFacade.getInstance(project).findPackage(psiJavaFile.getPackageName());
                    if (aPackage != null) {
                        foundPackages.add(aPackage);
                    }
                }
            }
        });
        return foundPackages;
    }
}