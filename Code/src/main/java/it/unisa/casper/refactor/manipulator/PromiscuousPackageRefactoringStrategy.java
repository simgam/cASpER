package it.unisa.casper.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.refactoring.extractclass.ExtractClassProcessor;
import it.unisa.casper.refactor.exceptions.PromiscuousPackageException;
import it.unisa.casper.refactor.strategy.RefactoringStrategy;
import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Promiscuous Package Refactor Strategy (PP Refactor) è la classe che si occupa del Refactor degli smells di tipo Promiscuous Package
 */

public class PromiscuousPackageRefactoringStrategy implements RefactoringStrategy {

    private PackageBean packageBeanSource;
    private List<PackageBean> newPackages;
    private Project project;

    /**
     * PromiscuousPackageRefactoringStrategy è il costruttore dell'omonima classe
     *
     * @param packageBeanSource Package dal quale verranno estratte le classi
     * @param newPackages       Lista dei package da estrarre
     * @param project           Progetto nel quale sono presenti i PackageBean
     */
    public PromiscuousPackageRefactoringStrategy(PackageBean packageBeanSource, Collection<PackageBean> newPackages, Project project) {
        this.packageBeanSource = packageBeanSource;
        this.newPackages = (List<PackageBean>) newPackages;
        this.project = project;
    }

    /**
     * Metodo che permette il refactoring di codeSmell di tipo Promiscuous Package
     *
     * @throws PromiscuousPackageException
     */
    @Override
    public void doRefactor() throws PromiscuousPackageException {
        //Creo il javaPsiFacade per trovare i psiClass
        PsiClass aClass;
        List<PsiField> psiFields;
        List<PsiMethod> psiMethods;
        List<PsiClass> psiInnerClasses;
        List<ClassBean> classBeanList;
        String pathPackage = packageBeanSource.getClassList().get(0).getPathToFile();
        String incopletePath = pathPackage.substring(0, pathPackage.lastIndexOf('/'));
        String path, fqn;
        PsiPackage newPackage;
        int inizio = 1, i;

        try {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            fqn = packageBeanSource.getFullQualifiedName();
            fqn = fqn.substring(0, fqn.lastIndexOf(".") + 1);
            if (fqn.equalsIgnoreCase("")) {
                fqn += ".";
            }

            while (javaPsiFacade.findPackage(fqn + "Package" + inizio) != null) {
                inizio++;
            }
            i = inizio;
            for (PackageBean toPackage : newPackages) {

                path = fqn + "Package" + i++;
                toPackage.setFullQualifiedName(path);
                classBeanList = toPackage.getClassList();

                path = incopletePath + "/" + toPackage.getFullQualifiedName().substring(toPackage.getFullQualifiedName().lastIndexOf(".") + 1);
                new File(path).mkdir();

                for (ClassBean classToMove : classBeanList) {
                    psiFields = new ArrayList<>();
                    psiMethods = new ArrayList<>();
                    psiInnerClasses = new ArrayList<>();
                    aClass = PsiUtil.getPsi(classToMove, project);
                    // aClass = javaPsiFacade.findClasses(classToMove.getFullQualifiedName(), GlobalSearchScope.allScope(project))[0];

                    for (PsiField field : aClass.getFields()) {
                        psiFields.add(field);
                    }

                    for (PsiMethod m : aClass.getMethods()) {
                        psiMethods.add(m);
                    }

                    for (PsiClass c : aClass.getInnerClasses()) {
                        psiInnerClasses.add(c);
                    }

                    try {
                        //Estraggo completamente la classe per spostarla nel nuovo package
                        ExtractClassProcessor extractClassProcessor = new ExtractClassProcessor(aClass, psiFields, psiMethods, psiInnerClasses, toPackage.getFullQualifiedName(), aClass.getName());
                        extractClassProcessor.run();
                    } catch (Exception e) {

                        throw new PromiscuousPackageException(e.getMessage());
                    }
                }
            }

            File file;
            List<PsiMethod> methodsToMove;
            for (PackageBean toPackage : newPackages) {
                i = 0;
                newPackage = JavaPsiFacade.getInstance(project).findPackage(toPackage.getFullQualifiedName());

                for (PsiClass classe : newPackage.getClasses()) {
                    methodsToMove = new ArrayList<>();
                    methodsToMove.add(classe.getMethods()[0]);
                    ExtractClassProcessor processor = new ExtractClassProcessor(classe, new ArrayList<>(), methodsToMove, new ArrayList<>(), toPackage.getFullQualifiedName(), "classForFixingPromiscuousPackage" + (i + inizio));
                    processor.run();
                    i++;
                }
                i = 0;
                while (i < toPackage.getClassList().size()) {
                    path = incopletePath + "/" + toPackage.getFullQualifiedName().substring(toPackage.getFullQualifiedName().lastIndexOf(".") + 1);
                    path = path.substring(toPackage.getFullQualifiedName().lastIndexOf("/") + 1) + "/classForFixingPromiscuousPackage" + (i + inizio) + ".java";
                    file = new File(path);
                    file.delete();
                    i++;
                }
            }

            //elimino il vecchio package affetto
            file = new File(pathPackage);
            for (File f : file.listFiles()) {
                f.delete();
            }
            if (!file.delete()) {
                Messages.showMessageDialog("Error during delete of original package, pleace delete it manually", "Attention", Messages.getWarningIcon());
            }

        } catch (
                Exception e) {

            throw new PromiscuousPackageException(e.getMessage());

        }

    }

}
