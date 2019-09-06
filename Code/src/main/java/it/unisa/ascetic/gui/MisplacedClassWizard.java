package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.refactor.manipulator.MisplacedClassRefactoringStrategy;
import it.unisa.ascetic.refactor.strategy.RefactoringManager;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.topic.TopicExtracter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import src.main.java.it.unisa.ascetic.gui.StyleText;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private JPanel livelli;
    private JPanel codici;
    private static StringBuilder textAreaContent;
    boolean errorOccurred;

    public MisplacedClassWizard(ClassBean smellClass, Project project) {
        super(true);
        this.smellClass = smellClass;
        this.project = project;
        this.errorOccurred = false;
        setResizable(false);
        init();
        setTitle("MISPLACED CLASS REFACTORING");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        contentPanel = new JPanel(); // pannello principale
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setPreferredSize(new Dimension(1250, 900));
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        livelli = new JPanel();
        radarmaps = new JPanel(); // pannello per visualizzare le radarMaps
        codici = new JPanel();
        radars = new RadarMapUtilsAdapter();

        livelli.setLayout(new GridLayout(2, 0));
        radarmaps.setLayout(new GridLayout(0, 4));
        codici.setLayout(new GridLayout(0, 2));

        oldcurrentpackage = radars.createRadarMapFromPackageBean(smellClass.getBelongingPackage(), "Old Current Package Topics");
        oldenviedpackage = radars.createRadarMapFromPackageBean(smellClass.getEnviedPackage(), "Old Envied Package Topics");
        newcurrentpackage = getRadarMapFromNewCurrentPackage(smellClass, smellClass.getBelongingPackage());
        newenviedpackage = getRadarMapFromNewEnviedPackage(smellClass, smellClass.getEnviedPackage());

        oldcurrentpackage.setSize(100, 100);
        oldenviedpackage.setSize(200, 200);
        newcurrentpackage.setSize(200, 200);
        newenviedpackage.setSize(200, 200);

        radarmaps.add(oldcurrentpackage);
        radarmaps.add(oldenviedpackage);
        radarmaps.add(newcurrentpackage);
        radarmaps.add(newenviedpackage);

        contentPanel.add(centralPanel, BorderLayout.CENTER);

        createTextArea("Old Text Content", smellClass.getTextContent());
        String newText = smellClass.getEnviedPackage().getTextContent();
        newText = newText.substring(0, newText.length() - 1) + "\n\n" + smellClass.getTextContent();
        createTextArea("New Text Content", newText);

        livelli.add(radarmaps);
        livelli.add(codici);
        contentPanel.add(livelli);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(livelli, BorderLayout.CENTER);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textAreaContent = new StringBuilder();
        textAreaContent.append("By clicking \"REFACTOR\" button, class ");
        textAreaContent.append(smellClass.getFullQualifiedName());
        textAreaContent.append(" will be moved from ");
        textAreaContent.append(smellClass.getBelongingPackage().getFullQualifiedName());
        textAreaContent.append(" to ");
        textAreaContent.append(smellClass.getEnviedPackage().getFullQualifiedName());

        textArea.setText(textAreaContent.toString());

        contentPanel.add(new JBScrollPane(textArea), BorderLayout.SOUTH);

        return contentPanel;
    }

    private JPanel getRadarMapFromNewCurrentPackage(ClassBean smellClass, PackageBean oldBelongingPackage) {
        TreeMap<String, Integer> belongingPackageTopicsFinali = new TreeMap<>(); // treemap in cui inserisco i topic definitivi del new belonging package
        TopicExtracter extracter1 = new TopicExtracter();
        TreeMap<String, Integer> oldBelongingPackageTopics = extracter1.extractTopicFromPackageBean(oldBelongingPackage);
        Set<Map.Entry<String, Integer>> oldBelongingPackageTopicSet = oldBelongingPackageTopics.entrySet(); //set dei topic del belonging package PRIMA del refactoring

        //istanzia il new belonging package senza la classe smell
        TopicExtracter extracter2 = new TopicExtracter();
        PackageBean newBelongingPackage = createNewBelongingPackage(smellClass, oldBelongingPackage);
        TreeMap<String, Integer> newBelongingPackageTopics = extracter2.extractTopicFromPackageBean(newBelongingPackage);
        Set<Map.Entry<String, Integer>> newBelongingPackageTopicSet = newBelongingPackageTopics.entrySet(); //set di topic del belonging package DOPO il refactoring

        belongingPackageTopicsFinali = getStringIntegerTreeMap(oldBelongingPackageTopicSet, newBelongingPackageTopicSet);
        return radars.getRadarMapPanel(belongingPackageTopicsFinali, "New Current Package Topic");
    }

    private void createTextArea(String titolo, String message) {
        JTextPane textContentArea = new JTextPane();
        textContentArea.setEditable(false);
        JPanel nuovo = new JPanel();
        nuovo.setBorder(new TitledBorder(titolo));
        JScrollPane scroll = new JScrollPane(textContentArea);
        nuovo.setLayout(new BorderLayout(0, 0));
        nuovo.add(scroll, BorderLayout.CENTER);
        StyleText generator = new StyleText();
        textContentArea.setStyledDocument(generator.createDocument(message));
        codici.add(nuovo);
    }

    @NotNull
    static TreeMap<String, Integer> getStringIntegerTreeMap(Set<Map.Entry<String, Integer>> oldBelongingClassTopicSet, Set<Map.Entry<String, Integer>> newBelongingClassTopicSet) {
        TreeMap<String, Integer> belongingClassTopicsFinali = new TreeMap<>(); // treemap in cui inserisco i topic definitivi della new belonging class
        for (Map.Entry<String, Integer> anOldTopic : oldBelongingClassTopicSet) {  //confronto ogni topic dei set old e new
            for (Map.Entry<String, Integer> aNewTopic : newBelongingClassTopicSet) {
                if (anOldTopic.getKey().equalsIgnoreCase(aNewTopic.getKey())) { //se i topic hanno la stessa chiave, cioè il nome, allora lo aggiungo alla treemap dei topic finali
                    belongingClassTopicsFinali.put(anOldTopic.getKey(), aNewTopic.getValue()); //il valore numerico relativo al topic aggiunto è quello presente nei topic della nuova belonging classù
                }
            }
        }
        return belongingClassTopicsFinali;
    }

    private PackageBean createNewBelongingPackage(ClassBean smellClass, PackageBean oldBelongingPackage) {
        String newBelongingPackageTextContent = oldBelongingPackage.getTextContent().replace(smellClass.getTextContent(), "");
        return new PackageBean.Builder(oldBelongingPackage.getFullQualifiedName(), newBelongingPackageTextContent).build();
    }

    private JPanel getRadarMapFromNewEnviedPackage(ClassBean smellClass, PackageBean enviedPackage) {
        String newTextContent = enviedPackage.getTextContent() + smellClass.getTextContent();
        PackageBean newEnviedPackageBean = new PackageBean.Builder(enviedPackage.getFullQualifiedName(), newTextContent).build();
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
                    try {
                        FileWriter f = new FileWriter(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "refactoring.txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                close(0);
                Messages.showMessageDialog(message, "Outcome of refactoring", icon);
            }
        };
        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }
}
