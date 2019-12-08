package it.unisa.casper.refactor.strategy;

import it.unisa.casper.refactor.exceptions.BlobException;
import it.unisa.casper.refactor.exceptions.FeatureEnvyException;
import it.unisa.casper.refactor.exceptions.MisplacedClassException;
import it.unisa.casper.refactor.exceptions.PromiscuousPackageException;

public interface RefactoringStrategy {
    void doRefactor() throws FeatureEnvyException, MisplacedClassException, PromiscuousPackageException, BlobException;
}
