package it.unisa.ascetic.analysis.code_smell_detection.feature_envy;

import it.unisa.ascetic.analysis.code_smell_detection.similarityComputation.CosineSimilarity;
import it.unisa.ascetic.analysis.code_smell_detection.strategy.MethodSmellDetectionStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/*

Feature Envy: Si ha quando un metodo è più usato in un'altra classe rispetto a quella in cui si trova.
Quindi questo metodo non è correttamente posizionato ed aumenta l'accoppiamento con classi differenti da quella in cui si trova.
Il refactoring per rimuovere questo smell è il Move Method, che sposta il metodo nella classe adatta.
Un metodo è affetto da questo smell quando è molto più simile ai concetti implementati nella classe esterna rispetto a quella in cui si trova.

 */

public class TextualFeatureEnvyStrategy implements MethodSmellDetectionStrategy {

    private List<PackageBean> systemPackages;
    private double soglia;

    public TextualFeatureEnvyStrategy(List<PackageBean> systemPackages, double soglia) {
        this.systemPackages = systemPackages;
        this.soglia = soglia;
    }

    public boolean isSmelly(MethodBean pMethod) {

        if (!isGetter(pMethod) && !isSetter(pMethod) && !(pMethod.getFullQualifiedName().toLowerCase()).contains("main") && !isConstructor(pMethod)) {

            SortedMap<ClassBean, Double> similaritiesWithMethod = new TreeMap<ClassBean, Double>();
            CosineSimilarity cosineSimilarity = new CosineSimilarity();
            ArrayList<ClassBean> candidateEnviedClasses = MisplacedComponentsUtilities.getCandidates(pMethod, systemPackages, soglia);

            String[] document1 = new String[2];
            document1[0] = "method";
            document1[1] = pMethod.getTextContent();

            for (ClassBean classBean : candidateEnviedClasses) {

                String[] document2 = new String[2];
                document2[0] = "class";
                document2[1] = classBean.getTextContent();

                try {
                    similaritiesWithMethod.put(classBean, cosineSimilarity.computeSimilarity(document1, document2));

                } catch (IOException e) {
                    e.getMessage();
                    return false;
                }
            }

            if (!similaritiesWithMethod.entrySet().iterator().hasNext()) {
                return false;
            }

            Set<ClassBean> list = similaritiesWithMethod.keySet();
            ClassBean firstRankedClass = list.iterator().next();

            for (ClassBean classBean : list) {
                if (classBean.getSimilarity() > firstRankedClass.getSimilarity()) {
                    firstRankedClass = classBean;
                }
            }

            if (firstRankedClass.getFullQualifiedName().equals(pMethod.getBelongingClass().getFullQualifiedName())) {
                return false;
            } else {
                pMethod.setEnviedClass(firstRankedClass);
                pMethod.setIndex(firstRankedClass.getSimilarity());
                Logger logger = Logger.getLogger("global");
                logger.severe("Found FE:" + pMethod.getFullQualifiedName() + " Envied Class:" + firstRankedClass.getFullQualifiedName());
                return true;
            }
        }
        return false;
    }

    private boolean isGetter(MethodBean pMethodBean) {
        if ((pMethodBean.getFullQualifiedName().toLowerCase()).contains("get") && pMethodBean.getParameters().isEmpty() &&
                !((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private boolean isSetter(MethodBean pMethodBean) {
        if ((pMethodBean.getFullQualifiedName().toLowerCase()).contains("set") && pMethodBean.getParameters().size() == 1 &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

    private boolean isConstructor(MethodBean pMethodBean) {
        String[] fullClass = ((ClassBean) pMethodBean.getBelongingClass()).getFullQualifiedName().split(Pattern.quote("."));
        String[] fullMethod = pMethodBean.getFullQualifiedName().split(Pattern.quote("."));

        if (fullMethod[fullMethod.length - 1].equalsIgnoreCase(fullClass[fullClass.length - 1]) &&
                ((ClassBean) pMethodBean.getReturnType()).getFullQualifiedName().equalsIgnoreCase("void")) {
            return true;
        }
        return false;
    }

}