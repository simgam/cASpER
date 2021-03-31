package it.unisa.casper.refactor.strategy;

import it.unisa.casper.refactor.exceptions.SplittingException;
import it.unisa.casper.storage.beans.ClassBean;

import java.util.Collection;

public interface SplittingStrategy {
    Collection<ClassBean> split(ClassBean toSplit, double threshold) throws SplittingException, Exception;
}
