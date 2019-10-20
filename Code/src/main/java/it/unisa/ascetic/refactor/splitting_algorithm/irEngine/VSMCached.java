package it.unisa.ascetic.refactor.splitting_algorithm.irEngine;

import java.util.Collection;

public class VSMCached extends VectorSpaceModel {
    /* This is the instance of my object */
    private static VSMCached instance = null;


    private VSMCached() {

    }

    public static VSMCached getInstance() {
        if (instance == null)
            instance = new VSMCached();
        return instance;
    }

    public static void resetObject() {
        instance = new VSMCached();
    }

    public void generateMatrix(Collection<String[]> documents) {
        if (this.documentsList == null) {
            super.generateMatrix(documents);
        }
    }

}
