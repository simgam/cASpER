package it.unisa.casper.refactor.strategy;


import it.unisa.casper.refactor.exceptions.BlobException;
import it.unisa.casper.refactor.exceptions.FeatureEnvyException;
import it.unisa.casper.refactor.exceptions.MisplacedClassException;
import it.unisa.casper.refactor.exceptions.PromiscuousPackageException;

public class RefactoringManager {
    private RefactoringStrategy refactoringStrategy;

    public RefactoringManager(RefactoringStrategy Rstrategy) {

        this.refactoringStrategy = Rstrategy;
    }

    public void executeRefactor() throws PromiscuousPackageException, BlobException, FeatureEnvyException, MisplacedClassException {
        refactoringStrategy.doRefactor();
    }

}
