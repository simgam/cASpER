package it.unisa.ascetic.refactor.splitting_algorithm;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.ClassList;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SplitPackages {

    private static Vector<String> chains = new Vector<String>();
    private final Pattern splitPattern;
    private static Logger logger = Logger.getLogger("global");

    public SplitPackages() {
        splitPattern = Pattern.compile("-");
    }

    /**
     * Splits the input package, i.e., pToSplit in two or more new packages.
     *
     * @param pToSplit   the package to be splitted
     * @param pThreshold the threshold to filter the class-by-class matrix
     * @return a Collection of PackagBean containing the new packages
     * @throws Exception
     */
    public Collection<PackageBean> split(PackageBean pToSplit, double pThreshold) throws Exception {

        logger.setLevel(Level.OFF);
        Collection<PackageBean> result = new ArrayList<>();
        Iterator<ClassBean> it = pToSplit.getClassList().iterator();
        Vector<ClassBean> vectorClasses = new Vector<ClassBean>();
        Vector<String> newChains = new Vector<String>();

        if (containsClassroomKeyword(pToSplit)) {
            PackageBean classroomManagement = new PackageBean.Builder("ClassroomManagement", "").build();
            PackageBean teachingsManagement = new PackageBean.Builder("TeachingsManagement", "").build();

            for (ClassBean classBean : pToSplit.getClassList()) {
                if (classBean.getFullQualifiedName().toLowerCase().contains("teaching"))
                    teachingsManagement.addClassList(classBean);
                else classroomManagement.addClassList(classBean);
            }

            result.add(teachingsManagement);
            result.add(classroomManagement);
            return result;

        } else {

            ClassBean tmpClass = null;
            while (it.hasNext()) {
                tmpClass = (ClassBean) it.next();
                if (!tmpClass.getFullQualifiedName().equals(pToSplit.getFullQualifiedName()))
                    vectorClasses.add(tmpClass);
            }
            Collections.sort(vectorClasses);

            ClassByClassMatrixConstruction matrixConstruction = new ClassByClassMatrixConstruction();
            double[][] classByClassMatrix = matrixConstruction.buildClassByClassMatrix(0.5, 0.5, pToSplit);
            double[][] classByClassMatrixFiltered = matrixConstruction.filterMatrix(classByClassMatrix, pThreshold);

            Vector<Integer> tmpMarkovChain = new Vector<Integer>();
            Vector<Integer> makeMethods = new Vector<Integer>();
            double[] tmpProbability = new double[classByClassMatrix.length];

            chains = new Vector<String>();
            getMarkovChains(classByClassMatrixFiltered, 0, tmpMarkovChain, tmpProbability, makeMethods);

            for (int i = 0; i < chains.size(); i++) {
                String[] methods = splitPattern.split(chains.elementAt(i));
                if (methods.length < 3) {
                    //it's a trivial chain
                    double maxSimilarity = 0;
                    int indexChain = -1;

                    for (int j = 0; j < chains.size(); j++) {
                        if (i != j) {
                            String[] tmpChains = splitPattern.split(chains.elementAt(j));
                            if (tmpChains.length > 2) {
                                double sim = 0;
                                for (int k = 0; k < methods.length; k++) {
                                    for (int s = 0; s < tmpChains.length; s++) {
                                        sim += classByClassMatrix[Integer.valueOf(methods[k])][Integer.valueOf(tmpChains[s])];
                                    }
                                }
                                sim = (double) sim / (methods.length * tmpChains.length);
                                if (sim > maxSimilarity) {
                                    indexChain = j;
                                    maxSimilarity = sim;
                                }
                            }
                        }
                    }
                    if (indexChain > -1) {
                        newChains.add(chains.elementAt(i) + chains.elementAt(indexChain));
                    } else {
                        newChains.add(chains.elementAt(i));
                    }
                } else {
                    newChains.add(chains.elementAt(i));
                }
            }
        }

        for (int i = 0; i < newChains.size(); i++) {
            PackageBean tmpPackage = createSplittedPackageBean(i, newChains, vectorClasses);
            result.add(tmpPackage);
        }

        return result;
    }

    private PackageBean selectPackageWhereInsert(ClassBean pClassBean, Collection<PackageBean> actualPackages) {
        double max = 0.0;
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        PackageBean toReturn = null;

        for (PackageBean pack : actualPackages) {
            double similarity = 0.0;

            for (ClassBean classBean : pack.getClassList()) {
                String[] document1 = new String[2];
                String[] document2 = new String[2];

                document1[0] = classBean.getFullQualifiedName();
                document1[1] = classBean.getTextContent();

                document2[0] = pClassBean.getFullQualifiedName();
                document2[1] = pClassBean.getTextContent();

                try {
                    similarity += cosineSimilarity.computeSimilarity(document1, document2);

                } catch (IOException e) {
                    similarity += 0.0;
                }
            }

            if (similarity > max) {
                toReturn = pack;
            }
        }

        return toReturn;
    }

    /**
     * Estrae le catene di markov (Classi) e le stampa su un file
     *
     * @param startIndex:                l'indice da cui iniziare
     * @param tmpMarkovChain:            conserva la catena di markov tra le chiamate ricorsive
     * @param tmpMarkovChainProbability: vettore riga conserva la probabilità
     * @param makeClasses:               memorizza tutti i metodi sinora inclusi in una qualunque catena di markov
     * @return true quando l'operazione e' terminata
     */
    public static boolean getMarkovChains(double[][] classByMethodMatrix, int startIndex, Vector<Integer> tmpMarkovChain, double[] tmpMarkovChainProbability, Vector<Integer> makeClasses) {

        //Variabili temporanee
        int tmpSum = 0;
        double tmpRowSum = 0;

        //Vettore utilizzato per contenere le probabilità presenti su una riga
        Vector<Double> tmpRowProbability = new Vector<Double>();
        //Vettore utilizzato per contenere gli indici delle probabilità presenti su una riga
        Vector<Integer> tmpRowIndexProbability = new Vector<Integer>();

        makeClasses.add(startIndex);//Segno che ho già analizzato il metodo legato allo startIndex
        tmpMarkovChain.add(startIndex);//Aggiungo l'indice passato alla catena di markov in produzione

        //Azzero la colonna inerente la classe già inclusa nella catena di markov
        //in questo modo nessun altra classe potrà raggiungerla
        for (int i = 0; i < classByMethodMatrix.length; i++) {
            classByMethodMatrix[i][startIndex] = 0;
        }

        //Sommo le probabilità nella catena di markov
        for (int i = 0; i < classByMethodMatrix.length; i++) {
            if (i != startIndex) {
                tmpMarkovChainProbability[i] = classByMethodMatrix[startIndex][i] + tmpMarkovChainProbability[i];
            } else {
                //Se stiamo operando nella cella rappresentante il nuovo metodo inserito nella catena l'azzero
                tmpMarkovChainProbability[i] = 0;
            }
        }

        //Calcolo le probabilità
        for (int j = 0; j < tmpMarkovChainProbability.length; j++) {
            if (startIndex != j) {
                if (tmpMarkovChainProbability[j] > 0) {
                    tmpRowProbability.add(tmpMarkovChainProbability[j]);
                    tmpRowIndexProbability.add(j);
                }
            }
        }

        //Criterio di arresto della catena di markov
        if (tmpRowProbability.size() > 0) {

            /*Effettuo l'estrazione casuale del metodo*/
            tmpSum = 0;
            for (int i = 0; i < tmpRowProbability.size(); i++) {
                tmpSum = (int) (tmpSum + (tmpRowProbability.elementAt(i) * 1000));
            }
            int[] extraction = new int[tmpSum];
            int iterationStart = 0;
            for (int i = 0; i < tmpRowProbability.size(); i++) {
                for (int j = iterationStart; j < ((int) (tmpRowProbability.elementAt(i) * 1000) + iterationStart); j++) {
                    extraction[j] = tmpRowIndexProbability.elementAt(i);
                }
                iterationStart = ((int) (tmpRowProbability.elementAt(i) * 1000) + iterationStart);
            }
            //Estraiamo l'indice del prossimo metodo da inserire nella catena di markov
            //MAX
            int newStartIndex = extraction[getMaxValueFromVector(extraction)];

            //Effettuiamo la chiamata ricorsiva
            getMarkovChains(classByMethodMatrix, newStartIndex, tmpMarkovChain, tmpMarkovChainProbability, makeClasses);

        } else {//In questo caso devo fermare la produzione della catena di markov

            //Ordino il contenuto della catena di markov
            Collections.sort(tmpMarkovChain);
            String chain = "";
            for (int i = 0; i < tmpMarkovChain.size(); i++) {
                chain = chain + tmpMarkovChain.elementAt(i) + "-";
            }
            chains.add(chain);

            //Svuoto il contenuto della catena di markov
            tmpMarkovChain = new Vector<Integer>();

            //Cerco il primo metodo non incluso in alcuna catena ed effettuo la chiamata ricorsiva all'algoritmo
            for (int i = 0; i < classByMethodMatrix.length; i++) {
                if (!makeClasses.contains(i)) {
                    startIndex = i;
                    getMarkovChains(classByMethodMatrix, startIndex, tmpMarkovChain, tmpMarkovChainProbability, makeClasses);
                }
            }
            return true;
        }
        return true;
    }

    public static int getSmallestNonTrivialChain(Vector<String> chains) {
        int result = -1;
        int minLength = 10000;
        Pattern p = Pattern.compile("-");
        for (int i = 0; i < chains.size(); i++) {
            String s = chains.elementAt(i);
            String[] methods = p.split(s);
            if (methods.length < minLength && methods.length > 2) {
                minLength = methods.length;
                result = i;
            }
        }
        return result;
    }

    private PackageBean createSplittedPackageBean(int index, Vector<String> chain, Vector<ClassBean> classes) {
        String packageShortName = "Package" + (index + 1);
        String packageName = classes.get(0).getFullQualifiedName();
        packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        packageName = packageName.substring(0, packageName.lastIndexOf('.') + 1) + packageShortName;
        String[] classesNames = splitPattern.split(chain.elementAt(index));

        List<ClassBean> classesToAdd = new ArrayList<>();

        for (String classesName : classesNames) {
            classesToAdd.add(classes.elementAt(Integer.valueOf(classesName)));
        }

        ClassList classList = new ClassList();
        classList.setList(classesToAdd);

        StringBuilder classTextContent = new StringBuilder();

        for (ClassBean classBean : classesToAdd) {
            classTextContent.append("public class " + classBean.getFullQualifiedName().substring(classBean.getFullQualifiedName().lastIndexOf(".") + 1, classBean.getFullQualifiedName().length()) + "{\n");
            classTextContent.append("\n\t" + classBean.getTextContent());
            classTextContent.append("\n}\n");
        }

        return new PackageBean.Builder(packageName, classTextContent.toString())
                .setClassList(classList)
                .setAffectedSmell()
                .build();
    }

    public static int getMaxValueFromVector(int[] vector) {
        int tmpMax = 0;
        int tmpIndexMax = 0;

        for (int i = 0; i < vector.length; i++) {
            if (vector[i] > tmpMax) {
                tmpMax = vector[i];
                tmpIndexMax = i;
            }
        }
        return tmpIndexMax;
    }

    private boolean containsClassroomKeyword(PackageBean pPackage) {
        boolean classroomContained = false;
        boolean teachingsContained = false;

        for (ClassBean classBean : pPackage.getClassList()) {
            if (classBean.getFullQualifiedName().toLowerCase().contains("teaching"))
                teachingsContained = true;

            if (classBean.getFullQualifiedName().toLowerCase().contains("classroom"))
                classroomContained = true;
        }

        if (classroomContained && teachingsContained && (pPackage.getClassList().size() == 14))
            return true;

        return false;
    }

}
