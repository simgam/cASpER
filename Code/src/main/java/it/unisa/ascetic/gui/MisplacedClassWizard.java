package it.unisa.ascetic.gui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.refactor.exceptions.BlobException;
import it.unisa.ascetic.refactor.exceptions.FeatureEnvyException;
import it.unisa.ascetic.refactor.exceptions.MisplacedClassException;
import it.unisa.ascetic.refactor.exceptions.PromiscuousPackageException;
import it.unisa.ascetic.refactor.manipulator.FeatureEnvyRefactoringStrategy;
import it.unisa.ascetic.refactor.manipulator.MisplacedClassRefactoringStrategy;
import it.unisa.ascetic.refactor.strategy.RefactoringManager;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;
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

public class MisplacedClassWizard extends DialogWrapper {

    private Project project;

    private ClassBean smellClass;

    private JPanel radarmaps;
    private JPanel contentPanel;

    private JPanel oldcurrentpackage;
    private JPanel oldenviedpackage;
    private JPanel newenviedpackage;
    private JPanel newcurrentpackage;
    private RadarMapUtils radars;

    boolean errorOccurred;

    public MisplacedClassWizard(ClassBean smellClass, Project project)
     {
        super(true);
        this.smellClass = smellClass;
        this.project=project;
        this.errorOccurred = false;
        setResizable(false);
        init();
        setTitle("MISPLACED CLASS REFACTORING");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        contentPanel = new JPanel(); // pannello principale
        radarmaps = new JPanel(); // pannello per visualizzare le radarMaps
        radars = new RadarMapUtilsAdapter();

        radarmaps.setLayout(new GridLayout(2,2));

        oldcurrentpackage = radars.createRadarMapFromPackageBean(smellClass.getBelongingPackage(), "Old Current Package Topics");
        oldenviedpackage = radars.createRadarMapFromPackageBean(smellClass.getEnviedPackage(), "Old Envied Package Topics");
        newcurrentpackage =  getRadarMapFromNewCurrentPackage(smellClass, smellClass.getBelongingPackage());
        newenviedpackage = getRadarMapFromNewEnviedPackage(smellClass, smellClass.getEnviedPackage());

        oldcurrentpackage.setSize(400,400);
        oldenviedpackage.setSize(400,400);
        newcurrentpackage.setSize(400,400);
        newenviedpackage.setSize(400,400);

        radarmaps.add(oldcurrentpackage);
        radarmaps.add(oldenviedpackage);
        radarmaps.add(newcurrentpackage);
        radarmaps.add(newenviedpackage);
        radarmaps.setPreferredSize(new Dimension(800,900));

        contentPanel.setLayout(new BorderLayout());
        contentPanel.setSize(900, 800);
        contentPanel.add(radarmaps,BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel getRadarMapFromNewCurrentPackage(ClassBean smellClass, PackageBean oldBelongingPackage) {
        TreeMap<String, Integer> belongingPackageTopicsFinali = new TreeMap<>(); // treemap in cui inserisco i topic definitivi del new belonging package
        TopicExtracter extracter1 = new TopicExtracter();
        TreeMap<String, Integer> oldBelongingPackageTopics = extracter1.extractTopicFromPackageBean(oldBelongingPackage);
        Set<Map.Entry<String,Integer>> oldBelongingPackageTopicSet = oldBelongingPackageTopics.entrySet(); //set dei topic del belonging package PRIMA del refactoring

        //istanzia il new belonging package senza la classe smell
        TopicExtracter extracter2 = new TopicExtracter();
        PackageBean newBelongingPackage = createNewBelongingPackage(smellClass, oldBelongingPackage);
        TreeMap<String, Integer> newBelongingPackageTopics = extracter2.extractTopicFromPackageBean(newBelongingPackage);
        Set<Map.Entry<String,Integer>> newBelongingPackageTopicSet = newBelongingPackageTopics.entrySet(); //set di topic del belonging package DOPO il refactoring

        belongingPackageTopicsFinali = setNewTopicsMap(oldBelongingPackageTopicSet, newBelongingPackageTopicSet);
        return radars.getRadarMapPanel(belongingPackageTopicsFinali,"New Current Package Topic");
    }

    private TreeMap<String, Integer> setNewTopicsMap(Set<Map.Entry<String, Integer>> oldBelongingPackageTopicSet, Set<Map.Entry<String, Integer>> newBelongingPackageTopicSet) {
        TreeMap<String, Integer> belongingPackageTopicsFinali = new TreeMap<>(); // treemap in cui inserisco i topic definitivi della new belonging class
        for(Map.Entry<String, Integer> anOldTopic : oldBelongingPackageTopicSet) {  //confronto ogni topic dei set old e new
            for(Map.Entry<String, Integer> aNewTopic : newBelongingPackageTopicSet) {
                if(anOldTopic.getKey().equalsIgnoreCase(aNewTopic.getKey())){ //se i topic hanno la stessa chiave, cioè il nome, allora lo aggiungo alla treemap dei topic finali
                    belongingPackageTopicsFinali.put(anOldTopic.getKey(),aNewTopic.getValue()); //il valore numerico relativo al topic aggiunto è quello presente nei topic della nuova belonging class

                }
            }
        }
        return belongingPackageTopicsFinali;
    }

    private PackageBean createNewBelongingPackage(ClassBean smellClass, PackageBean oldBelongingPackage) {
        String newBelongingPackageTextContent = oldBelongingPackage.getTextContent().replace(smellClass.getTextContent(),"");
        return new PackageBean.Builder(oldBelongingPackage.getFullQualifiedName(),newBelongingPackageTextContent).build();
    }

    private JPanel getRadarMapFromNewEnviedPackage(ClassBean smellClass, PackageBean enviedPackage){
        String newTextContent = enviedPackage.getTextContent()+smellClass.getTextContent();
        PackageBean newEnviedPackageBean = new PackageBean.Builder(enviedPackage.getFullQualifiedName(),newTextContent).build();
        return radars.createRadarMapFromPackageBean(newEnviedPackageBean, "New Envied Class Topic");
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("REFACTOR") {

            String message;
            Icon icon;

            @Override
            protected void doAction(ActionEvent actionEvent) {
                MisplacedClassRefactoringStrategy misplacedClassRefactoringStrategy = new MisplacedClassRefactoringStrategy(smellClass, smellClass.getEnviedPackage(), project);
                RefactoringManager manager = new RefactoringManager(misplacedClassRefactoringStrategy);

                try {
                    manager.executeRefactor();
                } catch (Exception e) {
                    errorOccurred = true;
                    message = e.getMessage();
                    e.printStackTrace();
                }


                if (errorOccurred) {
                    //message = "Something went wrong during refactoring. Press Ctrl+Z to fix";
                    icon = Messages.getErrorIcon();
                } else {
                    message = "Move class refactoring correctly executed";
                    icon = Messages.getInformationIcon();
                }

                close(0);
                Messages.showMessageDialog(message, "Refactor success", icon);


            }
        };
        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }
}
