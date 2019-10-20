package it.unisa.ascetic.refactor.manipulator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.extractclass.ExtractClassProcessor;
import it.unisa.ascetic.refactor.exceptions.BlobException;
import it.unisa.ascetic.refactor.strategy.RefactoringStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.InstanceVariableBean;
import it.unisa.ascetic.storage.beans.MethodBean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BlobRefatoringStrategy implements RefactoringStrategy {
    Logger logger = Logger.getLogger("global");
    private ClassBean originalClass;
    private List<ClassBean> splittedList;
    protected Project project;
    private PsiClass psiOriginalClass = null;
    private ExtractClassProcessor processor;

    public BlobRefatoringStrategy(ClassBean originalClass, List<ClassBean> splittedList, Project project) {
        this.originalClass = originalClass;
        this.splittedList = splittedList;
        this.project = project;
    }

    @Override
    public void doRefactor() throws BlobException {

        String packageName = originalClass.getBelongingPackage().getFullQualifiedName();

        for (ClassBean classBean : splittedList) {
            List<PsiMethod> methodsToMove = new ArrayList<>();
            List<PsiField> fieldsToMove = new ArrayList<>();
            List<PsiClass> innerClasses = new ArrayList<>();
            String classShortName = classBean.getFullQualifiedName().substring(classBean.getFullQualifiedName().lastIndexOf('.') + 1);

            ApplicationManager.getApplication().runReadAction(() -> {
                psiOriginalClass = JavaPsiFacade.getInstance(project).findClass(originalClass.getFullQualifiedName(), GlobalSearchScope.everythingScope(project));//PsiUtil.getPsi(,project);

                // creo una lista di metodi
                for (MethodBean metodoSplittato : classBean.getMethodList()) {
                    String methodShortName = metodoSplittato.getFullQualifiedName().substring(metodoSplittato.getFullQualifiedName().lastIndexOf('.') + 1);
                    methodsToMove.add(psiOriginalClass.findMethodsByName(methodShortName, true)[0]);
                }

                //creo una lista di fields
                for (InstanceVariableBean instanceVariableBean : classBean.getInstanceVariablesList()) {
                    fieldsToMove.add(psiOriginalClass.findFieldByName(instanceVariableBean.getFullQualifiedName(), true));
                }

                for (PsiClass innerClass : psiOriginalClass.getInnerClasses()) {
                    innerClasses.add(innerClass);
                }

            });
            try {
                processor = new ExtractClassProcessor(psiOriginalClass, fieldsToMove, methodsToMove, innerClasses, packageName, classShortName);
                processor.run();
            } catch (NullPointerException e) {
                e.printStackTrace();
                throw new BlobException(e.getMessage());
            }
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
