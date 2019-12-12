package it.unisa.casper.refactor.splitting_algorithm.irEngine;

import java.util.Collection;

public interface IREngine {

    /**
     * @param documents Collection of document, this collection contains two data:
     *                  <p>
     *                  - [0] name of document
     *                  - [0] content of document
     */
    void generateMatrix(Collection<String[]> documents);

    /**
     * This method return the similarity between two documents
     *
     * @param documentName1 Name of first document
     * @param documentName2 Name of second document
     * @return similarity
     * @throws Exception Document not found
     */
    double getSimilarity(String documentName1, String documentName2) throws Exception;
}
