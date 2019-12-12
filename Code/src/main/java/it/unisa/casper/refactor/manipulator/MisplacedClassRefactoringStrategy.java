package it.unisa.casper.refactor.manipulator;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesProcessor;
import com.intellij.refactoring.move.moveClassesOrPackages.SingleSourceRootMoveDestination;
import it.unisa.casper.refactor.exceptions.MisplacedClassException;
import it.unisa.casper.refactor.strategy.RefactoringStrategy;
import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.PackageBean;

import javax.swing.*;

public class MisplacedClassRefactoringStrategy implements RefactoringStrategy {
    private ClassBean classToMove;
    private PackageBean fromPackage, toPackage;
    private Project project;            //Aggiunta variabile d'istanza project

    /**
     * Costruttore di MisplacedClassRefactoringStrategy
     *
     * @param destinationPackage
     * @param classToMove
     */
    public MisplacedClassRefactoringStrategy(ClassBean classToMove, PackageBean destinationPackage, Project project) {  //Aggiunto il parametro project
        this.classToMove = classToMove;
        this.fromPackage = classToMove.getBelongingPackage();
        this.toPackage = destinationPackage;
        this.project = project;

    }

    @Override
    public void doRefactor() throws MisplacedClassException {
        try {

            PsiClass psiClass = PsiUtil.getPsi(classToMove, project);
            PsiPackage destinationPackage = JavaPsiFacade.getInstance(project).findPackage(toPackage.getFullQualifiedName());

            doMoveClass(project, psiClass, destinationPackage);
        } catch (Exception e) {
            throw new MisplacedClassException(e.getMessage());
        }
    }

    private void doMoveClass(Project project, PsiClass sourceClass, PsiPackage destinationPackage) {

        PsiClass[] classesToMove = {sourceClass};
        SingleSourceRootMoveDestination destination = new SingleSourceRootMoveDestination(PackageWrapper.create(destinationPackage), destinationPackage.getDirectories()[0]);
        int i = 0;
        while (i < destinationPackage.getClasses().length && !destinationPackage.getClasses()[i].getName().equals(sourceClass.getName())) {
            i++;
        }
        MoveClassesOrPackagesProcessor processor = new MoveClassesOrPackagesProcessor(project, classesToMove, destination, false, false, null);
        int finalI = i;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                if (finalI < destinationPackage.getClasses().length) {
                    sourceClass.setName(JOptionPane.showInputDialog("Homonymous class present. Enter new name:", sourceClass.getName() + "_2"));
                }
            } catch (Exception e) {
                sourceClass.setName(sourceClass.getName() + "_2");
            }
        });
        processor.run();
    }

}
