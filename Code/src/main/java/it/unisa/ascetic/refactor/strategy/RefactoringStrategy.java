package it.unisa.ascetic.refactor.strategy;

import it.unisa.ascetic.refactor.exceptions.BlobException;
import it.unisa.ascetic.refactor.exceptions.FeatureEnvyException;
import it.unisa.ascetic.refactor.exceptions.MisplacedClassException;
import it.unisa.ascetic.refactor.exceptions.PromiscuousPackageException;

public interface RefactoringStrategy {
    void doRefactor() throws FeatureEnvyException, MisplacedClassException, PromiscuousPackageException, BlobException;
}
