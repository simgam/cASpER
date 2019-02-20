package it.unisa.ascetic.analysis.code_smell_detection.strategy;

import it.unisa.ascetic.storage.beans.PackageBean;

/**
 * Interfaccia che dichiara i metodi da implementare negli Strategy addetti al rilevamento
 * di smell nei package
 */
public interface PackageSmellDetectionStrategy extends CodeSmellDetectionStrategy<PackageBean> {
    /**
     * metodo che sancisce la presenza di uno smell in un package
     *
     * @param aPackage package da passare allo strategy per effettuare l'analisi
     * @return true se il package è affetto da smell, false altrimenti
     */
    public boolean isSmelly(PackageBean aPackage);

}
