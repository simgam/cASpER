package it.unisa.casper.refactor.strategy;

import it.unisa.casper.refactor.exceptions.SplittingException;
import it.unisa.casper.storage.beans.ClassBean;

import java.util.Collection;
import java.util.List;

public class SplittingManager {

    private SplittingStrategy splittingStrategy;

    public SplittingManager(SplittingStrategy splittingStrategy){
        this.splittingStrategy = splittingStrategy;
    }

    public Collection<ClassBean> excuteSplitting(ClassBean toSplit, double threshold) throws SplittingException,Exception
    {
        return splittingStrategy.split(toSplit, threshold);
    }
}
