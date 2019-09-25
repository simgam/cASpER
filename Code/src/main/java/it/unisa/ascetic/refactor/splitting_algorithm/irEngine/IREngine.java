package it.unisa.ascetic.refactor.splitting_algorithm.irEngine;

import java.util.Collection;

public interface IREngine {

	/**
	 * This method generate the Term-Document matrix, for example:
	 * 
	 * Document1: Hi i'm Alessandro
	 * 
	 * Document2: Hi i'm Francesco
	 * 
	 * Matrix:
	 * 
	 * Term 		|Document1 	| Document2
	 * 
	 * 
	 * Hi 			| 1 		| 1
	 * 
	 * i 			| 1 		| 1
	 * 
	 * m 			| 1 		| 1
	 * 
	 * Alessandro	| 1 		| 0
	 * 
	 * Francesco 	| 0 		| 1
	 * 
	 * 
	 * @param documents
	 *            Collection of document, this collection contains two data:
	 *            
	 *            - [0] name of document
	 *            - [0] content of document
	 * @return
	 */
	public void generateMatrix(Collection<String[]> documents);

	/**
	 * This method return the similarity between two documents
	 * 
	 * @param documentName1
	 * 			Name of first document
	 * @param documentName2
	 * 			Name of second document
	 * @return similarity
	 * @throws Exception
	 * 			Document not found
	 */
	public double getSimilarity(String documentName1, String documentName2)  throws Exception;
}
