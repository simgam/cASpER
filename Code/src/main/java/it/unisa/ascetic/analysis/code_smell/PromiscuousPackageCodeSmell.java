package it.unisa.ascetic.analysis.code_smell;

import it.unisa.ascetic.analysis.code_smell_detection.strategy.PackageSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.PackageBean;

/**
 * Classe concreta che istanzia lo smell "Promiscuous Package"
 */
public class PromiscuousPackageCodeSmell extends PackageLevelCodeSmell {

    /**
     * Costruttore
     *
     * @param detectionStrategy Strategy associato al rilevamento di smell che
     *                          riguardano i package. Il nome viene istanziato nella classe padre
     */
    public PromiscuousPackageCodeSmell(PackageSmellDetectionStrategy detectionStrategy) {
        super(PROMISCUOUS_PACKAGE, detectionStrategy);
    }

    /**
     * Metodo che sancisce la presenza di promiscuous package in un package
     *
     * @param aPackage package da passare allo strategy per l'analisi
     * @return true se il package è affetto da promiscuous package, false altrimenti
     */
    public boolean affects(PackageBean aPackage) {
        if (detectionStrategy.isSmelly(aPackage)) {
            aPackage.addSmell(this);
            return true;
        }
        return false;
    }
}
