package it.unisa.ascetic.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.extractclass.ExtractClassProcessor;
import it.unisa.ascetic.refactor.exceptions.PromiscuousPackageException;
import it.unisa.ascetic.refactor.strategy.RefactoringStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Promiscuous Package Refactor Strategy (PP Refactor) è la classe che si occupa del Refactor degli smells di tipo Promiscuous Package
 */

public class PromiscuousPackageRefactoringStrategy implements RefactoringStrategy {
    Logger logger = Logger.getLogger(this.getClass().getName());

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
     * @throws PromiscuousPackageException
     */
    @Override
    public void doRefactor() throws PromiscuousPackageException {
        classExtract();
    }

    private void classExtract() {
        //Creo il javaPsiFacade per trovare i psiClass
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);

        List<PackageBean> newPackagesList = new ArrayList<>();

        //Inizializzo la lista con i packageBean di destinazione
        int count = 0;
        while (count < newPackages.size()) {
            newPackagesList.add(newPackages.get(count));
            count++;
        }

        for (int i = 0; i < newPackagesList.size(); i++) {

            List<ClassBean> classBeanList = newPackagesList.get(i).getClassList();

            for (int j = 0; j < classBeanList.size(); j++) {
                PsiClass aClass = javaPsiFacade.findClass(classBeanList.get(j).getFullQualifiedName(), GlobalSearchScope.allScope(project));
                List<PsiField> psiFields = new ArrayList<>();
                List<PsiMethod> psiMethods = new ArrayList<>();
                List<PsiClass> psiInnerClasses = new ArrayList<>();

                for (int k = 0; k < aClass.getFields().length; k++) {
                    psiFields.add(aClass.getFields()[k]);
                }
                for (int k = 0; k < aClass.getMethods().length; k++) {
                    psiMethods.add(aClass.getMethods()[k]);
                }
                for (int k = 0; k < aClass.getInnerClasses().length; k++) {
                    psiInnerClasses.add(aClass.getInnerClasses()[k]);
                }

                //Estraggo completamente la classe per spostarla nel nuovo package
                ExtractClassProcessor extractClassProcessor = new ExtractClassProcessor(aClass, psiFields, psiMethods,
                        psiInnerClasses, newPackagesList.get(i).getFullQualifiedName(), aClass.getName());
                extractClassProcessor.run();

                //Elimino il file contenente la classe da cui abbiamo fatto l'extract
                aClass.getContainingFile().delete();

            }
        }


    }
}
