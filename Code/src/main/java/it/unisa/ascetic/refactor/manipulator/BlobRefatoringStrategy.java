package it.unisa.ascetic.refactor.manipulator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
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

public class BlobRefatoringStrategy implements RefactoringStrategy {
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
                    //System.out.println(psiOriginalClass.getConstructors().length);
                    String methodShortName = metodoSplittato.getFullQualifiedName().substring(metodoSplittato.getFullQualifiedName().lastIndexOf('.') + 1);
                    methodsToMove.add(psiOriginalClass.findMethodsByName(methodShortName, true)[0]);
                }

                //creo una lista di fields
                for (InstanceVariableBean instanceVariableBean : classBean.getInstanceVariablesList()) {
                    System.out.println(psiOriginalClass.findFieldByName(instanceVariableBean.getFullQualifiedName(), true).getName());
                    fieldsToMove.add(psiOriginalClass.findFieldByName(instanceVariableBean.getFullQualifiedName(), true));
                }

                for (PsiClass innerClass : psiOriginalClass.getInnerClasses()) {
                    innerClasses.add(innerClass);
                }

            });
            try {

                processor = new ExtractClassProcessor(psiOriginalClass, fieldsToMove, methodsToMove, innerClasses, packageName, classShortName);
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    PsiMethod toDelete = processor.getCreatedClass().getMethods()[0];
                    toDelete.delete();
                });
                processor.run();


//                WriteCommandAction.runWriteCommandAction(project, () -> {
//                    //                   PsiStatement[] statements = methodProcessor.getBody().getStatements();
////                    String methodName = processor.getExtractedMethod().getName() + "();";
////                    for (PsiStatement statement : statements) {
////                        if (statement.getText().equals(methodName)) {
////                            statement.delete();
////                        }
////                    }
//                    PsiMethod[] elementsToMove = processor.getCreatedClass().getConstructors();
//                    System.out.println(elementsToMove.length);
//                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    //ExtractMethodProcessor methodProcessor = new ExtractMethodProcessor(project, editor, elementsToMove, null, "setUpRefactored", "setUp", null);
////
////                    System.out.println(elementsToMove.getName() + " " + !elementsToMove.hasParameters());
////                    if (!elementsToMove.hasParameters())
////                        elementsToMove.delete();
//
//                    PsiStatement[] statements = elementsToMove[0].getBody().getStatements();
//                    String methodName = methodProcessor.getExtractedMethod().getName() + "();";
//                    for (PsiStatement statement : statements) {
//                        if (statement.getText().equals(methodName)) {
//                            statement.delete();
//                        }
//                    }
//                });
                //System.out.println(psiOriginalClass.getConstructors().length);

            } catch (Exception e) {
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
