package it.unisa.casper.analysis.code_smell_detection.strategy;

import it.unisa.casper.storage.beans.PackageBean;

import java.util.HashMap;

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

    /**
     * metodo che ritorna la lista di soglie individuate durante l'analisi
     *
     * @param aPackage package da dare in input allo strategy per l'analisi
     * @return List<Double> lista delle soglie individuate durante l'analisi
     */
    HashMap<String, Double> getThresold(PackageBean aPackage);
}
