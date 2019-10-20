package it.unisa.ascetic.refactor.strategy;


import it.unisa.ascetic.refactor.exceptions.BlobException;
import it.unisa.ascetic.refactor.exceptions.FeatureEnvyException;
import it.unisa.ascetic.refactor.exceptions.MisplacedClassException;
import it.unisa.ascetic.refactor.exceptions.PromiscuousPackageException;

public class RefactoringManager {
    private RefactoringStrategy refactoringStrategy;

    public RefactoringManager(RefactoringStrategy Rstrategy) {

        this.refactoringStrategy = Rstrategy;
    }

    public void executeRefactor() throws PromiscuousPackageException, BlobException, FeatureEnvyException, MisplacedClassException {
        refactoringStrategy.doRefactor();
    }

}
