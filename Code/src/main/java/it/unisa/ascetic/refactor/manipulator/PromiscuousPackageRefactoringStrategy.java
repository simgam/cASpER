package it.unisa.ascetic.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.extractclass.ExtractClassProcessor;
import it.unisa.ascetic.refactor.exceptions.PromiscuousPackageException;
import it.unisa.ascetic.refactor.strategy.RefactoringStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Promiscuous Package Refactor Strategy (PP Refactor) è la classe che si occupa del Refactor degli smells di tipo Promiscuous Package
 */

public class PromiscuousPackageRefactoringStrategy implements RefactoringStrategy {
    Logger logger = Logger.getLogger("global");

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
        logger.severe("oggetto PP_STGY creato\n");
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
        String pathPackage = packageBeanSource.getClassList().get(0).getPathToFile();
        try {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiMethod m;

            List<Boolean> contructVuoto = new ArrayList<Boolean>();
            int i = 0;

            for (PackageBean toPackage : newPackages) {

                List<ClassBean> classBeanList = toPackage.getClassList();

                String path = pathPackage.substring(0, pathPackage.lastIndexOf('/'));
                path = path + "/" + toPackage.getFullQualifiedName().substring(toPackage.getFullQualifiedName().lastIndexOf(".") + 1);
                new File(path).mkdir();
                PsiClass aClass;
                List<PsiField> psiFields;
                List<PsiMethod> psiMethods;
                List<PsiClass> psiInnerClasses;

                for (ClassBean classToMove : classBeanList) {
                    contructVuoto.add(false);
                    psiFields = new ArrayList<>();
                    psiMethods = new ArrayList<>();
                    psiInnerClasses = new ArrayList<>();
                    aClass = javaPsiFacade.findClass(classToMove.getFullQualifiedName(), GlobalSearchScope.allScope(project));

                    for (int k = 0; k < aClass.getFields().length; k++) {
                        psiFields.add(aClass.getFields()[k]);
                    }
                    for (int k = 0; k < aClass.getMethods().length; k++) {
                        psiMethods.add(aClass.getMethods()[k]);
                        m = aClass.getMethods()[k];
                        if (m.isConstructor() && m.hasParameters()) {
                            contructVuoto.set(i, true);
                        }
                    }

                    for (int k = 0; k < aClass.getInnerClasses().length; k++) {
                        psiInnerClasses.add(aClass.getInnerClasses()[k]);
                    }

                    //Estraggo completamente la classe per spostarla nel nuovo package
                    ExtractClassProcessor extractClassProcessor = new ExtractClassProcessor(aClass, psiFields, psiMethods, psiInnerClasses, toPackage.getFullQualifiedName(), aClass.getName());
                    extractClassProcessor.run();

                    i++;
                }
            }

            //elimino il vecchio package affetto
            File file = new File(pathPackage);

            for (File f : file.listFiles()) {
                f.delete();
            }
            if (!file.delete()) {
                Messages.showMessageDialog("Error during delete of original package, pleace delete it manually", "Attention", Messages.getInformationIcon());
            }

//            i = 0;
//
//            for (PackageBean toPackage : newPackages) {
//                PsiPackage newPackage = JavaPsiFacade.getInstance(project).findPackage(toPackage.getFullQualifiedName());
//                for (PsiClass classe : newPackage.getClasses()) {
//                    System.out.println(contructVuoto.get(i));
//                    if (contructVuoto.get(i)) {
//
//                        for (PsiMethod c : classe.getMethods()) {
//                            System.out.println(c.getName());
//                            if (c.isConstructor() && c.hasParameters()) {
//                                System.out.println(c.getName() + " tolto");
//                                c.delete();
//                            }
//                        }
//                    }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new PromiscuousPackageException(e.getMessage());

        }

    }

}
