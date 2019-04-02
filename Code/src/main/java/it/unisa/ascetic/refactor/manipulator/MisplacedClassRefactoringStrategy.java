package it.unisa.ascetic.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesProcessor;
import com.intellij.refactoring.move.moveClassesOrPackages.SingleSourceRootMoveDestination;
import it.unisa.ascetic.refactor.exceptions.MisplacedClassException;
import it.unisa.ascetic.refactor.strategy.RefactoringStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.util.logging.Logger;

public class MisplacedClassRefactoringStrategy implements RefactoringStrategy {
    Logger logger = Logger.getLogger(this.getClass().getName());
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
        logger.severe("Oggetto MC_STGY creaton\n");
        this.classToMove = classToMove;
        this.fromPackage = classToMove.getBelongingPackage();
        this.toPackage = destinationPackage;
        this.project = project;

    }

    @Override
    public void doRefactor() throws MisplacedClassException {
        moveClass();
    }

    /**
     * Metodo che di occupa di implementare mediante chiamate alla repository lo spostamento della classe dal package sorgente a quello di destinazione
     *
     * @throws MisplacedClassException
     */
    private void moveClass() throws MisplacedClassException {
        logger.severe("inizio lo spostamento");
        PsiPackage destinaPackage = JavaPsiFacade.getInstance(project).findPackage(toPackage.getFullQualifiedName());


        PsiClass sourceClass = JavaPsiFacade.getInstance(project).findClass(classToMove.getFullQualifiedName(), GlobalSearchScope.everythingScope(project));
        PsiPackage destinationPackage = JavaPsiFacade.getInstance(project).findPackage(toPackage.getFullQualifiedName());

        doMoveClass(project, sourceClass, destinationPackage);
    }

    private void doMoveClass(Project project, PsiClass sourceClass, PsiPackage destinationPackage) {

        PsiClass[] classesToMove = {sourceClass};
        SingleSourceRootMoveDestination destination = new SingleSourceRootMoveDestination(PackageWrapper.create(destinationPackage), destinationPackage.getDirectories()[0]);

        MoveClassesOrPackagesProcessor processor = new MoveClassesOrPackagesProcessor(project, classesToMove, destination, false, false, null);
        processor.run();
    }

}
