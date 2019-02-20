package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.PackageSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.PackageBean;

/**
 * Classe astratta figlia di CodeSmell, estendendo questa classe verranno istanziati tutti
 * gli oggetti CodeSmell che riguardano l'ambito dei package
 */

public abstract class PackageLevelCodeSmell extends CodeSmell<PackageBean> {
    /**
     * Costruttore
     *
     * @param name              rappresenta nome dello smell
     * @param detectionStrategy Strategy associato agli smell che riguardano i package
     */
    public PackageLevelCodeSmell(String name, PackageSmellDetectionStrategy detectionStrategy) {
        super(name, detectionStrategy);
    }

    /**
     * Metodo che stabilisce la presenza di uno smell in un package
     *
     * @param aPackage PackageBean da passare allo Strategy per effettuare l'analisi
     * @return true se il package è affetto da smell, false altrimenti
     */
    public abstract boolean affects(PackageBean aPackage);

}

