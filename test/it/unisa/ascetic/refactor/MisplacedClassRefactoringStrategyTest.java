package it.unisa.ascetic.refactor;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaPsiFacadeEx;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import it.unisa.ascetic.refactor.exceptions.MisplacedClassException;
import it.unisa.ascetic.refactor.manipulator.MisplacedClassRefactoringStrategy;
import it.unisa.ascetic.storage.beans.*;

import java.io.File;
import java.util.ArrayList;

public class MisplacedClassRefactoringStrategyTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return new File(getSourceRoot(), "testData").getPath();
    }

    private static File getSourceRoot() {
        String testOutput = PathManager.getJarPathForClass(MisplacedClassRefactoringStrategy.class);
        return new File(testOutput, "../../..");
    }

    public void testMisplacedClass() {
        //Ottengo il Project dalla test Suite
        Project project = myFixture.getProject();
        //Ottengo i files
        PsiFile[] files = myFixture.configureByFiles("moveClass/moveClass1/Licantropo.java", "moveClass/moveClass1/Customer.java",
                "moveClass/moveClass1/Phone.java", "moveClass/moveClass2/Vampiro.java");
        //Ottengo il JavaPsiFacade ovvero l'oggetto per fare ricerche
        JavaPsiFacade javaPsiFacade = myFixture.getJavaFacade();
        //Settiamo i psi della classe da spostare
        PsiClass psiClassToMove = ((JavaPsiFacadeEx) javaPsiFacade).findClass("moveMethod.moveClass1.Licantropo", files[0].getResolveScope());
        //Creo un Array di IstanceVariableBean per la classe da spostare
        PsiField[] fields = psiClassToMove.getFields();
        InstanceVariableList classToMoveIVBList = new InstanceVariableList();
        {
            ArrayList<InstanceVariableBean> listIVB = new ArrayList<>();
            for (PsiField field : fields) {
                String name = field.getName();
                String type = field.getType().getCanonicalText();
                String initialization = "";
                String visibility = field.getModifiers()[0].name();
                if (field.getInitializer() != null)
                    initialization = field.getInitializer().getText();
                listIVB.add(new InstanceVariableBean(name, type, initialization,visibility));
            }
            classToMoveIVBList.setList(listIVB);
        }
        //Creo un array di metodi per la classe da spostare
        MethodList classToMoveMBList = new MethodList();
        {
            ArrayList<MethodBean> listMethod = new ArrayList<>();
            PsiMethod[] psiMethods = psiClassToMove.getMethods();

            for (PsiMethod method : psiMethods) {
                String methodBody = method.getBody().getText();
                MethodBean.Builder builder = new MethodBean.Builder(method.getName(), methodBody);
                builder.setStaticMethod(method.getModifierList().hasExplicitModifier("statico"));
                ArrayList<InstanceVariableBean> list = new ArrayList<>();
                // controllo se nel metodo ci sono variabili d'istanza
                for (PsiField field : fields) {
                    if (methodBody.contains(field.getName())) {
                        String name = field.getName();
                        String type = field.getType().getCanonicalText();
                        String initialization = "";
                        String visibility = "private";
                        if (field.getInitializer() != null)
                            initialization = field.getInitializer().getText();
                        list.add(new InstanceVariableBean(name, type, initialization,visibility));
                    }
                }
                InstanceVariableList instanceVariableList = new InstanceVariableList();
                instanceVariableList.setList(list);
                builder.setInstanceVariableList(instanceVariableList);
                builder.setReturnType(new ClassBean.Builder(method.getReturnType().getCanonicalText(), "").build());
                builder.setBelongingClass(new ClassBean.Builder(psiClassToMove.getQualifiedName(), psiClassToMove.getScope().getText()).build());
                listMethod.add(builder.build());
            }
            classToMoveMBList.setList(listMethod);
        }
        //Creo il Bean della Classe da spostare
        ClassBean classToMoveClassBean;
        ClassBean.Builder classToMoveBeanBuilder = new ClassBean.Builder(psiClassToMove.getQualifiedName(), psiClassToMove.getScope().getText());
        classToMoveBeanBuilder.setMethods(classToMoveMBList);
        classToMoveBeanBuilder.setInstanceVariables(classToMoveIVBList);
        classToMoveClassBean = classToMoveBeanBuilder.build();

        //Settiamo i psi del package di destinazione
        PsiPackage psiDestination = ((JavaPsiFacadeEx) javaPsiFacade).findPackage("moveMethod.moveClass2");
        //Creo un Array di ClassBean per il package destinazione
        PsiClass[] classes = psiDestination.getClasses();
        //Creo una lista temporanea dove salvare i ClassBean presenti nell'array
        ArrayList<ClassBean> listCB = new ArrayList<>();
        //Creo il textContent del PackageBean di destinazione;
        String textContent = "";
        for (PsiClass psiClass : classes) {
            textContent += psiClass.getText() + "\n";
            //Creo un Array di IstanceVariableBean per la classe i-esima
            PsiField[] aClassfields = psiClass.getFields();
            InstanceVariableList aIVBList = new InstanceVariableList();
            {
                ArrayList<InstanceVariableBean> listIVB = new ArrayList<>();
                for (PsiField field : aClassfields) {
                    String name = field.getName();
                    String type = field.getType().getCanonicalText();
                    String initialization = "";
                    if (field.getInitializer() != null)
                        initialization = field.getInitializer().getText();
                    listIVB.add(new InstanceVariableBean(name, type, initialization,"private"));
                }
                aIVBList.setList(listIVB);
            }

            //Creo un array di metodi per la classe i-esima
            MethodList aMBList = new MethodList();
            {
                ArrayList<MethodBean> listMethod = new ArrayList<>();
                PsiMethod[] psiMethods = psiClass.getMethods();

                for (PsiMethod method : psiMethods) {
                    String methodBody = method.getBody().getText();
                    MethodBean.Builder builder = new MethodBean.Builder(method.getName(), method.getBody().getText());
                    builder.setStaticMethod(method.getModifierList().hasExplicitModifier("statico"));
                    ArrayList<InstanceVariableBean> list = new ArrayList<>();
                    // controllo se nel metodo ci sono variabili d'istanza
                    for (PsiField field : fields) {
                        if (methodBody.contains(field.getName())) {
                            String name = field.getName();
                            String type = field.getType().getCanonicalText();
                            String initialization = "";
                            if (field.getInitializer() != null)
                                initialization = field.getInitializer().getText();
                            list.add(new InstanceVariableBean(name, type, initialization,"private"));
                        }
                    }
                    InstanceVariableList instanceVariableList = new InstanceVariableList();
                    instanceVariableList.setList(list);
                    builder.setInstanceVariableList(instanceVariableList);
                    builder.setReturnType(new ClassBean.Builder(method.getReturnType().getCanonicalText(), "").build());
                    builder.setBelongingClass(new ClassBean.Builder(psiClass.getQualifiedName(), psiClass.getScope().getText()).build());
                    listMethod.add(builder.build());
                }
                aMBList.setList(listMethod);
            }

            //Creo il Bean della Classe i-esima
            ClassBean aClassBean;
            ClassBean.Builder sourceBeanBuilder = new ClassBean.Builder(psiClass.getQualifiedName(), psiClass.getScope().getText());
            sourceBeanBuilder.setMethods(aMBList);
            sourceBeanBuilder.setInstanceVariables(aIVBList);
            aClassBean = sourceBeanBuilder.build();
            //aggiungiamo l'i-esima classe alla nostra lista temporanea
            listCB.add(aClassBean);
        }

        //Creo la lista di classi presenti nel Package di destinazione
        ClassList destinationClassList = new ClassList();
        destinationClassList.setList(listCB);

        //Creo il Bean del Package di destinazione
        PackageBean destinationPackageBean;
        PackageBean.Builder destinationBeanBuilder = new PackageBean.Builder(psiDestination.getQualifiedName(), textContent);
        destinationBeanBuilder.setClassList(destinationClassList);
        destinationPackageBean = destinationBeanBuilder.build();


        //testiamo i bean
        System.out.println("*************************************");
        System.out.println("Ciao, sono il Bean della classToMoveClassBean");
        System.out.println(classToMoveClassBean.getFullQualifiedName() + "\n\t" + classToMoveClassBean.getTextContent());
        System.out.println("*************************************");
        System.out.println("Ciao, sono il Bean della destinationPackageBean");
        System.out.println(destinationPackageBean.getFullQualifiedName() + "\n\t" + destinationPackageBean.getTextContent());
        System.out.println("*************************************");

        //Inizializziamo la variabile di tipo MisplacedClassRefactoringStrategy
        MisplacedClassRefactoringStrategy misplacedClassRefactoringStrategy = new MisplacedClassRefactoringStrategy(classToMoveClassBean, destinationPackageBean, project);

        WriteCommandAction.runWriteCommandAction(project, () -> {       //ESEGUIAMO IL METODO DA TESTARE
            try {
                misplacedClassRefactoringStrategy.doRefactor();
            } catch (MisplacedClassException e) {
                e.printStackTrace();
            }
        });

        //Trovo il package sorgente e la corrispettiva directory dopo aver eseguito il refactoring
        PsiPackage pack1 = ((JavaPsiFacadeEx) javaPsiFacade).findPackage("moveMethod.moveClass1");
        PsiDirectory directory1 = pack1.getDirectories()[0];
        System.out.println("***INIZIO DIRECTORY1***");
        for (PsiFile psiFile : directory1.getFiles()) {
            System.out.println("INIZIO FILE \n\t" + psiFile.getText() + "\nFINE FILE");
        }
        System.out.println("***FINE DIRECTORY1***");

        //Trovo il package destinazione e la corrispettiva directory dopo aver eseguito il refactoring
        PsiPackage pack2 = ((JavaPsiFacadeEx) javaPsiFacade).findPackage("moveMethod.moveClass2");
        PsiDirectory directory2 = pack2.getDirectories()[0];

        System.out.println("***INIZIO DIRECTORY2***");
        for (PsiFile psiFile : directory2.getFiles()) {
            System.out.println("INIZIO FILE \n\t" + psiFile.getText() + "\nFINE FILE");
        }
        System.out.println("***FINE DIRECTORY2***");

        assertNull(directory1.findFile("Licantropo.java"));         //Controllo che il file non sia più presente nella directory associata al package sorgente
        assertNotNull(directory2.findFile("Licantropo.java"));      //Controllo che il file sia presente nella directory associata al package destinazione
    }
}
