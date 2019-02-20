package it.unisa.ascetic.gui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.refactor.exceptions.BlobException;
import it.unisa.ascetic.refactor.exceptions.FeatureEnvyException;
import it.unisa.ascetic.refactor.exceptions.MisplacedClassException;
import it.unisa.ascetic.refactor.exceptions.PromiscuousPackageException;
import it.unisa.ascetic.refactor.manipulator.FeatureEnvyRefactoringStrategy;
import it.unisa.ascetic.refactor.strategy.RefactoringManager;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.topic.TopicExtracter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FeatureEnvyWizard extends DialogWrapper {

    private Project project;

    private MethodBean smellMethod;

    private JPanel radarmaps;
    private JPanel contentPanel;

    private JPanel oldcurrentclass;
    private JPanel oldenviedclass;
    private JPanel newenviedclass;
    private JPanel newcurrentclass;
    private RadarMapUtils radars;

    private boolean errorOccurred;

    public FeatureEnvyWizard(MethodBean smellMethod, Project project)
    {
        super(true);
        this.smellMethod = smellMethod;
        this.project=project;
        this.errorOccurred = false;
        init();
        setTitle("FEATURE ENVY REFACTORING");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        contentPanel = new JPanel(); // pannello principale
        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
        contentPanel.setSize(900, 800);
        radarmaps = new JPanel(); // pannello per visualizzare le radarMaps
        radars = new RadarMapUtilsAdapter();

        radarmaps.setLayout(new GridLayout(2,2));

        oldcurrentclass = radars.createRadarMapFromClassBean(smellMethod.getBelongingClass(), "Old Current Class Topics");
        oldenviedclass = radars.createRadarMapFromClassBean(smellMethod.getEnviedClass(), "Old Envied Class Topics");
        newcurrentclass =  getRadarMapFromNewCurrentClass(smellMethod, new ClassBean.Builder(smellMethod.getBelongingClass().getFullQualifiedName(), smellMethod.getBelongingClass().getTextContent()).build());
        newenviedclass = getRadarMapFromNewEnviedClass(smellMethod, smellMethod.getEnviedClass());

        oldcurrentclass.setSize(400,400);
        oldenviedclass.setSize(400,400);
        newcurrentclass.setSize(400,400);
        newenviedclass.setSize(400,400);
        radarmaps.add(oldcurrentclass);
        radarmaps.add(oldenviedclass);
        radarmaps.add(newcurrentclass);
        radarmaps.add(newenviedclass);
        radarmaps.setPreferredSize(new Dimension(800,800));

        contentPanel.add(radarmaps);

        contentPanel.add(Box.createVerticalGlue());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        StringBuilder textAreaContent = new StringBuilder();
        textAreaContent.append("By clicking \"REFACTOR\" button, method ");
        textAreaContent.append(smellMethod.getFullQualifiedName());
        textAreaContent.append(" will be moved from ");
        textAreaContent.append(smellMethod.getBelongingClass().getFullQualifiedName());
        textAreaContent.append(" to");
        textAreaContent.append(smellMethod.getEnviedClass().getFullQualifiedName());

        textArea.setText(textAreaContent.toString());

        contentPanel.add(new JBScrollPane(textArea));

        return contentPanel;
    }

    private JPanel getRadarMapFromNewCurrentClass(MethodBean smellMethod, ClassBean oldBelongingClass) {
        TreeMap<String, Integer> belongingClassTopicsFinali = new TreeMap<>(); // treemap in cui inserisco i topic definitivi della new belonging class
        TopicExtracter extracter1 = new TopicExtracter();
        TreeMap<String, Integer> oldBelongingClassTopics = extracter1.extractTopicFromClassBean(oldBelongingClass);
        Set<Map.Entry<String,Integer>> oldBelongingClassTopicSet = oldBelongingClassTopics.entrySet(); //set dei topic della belonging class PRIMA del refactoring   

        //istanzia il new belonging class senza il metodo smell
        TopicExtracter extracter2 = new TopicExtracter();
        ClassBean newBelongingClass = createNewBelongingClass(smellMethod, oldBelongingClass);
        TreeMap<String, Integer> newBelongingClassTopics = extracter2.extractTopicFromClassBean(newBelongingClass);
        Set<Map.Entry<String,Integer>> newBelongingClassTopicSet = newBelongingClassTopics.entrySet(); //set di topic della belonging class DOPO il refactoring

        belongingClassTopicsFinali = setNewTopicsMap(oldBelongingClassTopicSet, newBelongingClassTopicSet);
        return radars.getRadarMapPanel(belongingClassTopicsFinali,"New Current Class Topic");
    }

    private TreeMap<String, Integer> setNewTopicsMap(Set<Map.Entry<String, Integer>> oldBelongingClassTopicSet, Set<Map.Entry<String, Integer>> newBelongingClassTopicSet) {
        TreeMap<String, Integer> belongingClassTopicsFinali = new TreeMap<>(); // treemap in cui inserisco i topic definitivi della new belonging class
        for(Map.Entry<String, Integer> anOldTopic : oldBelongingClassTopicSet) {  //confronto ogni topic dei set old e new
            for(Map.Entry<String, Integer> aNewTopic : newBelongingClassTopicSet) {
                if(anOldTopic.getKey().equalsIgnoreCase(aNewTopic.getKey())){ //se i topic hanno la stessa chiave, cioè il nome, allora lo aggiungo alla treemap dei topic finali
                    belongingClassTopicsFinali.put(anOldTopic.getKey(),aNewTopic.getValue()); //il valore numerico relativo al topic aggiunto è quello presente nei topic della nuova belonging class

                }
            }
        }
        return belongingClassTopicsFinali;
    }

    private ClassBean createNewBelongingClass(MethodBean smellMethod, ClassBean oldBelongingClass) {
        String newBelongingClassTextContent = oldBelongingClass.getTextContent().replace(smellMethod.getTextContent(),"");
        return new ClassBean.Builder(oldBelongingClass.getFullQualifiedName(),newBelongingClassTextContent).build();
    }


    private JPanel getRadarMapFromNewEnviedClass(MethodBean smellMethod, ClassBean enviedClass){
        String newTextContent = enviedClass.getTextContent()+smellMethod.getTextContent();
        ClassBean newEnviedClassBean = new ClassBean.Builder(enviedClass.getFullQualifiedName(),newTextContent).build();
        return radars.createRadarMapFromClassBean(newEnviedClassBean, "New Envied Class Topic");
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("REFACTOR") {

            String message;
            Icon icon;

            @Override
            protected void doAction(ActionEvent actionEvent) {
                FeatureEnvyRefactoringStrategy featureEnvyRefactoringStrategy = new FeatureEnvyRefactoringStrategy(smellMethod, project);
                RefactoringManager refactoringManager = new RefactoringManager(featureEnvyRefactoringStrategy);


                WriteCommandAction.runWriteCommandAction(project, () -> {
                    try {
                        refactoringManager.executeRefactor();
                    } catch (Exception e){
                        errorOccurred = true;
                        message = e.getMessage();
                    }
                });




                if(errorOccurred){
                    //message = "Something went wrong during refactoring. Press Ctrl+Z to fix";
                    icon = Messages.getErrorIcon();
                } else {
                    message = "Move method refactoring correctly executed";
                    icon = Messages.getInformationIcon();
                }

                close(0);
                Messages.showMessageDialog(message,"Refactor success",icon);

            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }
}
