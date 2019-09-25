package it.unisa.ascetic.refactor.splitting_algorithm.irEngine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

public class VectorSpaceModel implements IREngine {

    private static Logger logger = Logger.getLogger("global");

    /* The documents name */
    Map<String, Map<Integer, Double>> documentsList;

    /* Collection for all terms of all documents */
    Map<String, Integer> terms;

    /* Configuration loader */
    ConfigLoader conf = null;

    public VectorSpaceModel() {
        conf = new ConfigLoader();
        steamer = Stemmer.getInstance();
    }


    /* the steamer */
    Stemmer steamer = null;

    private Collection<String> badWordsCollection = null;

    /**
     * This method check and count the occurrence for the past term
     *
     * @return array with the occurrence for every document
     */
    private double[] countOccurrence(Map<String, Integer> terms, String document) {
        double[] occurrence = new double[terms.size()];
        String[] words = document.split(" ");
        int i = 0;
        for (String term : terms.keySet()) {

            for (int j = 0; j < words.length; j++) {
                if (words[j].equals(term) == true) {
                    occurrence[i]++;
                }
            }

            i++;
        }
        return occurrence;
    }

    /**
     * This method parse the normalized documents and extract from it all the
     * terms
     */
    private Map<String, Integer> extractTerms(String[] document) {
        if (badWordsCollection == null) {
            /*
             * read bad word from config file
             */
            //String badWords = steamer
            //.stemString(conf.getProperties("BadWords"));

            String badWords = conf.getProperties("BadWords");
            String[] badWordsArray = badWords.split(",");

            badWordsCollection = new Vector<String>();
            /*
             * transform the bad words in a collection
             */
            for (String badWord : badWordsArray) {
                badWordsCollection.add(badWord);
                //logger.severe(badWord);
            }
        }

        Map<String, Integer> words = new HashMap<String, Integer>();
        String[] tmpTerms;
        /* Insert all terms by all documents in just one collection */

        tmpTerms = document[1].split(" ");
        for (String tmpTerm : tmpTerms) {
            if ((badWordsCollection.contains(tmpTerm) == false)) {
                if (tmpTerm.length() > 1) {
                    // if (tmpTerm.equals("") == false) {

                    if (terms.containsKey(tmpTerm) == false) {
                        terms.put(tmpTerm, terms.size());
                    }

                    if (words.containsKey(tmpTerm) == false) {
                        words.put(tmpTerm, terms.get(tmpTerm));
                    }

                }
            }
        }
        return words;
    }

    @Override
    public void generateMatrix(Collection<String[]> documents) {
        documentsList = new HashMap<String, Map<Integer, Double>>();
        this.terms = new HashMap<String, Integer>();
        Map<String, Integer> terms;
        double[] occurrence;
        for (String[] document : documents) {
            /*
             * Normalize the document
             */
            document = normalize(document);

            /*
             * Extract the terms from current documents, the method returns the
             * map<String, integer> the string is the word and the integer is
             * the index for it
             */
            terms = extractTerms(document);

            //logger.severe("Termini Estratti!");

            /*
             * count the occurrence for everyone words in this document
             */
            occurrence = countOccurrence(terms, document[1]);

            //logger.severe("Occorrenze Contate");

            /*
             * Insert the current document in to matrix
             */
            Map<Integer, Double> occurrenceMap = new HashMap<Integer, Double>();
            int j = 0;
            for (Integer term : terms.values()) {
                occurrenceMap.put(term, occurrence[j]);
                j++;
            }

            documentsList.put(document[0], occurrenceMap);
        }
        /* set the TD-IDF */
        // wheight();
    }

    /**
     * This method return the cosine from two multidimensional vectors
     *
     * @param vector1
     * @param vector2
     * @return
     */
    private double getCosine(Map<Integer, Double> vector1,
                             Map<Integer, Double> vector2) {

        double numerator = 0, denominatorPart1 = 0, denominatorPart2 = 0;

        for (Integer key : vector1.keySet()) {
            if (vector2.containsKey(key))
                numerator = numerator + (vector1.get(key) * vector2.get(key));
            denominatorPart1 = denominatorPart1 + Math.pow(vector1.get(key), 2);
        }
        for (Integer key : vector2.keySet()) {
            denominatorPart2 = denominatorPart2 + Math.pow(vector2.get(key), 2);
        }

        denominatorPart1 = Math.sqrt(denominatorPart1);
        denominatorPart2 = Math.sqrt(denominatorPart2);
        if (denominatorPart1 * denominatorPart2 == 0)
            return 0;
        double similarity = numerator / (denominatorPart1 * denominatorPart2);

        if (similarity > 1.0)
            similarity = 1.0;
        return similarity;
    }

    @Override
    public double getSimilarity(String documentName1, String documentName2)
            throws Exception {

        Map<Integer, Double> vector1 = documentsList.get(documentName1);
        Map<Integer, Double> vector2 = documentsList.get(documentName2);

        return getCosine(vector1, vector2);
    }

    /**
     * This method normalize the documents, delete from documents all deny word,
     * or symbol and transform it in lower case.
     *
     * @return the collection of normalized documents
     */
    private String[] normalize(String[] document) {
        String badChars = conf.getProperties("BadChars");
        String[] badCharsArray = badChars.split(",");

        document[1] = splitCamelCase(document[1]);
        document[1] = document[1].toLowerCase();

        /* Delete all bad char */
        document[1] = document[1].replace(",", " ");
        for (String badChar : badCharsArray) {
            document[1] = document[1].replace(badChar, " ");
        }

        document[1] = " " + document[1] + " ";
        /* Delete all consecutive blank space */
        document[1] = document[1].replaceAll(" {2,}", " ");

        return document;
    }

    private String splitCamelCase(String s) {
        return s.replaceAll(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
    }

    /**
     * This method set the weight TF-IDF for all terms in our matrix Term-Doc
     */
    // private void wheight() {
    // double[] numberOfWords;
    // //double numberOfDocuments = documentsList.size();
    // double[] numberOfDocumentsCointainTerm;
    // int i = 0;
    //
    // numberOfDocumentsCointainTerm = new double[terms.size()];
    //
    // /*
    // * Count the number of words for every one document
    // */
    // numberOfWords = new double[documentsList.size()];
    // for (Map<Integer, Double> document : documentsList.values()) {
    // numberOfWords[i] = 0.0;
    // for (double occ : document.values()) {
    // numberOfWords[i] += occ;
    // }
    // i++;
    // }
    //
    // /*
    // * calc the TF
    // */
    // i = 0;
    // for (Map<Integer, Double> document : documentsList.values()) {
    // for (int term : document.keySet()) {
    // document.put(term, document.get(term) / numberOfWords[i]);
    // numberOfDocumentsCointainTerm[term]++;
    // }
    // i++;
    // }
    //
    // /*
    // * Calc the IDF
    // */
    // /*
    // i = 0;
    // for (Map<Integer, Double> document : documentsList.values()) {
    // for (int term : document.keySet()) {
    // double idf = numberOfDocuments / numberOfDocumentsCointainTerm[term];
    // document.put(term, document.get(term) * idf);
    // }
    // i++;
    // }
    // */
    // }
}
