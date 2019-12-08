package it.unisa.casper.topic;

import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.MethodBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Estrae i topic, cioè i termini più frequenti, dal contenuto testuale di un PackageBean, un ClassBean o un MethodBean
 *
 * @author Sara Patierno
 * @version 2.0
 */
public class TopicExtracter {

    private ArrayList<Double> occourences = new ArrayList<Double>();
    private ArrayList<Double> values = new ArrayList<Double>();
    private HashMap<String, Integer> termsSet = new HashMap<String, Integer>();
    private TreeMap<String, Integer> topicsMap = new TreeMap<String, Integer>();

    public TopicExtracter() {
    }

    /**
     * Estrae i topic da un PackageBean
     *
     * @param aBean PackageBean da cui estrarre i topic
     * @return TreeMap<String, Integer>
     * con String valore del topic (chiave univoca)
     * e Integer numero di volte che il topic appare nel PackageBean
     */
    public TreeMap<String, Integer> extractTopicFromPackageBean(PackageBean aBean) {
        String textContent = aBean.getTextContent();
        return extractTopicFromText(textContent);
    }

    /**
     * Estrae i topic da un ClassBean
     *
     * @param aBean ClassBean da cui estrarre i topic
     * @return TreeMap<String, Integer>
     * con String valore del topic (chiave univoca)
     * e Integer numero di volte che il topic appare nel ClassBean
     */
    public TreeMap<String, Integer> extractTopicFromClassBean(ClassBean aBean) {
        String textContent = aBean.getTextContent();
        return extractTopicFromText(textContent);
    }

    /**
     * Estrae i topic da un MethodBean
     *
     * @param aBean MethodBean da cui estrarre i topic
     * @return TreeMap<String, Integer>
     * con String valore del topic (chiave univoca)
     * e Integer numero di volte che il topic appare nel MethodBean
     */
    public TreeMap<String, Integer> extractTopicFromMethodBean(MethodBean aBean) {
        String textContent = aBean.getTextContent();
        return extractTopicFromText(textContent);
    }

    /**
     * Estrae i topic da un testo
     *
     * @param textContent contenuto testuale del Bean da cui estrarre i topic
     * @return TreeMap<String, Integer>
     * con String valore del topic (chiave univoca)
     * e Integer numero di volte che il topic appare nel testo
     */
    private TreeMap<String, Integer> extractTopicFromText(String textContent) {
        Collection<String> terms = this.termsExtracter(textContent);
        terms = this.deleteSpaces(terms);
        terms = this.deleteNumbers(terms);
        terms = this.splitCamelCaseWords(terms);
        terms = (ArrayList<String>) this.stemming(terms);
        terms = (ArrayList<String>) this.lowerCase(terms);
        terms = (ArrayList<String>) this.deleteTrivialWords(terms);
        occourences = new ArrayList<Double>();
        values = new ArrayList<Double>();
        for (String term : terms) {
            if (!termsSet.containsKey(term)) {
                termsSet.put(term, 0);
                for (String s : terms) {
                    if (term.equals(s)) {
                        int value = termsSet.get(term) + 1;
                        termsSet.put(term, value);
                    }
                }
            }
        }
        double total = 0;
        int calculateMax = 0;
        int numTerms = termsSet.size();
        while ((calculateMax != 5) && (numTerms > calculateMax)) {
            String key = max(termsSet);
            int value = termsSet.get(key);
            topicsMap.put(key, value);
            this.values.add((double) termsSet.get(key));
            total += (double) termsSet.get(key);
            termsSet.remove(key);
            calculateMax++;
        }
        for (int j = 0; j < this.values.size(); j++) {
            this.occourences.add(this.values.get(j) / total);
        }
        return topicsMap;
    }

    /**
     * Estrae tutte le parole di un testo
     *
     * @param textContent testo da cui estrarre i termini
     * @return Collection<String> di tutti i termini estratti
     */
    public Collection<String> termsExtracter(String textContent) {
        textContent = deleteComments(textContent);
        char[] charText = textContent.toCharArray();
        String term = "";
        ArrayList<String> terms = new ArrayList<String>();

        for (int i = 0; i < charText.length; i++) {
            if (this.isPuntuaction(charText[i])) {
                if (!term.equals("")) {
                    terms.add(term);
                }
                term = "";
            } else if (charText[i] != ' ') {
                term += charText[i];
            }
        }
        return terms;
    }

    /**
     * Restituisce la parola che ha frequenza maggiore
     *
     * @param pTermsSet HashMap<String, Integer> dove String è il termine e Integer è il numero di volte che il termine appare
     * @return String che contiene il termine con frequenza maggiore
     */
    private String max(HashMap<String, Integer> pTermsSet) {
        String term = "";
        int maxValue = 0;
        for (String key : pTermsSet.keySet()) {
            int value = pTermsSet.get(key);
            if (value > maxValue) {
                maxValue = value;
                term = key;
            }
        }
        return term;

    }

    /**
     * Rimuove i commenti dal testo
     *
     * @param pTextContent testo da cui eliminare i commenti
     * @return String testo senza commenti
     */
    public String deleteComments(String pTextContent) {
        char[] charText = pTextContent.toCharArray();
        String textWithoutComments = "";
        for (int i = 0; i < charText.length; i++) {
            if ((charText[i] == '/') && (charText[i + 1] == '*')) {
                i = i + 2;
                while (!((charText[i] == '*') && (charText[i + 1] == '/'))) {
                    i++;
                }
                i++;
            } else if ((charText[i] == '/') && (charText[i + 1] == '/')) {
                while (charText[i] != '\n') {
                    i++;
                    if (i == charText.length) break;
                }
            } else textWithoutComments += charText[i];
        }
        return textWithoutComments;
    }

    /**
     * Verifica se un carattere sia un simbolo
     *
     * @param pCharacter carattere da controllare
     * @return boolean true se il carattere controllato è un simbolo, false altrimenti
     */
    private boolean isPuntuaction(char pCharacter) {
        if ((pCharacter != ' ') && (pCharacter != ';') && (pCharacter != '.') && (pCharacter != '[') &&
                (pCharacter != ']') && (pCharacter != '{') && (pCharacter != '}') && (pCharacter != '<') &&
                (pCharacter != '>') && (pCharacter != '+') && (pCharacter != '-') && (pCharacter != '*') &&
                (pCharacter != ':') && (pCharacter != '/') && (pCharacter != '!') && (pCharacter != '"') &&
                (pCharacter != '$') && (pCharacter != '%') && (pCharacter != '(') && (pCharacter != ')') &&
                (pCharacter != '=') && (pCharacter != '^') && (pCharacter != '?') && (pCharacter != '&') &&
                (pCharacter != '|') && (pCharacter != '~') && (pCharacter != ',') && (pCharacter != '	') &&
                (pCharacter != '\'') && (pCharacter != '\n') && (pCharacter != '\t') && (pCharacter != '\b') &&
                (pCharacter != '\r') && (pCharacter != '\f')) return false;
        return true;
    }

    /**
     * Rimuove i numeri dal testo
     *
     * @param pTerms Collection<String> di termini estratti
     * @return Collection<String> dei termini estratti con numeri rimossi
     */
    private Collection<String> deleteNumbers(Collection<String> pTerms) {
        ArrayList<String> terms = new ArrayList<String>();
        for (String s : pTerms) {
            if (!this.isNumber(s)) {
                terms.add(s);
            }
        }
        return terms;
    }

    /**
     * Verifica se una parola sia un numero
     *
     * @param pIstruction carattere da controllare
     * @return boolean true se il carattere controllato è un numero, false altrimenti
     */
    private boolean isNumber(String pIstruction) {
        try {
            Integer.parseInt(pIstruction);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Rimuove le keywords dal testo
     *
     * @param pWords Collection<String> di termini estratti
     * @return Collection<String> dei termini estratti con rimozione di keywords
     */
    private Collection<String> stemming(Collection<String> pWords) {
        ArrayList<String> effectiveWords = new ArrayList<String>();
        for (String s : pWords) {
            if (!this.isKeyword(s)) {
                effectiveWords.add(s);
            }
        }
        return effectiveWords;
    }

    /**
     * Verifica se la parola sia una keyword
     *
     * @param pWord String parola da controllare
     * @return boolean true se la parola controllate è una keyword, false altrimenti
     */
    private boolean isKeyword(String pWord) {
        if ((pWord.equalsIgnoreCase("for")) || (pWord.equalsIgnoreCase("if")) || (pWord.equalsIgnoreCase("while")) || (pWord.equalsIgnoreCase("else")) ||
                (pWord.equalsIgnoreCase("continue")) || (pWord.equalsIgnoreCase("int")) || (pWord.equalsIgnoreCase("String")) || (pWord.equalsIgnoreCase("float")) ||
                (pWord.equalsIgnoreCase("double")) || (pWord.equalsIgnoreCase("return")) || (pWord.equalsIgnoreCase("false")) || (pWord.equalsIgnoreCase("true")) ||
                (pWord.equalsIgnoreCase("char")) || (pWord.equalsIgnoreCase("boolean")) || (pWord.equalsIgnoreCase("public")) || (pWord.equalsIgnoreCase("private")) ||
                (pWord.equalsIgnoreCase("protected")) || (pWord.equalsIgnoreCase("class")) || (pWord.equalsIgnoreCase("Integer")) || (pWord.equalsIgnoreCase("Double")) ||
                (pWord.equalsIgnoreCase("Boolean")) || (pWord.equalsIgnoreCase("Character")) || (pWord.equalsIgnoreCase("import")) || (pWord.equalsIgnoreCase("package")) ||
                (pWord.equalsIgnoreCase("null")) || (pWord.equalsIgnoreCase("this")) || (pWord.equalsIgnoreCase("Float")) || (pWord.equalsIgnoreCase("abstract")) ||
                (pWord.equalsIgnoreCase("new")) || (pWord.equalsIgnoreCase("switch")) || (pWord.equalsIgnoreCase("assert")) || (pWord.equalsIgnoreCase("default")) ||
                (pWord.equalsIgnoreCase("goto")) || (pWord.equalsIgnoreCase("synchronized")) || (pWord.equalsIgnoreCase("do")) || (pWord.equalsIgnoreCase("break")) ||
                (pWord.equalsIgnoreCase("implements")) || (pWord.equalsIgnoreCase("throw")) || (pWord.equalsIgnoreCase("byte")) || (pWord.equalsIgnoreCase("throws")) ||
                (pWord.equalsIgnoreCase("case")) || (pWord.equalsIgnoreCase("enum")) || (pWord.equalsIgnoreCase("instanceof")) || (pWord.equalsIgnoreCase("transient")) ||
                (pWord.equalsIgnoreCase("catch")) || (pWord.equalsIgnoreCase("extends")) || (pWord.equalsIgnoreCase("short")) || (pWord.equalsIgnoreCase("try")) ||
                (pWord.equalsIgnoreCase("final")) || (pWord.equalsIgnoreCase("interface")) || (pWord.equalsIgnoreCase("statico")) || (pWord.equalsIgnoreCase("void")) ||
                (pWord.equalsIgnoreCase("finally")) || (pWord.equalsIgnoreCase("long")) || (pWord.equalsIgnoreCase("strictfp")) || (pWord.equalsIgnoreCase("volatile")) ||
                (pWord.equalsIgnoreCase("const")) || (pWord.equalsIgnoreCase("native")) || (pWord.equalsIgnoreCase("super")) || (pWord.equalsIgnoreCase("Long")) ||
                (pWord.equalsIgnoreCase("Byte")) || (pWord.equalsIgnoreCase("Short")) || (pWord.equalsIgnoreCase("exception")) || (pWord.equalsIgnoreCase("org")) ||
                (pWord.equalsIgnoreCase("eclipse")) || (pWord.equalsIgnoreCase("FileNotFoundException")) || (pWord.equalsIgnoreCase("IOException")) ||
                (pWord.equalsIgnoreCase("NotValidValueException")) || (pWord.equalsIgnoreCase("Object")) || (pWord.equalsIgnoreCase("println") || (pWord.equalsIgnoreCase("System")) ||
                (pWord.equalsIgnoreCase("out")) || (pWord.equalsIgnoreCase("JOptionPane")) || (pWord.equalsIgnoreCase("equalsIgnoreCase")) || (pWord.equalsIgnoreCase("equalsIgnoreCaseIgnoreCase")) ||
                (pWord.equalsIgnoreCase("ArrayList")) || (pWord.equalsIgnoreCase("List")) || (pWord.equalsIgnoreCase("Vector")) || (pWord.equalsIgnoreCase("Set")) || (pWord.equalsIgnoreCase("HashMap")) ||
                (pWord.equalsIgnoreCase("Map")) || (pWord.equalsIgnoreCase("SortedSet")) || (pWord.equalsIgnoreCase("SortedMap")) || (pWord.equalsIgnoreCase("TreeSet")) ||
                (pWord.equalsIgnoreCase("LinkedList")) || (pWord.equalsIgnoreCase("TreeMap")) || (pWord.equalsIgnoreCase("Properties")) || (pWord.equalsIgnoreCase("Stack")) ||
                (pWord.equalsIgnoreCase("Hashtable")) || (pWord.equalsIgnoreCase("Iterator")) || (pWord.equalsIgnoreCase("AbstractCollection")) || (pWord.equalsIgnoreCase("ListIterator")) ||
                (pWord.equalsIgnoreCase("Properties")) || (pWord.equalsIgnoreCase("AbstractSequentialList")) || (pWord.equalsIgnoreCase("AbstractMap")) || (pWord.equalsIgnoreCase("Comparable")) ||
                (pWord.equalsIgnoreCase("BigDecimal")) || (pWord.equalsIgnoreCase("BigInteger")) || (pWord.equalsIgnoreCase("CollationKey")) || (pWord.equalsIgnoreCase("ObjectStreamField")) ||
                (pWord.equalsIgnoreCase("Date")) || (pWord.equalsIgnoreCase("Collator")) || (pWord.equalsIgnoreCase("Comparator")) || (pWord.equalsIgnoreCase("java")) || (pWord.equalsIgnoreCase("util")) ||
                (pWord.equalsIgnoreCase("arraycopy")) || (pWord.equalsIgnoreCase("Enumeration")) || (pWord.equalsIgnoreCase("Collections")) || (pWord.equalsIgnoreCase("Dimension")) ||
                (pWord.equalsIgnoreCase("Arrays")) || (pWord.equalsIgnoreCase("SortedListModel")) || (pWord.equalsIgnoreCase("connection")) || (pWord.equalsIgnoreCase("get")) ||
                (pWord.equalsIgnoreCase("set")) || (pWord.equalsIgnoreCase("is")) || (pWord.equalsIgnoreCase("main")) || (pWord.equalsIgnoreCase("args")) || (pWord.equalsIgnoreCase("argv")))) {
            return true;
        } else if (pWord.contains("Exception")) return true;
        return false;
    }

    /**
     * Rimuove gli spazi fra le parole del testo
     *
     * @param pTerms Collection<String> delle parole estratte
     * @return Collection<String> delle parole estratte con rimozione degli spazi fra una parola e l'altra
     */
    private Collection<String> deleteSpaces(Collection<String> pTerms) {
        ArrayList<String> terms = new ArrayList<String>();

        for (String s : pTerms) {
            terms.add(s.replace(" ", ""));
			/*if(!s.equals(" ")){
				char[] charTerm=s.toCharArray();
				for(int i=0; i<charTerm.length;i++){
					if((charTerm[i]!=' ')){
						term+=charTerm[i];
					}
				}
			terms.add(term);
			term="";
			}*/
        }
        return terms;
    }

    /**
     * Trasforma in lower case le parole del testo se necessario
     *
     * @param pTerms Collection<String> delle parole estratte
     * @return Collection<String> contiene le parole estratte trasformate in minuscolo
     */
    private Collection<String> lowerCase(Collection<String> pTerms) {
        Collection<String> lowerCaseTerms = new ArrayList<String>();
        for (String s : pTerms) {
            lowerCaseTerms.add(s.toLowerCase());
        }
        return lowerCaseTerms;
    }

    /**
     * Aggiunge spazi nelle parole scritte con camel case e crea una collezione contenente tali parole
     *
     * @param pTerms Collection<String> delle parole estratte
     * @return Collection<String> contiene le parole in camel case a cui sono stati aggiunti spazi
     */
    private Collection<String> splitCamelCaseWords(Collection<String> pTerms) {
        Collection<String> noCamelCaseWords = new ArrayList<String>();
        for (String s : pTerms) {
            String[] splittedString = this.splitCamelCase(s).split(" ");
            for (String splitted : splittedString) {
                noCamelCaseWords.add(splitted);
            }
        }
        return noCamelCaseWords;
    }

    /**
     * Aggiunge gli spazi nelle stringhe scritte con camel case
     *
     * @param s String stringa in camel case
     * @return String stringa con aggiunta di spazi fra le parole in camel case
     */
    private String splitCamelCase(String s) {
        return s.replaceAll(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
    }

    /**
     * Rimuove le parole banali, composte da una sola lettera
     *
     * @param pTerms Collection<String> delle parole estratte
     * @return ArrayList<String> di parole con lunghezza superiore a uno
     */
    private ArrayList<String> deleteTrivialWords(Collection<String> pTerms) {
        ArrayList<String> newTerms = new ArrayList<String>();
        for (String s : pTerms) {
            if (s.length() == 1) {
                // do nothing
            } else {
                newTerms.add(s);
            }
        }
        return newTerms;
    }

    /**
     * Rimuove spazi e separa le stringhe con camel case
     *
     * @param pTerms Collection<String> delle parole estratte
     * @return Collection<String> di stringhe senza camel case, in lower case, e senza keyword
     */
    private Collection<String> filter(Collection<String> pTerms) {
        ArrayList<String> terms = new ArrayList<String>();
        for (String term : pTerms) {
            term = term.replace(" ", "");
            String[] splittedString = this.splitCamelCase(term).split(" ");
            for (String splitted : splittedString) {
                if (splitted.length() > 1 && !this.isKeyword(splitted))
                    terms.add(splitted.toLowerCase());
            }
        }
        return terms;
    }

    /**
     * @return occorrenze di una parola
     */
    public Collection<Double> getOccourences() {
        return this.occourences;
    }

    /**
     * @return termini estratti dal contenuto testuale del Bean
     */
    public HashMap<String, Integer> getTermsSet() {
        return termsSet;
    }

    /**
     * @param termsSet termini estratti dal contenuto testuale del Bean
     */
    public void setTermsSet(HashMap<String, Integer> termsSet) {
        this.termsSet = termsSet;
    }

    /**
     * @return cinque termini più frequenti nel contenuto testuale del Bean
     */
    public TreeMap<String, Integer> getTopicsMap() {
        return topicsMap;
    }

    /**
     * @param topicsMap cinque termini più frequenti nel contenuto testuale del Bean
     */
    public void setTopicsMap(TreeMap<String, Integer> topicsMap) {
        this.topicsMap = topicsMap;
    }
}
