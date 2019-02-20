package it.unisa.ascetic.refactor.strategy;

import it.unisa.ascetic.refactor.exceptions.*;

public interface RefactoringStrategy{
    void doRefactor() throws  FeatureEnvyException, MisplacedClassException, PromiscuousPackageException, BlobException;
}
