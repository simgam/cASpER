package it.unisa.ascetic.refactor;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaPsiFacadeEx;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import it.unisa.ascetic.refactor.exceptions.BlobException;
import it.unisa.ascetic.refactor.exceptions.FeatureEnvyException;
import it.unisa.ascetic.refactor.manipulator.BlobRefatoringStrategy;
import it.unisa.ascetic.refactor.manipulator.FeatureEnvyRefactoringStrategy;
import it.unisa.ascetic.storage.beans.*;

import java.io.File;
import java.util.ArrayList;

public class BlobRefactoringStrategyTest extends LightCodeInsightFixtureTestCase {   //NON SO PERCHE' NON ESISTE LA CLASSE DA ESTENDERE

    @Override
    protected String getTestDataPath() {
        return new File(getSourceRoot(), "testData").getPath();
    }

    private static File getSourceRoot() {
        String testOutput = PathManager.getJarPathForClass(BlobRefactoringStrategyTest.class);
        return new File(testOutput, "../../..");
    }

    public void testExtractMethod() {
        //Ottengo il Project dalla test Suite
        Project project = myFixture.getProject();
        //Ottengo i files
        PsiFile files = myFixture.configureByFile("moveMethod/global/bCustomer.java");
        //Ottengo il JavaPsiFacade ovvero l'oggetto per fare ricerche
        JavaPsiFacade javaPsiFacade = myFixture.getJavaFacade();
        //Settiamo i psi Class
        PsiClass psiSource = ((JavaPsiFacadeEx) javaPsiFacade).findClass("moveMethod.global.Customer");
        //Ottengo i bean Oracolo
        ClassBean sourceClassBean = generateOracle(psiSource);
        ClassBean destinationClassBean = generateOracle(psiSource);
        MethodBean methodBean = generateOracle(psiSource.findMethodsByName("getMobilePhoneNumber", true)[0], psiSource);
        stampaBean(sourceClassBean);
        stampaBean(destinationClassBean);
        stampaBean(methodBean);
        //Creo un FeatureEnvy per il test
        BlobRefatoringStrategy blobRefatoringStrategy = new BlobRefatoringStrategy(methodBean,"Deblobed",project);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                blobRefatoringStrategy.doRefactor();
            } catch (BlobException e) {
                e.printStackTrace();
            }
        });
        //Confronto il moveMethod con i files oracolo
        myFixture.checkResultByFile("moveMethod/global/bCustomer.java", "moveMethod/global/aCustomer.java", true); //CONFRONTO CON ORACOLO
        myFixture.checkResultByFile("moveMethod/global/bPhone.java", "moveMethod/global/aPhone.java", true); //CONFRONTO CON ORACOLO*/

    }
    private PackageBean generateOracle(PsiPackage psiPackage) {
        return null;
    }

    private ClassBean generateOracle(PsiClass psiClass) {
        //Creo un Array di IstanceVariableBean per la classe Sorgente
        PsiField[] fields = psiClass.getFields();
        InstanceVariableList sourceIVBList = new InstanceVariableList();
        ArrayList<InstanceVariableBean> listISB = new ArrayList<>();
        for (PsiField field : fields) {
            listISB.add(generateOracle(field));
        }
        sourceIVBList.setList(listISB);
        //Creo un array di metodi per la classe Sorgente
        MethodList sourceMBList = new MethodList();
        ArrayList<MethodBean> listMetodi = new ArrayList<>();
        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod method : psiMethods) {
            String methodBody = method.getBody().getText();
            MethodBean.Builder builder = new MethodBean.Builder(method.getName(), method.getBody().getText());
            builder.setStaticMethod(method.getModifierList().hasExplicitModifier("statico"));
            ArrayList<InstanceVariableBean> list = new ArrayList<>();
            // controllo se nel metodo ci sono variabili d'istanza
            for (PsiField field : fields) {
                if (methodBody.contains(field.getName())) {
                    list.add(generateOracle(field));
                }
            }
            InstanceVariableList instanceVariableList = new InstanceVariableList();
            instanceVariableList.setList(list);
            builder.setInstanceVariableList(instanceVariableList);
            try {
                builder.setReturnType(new ClassBean.Builder(method.getReturnType().getCanonicalText(), "").build());
            } catch (NullPointerException npe) {
                System.out.println("Ho avuto un NullPointerException, probabilmente perchè ho analizzato un costruttore\n\t\tTranquillo TUTTO NORMALE!");
            }
            builder.setBelongingClass(new ClassBean.Builder(psiClass.getQualifiedName(), psiClass.getScope().getText()).build());
            listMetodi.add(builder.build());
        }
        sourceMBList.setList(listMetodi);
        //Creo il Bean della Classe Sorgente
        ClassBean.Builder sourceBeanBuilder = new ClassBean.Builder(psiClass.getQualifiedName(), psiClass.getScope().getText());
        sourceBeanBuilder.setMethods(sourceMBList);
        sourceBeanBuilder.setInstanceVariables(sourceIVBList);
        return sourceBeanBuilder.build();
    }

    private MethodBean generateOracle(PsiMethod psiMethod, PsiClass destinationClassPsi) {
        PsiClass souceClassPsi = psiMethod.getContainingClass();
        //Creo il Bean del Metodo da testare
        PsiField[] fields = souceClassPsi.getFields();
        String methodBody = psiMethod.getBody().getText();
        MethodBean.Builder builder = new MethodBean.Builder(psiMethod.getName(), psiMethod.getBody().getText());
        builder.setStaticMethod(psiMethod.getModifierList().hasExplicitModifier("statico"));
        ArrayList<InstanceVariableBean> list = new ArrayList<>();
        // controllo se nel metodo ci sono variabili d'istanza
        for (PsiField field : fields) {
            try {
                if (methodBody.contains(field.getName())) {
                    list.add(generateOracle(field));
                }
            } catch (NullPointerException npe) {
                System.out.println("Costruttore");
            }
        }
        builder.setBelongingClass(generateOracle(souceClassPsi));
        builder.setEnviedClass(generateOracle(destinationClassPsi));
        InstanceVariableList instanceVariableList = new InstanceVariableList();
        instanceVariableList.setList(list);
        builder.setInstanceVariableList(instanceVariableList);
        return builder.build();
    }

    private InstanceVariableBean generateOracle(PsiField field) {
        String name = field.getName();
        String type = field.getType().getCanonicalText();
        String initialization = "";
        if (field.getInitializer() != null)
            initialization = field.getInitializer().getText();
        return new InstanceVariableBean(name, type, initialization,"private");
    }

    private void stampaBean(ClassBean classBean) {
        System.out.println("*************************************");
        System.out.println("Ciao, sono il Bean della " + classBean.getFullQualifiedName());
        System.out.println("\n\t" + classBean.getTextContent());
    }

    private void stampaBean(MethodBean methodBean) {
        System.out.println("*************************************");
        System.out.println("Ciao, sono il Bean della " + methodBean.getFullQualifiedName());
        System.out.println("\n\t" + methodBean.getTextContent());
    }

}
