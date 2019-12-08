package it.unisa.casper.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.extractclass.ExtractClassProcessor;
import it.unisa.casper.refactor.exceptions.BlobException;
import it.unisa.casper.refactor.strategy.RefactoringStrategy;
import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.InstanceVariableBean;
import it.unisa.casper.storage.beans.MethodBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlobRefatoringStrategy implements RefactoringStrategy {
    private ClassBean originalClass;
    private List<ClassBean> splittedList;
    protected Project project;
    private PsiClass psiOriginalClass = null;

    public BlobRefatoringStrategy(ClassBean originalClass, List<ClassBean> splittedList, Project project) {
        this.originalClass = originalClass;
        this.splittedList = splittedList;
        this.project = project;
    }

    @Override
    public void doRefactor() throws BlobException {

        String pathClass = originalClass.getPathToFile();
        String incopletePath = pathClass.substring(0, pathClass.lastIndexOf('/'));
        String packageName = originalClass.getBelongingPackage().getFullQualifiedName();
        List<PsiMethod> methodsToMove;
        List<PsiField> fieldsToMove;
        List<PsiClass> innerClasses;
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);

        for (ClassBean classBean : splittedList) {
            methodsToMove = new ArrayList<>();
            fieldsToMove = new ArrayList<>();
            innerClasses = new ArrayList<>();
            String classShortName = classBean.getFullQualifiedName().substring(classBean.getFullQualifiedName().lastIndexOf('.') + 1);

            //ApplicationManager.getApplication().runReadAction(() -> {
            psiOriginalClass = PsiUtil.getPsi(originalClass, project);
//
//            JavaPsiFacade.getInstance(project).findClass(originalClass.getFullQualifiedName(), GlobalSearchScope.everythingScope(project));//PsiUtil.getPsi(,project);

            // creo una lista di metodi
            for (MethodBean metodoSplittato : classBean.getMethodList()) {
                //System.out.println(psiOriginalClass.getConstructors().length);
                String methodShortName = metodoSplittato.getFullQualifiedName().substring(metodoSplittato.getFullQualifiedName().lastIndexOf('.') + 1);
                methodsToMove.add(psiOriginalClass.findMethodsByName(methodShortName, true)[0]);
            }

            //creo una lista di fields
            for (InstanceVariableBean instanceVariableBean : classBean.getInstanceVariablesList()) {
                //System.out.println(psiOriginalClass.findFieldByName(instanceVariableBean.getFullQualifiedName(), true).getName());
                fieldsToMove.add(psiOriginalClass.findFieldByName(instanceVariableBean.getFullQualifiedName(), true));
            }

            for (PsiClass innerClass : psiOriginalClass.getInnerClasses()) {
                innerClasses.add(innerClass);
            }

            // });
            try {

                ExtractClassProcessor processor = new ExtractClassProcessor(psiOriginalClass, fieldsToMove, methodsToMove, innerClasses, packageName, classShortName);
                processor.run();

            } catch (Exception e) {
                e.printStackTrace();
                throw new BlobException(e.getMessage());
            }
        }

        File file;
        PsiClass aClass;
        int i = 0;
        for (ClassBean classToMove : splittedList) {
            aClass = javaPsiFacade.findClasses(classToMove.getFullQualifiedName(), GlobalSearchScope.allScope(project))[0];

            methodsToMove = new ArrayList<>();
            methodsToMove.add(aClass.getMethods()[0]);
            ExtractClassProcessor processor = new ExtractClassProcessor(aClass, new ArrayList<>(), methodsToMove, new ArrayList<>(), packageName, "appoggioAbcdefg" + i);
            processor.run();
            i++;
        }
        i = 0;
        String path = "";
        while (i < splittedList.size()) {
            path = incopletePath + "/" + packageName.substring(packageName.lastIndexOf(".") + 1);
            path = path.substring(packageName.lastIndexOf("/") + 1) + "/appoggioAbcdefg" + i + ".java";
            file = new File(path);
            file.delete();
            i++;
        }
    }

    private void createDelegation(PsiMethod metodoSplittato) {
        //ottengo il metodo da aggiornare
        PsiMethod metodoDaAggiornare = psiOriginalClass.findMethodsByName(metodoSplittato.getName(), true)[0];
        //Creo il corpo del metodo da modificare
        String scope = metodoDaAggiornare.getModifierList().getText();
        String returnType = metodoDaAggiornare.getReturnType().getPresentableText();
        String name = metodoDaAggiornare.getName();
        String parameters = metodoDaAggiornare.getParameterList().getText();
        String throwsList = metodoDaAggiornare.getThrowsList().getText();
        //Creo il nuovo Body
        StringBuilder newMethodBody = new StringBuilder("{\n\t");
        newMethodBody.append(metodoSplittato.getContainingClass().getQualifiedName() + " variabile = new " + metodoSplittato.getContainingClass().getQualifiedName() + "();\n\t");
        if (metodoDaAggiornare.getReturnType().getCanonicalText() != "void")
            newMethodBody.append("return  ");
        //Setto la lista dei parametri da passare
        StringBuilder parametriDaPassareBuilder = new StringBuilder("(");
        for (PsiParameter parametroName : metodoDaAggiornare.getParameterList().getParameters()) {
            parametriDaPassareBuilder.append(parametroName.getName() + ", ");
        }
        String parametriDaPassare = parametriDaPassareBuilder.toString();
        try {
            parametriDaPassare = parametriDaPassare.substring(0, parametriDaPassare.length() - 2);
        } catch (IndexOutOfBoundsException ioB) {
            parametriDaPassare = parametriDaPassare.substring(0, parametriDaPassare.length() - 1);
        }
        parametriDaPassare += ")";
        if (parameters.length() < 3)
            parametriDaPassare = parameters;
        newMethodBody.append("variabile." + metodoSplittato.getName() + parametriDaPassare + ";\n}");
        String textToWrite = MethodMover.buildMethod(scope, returnType, name, parameters, throwsList, newMethodBody.toString());
        MethodMover.methodWriter(textToWrite, metodoDaAggiornare, psiOriginalClass, true, project);
    }
}
