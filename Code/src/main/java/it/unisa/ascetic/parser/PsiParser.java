package it.unisa.ascetic.parser;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import it.unisa.ascetic.analysis.code_smell.*;
import it.unisa.ascetic.analysis.code_smell_detection.blob.StructuralBlobStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.blob.TextualBlobStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.StructuralFeatureEnvyStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.TextualFeatureEnvyStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.StructuralMisplacedClassStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.TextualMisplacedClassStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.StructuralPromiscuousPackageStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.TextualPromiscuousPackageStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.storage.repository.*;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

//import it.unisa.ascetic.storage.repository.*;
//import it.unisa.ascetic.storage.beans.*;

public class PsiParser implements Parser {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private Project project;
    private static PackageBeanRepository packageBeanRepository;
    private static ClassBeanRepository classBeanRepository;
    private static MethodBeanRepository methodBeanRepository;
    private static InstanceVariableBeanRepository instanceVariableBeanRepository;
    private final List<PackageBean> projectPackages;

    public PsiParser(Project project) {
        this.project = project;
        projectPackages = new ArrayList<PackageBean>();
        packageBeanRepository = new PackageRepository();
        classBeanRepository = new ClassRepository();
        methodBeanRepository = new MethodRepository();
        instanceVariableBeanRepository = new InstanceVariableRepository();
        logger.setLevel(Level.OFF);
    }

    @Override
    public void parse(double soglia) throws ParsingException, RepositoryException {
        logger.severe("Eseguo il metodo parse");
        if (packageBeanRepository == null || classBeanRepository == null || methodBeanRepository == null || instanceVariableBeanRepository == null) {
            throw new RepositoryException("Repository non inizializzate!");
        }

        String nameProject = project.getName();
        SQLiteConnector.setNameDB(nameProject);
        if (SQLiteConnector.DBexists()) {
            SQLiteConnector.PrepareDB();
        }
        PackageBean parsedPackageBean;
        for (PsiPackage psiPackage : getAllPackagesBeans()) {
            parsedPackageBean = parse(psiPackage);
            packageBeanRepository.add(parsedPackageBean);
            projectPackages.add(parsedPackageBean);
        }

        //MethodRepository repo = new MethodRepository();

        for (PackageBean p : projectPackages) {
            for (ClassBean c : p.getClassList()) {
                for (MethodBean m : c.getMethodList()) {
                    methodBeanRepository.update(m);
                }
            }
        }

        analizeProject(soglia);
    }


//    private void parseChanges() throws ParsingException {
//        try {
//            HashSet<PsiPackage> foundPackages = new HashSet<>();
//            List<Pair<String, String>> listOfChangePairs = getFile("changes");
//            String fileName = System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "changes.csv";
//            File file = new File(fileName);
//
//            List<String> updateList = new ArrayList<>();
//            List<String> removeList = new ArrayList<>();
//            List<String> addList = new ArrayList<>();
//
//            for (Pair<String, String> pair : listOfChangePairs) {
//                if (pair.getValue().equalsIgnoreCase("create")) {
//                    addList.add(pair.getKey());
//                    if (removeList.contains(pair.getKey())) {
//                        removeList.remove(pair.getKey());
//                    }
//                }
//                if (pair.getValue().equalsIgnoreCase("update")) {
//                    updateList.add(pair.getKey());
//                    if (removeList.contains(pair.getKey())) {
//                        removeList.remove(pair.getKey());
//                    }
//                }
//                if (pair.getValue().equalsIgnoreCase("remove")) {
//                    removeList.add(pair.getKey());
//                    if (addList.contains(pair.getKey())) {
//                        addList.remove(pair.getKey());
//                    }
//                    if (updateList.contains(pair.getKey())) {
//                        updateList.remove(pair.getKey());
//                    }
//                }
//            }
//
//            //faccio le remove
//
//            for (String x : removeList) {
//                ClassBean toRemove = new ClassBean.Builder(x, "").build();
//                classBeanRepository.remove(toRemove);
//                removeLines(file, toRemove.getFullQualifiedName() + ",delete");
//            }
//
//            //faccio le add
//            for (String x : addList) {
//                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(x, GlobalSearchScope.allScope(project));
//                try {
//                    removeLines(file, psiClass.getQualifiedName() + ",create");
//                    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingClass();
//                    PsiPackage pkg = JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
//                    foundPackages.add(pkg);
//                } catch (NullPointerException e) {
//                    //tutto ok, vuol dire che nel file changes ho trovato una classe di un altro progetto
//                    e.printStackTrace();
//                }
//            }
//
//            for (PsiPackage psiPackage : foundPackages) {
//                PackageBean packageBean = parse(psiPackage);
//                packageBeanRepository.add(packageBean);
//            }
//
//            //faccio gli update
//            for (String x : updateList) {
//                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(x, GlobalSearchScope.allScope(project));
//                try {
//                    removeLines(file, psiClass.getQualifiedName() + ",update");
//                    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
//                    PsiPackage pkg = JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
//                    foundPackages.add(pkg);
//                } catch (NullPointerException e) {
//                    //same as above
//                    e.printStackTrace();
//                }
//            }
//
//            for (PsiPackage psiPackage : foundPackages) {
//                PackageBean packageBean = parse(psiPackage);
//                packageBeanRepository.add(packageBean);
//                projectPackages.add(packageBean);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ParsingException();
//        }
//    }

    /**
     * @throws RepositoryException
     */
    private void analizeProject(double soglia) throws RepositoryException {

        for (PackageBean packageBean : projectPackages) {
            TextualPromiscuousPackageStrategy textualPromiscuousPackageStrategy = new TextualPromiscuousPackageStrategy(soglia);
            PromiscuousPackageCodeSmell tPromiscuousPackagecodeSmell = new PromiscuousPackageCodeSmell(textualPromiscuousPackageStrategy, "Textual");
            if (packageBean.isAffected(tPromiscuousPackagecodeSmell)) {
                packageBeanRepository.update(packageBean);
            }
            packageBean.setSimilarity(0);
            StructuralPromiscuousPackageStrategy structuralPromiscuousPackageStrategy = new StructuralPromiscuousPackageStrategy();
            PromiscuousPackageCodeSmell sPromiscuousPackagecodeSmell = new PromiscuousPackageCodeSmell(structuralPromiscuousPackageStrategy, "Structural");
            if (packageBean.isAffected(sPromiscuousPackagecodeSmell)) {
                packageBeanRepository.update(packageBean);
            }
            packageBean.setSimilarity(0);

            for (ClassBean classBean : packageBean.getClassList()) {

                TextualBlobStrategy textualBlobStrategy = new TextualBlobStrategy(soglia);
                BlobCodeSmell tBlobCodeSmell = new BlobCodeSmell(textualBlobStrategy, "Textual");
                TextualMisplacedClassStrategy textualMisplacedClassStrategy = new TextualMisplacedClassStrategy(projectPackages, soglia);
                MisplacedClassCodeSmell tMisplacedClassCodeSmell = new MisplacedClassCodeSmell(textualMisplacedClassStrategy, "Textual");

                if (classBean.isAffected(tBlobCodeSmell) || classBean.isAffected(tMisplacedClassCodeSmell)) {
                    classBeanRepository.update(classBean);
                }
                classBean.setSimilarity(0);

                StructuralBlobStrategy structuralBlobStrategy = new StructuralBlobStrategy();
                BlobCodeSmell sBlobCodeSmell = new BlobCodeSmell(structuralBlobStrategy, "Structural");
                StructuralMisplacedClassStrategy structuralMisplacedClassStrategy = new StructuralMisplacedClassStrategy(projectPackages);
                MisplacedClassCodeSmell sMisplacedClassCodeSmell = new MisplacedClassCodeSmell(structuralMisplacedClassStrategy, "Structural");
                if (classBean.isAffected(sBlobCodeSmell) || classBean.isAffected(sMisplacedClassCodeSmell)) {
                    classBeanRepository.update(classBean);
                }
                classBean.setSimilarity(0);

                for (MethodBean methodBean : classBean.getMethodList()) {

                    TextualFeatureEnvyStrategy textualFeatureEnvyStrategy = new TextualFeatureEnvyStrategy(projectPackages, soglia);
                    FeatureEnvyCodeSmell tFeatureEnvyCodeSmell = new FeatureEnvyCodeSmell(textualFeatureEnvyStrategy, "Textual");
                    if (methodBean.isAffected(tFeatureEnvyCodeSmell)) {
                        methodBeanRepository.update(methodBean);
                    }
                    methodBean.setIndex(0);

                    StructuralFeatureEnvyStrategy structuralFeatureEnvyStrategy = new StructuralFeatureEnvyStrategy(projectPackages);
                    FeatureEnvyCodeSmell sFeatureEnvyCodeSmell = new FeatureEnvyCodeSmell(structuralFeatureEnvyStrategy, "Structural");
                    if (methodBean.isAffected(sFeatureEnvyCodeSmell)) {
                        methodBeanRepository.update(methodBean);
                    }
                    methodBean.setIndex(0);

                }

            }
        }
    }

    public List<Pair<String, String>> getFile(String nameOfFIle) throws IOException {
        String fileName = System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + nameOfFIle + ".csv";
        File file = new File(fileName);
        List<Pair<String, String>> allMethod = new ArrayList<Pair<String, String>>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String nextString, nameClass, operation;
        nextString = bufferedReader.readLine();
        String[] split;
        while (nextString != null) {
            split = nextString.split(",");
            nameClass = split[0];
            operation = split[1];
            Pair<String, String> pair = new Pair<>(nameClass, operation);
            allMethod.add(pair);

            nextString = bufferedReader.readLine();
        }
        return allMethod;
    }

    private void removeLines(File inputFile, String lineToRemove) throws IOException {
        File tempFile = new File("myTempFile.csv");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)));

        String currentLine;

        while ((currentLine = reader.readLine()) != null) {

            String trimmedLine = currentLine.trim();
            if (trimmedLine.equals(lineToRemove)) continue;
            writer.println(currentLine);
        }
        writer.close();
        reader.close();
        tempFile.renameTo(inputFile);
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
            list.add(parse(psiClass));
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
    public ClassBean parse(PsiClass psiClass) {

        //Creo packageBean di appartenenza
        String contentForPackage = "";
        PsiJavaFile file = (PsiJavaFile) psiClass.getContainingFile();
        PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(file.getPackageName());
        String pkgName = psiPackage.getQualifiedName();
        //PackageBean pezzotto
        PackageBean packageBean = new PackageBean.Builder(pkgName, contentForPackage).build();
        // analizzo la classe
        String name = psiClass.getQualifiedName();
        String text = psiClass.getScope().getText();

        //creo la lista delle variabili d'istanza
        ClassBean.Builder builder = new ClassBean.Builder(name, text);
        builder.setBelongingPackage(packageBean);

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
        //logger.severe("aggiunte le variabili d'istanza alla lista della classe\n");

        //creo la lista dei metodi
        ArrayList<MethodBean> listaMetodi = new ArrayList<>();
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            listaMetodi.add(parse(method));
        }
        MethodList methodList = new MethodList();
        methodList.setList(listaMetodi);
        builder.setMethods(methodList);
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
    public MethodBean parse(PsiMethod psiMethod) {
        //logger.severe("Creo un MethodBean da un PsiMethod");
        //MethodBan da Parsare
        MethodBean.Builder builder = new MethodBean.Builder(psiMethod.getContainingClass().getQualifiedName() + "." + psiMethod.getName(), psiMethod.getText());
        builder.setStaticMethod(psiMethod.getModifierList().hasExplicitModifier("static"));

        //Genero la Lista di Variabili d'istanza utilizzate
        ArrayList<InstanceVariableBean> list = new ArrayList<>();
        // controllo se nel metodo ci sono variabili d'istanza
        try {
            PsiField[] fields = psiMethod.getContainingClass().getFields();
            String methodBody = psiMethod.getBody().getText();
            for (PsiField field : fields) {
                if (methodBody.contains(field.getName()))
                    list.add(parse(field));
            }
        } catch (NullPointerException npe) {
            //logger.severe("nessuna Variabile d'istanza");
            npe.printStackTrace();
        }
        InstanceVariableList instanceVariableList = new InstanceVariableList();
        instanceVariableList.setList(list);

        //ClassBean Pezzotto Return Type
        String returnTypeName;
        try {
            returnTypeName = psiMethod.getReturnType().getInternalCanonicalText();
        } catch (NullPointerException e) {
            returnTypeName = "void";
        }
        ClassBean returnTypeBean = new ClassBean.Builder(returnTypeName, "").build();
        builder.setReturnType(returnTypeBean);
        builder.setInstanceVariableList(instanceVariableList);


        //ClassBean Pezzotto belonging Class
        ClassBean belongingClass = new ClassBean.Builder(psiMethod.getContainingClass().getQualifiedName(), "").build();
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
        //logger.severe("Creo un InstanceVariableBean da un PsiField");
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

        return (ArrayList) invocations;
    }


    /**
     * CODICE DI MANUEL ATTENZIONE
     * non toccare qui sennò Gesù si arrabbia
     *
     * @return Restituisce la lista dei Package
     */
    private Set<PsiPackage> getAllPackagesBeans() {
        // TODO: test,javadoc e aggiungere logger
        final Set<PsiPackage> foundPackages = new HashSet<>();

        AnalysisScope scope = new AnalysisScope(this.project);
        scope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {
                if (file instanceof PsiJavaFile) {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                    final PsiPackage aPackage =
                            JavaPsiFacade.getInstance(project).findPackage(psiJavaFile.getPackageName());
                    if (aPackage != null) {
                        foundPackages.add(aPackage);
                        logger.severe("found " + aPackage.getQualifiedName());//aggiungi logger
                    }
                }
            }
        });
        return foundPackages;
    }
}