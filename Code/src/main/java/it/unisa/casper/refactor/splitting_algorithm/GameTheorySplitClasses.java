package it.unisa.casper.refactor.splitting_algorithm;


import it.unisa.casper.refactor.exceptions.SplittingException;
import it.unisa.casper.refactor.splitting_algorithm.MethodByMethodMatrixConstruction;
import it.unisa.casper.refactor.strategy.SplittingStrategy;
import it.unisa.casper.storage.beans.*;
//import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 classe per eseguire lo splitting delle classi tramite Game Theory,
 una classe potrebbe essere non coesa ed accoppiata con troppe classi,
 splitto una data classe in due classi per aumentare la coesione e diminuire l'accoppiamento
 @author Valentina Pascucci
 */

public class GameTheorySplitClasses implements SplittingStrategy {


    public GameTheorySplitClasses() {

    }

    /**
     * il metodo che splitta la classa data in input in due nuove classi
     *
     * @param toSplit   la classe da splittare
     * @param threshold la soglia sotto la quale non considero certi valori
     * @return restituisce le nuove classi ottenute tramite applicazione di algoritmo di teoria dei giochi
     * @throws SplittingException eccezione
     * @throws Exception          eccezione
     */
    @Override
    public Collection<ClassBean> split(ClassBean toSplit, double threshold) throws SplittingException, Exception {

        //le classi splittate di Tom e Sally, l'output della funzione
        Collection<ClassBean> res = new Vector<>();

        //la lista dei metodi (costruttore compreso) della classe toSplit
        Collection<MethodBean> methodsClassToSplit = toSplit.getMethodList();

        //lista riempita con i metodi della classe toSplit, costruttore escluso
        Vector<MethodBean> methods = new Vector<>();
        for (MethodBean m : methodsClassToSplit) {
            if (!m.getFullQualifiedName().equals(toSplit.getFullQualifiedName())) {
                methods.add(m);
            }
        }

        //serve che gli indici della matrice utilità siano numeri che, però, corrispondono a metodi
        //grazie alla variabile vectorOfMethods (uso anche methods), farò tale associazione
        Vector<MethodBean> vectorOfMethods = new Vector<>();
        //riempiamo quindi vectorOfMethods
        for (MethodBean method : methods) {
            vectorOfMethods.add(method);
        }


        MethodByMethodMatrixConstruction matrixConstructor = new MethodByMethodMatrixConstruction();
        //costruisco la matrice utilità Fcc
        double[][] utilityMatrix = matrixConstructor.buildMethodByMethodMatrix(0.2, 0.1, 0.7, toSplit);


        //conserva i metodi scelti da Sally
        Vector<MethodBean> sallyMethods = new Vector<>();
        //conserva i metodi scelti da Tom
        Vector<MethodBean> tomMethods = new Vector<>();

        //conserva i metodi e gli indici relativi per Sally
        Collection<MethodAndIndex> sallyMethodsIndexes = new Vector<>();
        //conserva i metodi e gli indici relativi per Tom
        Collection<MethodAndIndex> tomMethodsIndexes = new Vector<>();

        //le scelte fatte ad ogni iterazione
        MethodBean sallyMethod, tomMethod;
        MethodAndIndex sallyMethodIndex, tomMethodIndex;

        //riga e colonna da "eliminare" ad ogni iterazione, setto a (-1,-1) le loro entries nella payoff matrix
        int chosenI, chosenJ;

        //primo step: i metodi con le similartià minori fanno iniziare il gioco
        int[] chosenIndexes = minSimilarity(utilityMatrix);
        chosenI = chosenIndexes[0];
        chosenJ = chosenIndexes[1];

        //aggiornamento dei metodi attuali di Sally e Tom
        sallyMethod = vectorOfMethods.elementAt(chosenI); //ex: metodo in posizione 1
        tomMethod = vectorOfMethods.elementAt(chosenJ);   //ex: metodo in posizione 6
        sallyMethodIndex = new MethodAndIndex(sallyMethod, chosenI);  //ex: {(m1, 1)}
        tomMethodIndex = new MethodAndIndex(tomMethod, chosenJ);      //ex: {(m6, 6)}

        sallyMethods.add(sallyMethod); //la classe di Sally,    ex: Cs = {m1}
        tomMethods.add(tomMethod);     //la classe di Tom,      ex: Ct = {m6}
        sallyMethodsIndexes.add(sallyMethodIndex);       //ex: Cs = {(m1,1)}
        tomMethodsIndexes.add(tomMethodIndex);           //ex: Ct = {(m6,6)}

        //uso METHODS per panoramica generale, eliminando ogni volta da methods i metodi selezionati dai players
        //in modo da conservare in VECTOR_OF_METHODS sempre tutti i metodi e indici in cui sono posizionati
        methods.remove(sallyMethod);
        methods.remove(tomMethod);
        //pensa che adesso methods ha già due metodi in meno

        //filtro la matrice utilità, ponendo a 0 i valori sotto la soglia 0.20 (non statisticamente utili)
        double[][] filteredUtilityMatrix = matrixConstructor.filterMatrix(utilityMatrix, threshold);

        //computo la payoff matrix per la prima volta, in base alle entries della Fcc matrix
        PayoffPair[][] startingPayoffMatrix = computeStartingPayoffMatrix(filteredUtilityMatrix, chosenI, chosenJ);
        //sulla payoff matrix ottenuta, i player faranno le loro prime mosse
        letThemPlay(startingPayoffMatrix);
        //la coppia corrispondente al Nash Equilibrium determinerà i metodi che saranno scelti da Sally e Tom
        findNashEquilibrium(startingPayoffMatrix, sallyMethods, tomMethods, sallyMethodsIndexes, tomMethodsIndexes, vectorOfMethods, methods);

        //tutto termina quando tutti i metodi della classe toSplit sono stati assegnati alle due classi
        while (!methods.isEmpty()) {

            double[][] fccMatrix = computeFccMatrix(filteredUtilityMatrix, sallyMethodsIndexes, tomMethodsIndexes);

            PayoffPair[][] payoffMatrix = computePayoffMatrix(fccMatrix, startingPayoffMatrix);
            startingPayoffMatrix = payoffMatrix;

            letThemPlay(payoffMatrix);

            findNashEquilibrium(payoffMatrix, sallyMethods, tomMethods, sallyMethodsIndexes, tomMethodsIndexes, vectorOfMethods, methods);

        }

        /*
         al termine del gioco, devo creare queste due classi nuove (a partire dalla classe splittata)
         e devo mettere in ognuna di queste classi i metodi che sono stati selezionati per l'una e per l'altra
        */
        String packageName = toSplit.getFullQualifiedName().substring(0, toSplit.getFullQualifiedName().lastIndexOf('.'));

        ClassBean sallyClass;
        sallyClass = createSplittedClassBean(0, packageName, sallyMethods, toSplit.getBelongingPackage());

        ClassBean tomClass;
        tomClass = createSplittedClassBean(1, packageName, tomMethods, toSplit.getBelongingPackage());


        res.add(sallyClass);
        res.add(tomClass);


        return res;
    }

    /**
     * PRIMO PASSO: Sally e Tom prendono i due metodi con le similarità peggiori
     *
     * @param utilityMatrix la matrice da cui estrarre tali metodi
     * @return gli indici relativi ai metodi selezionati
     */
    public int[] minSimilarity(double[][] utilityMatrix) {

        //riga e colonna da "eliminare" ad ogni iterazione, setto a (-1,-1) le loro entries nella payoff matrix
        int chosenI = 0;
        int chosenJ = 0;

        double minSimilarity = utilityMatrix[0][0];

        int[] chosenIndexes = new int[2];

        for (int i = 0; i < utilityMatrix.length; i++) {
            for (int j = 0; j < utilityMatrix.length; j++) {

                if (utilityMatrix[i][j] < minSimilarity) {  //&& filteredUtilityMatrix[i][j] != 1.0

                    minSimilarity = utilityMatrix[i][j];
                    //ho bisogno di conservare gli indici relativi ai due metodi inizialmente scelti
                    //per poter annullare (nella payoff matrix) righe e colonne corrispondenti a tali indici
                    chosenI = i; //ex: 1
                    chosenJ = j; //ex: 6
                }
            }
        }
        chosenIndexes[0] = chosenI;
        chosenIndexes[1] = chosenJ;

        return chosenIndexes;
    }


    /**
     * metodo per computare la payoff matrix della prima iterazione, in base ai valori contenuti in filteredUtilityMatrix,
     * setto a (-1,-1) le coppie della payoff matrix relative agli indici chosenI e chosenJ,
     * i quali corrispondono agli indici precedentemente selezionati dai players
     * gli indici chosenI,chosenJ servono anche per calcolare la similarità tra la classe di Sally/Tom e i rispettivi metodi scelti
     *
     * @param filteredUtilityMatrix della quale uso le entries per costruire la payoff matrix
     * @param chosenI               indice i la cui riga e colonna relativa sarà annullata
     * @param chosenJ               indice j la cui riga e colonna relativa sarà annullata
     * @return la prima costruzione della payoff matrix
     */
    public PayoffPair[][] computeStartingPayoffMatrix(double[][] filteredUtilityMatrix,
                                                      int chosenI, int chosenJ) {

        //la matrice ha un rigo e una colonna in più, corrispondenti rispettivamente alle mosse null di Sally e Tom
        PayoffPair[][] startingPayoffMatrix = new PayoffPair[filteredUtilityMatrix.length + 1][filteredUtilityMatrix.length + 1];

        double greekMiValue = 0.5; //mi permette di giocare la mossa null
        double payoffI, payoffJ;

        for (int i = 0; i < filteredUtilityMatrix.length; i++) {
            for (int j = 0; j < filteredUtilityMatrix.length; j++) {
                //le entries relative agli indici dei metodi già selezionati sono settate a (-1,-1)
                if (i == chosenI || j == chosenJ || i == chosenJ || j == chosenI) {
                    startingPayoffMatrix[i][j] = new PayoffPair(-1.0, -1.0);
                    //tutto il resto (tranne ultima riga e colonna)
                } else {
                    //fcc(Cs,mi) - fcc(Ct,mj)
                    payoffI = filteredUtilityMatrix[chosenI][i] - filteredUtilityMatrix[chosenI][j];
                    //fcc(Ct,mj) - fcc(Cs,mi)
                    payoffJ = filteredUtilityMatrix[chosenJ][j] - filteredUtilityMatrix[chosenJ][i];
                    startingPayoffMatrix[i][j] = new PayoffPair(payoffI, payoffJ);
                }
                //le entries sulla diagonale sono settate a (-1,-1), così che i player non scelgano lo stesso metodo
                if (i == j) {
                    startingPayoffMatrix[i][j] = new PayoffPair(-1.0, -1.0);
                }
            }
        }

        //per riempire ultima riga e colonna
        for (int i = 0; i < startingPayoffMatrix.length; i++) {
            for (int j = 0; j < startingPayoffMatrix.length; j++) {
                //se Sally fa mossa NULL
                if (i == startingPayoffMatrix.length - 1 && j != startingPayoffMatrix.length - 1) {
                    if (j != chosenI && j != chosenJ) {
                        //mi-fcc(Cs,mj)
                        payoffI = greekMiValue - filteredUtilityMatrix[chosenI][j];
                        //fcc(Ct,mj)
                        payoffJ = filteredUtilityMatrix[chosenJ][j];
                        startingPayoffMatrix[i][j] = new PayoffPair(payoffI, payoffJ);
                    } else //if(j==chosenI || j==chosenJ)
                    {
                        startingPayoffMatrix[i][j] = new PayoffPair(-1.0, -1.0);
                    }
                }

                //se Tom fa mossa NULL
                else if (j == startingPayoffMatrix.length - 1 && i != startingPayoffMatrix.length - 1) {
                    if (i != chosenI && i != chosenJ) {
                        //fcc(Cs,mi)
                        payoffI = filteredUtilityMatrix[chosenI][i];
                        //mi - fcc(Ct,mi)
                        payoffJ = greekMiValue - filteredUtilityMatrix[chosenJ][i];
                        startingPayoffMatrix[i][j] = new PayoffPair(payoffI, payoffJ);
                    } else //if(i==chosenI || i==chosenJ)
                    {
                        startingPayoffMatrix[i][j] = new PayoffPair(-1.0, -1.0);
                    }

                    //ultimo elemento della matrice payoff
                } else if (i == startingPayoffMatrix.length - 1 && j == startingPayoffMatrix.length - 1) {
                    //(null, null)
                    startingPayoffMatrix[i][j] = new PayoffPair(-1, -1);

                }
            }
        }

        return startingPayoffMatrix;
    }


    /**
     * i due player giocano sulla payoff matrix:
     * Sally seleziona i valori massimi su ogni colonna relativi al primo elemento di ciascuna coppia
     * Tom seleziona i valori massimi su ogni riga relativi al secondo elemento di ciascuna coppia.
     * Setto a true i valori selezionati dai player
     *
     * @param payoffMatrix la matrice sulla quale si gioca
     */
    public void letThemPlay(PayoffPair[][] payoffMatrix) {

        int i, j;

        int chosenI = 0;
        int chosenJ = 0;


        //GIOCA SALLY
        double bestPayoffi = -1.0;
        j = 0;

        while (j != payoffMatrix.length) {
            //di colonna in colonna
            for (i = 0; i < payoffMatrix.length; i++) {

                if (payoffMatrix[i][j].getPayoffI() > bestPayoffi) {
                    //valore massimo di payoffI in colonna j-sima
                    bestPayoffi = payoffMatrix[i][j].getPayoffI();
                    chosenI = i;
                    chosenJ = j;
                }
                //se trovo su stessa colonna due o più valori uguali
                else if (payoffMatrix[i][j].getPayoffI() == bestPayoffi
                        && bestPayoffi != -1.0) {
                    if (payoffMatrix[i][j].getPayoffI() + payoffMatrix[i][j].getPayoffJ()
                            >
                            bestPayoffi + payoffMatrix[chosenI][chosenJ].getPayoffJ()) {
                        bestPayoffi = payoffMatrix[i][j].getPayoffI();
                        chosenI = i;
                        chosenJ = j;
                    }
                }
            }
            if (bestPayoffi >= 0) payoffMatrix[chosenI][chosenJ].setMaximumSally(true);
            bestPayoffi = -1.0;
            j++;
        }

        //GIOCA TOM
        double bestPayoffj = -1.0;

        for (i = 0; i < payoffMatrix.length; i++) {

            for (j = 0; j < payoffMatrix.length; j++) {
                //di rigo in rigo
                if (payoffMatrix[i][j].getPayoffJ() > bestPayoffj) {
                    //valore massimo di payoffJ in rigo i-simo
                    bestPayoffj = payoffMatrix[i][j].getPayoffJ();
                    chosenI = i;
                    chosenJ = j;

                }
                //e se trovo su stesso rigo due o più valori uguali?
                else if (payoffMatrix[i][j].getPayoffJ() == bestPayoffj
                        && bestPayoffj != -1.0) {
                    if (payoffMatrix[i][j].getPayoffI() + payoffMatrix[i][j].getPayoffJ()
                            >
                            bestPayoffj + payoffMatrix[chosenI][chosenJ].getPayoffJ()) {
                        bestPayoffj = payoffMatrix[i][j].getPayoffJ();
                        chosenI = i;
                        chosenJ = j;
                    }
                }
            }
            if (bestPayoffj >= 0) payoffMatrix[chosenI][chosenJ].setMaximumTom(true);
            bestPayoffj = -1.0;
        }
    }


    /**
     * l'obiettivo dei player è massimizzare la loro scelta, bisogna quindi trovare la combinazione di scelte che
     * ottimizzi il guadagno delle due classi, in assenza di collaborazione.
     * il nash equilibrium sta nelle coppie dove entrambi i valori sono settati a true,
     * trovato il nash equilibrium, i metodi corrispondenti saranno assegnati ed eliminati
     *
     * @param payoffMatrix      da esplorare
     * @param sallyMethods      i metodi selezionati da Sally
     * @param tomMethods        i metodi selezionati da Tom
     * @param sallyIndexMethods per mantenere la relazione indice-metodo di Sally
     * @param tomIndexMethods   per mantenere la relazione indice-metodo di Tom
     * @param vectorOfMethods   un vettore utile per la corrispondenza (indice matrice-metodo)
     * @param methods           vettore dei metodi da aggiornare, eliminando i metodi eventualmente scelti
     */
    public void findNashEquilibrium(PayoffPair[][] payoffMatrix,
                                    Collection<MethodBean> sallyMethods, Collection<MethodBean> tomMethods,
                                    Collection<MethodAndIndex> sallyIndexMethods, Collection<MethodAndIndex> tomIndexMethods,
                                    Vector<MethodBean> vectorOfMethods,
                                    Vector<MethodBean> methods) {

        PayoffPair nashEquilibrium = new PayoffPair(-1.0, -1.0);

        MethodBean sallyMethod, tomMethod;

        MethodAndIndex sallyIndexMethod, tomIndexMethod;

        //ho sempre bisogno di loro, per le eliminazioni (annullamenti a (-1,-1))
        int chosenI = -1;
        int chosenJ = -1;

        for (int i = 0; i < payoffMatrix.length; i++) {
            for (int j = 0; j < payoffMatrix.length; j++) {
                //voglio solo le coppie dove entrambi i valori max sono settati a true
                if (payoffMatrix[i][j].isMaximumSally() && payoffMatrix[i][j].isMaximumTom()) {
                    //posso trovare più equlibri in una matrice,
                    //voglio quello con la somma dei due payoff maggiore
                    if (payoffMatrix[i][j].getPayoffI() + payoffMatrix[i][j].getPayoffJ()
                            >
                            nashEquilibrium.getPayoffI() + nashEquilibrium.getPayoffJ()) {

                        if (i != payoffMatrix.length - 1 && j != payoffMatrix.length - 1) {
                            //entrambi hanno selezionato un metodo
                            //sallyMethod = vectorOfMethods.elementAt(i);
                            //tomMethod = vectorOfMethods.elementAt(j);
                            chosenI = i;
                            chosenJ = j;

                        } else if (i != payoffMatrix.length - 1 && j == payoffMatrix.length - 1) {
                            //tom ha scelto mossa NULL
                            //sallyMethod = vectorOfMethods.elementAt(i);
                            chosenI = i;
                            chosenJ = -1;

                        } else if (i == payoffMatrix.length - 1 && j != payoffMatrix.length - 1) {
                            //sally ha scelto mossa NULL
                            //tomMethod = vectorOfMethods.elementAt(j);
                            chosenJ = j;
                            chosenI = -1;
                        }
                    }
                }
            }
        }

        //aggiornamento, qualcuno avrà un nuovo metodo nella sua classe
        //tale eventuale metodo sarà eliminato dalla lista generica methods

        if (chosenI != -1) {
            sallyMethod = vectorOfMethods.elementAt(chosenI);
            sallyMethods.add(sallyMethod);

            sallyIndexMethod = new MethodAndIndex(sallyMethod, chosenI);
            sallyIndexMethods.add(sallyIndexMethod);

            methods.remove(sallyMethod);
        }

        if (chosenJ != -1) {
            tomMethod = vectorOfMethods.elementAt(chosenJ);
            tomMethods.add(tomMethod);

            tomIndexMethod = new MethodAndIndex(tomMethod, chosenJ);
            tomIndexMethods.add(tomIndexMethod);

            methods.remove(tomMethod);
        }

        //dopo aver selezionato il Nash Equilibrium, devo annullare le colonne e righe corrispondenti a i,j
        //(ex: selezionare m2 ed m5 ha come conseguenza l'annullamento di: riga e colonna 2, riga e colonna 5)

        for (int i = 0; i < payoffMatrix.length; i++) {
            for (int j = 0; j < payoffMatrix.length; j++) {
                if (i == chosenI || j == chosenI || i == chosenJ || j == chosenJ)
                    payoffMatrix[i][j] = new PayoffPair(-1.0, -1.0);
            }
        }

        //poi rimetto a false tutti i valori della matrice
        for (int i = 0; i < payoffMatrix.length; i++) {
            for (int j = 0; j < payoffMatrix.length; j++) {
                payoffMatrix[i][j].setMaximumSally(false);
                payoffMatrix[i][j].setMaximumTom(false);
            }
        }


    }

    /**
     * costruisce la fcc matrix a seconda della similarità tra i metodi rimasti e quelli presenti in ogni classe (Cs e Ct)
     *
     * @param filteredUtilityMatrix la matrice utilità di partenza
     * @param sallyMethodsIndexes   conserva la relazione metodo-indice di Sally
     * @param tomMethodsIndexes     conserva la relazione metodo-indice di Tom
     * @return la fccMatrix
     */
    public double[][] computeFccMatrix(double[][] filteredUtilityMatrix,
                                       Collection<MethodAndIndex> sallyMethodsIndexes,
                                       Collection<MethodAndIndex> tomMethodsIndexes) {

        //1/N * sim(mi, mj) per tutti gli mi contenuti nella classe di Sally/Tom, scansione fatta su fcc utilità originale

        //creo una nuova matrice che ha due righe: la riga di Sally e la riga di Tom
        //il numero di colonne è pari al numero di metodi rimasti
        //e invece no, il numero di colonne è lo stesso di filteredUtilityMatrix, dove pongo a 1 le entries
        //corrispondenti ai metodi già selezionati

        //int columnLength = filteredUtilityMatrix.length - (sallyMethodsIndexes.size() + tomMethodsIndexes.size());
        int columnLength = filteredUtilityMatrix.length;

        Vector<Integer> usedIndexes = new Vector<>();

        //prendo gli indici (relativi ai metodi selezionati) di Sally
        Vector<Integer> sallyIndexes = new Vector<>();
        for (MethodAndIndex m : sallyMethodsIndexes) {
            sallyIndexes.add(m.getIndex());
            usedIndexes.add(m.getIndex());
        }

        //prendo gli indici (relativi ai metodi selezionati) di Tom
        Vector<Integer> tomIndexes = new Vector<>();
        for (MethodAndIndex m : tomMethodsIndexes) {
            tomIndexes.add(m.getIndex());
            usedIndexes.add(m.getIndex());
        }

        Vector<Integer> fccIndexes = new Vector<>();
        for (int i = 0; i < filteredUtilityMatrix.length; i++) {
            fccIndexes.add(i);
        }

        //Collections.sort(usedIndexes);

        double[][] newUtilityMatrix = new double[2][columnLength];

        //for(int i=0; i<2; i++){

        for (int j = 0; j < columnLength; j++) {

            //for(Integer u: usedIndexes) {
            if (!usedIndexes.contains(j)) {
                //if (j != usedIndexes.elementAt(j)  ||  usedIndexes.contains(j)) {

                int methodIndex = fccIndexes.get(j);

                double simSally = 0;
                for (int s : sallyIndexes) {
                    simSally += filteredUtilityMatrix[s][methodIndex];
                }
                simSally /= sallyIndexes.size();

                newUtilityMatrix[0][j] = simSally;


                double simTom = 0;
                for (int t : tomIndexes) {
                    simTom += filteredUtilityMatrix[t][methodIndex];
                }
                simTom /= tomIndexes.size();

                newUtilityMatrix[1][j] = simTom;

            } else {
                newUtilityMatrix[0][j] = 1.0;
                newUtilityMatrix[1][j] = 1.0;
            }
        }
        //}
        return newUtilityMatrix;
    }


    /**
     * calcola la payoff matrix delle iterazioni successive alla prima
     *
     * @param filteredUtilityMatrix matrice utilità da seconda iterazione in poi
     * @param payoffMatrix          matrice dei payoff corrispondente all' iterazione precedente (su cui sono già annullate righe e colonne dei metodi scelti)
     * @return la nuova payoff matrix
     */
    public PayoffPair[][] computePayoffMatrix(double[][] filteredUtilityMatrix,
                                              PayoffPair[][] payoffMatrix) {
        double payoffI, payoffJ;
        double greekMi = 0.5;
        //hai già settato a (-1,-1) le righe e le colonne corrispondenti ai metodi precedentemente selezionati dai players
        for (int i = 0; i < payoffMatrix.length; i++) {
            for (int j = 0; j < payoffMatrix.length; j++) {
                if (payoffMatrix[i][j].getPayoffI() != -1.0 && payoffMatrix[i][j].getPayoffJ() != -1.0) {
                    if (i != payoffMatrix.length - 1 && j != payoffMatrix.length - 1) {
                        payoffI = filteredUtilityMatrix[0][i] - filteredUtilityMatrix[0][j];
                        payoffJ = filteredUtilityMatrix[1][j] - filteredUtilityMatrix[1][i];
                        payoffMatrix[i][j] = new PayoffPair(payoffI, payoffJ);
                    }
                    //Sally fa mossa NULL
                    else if (i == payoffMatrix.length - 1 && j != payoffMatrix.length - 1) {
                        payoffI = greekMi - filteredUtilityMatrix[0][j];
                        payoffJ = filteredUtilityMatrix[1][j];
                        payoffMatrix[i][j] = new PayoffPair(payoffI, payoffJ);
                    }
                    //Tom fa mossa NULL
                    else if (i != payoffMatrix.length - 1 && j == payoffMatrix.length - 1) {
                        payoffI = filteredUtilityMatrix[0][i];
                        payoffJ = greekMi - filteredUtilityMatrix[1][i];
                        payoffMatrix[i][j] = new PayoffPair(payoffI, payoffJ);
                    }
                }
            }
        }
        return payoffMatrix;
    }


    /**
     * per creare la classe dopo lo splittaggio
     */
    private ClassBean createSplittedClassBean(int index, String packageName, Vector<MethodBean> methods, PackageBean belongingPackage) {
        String classShortName = "Class_" + (index + 1);
        String tempName = packageName + "." + classShortName;
        List<MethodBean> methodsToAdd = new ArrayList<>();

        for (MethodBean m : methods)
            methodsToAdd.add(m);

        MethodList methodList = new MethodList();
        methodList.setList(methodsToAdd);

        StringBuilder classTextContent = new StringBuilder();
        classTextContent.append("public class ");
        classTextContent.append(classShortName);
        classTextContent.append(" {");


        for (MethodBean methodBean : methodsToAdd) {
            classTextContent.append(methodBean.getTextContent());
            classTextContent.append("\n");
        }

        classTextContent.append("}");

        return new ClassBean.Builder(tempName, classTextContent.toString())
                .setMethods(methodList)
                .setImports(new ArrayList<String>())
                .setLOC(0)
                .setBelongingPackage(belongingPackage)
                .setPathToFile("")
                .setEntityClassUsage(0)
                .setAffectedSmell()
                .build();
    }


    //posso usare Map??????

    /**
     * classe per associazione metodo con indice
     */
    public static class MethodAndIndex {
        private MethodBean methodBean;
        private int index;

        public MethodAndIndex(MethodBean methodBean, int index) {
            this.methodBean = methodBean;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public MethodBean getMethodBean() {
            return methodBean;
        }

        public void setMethodBean(MethodBean methodBean) {
            this.methodBean = methodBean;
        }
    }
}

