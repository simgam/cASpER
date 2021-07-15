package it.unisa.casper.refactor.splitting_algorithm;

import it.unisa.casper.refactor.exceptions.SplittingException;
import it.unisa.casper.refactor.strategy.SplittingStrategy;
import it.unisa.casper.storage.beans.*;

import java.util.*;
import java.util.regex.Pattern;


public class SplitClasses implements SplittingStrategy{

    private Vector<String> chains = new Vector<String>();
    private final Pattern splitPattern;

    public SplitClasses() {
        splitPattern = Pattern.compile("-");
    }

    /**
     * Splits the input class, i.e., pToSplit, in two or more new classes.
     *
     * @param pToSplit   the class to be splitted
     * @param pThreshold the threshold to filter the method-by-method matrix
     * @return a Collection of ClassBean containing the new classes
     * @throws Exception
     */
    public Collection<ClassBean> split(ClassBean pToSplit, double pThreshold) throws Exception {

        Collection<ClassBean> result = new Vector<>();

        Iterator<MethodBean> it = pToSplit.getMethodList().iterator();
        Vector<MethodBean> vectorMethods = new Vector<>();

        MethodBean tmpMethod = null;
        while (it.hasNext()) {
            tmpMethod = (MethodBean) it.next();
            if (!tmpMethod.getFullQualifiedName().equals(pToSplit.getFullQualifiedName()))
                vectorMethods.add(tmpMethod);
        }
        Collections.sort(vectorMethods);

        MethodByMethodMatrixConstruction matrixConstruction = new MethodByMethodMatrixConstruction();
        double[][] methodByMethodMatrix = matrixConstruction.buildMethodByMethodMatrix(0.5, 0.5, 0.5, pToSplit);
        double[][] methodByMethodMatrixFiltered = matrixConstruction.filterMatrix(methodByMethodMatrix, pThreshold);

        Vector<Integer> tmpMarkovChain = new Vector<Integer>();
        Vector<Integer> makeMethods = new Vector<Integer>();
        double[] tmpProbability = new double[methodByMethodMatrix.length];

        chains = new Vector<String>();
        getMarkovChains(methodByMethodMatrixFiltered, 0, tmpMarkovChain, tmpProbability, makeMethods);

        Vector<String> newChains = new Vector<String>();
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
                                    sim += methodByMethodMatrix[Integer.valueOf(methods[k])][Integer.valueOf(tmpChains[s])];
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

        String packageName = pToSplit.getFullQualifiedName().substring(0, pToSplit.getFullQualifiedName().lastIndexOf('.'));
        for (int i = 0; i < newChains.size(); i++) {
            ClassBean tmpClass = createSplittedClassBean(i, packageName, newChains, vectorMethods, new Vector<>(pToSplit.getInstanceVariablesList()), pToSplit.getBelongingPackage());
            result.add(tmpClass);
        }

        return result;
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

    private ClassBean createSplittedClassBean(int index, String packageName, Vector<String> chain, Vector<MethodBean> methods, Vector<InstanceVariableBean> instanceVariables, PackageBean belongingPackage) {
        String classShortName = "Class_" + (index + 1);
        String tempName = packageName + "." + classShortName;
        String[] methodsNames = splitPattern.split(chain.elementAt(index));

        List<MethodBean> methodsToAdd = new ArrayList<>();

        for (String methodsName : methodsNames) {
            methodsToAdd.add(methods.elementAt(Integer.valueOf(methodsName)));
        }

        Set<InstanceVariableBean> instanceVariableBeanSet = new HashSet<>();

        for (InstanceVariableBean currentInstanceVariable : instanceVariables) {
            for (MethodBean methodToInspect : methodsToAdd) {
                if (methodToInspect.getInstanceVariableList().contains(currentInstanceVariable)) {
                    instanceVariableBeanSet.add(currentInstanceVariable);
                }
            }

        }
        List<InstanceVariableBean> variableBeansToAdd = new ArrayList<>(instanceVariableBeanSet);
        InstanceVariableList instanceVariableList = new InstanceVariableList();
        instanceVariableList.setList(variableBeansToAdd);

        MethodList methodList = new MethodList();
        methodList.setList(methodsToAdd);

        StringBuilder classTextContent = new StringBuilder();
        classTextContent.append("public class ");
        classTextContent.append(classShortName);
        classTextContent.append(" {");

        for (InstanceVariableBean instanceVariableBean : variableBeansToAdd) {
            classTextContent.append(instanceVariableBean.getFullQualifiedName());
            classTextContent.append("\n");
        }

        for (MethodBean methodBean : methodsToAdd) {
            classTextContent.append(methodBean.getTextContent());
            classTextContent.append("\n");
        }

        classTextContent.append("}");

        return new ClassBean.Builder(tempName, classTextContent.toString())
                .setInstanceVariables(instanceVariableList)
                .setMethods(methodList)
                .setImports(new ArrayList<String>())
                .setLOC(0)
                .setBelongingPackage(belongingPackage)
                .setPathToFile("")
                .setEntityClassUsage(0)
                .setAffectedSmell()
                .build();
    }

    /**
     * Estrae le catene di markov (Classi) e le stampa su un file
     *
     * @param startIndex:                l'indice da cui iniziare
     * @param tmpMarkovChain:            conserva la catena di markov tra le chiamate ricorsive
     * @param tmpMarkovChainProbability: vettore riga conserva la probabilità
     * @param makeMethods:               memorizza tutti i metodi sinora inclusi in una qualunque catena di markov
     * @return true quando l'operazione e' terminata
     */
    public boolean getMarkovChains(double[][] methodByMethodMatrix, int startIndex, Vector<Integer> tmpMarkovChain, double[] tmpMarkovChainProbability, Vector<Integer> makeMethods) {

        //Variabili temporanee
        int tmpSum = 0;
        double tmpRowSum = 0;

        //Vettore utilizzato per contenere le probabilità presenti su una riga
        Vector<Double> tmpRowProbability = new Vector<Double>();
        //Vettore utilizzato per contenere gli indici delle probabilità presenti su una riga
        Vector<Integer> tmpRowIndexProbability = new Vector<Integer>();

        makeMethods.add(startIndex);//Segno che ho già analizzato il metodo legato allo startIndex
        tmpMarkovChain.add(startIndex);//Aggiungo l'indice passato alla catena di markov in produzione

        //Azzero la colonna inerente il metodo già incluso nella catena di markov
        //in questo modo nessun altro metodo potrà raggiungerlo
        for (int i = 0; i < methodByMethodMatrix.length; i++) {
            methodByMethodMatrix[i][startIndex] = 0;
        }

        //Sommo le probabilità nella catena di markov
        for (int j = 0; j < methodByMethodMatrix.length; j++) {
            if (j != startIndex) {
                tmpMarkovChainProbability[j] = methodByMethodMatrix[startIndex][j] + tmpMarkovChainProbability[j];
            } else {
                //Se stiamo operando nella cella rappresentante il nuovo metodo inserito nella catena l'azzero
                tmpMarkovChainProbability[j] = 0;
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
            getMarkovChains(methodByMethodMatrix, newStartIndex, tmpMarkovChain, tmpMarkovChainProbability, makeMethods);

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
            for (int i = 0; i < methodByMethodMatrix.length; i++) {
                if (!makeMethods.contains(i)) {
                    startIndex = i;
                    getMarkovChains(methodByMethodMatrix, startIndex, tmpMarkovChain, tmpMarkovChainProbability, makeMethods);
                }
            }
            return true;
        }
        return true;
    }

}
