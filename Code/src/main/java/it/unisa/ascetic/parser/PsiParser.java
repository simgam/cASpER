package it.unisa.ascetic.parser;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import it.unisa.ascetic.analysis.code_smell.*;
import it.unisa.ascetic.analysis.code_smell_detection.blob.TextualBlobStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.feature_envy.TextualFeatureEnvyStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.misplaced_class.TextualMisplacedClassStrategy;
import it.unisa.ascetic.analysis.code_smell_detection.promiscuous_package.TextualPromiscuousPackageStrategy;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.storage.beans.*;
import it.unisa.ascetic.storage.repository.*;
import it.unisa.ascetic.storage.sqlite_jdbc_driver_connection.SQLiteConnector;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PsiParser implements Parser {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private Project project;
    PackageBeanRepository packageBeanRepository;
    ClassBeanRepository classBeanRepository;
    MethodBeanRepository methodBeanRepository;
    InstanceVariableBeanRepository instanceVariableBeanRepository;
    private final List<PackageBean> projectPackages;

    public PsiParser(Project project) {
        this.project = project;
        projectPackages = new ArrayList<>();
        logger.setLevel(Level.OFF);
    }

    public void setRepository(PackageBeanRepository packageBeanRepository, ClassBeanRepository classBeanRepository, MethodBeanRepository methodBeanRepository, InstanceVariableBeanRepository instanceVariableBeanRepository){

        this.packageBeanRepository = packageBeanRepository;
        this.classBeanRepository = classBeanRepository;
        this.methodBeanRepository = methodBeanRepository;
        this.instanceVariableBeanRepository = instanceVariableBeanRepository;
    }

    @Override
    public void parse() throws ParsingException, RepositoryException {
        logger.info("Eseguo il metodo parse");
        /*if(packageBeanRepository == null || classBeanRepository == null || methodBeanRepository == null || instanceVariableBeanRepository == null){
            throw new RepositoryException("Repository non inizializzate!");
        }
        */
        String nameProject = project.getName();
        SQLiteConnector.setNameDB(nameProject);
        if(SQLiteConnector.DBexists()) {
            SQLiteConnector.PrepareDB();
        }
        for (PsiPackage psiPackage : getAllPackagesBeans()) {
            PackageBean parsedPackageBean = parse(psiPackage);
            packageBeanRepository.add(parsedPackageBean);
            projectPackages.add(parsedPackageBean);
        }
        analizeProject();
    }



    private void parseChanges() throws ParsingException {
        try {
            HashSet<PsiPackage> foundPackages = new HashSet<>();
            List<Pair<String, String>> listOfChangePairs = getFile("changes");
            String fileName = System.getProperty("user.home") + File.separator +".ascetic" + File.separator +"changes.csv";
            File file = new File(fileName);

            List<String> updateList = new ArrayList<>();
            List<String> removeList = new ArrayList<>();
            List<String> addList = new ArrayList<>();

            for(Pair<String,String> pair : listOfChangePairs){
                if(pair.getValue().equalsIgnoreCase("create")){
                    addList.add(pair.getKey());
                    if(removeList.contains(pair.getKey())){
                    removeList.remove(pair.getKey());
                    }
                }
                if(pair.getValue().equalsIgnoreCase("update")){
                    updateList.add(pair.getKey());
                    if(removeList.contains(pair.getKey())){
                        removeList.remove(pair.getKey());
                    }
                }
                if(pair.getValue().equalsIgnoreCase("remove")){
                    removeList.add(pair.getKey());
                    if (addList.contains(pair.getKey())){
                        addList.remove(pair.getKey());
                    }
                    if (updateList.contains(pair.getKey())){
                        updateList.remove(pair.getKey());
                    }
                }
            }

            //faccio le remove

            for(String x : removeList){
                ClassBean toRemove = new ClassBean.Builder(x,"").build();
                classBeanRepository.remove(toRemove);
                removeLines(file,toRemove.getFullQualifiedName()+",delete");
            }

            //faccio le add
            for(String x : addList){
                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(x,GlobalSearchScope.allScope(project));
                try {
                    removeLines(file,psiClass.getQualifiedName()+",create");
                    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingClass();
                    PsiPackage pkg = JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
                    foundPackages.add(pkg);
                } catch (NullPointerException e){
                    //tutto ok, vuol dire che nel file changes ho trovato una classe di un altro progetto
                }
            }

            for(PsiPackage psiPackage: foundPackages){
                PackageBean packageBean = parse(psiPackage);
                packageBeanRepository.add(packageBean);
            }

            //faccio gli update
            for(String x : updateList){
                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(x,GlobalSearchScope.allScope(project));
                try {
                    removeLines(file,psiClass.getQualifiedName()+",update");
                    PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
                    PsiPackage pkg = JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
                    foundPackages.add(pkg);
                } catch (NullPointerException e){
                    //same as above
                }
            }

            for(PsiPackage psiPackage: foundPackages){
                PackageBean packageBean = parse(psiPackage);
                packageBeanRepository.add(packageBean);
                projectPackages.add(packageBean);
            }

        }catch (Exception e) {
            throw new ParsingException();
        }
    }

    /**
     * @throws RepositoryException
     */
    private void analizeProject() throws RepositoryException {
        for (PackageBean packageBean : projectPackages) {
            TextualPromiscuousPackageStrategy textualPromiscuousPackageStrategy = new TextualPromiscuousPackageStrategy();
            PromiscuousPackageCodeSmell codeSmell = new PromiscuousPackageCodeSmell(textualPromiscuousPackageStrategy);
            if (packageBean.isAffected(codeSmell)) {
                packageBeanRepository.update(packageBean);
            }

            for (ClassBean classBean : packageBean.getClassList()) {

                TextualBlobStrategy blobStrategy = new TextualBlobStrategy();
                BlobCodeSmell blobCodeSmell = new BlobCodeSmell(blobStrategy);

                TextualMisplacedClassStrategy misplacedClassStrategy = new TextualMisplacedClassStrategy(projectPackages);
                MisplacedClassCodeSmell misplacedClassCodeSmell = new MisplacedClassCodeSmell(misplacedClassStrategy);

                if (classBean.isAffected(blobCodeSmell) || classBean.isAffected(misplacedClassCodeSmell)) {
                    classBeanRepository.update(classBean);
                }

                for (MethodBean methodBean : classBean.getMethodList()) {

                    TextualFeatureEnvyStrategy featureEnvyStrategy = new TextualFeatureEnvyStrategy(projectPackages);
                    FeatureEnvyCodeSmell featureEnvyCodeSmell = new FeatureEnvyCodeSmell(featureEnvyStrategy);
                    methodBean.isAffected(featureEnvyCodeSmell);
                    methodBeanRepository.update(methodBean);

                }

            }
        }
    }

    public List<Pair<String, String>> getFile(String nameOfFIle) throws IOException {
        String fileName = System.getProperty("user.home") + File.separator +".ascetic" + File.separator + nameOfFIle + ".csv";
        File file = new File(fileName);
        List<Pair<String, String>> allMethod = new ArrayList<Pair<String, String>>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String nextString, nameClass, operation;
        nextString = bufferedReader.readLine();
        String [] split;
        while(nextString != null){
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

        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove)) continue;
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
        //logger.info("Creo un PackageBean da un PsiPackage\n");
        StringBuilder textContent = new StringBuilder();
        String name;
        ArrayList<ClassBean> list = new ArrayList<>();
        name = psiPackage.getQualifiedName();

        for(PsiClass psiClass : psiPackage.getClasses()){
            textContent.append(psiClass.getContext().getText());
        }

        PackageBean.Builder builder = new PackageBean.Builder(name, textContent.toString());
        PsiClass[] classes = psiPackage.getClasses();
        ClassList classBeanList = new ClassList();
        for(PsiClass psiClass : classes){
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
        //logger.info("Creo un ClassBean da un PsiClass");
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
        //logger.info("aggiunte le variabili d'istanza alla lista della classe\n");

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
        //logger.info("Creo un MethodBean da un PsiMethod");
        //MethodBan da Parsare
        MethodBean.Builder builder = new MethodBean.Builder(psiMethod.getContainingClass().getQualifiedName()+"."+psiMethod.getName(), psiMethod.getText());
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
            //logger.info("nessuna Variabile d'istanza");
            npe.printStackTrace();
        }
        InstanceVariableList instanceVariableList = new InstanceVariableList();
        instanceVariableList.setList(list);

        //ClassBean Pezzotto Return Type
        String returnTypeName;
        try {
            returnTypeName = psiMethod.getReturnType().getInternalCanonicalText();
        } catch (NullPointerException e ){
            returnTypeName = "void";
        }
        ClassBean returnTypeBean = new ClassBean.Builder(returnTypeName, "").build();
        builder.setReturnType(returnTypeBean);
        builder.setInstanceVariableList(instanceVariableList);


        //ClassBean Pezzotto belonging Class
        ClassBean belongingClass = new ClassBean.Builder(psiMethod.getContainingClass().getQualifiedName(),"").build();
        builder.setBelongingClass(belongingClass);
        //setto i parametri del metodo
        HashMap<String, ClassBean> map = new HashMap<>();
        for (PsiParameter parametri:psiMethod.getParameterList().getParameters()){
            map.put(parametri.getName(),new ClassBean.Builder(parametri.getType().getInternalCanonicalText(),"").build());
        }
        builder.setParameters(map);

        builder.setVisibility(getFieldVisibilityString(psiMethod.getModifierList()));
        MethodBeanList methodCalls = new MethodList();
        List<MethodBean> methodCallList = new ArrayList<>(findMethodInvocations(psiMethod));
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
        //logger.info("Creo un InstanceVariableBean da un PsiField");
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
     * @param psiMethod il metodo in cui trovare le chiamate a metodi
     * @return
     */
    private Collection<MethodBean> findMethodInvocations(PsiMethod psiMethod){
        Collection<MethodBean> invocations = new HashSet<>();

        Collection<PsiMethodCallExpression> psiMethodCallExpressions = PsiTreeUtil.findChildrenOfType(psiMethod,PsiMethodCallExpression.class);

        for(PsiMethodCallExpression call : psiMethodCallExpressions){

            PsiElement elementFound = call.getMethodExpression().resolve();
            if(elementFound instanceof PsiMethod){
                PsiMethod method = (PsiMethod)elementFound;
                MethodBean bean = new MethodBean.Builder(method.getContainingClass().getQualifiedName()+"."+method.getName(),"").build();
                invocations.add(bean);
                //System.out.println();
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
                        logger.info("found " + aPackage.getQualifiedName());//aggiungi logger
                    }
                }
            }
        });
        return foundPackages;
    }
}