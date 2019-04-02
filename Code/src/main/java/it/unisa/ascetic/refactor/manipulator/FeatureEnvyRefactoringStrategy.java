package it.unisa.ascetic.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import it.unisa.ascetic.refactor.exceptions.FeatureEnvyException;
import it.unisa.ascetic.refactor.strategy.RefactoringStrategy;
import it.unisa.ascetic.storage.beans.MethodBean;

import java.util.logging.Logger;

/**
 * Feature Envy Refactor (FE Refactor) è la classe che si occupa del Refactor degli smells di tipo Feature Envy
 * <p>
 * ATTENZIONE CONSIDERANDO CHE LA CLASSE E' STATE REALIZZATA DA NICOLA AMORIELLO E DOMENICO PASCUCCI IL TUO COMPUTER
 * POTREBBE:
 * ->ESPLODERE
 * ->AVERE BUG
 * ->DISINSTALLARE WINDOWS E INSTALLARE AUTOMATICAMENTE UBUNTU 8024
 * ->PRENOTARTI UN BIGLIETTO PER IL MESSICO
 * <p>
 * SI SPERA FIXARE UN FEATURE ENVIE
 **/

public class FeatureEnvyRefactoringStrategy implements RefactoringStrategy {

    //PSI Section
    private PsiClass psiSourceClass, psiDestinationClass;
    private PsiMethod psiMethod;
    // Project
    private Project project;
    //Fixing Type
    private int fixtype = -1;
    private String variabileDaTrasformare = null;
    private boolean isStaticMethod;

    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Costruttore di Feature Envy Refactoring strategy con classe sorgente implicita
     *
     * @param methodToMove methodo da spostare
     * @param project      progetto di analisi
     */
    public FeatureEnvyRefactoringStrategy(MethodBean methodToMove, Project project) {
        //logger.severe("oggetto FE_STGY creato");
        this.project = project;
        psiMethod = PsiUtil.getPsi(methodToMove, project);
        psiSourceClass = PsiUtil.getPsi(methodToMove.getBelongingClass(), project);
        psiDestinationClass = PsiUtil.getPsi(methodToMove.getEnviedClass(), project);
        fixtype = selectFixingStrategy();
        isStaticMethod = methodToMove.getStaticMethod();
    }

    /**
     * implementazione dell'interfaccia @class {@link RefactoringStrategy}
     *
     * @throws FeatureEnvyException
     */
    @Override
    public void doRefactor() throws FeatureEnvyException {
        switch (fixtype) {
            case 1:
                instanceVariableFeatureEnvy();
                break;
            case 2:
                parametersFeatureEnvy();
                break;
            case 0:
                unfixableFE();
            default:
                throw new FeatureEnvyException("Invalid Fix Type");
        }
    }

    /**
     * Metodo che seleziona la strategia di fixing appropriata, restituendo:
     * 1 per feature envy con Variabile di instanza nella classe Sorgente;
     * 2 per Feature Envy con parametro passato al metodo;
     * 0 se non è possibile fixare lo smell automaticamente
     *
     * @return Numero di selezione della strategia di fixing
     */
    private int selectFixingStrategy() {
        {//Controllo se si tratta di una Variabile d'istanza
            PsiField[] psiFields = psiSourceClass.getFields();
            for (PsiField field : psiFields) {
                if (psiMethod.getBody().getText().contains(field.getName())) {
                    String a = field.getType().getInternalCanonicalText();
                    String b = psiDestinationClass.getQualifiedName();
                    if (a.equals(b)) {
                        boolean x = field.getModifierList().getText().contains("static");
                        boolean y = psiMethod.getModifierList().getText().contains("static");
                        if ((x || y) && !(x && y))//questo è uno XOR, VERO se e solo se gli ingressi sono diversi tra di loro.
                            return 0;
                        variabileDaTrasformare = field.getName();
                        logger.severe("selezionata la strategia VARIABILE D'ISTANZA");
                        return 1;
                    }
                }
            }
        }
        {//Conrollo i parametri
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            for (PsiParameter parametro : parameters) {
                if (parametro.getType().getInternalCanonicalText().equals(psiDestinationClass.getQualifiedName())) {
                    variabileDaTrasformare = parametro.getName();
                    logger.severe("selezionata la strategia PARAMETRI");
                    return 2;
                }
            }
        }
        logger.severe("selezionata la strategia NESSUNA");
        return 0;
    }


    /**
     * Quando il FE è non fixabile automaticamente si lancia l'eccazzione
     *
     * @throws FeatureEnvyException lanciata per segnalare l'impossibilità al fix automatizzato
     */
    private void unfixableFE() throws FeatureEnvyException {
        throw new FeatureEnvyException("unfixable");
    }

    /**
     * Metodo che si occupa di fixare il FE se la classe inviata è passata come parametro al metodo
     */
    private void parametersFeatureEnvy() {
        //setto le stringe per la costruzione del metodo
        String scope = psiMethod.getModifierList().getText();
        String returnType = psiMethod.getReturnType().getPresentableText();
        String name = psiMethod.getName();
        String parameters = psiMethod.getParameterList().getText();
        String throwsList = psiMethod.getThrowsList().getText();
        String body = psiMethod.getBody().getText();
        String othervariables = "";
        {//Modifico il metodo da Esistente
            //Creo il corpo del metodo da modificare
            StringBuilder newMethodBody = new StringBuilder();
            newMethodBody.append("{\n");
            //controllo se il metodo è void
            if (psiMethod.getReturnType().getCanonicalText() != "void")
                newMethodBody.append("return ");
            //aggiungo i parametri passati dal metodo originario
            newMethodBody.append(variabileDaTrasformare).append(".").append(psiMethod.getName()).append("(");
            PsiParameter[] parameters1 = psiMethod.getParameterList().getParameters();
            for (int i = 0; i < parameters1.length; i++) {
                PsiParameter parametriDaPassare = parameters1[i];
                if (parametriDaPassare.getName() != variabileDaTrasformare) {
                    newMethodBody.append(parametriDaPassare.getName());
                    if (i < parameters1.length - 2) {
                        newMethodBody.append(",");
                    }
                }
            }
            //controllo se devo passare qualche variabile d'istanza
            PsiField[] fields = psiSourceClass.getFields();
            for (int i = 0; i < fields.length; i++) {
                PsiField variabiliIstanza = fields[i];
                if (psiMethod.getBody().getText().contains(variabiliIstanza.getName())) {
                    newMethodBody.append(",").append(variabiliIstanza.getName());
                    othervariables += variabiliIstanza.getType().getPresentableText() + " " + variabiliIstanza.getName() + ",";
                }
            }
            newMethodBody.append(");\n}");
            String textToWrite = MethodMover.buildMethod(scope, returnType, name, parameters, throwsList, newMethodBody.toString());
            MethodMover.methodWriter(textToWrite, psiMethod, psiSourceClass, true, project);
        }
        {//Genera nuovo metedo
            scope = "public";
            if (isStaticMethod)
                scope += " static";
            StringBuilder stringBuilder = new StringBuilder();
            String s = parameters.replace(")", "");
            s = s.replace(variabileDaTrasformare, "");
            s = s.replace(psiDestinationClass.getName(), "");
            s += othervariables;
            s = s.substring(0, s.length() - 1);
            s += ")";
            body = body.replace(variabileDaTrasformare + ".", "");
            String textToWrite = MethodMover.buildMethod(scope, returnType, name, s, throwsList, body);
            MethodMover.methodWriter(textToWrite, psiMethod, psiDestinationClass, false, project);
        }
    }

    /**
     * Metodo che si occupa di fixare il FE se la classe inviata è una variabile d'istanza
     */
    private void instanceVariableFeatureEnvy() {
        String scope, returnType, name, parameters, throwsList, body;
        scope = psiMethod.getModifierList().getText();
        returnType = psiMethod.getReturnType().getPresentableText();
        name = psiMethod.getName();
        parameters = psiMethod.getParameterList().getText();
        throwsList = psiMethod.getThrowsList().getText();
        body = psiMethod.getBody().getText();
        //controllo se devo passare delle variabili
        PsiField[] fields = psiSourceClass.getFields();
        String parameterstoAdd = "";
        String methodSignature = "";
        for (PsiField field : fields) {
            if (body.contains(field.getName()) && field.getName() != variabileDaTrasformare) {
                parameterstoAdd += "," + field.getName();
                methodSignature += "," + field.getType().getPresentableText() + " " + field.getName();
            }
        }
        //Metodo Da aggiungere/Spostare
        String newBody = body.replaceAll(variabileDaTrasformare + ".", "");
        String newParameters = parameters.substring(0, parameters.length() - 1);
        newParameters += methodSignature + ")";
        String s = MethodMover.buildMethod(scope, returnType, name, newParameters, throwsList, newBody);
        MethodMover.methodWriter(s, psiMethod, psiDestinationClass, false, project);
        //Metodo da Fixare
        body = "{\n";
        if (!isStaticMethod && returnType != "void") {
            body += "return ";
            body += variabileDaTrasformare + "." + name + "(";
        } else if (isStaticMethod && returnType != "void") {
            body += "return ";
            body += psiDestinationClass.getQualifiedName() + "." + name + "(";
        } else if (isStaticMethod && returnType == "void") {
            body += psiDestinationClass.getQualifiedName() + "." + name + "(";
        } else {
            body += psiDestinationClass.getQualifiedName() + "." + name + "(";
        }
        for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
            body += parameter.getName() + ",";
        }
        body = body.substring(0, body.length() - 1);
        body += parameterstoAdd + ");\n}";
        s = MethodMover.buildMethod(scope, returnType, name, parameters, throwsList, body);
        MethodMover.methodWriter(s, psiMethod, psiSourceClass, true, project);
    }
}
