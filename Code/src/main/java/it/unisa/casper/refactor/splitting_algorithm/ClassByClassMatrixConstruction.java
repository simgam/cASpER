package it.unisa.casper.refactor.splitting_algorithm;

import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.PackageBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;


public class ClassByClassMatrixConstruction {

    private String casperDirectoryPath;
    private File matrixFolder;
    private File stopwordList;
    private FileInputStream fs;
    private InputStreamReader isr;
    private BufferedReader br;

    public ClassByClassMatrixConstruction() {
        casperDirectoryPath = System.getProperty("user.home") + "/.casper";
        matrixFolder = new File(casperDirectoryPath + "/matrix");
        stopwordList = new File(matrixFolder.getAbsolutePath() + "stopword.txt");
    }

    public double[][] buildClassByClassMatrix(double pWicp, double pWccbc, PackageBean pToSplit) throws Exception {
        PromiscuousPackageQualityChecker promiscuousPackageQualityChecker = new PromiscuousPackageQualityChecker();
        Collection<ClassBean> classes = pToSplit.getClassList();
        Iterator<ClassBean> it = classes.iterator();
        ClassBean tmpClass;
        Vector<ClassBean> vectorOfClasses = new Vector<ClassBean>();
        double[][] classByClassMatrix = new double[classes.size()][classes.size()];
        double[][] ICPmatrix = new double[classByClassMatrix.length][classByClassMatrix.length];
        double[][] CCBCmatrix = new double[classByClassMatrix.length][classByClassMatrix.length];

        matrixFolder.mkdirs();
        Utility.createStopwordListIfNotExists(stopwordList);
        File ICPmatrixFile = new File(matrixFolder.getAbsolutePath() + "/" + "ICP_matrix" + pToSplit.getFullQualifiedName() + ".txt");
        File CCBCmatrixFile = new File(matrixFolder.getAbsolutePath() + "/" + "CCBC_matrix" + pToSplit.getFullQualifiedName() + ".txt");

        while (it.hasNext()) {
            tmpClass = (ClassBean) it.next();
            vectorOfClasses.add(tmpClass);
        }
        Collections.sort(vectorOfClasses);
        for (int i = 0; i < ICPmatrix.length; i++) {
            for (int j = i + 1; j < ICPmatrix.length; j++) {

                ClassBean classSource = vectorOfClasses.elementAt(i);
                ClassBean classTarget = vectorOfClasses.elementAt(j);
                ICPmatrix[i][j] = promiscuousPackageQualityChecker.computeICP(classSource, classTarget, pToSplit);

                ICPmatrix[j][i] = ICPmatrix[i][j];
            }
        }

        for (int i = 0; i < CCBCmatrix.length; i++) {
            for (int j = i + 1; j < CCBCmatrix.length; j++) {

                ClassBean classSource = vectorOfClasses.elementAt(i);
                ClassBean classTarget = vectorOfClasses.elementAt(j);
                CCBCmatrix[i][j] = promiscuousPackageQualityChecker.computeCCBC(classSource, classTarget);

                CCBCmatrix[j][i] = CCBCmatrix[i][j];
            }
        }

        //Prepare the stopwords List
        fs = new FileInputStream(stopwordList);
        isr = new InputStreamReader(fs);
        br = new BufferedReader(isr);
        String tmpLine = null;
        Set<String> badWordsSet = new HashSet<>();
        tmpLine = br.readLine();
        while (tmpLine != null) {
            badWordsSet.add(tmpLine.replace("\n", ""));
            tmpLine = br.readLine();
        }

        ICPmatrix = filterMatrix(ICPmatrix, pWicp);
        CCBCmatrix = filterMatrix(CCBCmatrix, pWccbc);

        for (int i = 0; i < classByClassMatrix.length; i++) {
            for (int j = 0; j < classByClassMatrix.length; j++) {
                if (i != j) {
                    classByClassMatrix[i][j] = ICPmatrix[i][j] * pWicp + CCBCmatrix[i][j] * pWccbc;
                } else {
                    classByClassMatrix[i][j] = 1.0;
                }
                classByClassMatrix[j][i] = classByClassMatrix[i][j];
            }
        }

        return classByClassMatrix;
    }

    public double[][] filterMatrix(double[][] classByClassMatrix, double pThreshold) {
        for (int i = 0; i < classByClassMatrix.length; i++) {
            for (int j = 0; j < classByClassMatrix.length; j++) {
                if (classByClassMatrix[i][j] < pThreshold)
                    classByClassMatrix[i][j] = 0;
            }
        }
        return classByClassMatrix;
    }

}
