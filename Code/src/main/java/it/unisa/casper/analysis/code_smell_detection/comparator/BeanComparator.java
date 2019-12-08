package it.unisa.casper.analysis.code_smell_detection.comparator;

import java.util.Comparator;

/**
 * Classe concreta che fornisce metodo di conparazione tra due bean che implementano interfaccia ComparableBean
 */
public class BeanComparator implements Comparator<ComparableBean> {

    /**
     * @return torna 1 se il primo oggetto ha somiglianza maggiore al secondo
     * torna -1 se il secondo oggetto ha somiglianza maggiore al primo
     * torna 0 se il primo oggetto ha somiglianza uguale al secondo
     */
    @Override
    public int compare(ComparableBean o1, ComparableBean o2) {

        if (o1.getSimilarity() > o2.getSimilarity())
            return 1;
        else if (o2.getSimilarity() > o1.getSimilarity())
            return -1;
        else return 0;
    }
}