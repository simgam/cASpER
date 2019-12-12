package it.unisa.casper.refactor.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;

public class MethodMover {
    /**
     * Metodo che costruisce un metodo da un insieme di stringhe e controllo se ci sono caratteri anomali sostituendolo automaticamente
     *
     * @param scope      scope del metodo
     * @param returnType tipo di ritorno
     * @param nomeMetodo nome del metodo
     * @param parameters parametri del metodo tra tonde
     * @param throwsList lista delle eccezioni che il metodo potrebbe lanciare
     * @param body       corpo del metodo compreso di parentesi {..}
     * @return stringa singola con tutto già settato
     */
    public static String buildMethod(String scope, String returnType, String nomeMetodo, String parameters, String throwsList, String body) {
        StringBuilder stringBuilder = new StringBuilder(scope);

        stringBuilder.append(" ").
                append(returnType).append(" ").
                append(nomeMetodo).
                append(parameters).
                append(" ").
                append(throwsList).
                append(body.replaceAll("    ", "\t"));
        String s = stringBuilder.toString();
        return s;
    }

    /**
     * Metodo che scrive il metodo passato
     * nella classe passata
     *
     * @param methodToWrite methodo da scrivere
     * @param writePlace    classe dove scrivere il metodo
     * @param replace       settare true se si vuole sostituire, false per aggiungere da nuovo
     */
    public static void methodWriter(String methodToWrite, PsiMethod actualPsiMethod, PsiClass writePlace, Boolean replace, Project project) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiMethod newMethod = elementFactory.createMethodFromText(methodToWrite, writePlace);

        if (replace) {
            actualPsiMethod.replace(newMethod);
        } else {
            writePlace.add(newMethod);
        }
    }
}
