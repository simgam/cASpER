package it.unisa.casper.refactor.strategy;

import it.unisa.casper.refactor.exceptions.SplittingException;
import it.unisa.casper.storage.beans.ClassBean;

public class SplittingManager {

    private SplittingStrategy splittingStrategy;

    public SplittingManager(SplittingStrategy splittingStrategy){
        this.splittingStrategy = splittingStrategy;
    }

    public void excuteSplitting(ClassBean toSplit, double threshold) throws SplittingException,Exception
    {
        splittingStrategy.split(toSplit, threshold);
    }
}
