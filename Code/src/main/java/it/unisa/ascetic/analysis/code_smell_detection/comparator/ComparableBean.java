package it.unisa.ascetic.analysis.code_smell_detection.comparator;

/**
 * Interfaccia che presenta il metodo per ottenere la somiglianza di un bean che la implementa
 */
public interface ComparableBean {

    /**
     * getter
     *
     * @return similarity somiglianza del ComparableBean
     */
    public double getSimilarity();

}
