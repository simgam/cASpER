package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.table.JBTable;
import it.unisa.ascetic.analysis.code_smell.CodeSmell;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

public class CheckProjectPage extends DialogWrapper {

    private DefaultTableModel model;

    private Project currentProject;
    private List<PackageBean> promiscuousPackageList;
    private List<MethodBean> featureEnvyList;
    private List<ClassBean> misplacedClassList;
    private List<ClassBean> blobList;
    private String algorithm;
    private String selection;

    private JPanel contentPanel;
    private JPanel smellVisual;
    private JPanel panel;

    private JPanel pannello;
    private JTextArea codeVisual;
    private JTable table;
    private DecimalFormat df = new DecimalFormat("0.000");

    public CheckProjectPage() {
        super(true);
        init();
        setTitle("CODE SMELL ANALYSIS");
    }

    public CheckProjectPage(Project currentProj, List<PackageBean> promiscuous, List<ClassBean> blob, List<ClassBean> misplaced, List<MethodBean> feature, String algorithm) {
        super(true);
        this.currentProject = currentProj;
        this.promiscuousPackageList = promiscuous;
        this.featureEnvyList = feature;
        this.misplacedClassList = misplaced;
        this.blobList = blob;
        this.algorithm = algorithm;
        this.selection = "all";
        init();
        setTitle("CODE SMELL ANALYSIS");
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        contentPanel = new JPanel(); //pannello principale
        contentPanel.setLayout(new BorderLayout(0, 0));
        smellVisual = new JPanel(); //pannello per visualizzare lista di smell
        codeVisual = new JTextArea(); //pannello per visualizzare il codice dello smell

        smellVisual.setPreferredSize(new Dimension(350, 800));

        //roba della combobox
        panel = new JPanel();
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
        panel.setMaximumSize(maxSize);
        panel.setLayout(new BorderLayout(0, 0));
        //fine roba della combobox

        smellVisual.setBorder(new TitledBorder("List"));
        contentPanel.setLayout(new GridLayout(0, 2));//layout pannello principale
        contentPanel.setPreferredSize(new Dimension(1000, 800));
        smellVisual.setLayout(new BorderLayout(0, 0));
        smellVisual.add(panel, BorderLayout.NORTH);

        pannello = new JPanel();
        panel.add(pannello, BorderLayout.NORTH);
        JLabel comboLabel = new JLabel("Search smells: ");
        pannello.add(comboLabel);
        comboLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JComboBox<String> selectionSmell = new ComboBox<>();
        pannello.add(selectionSmell);
        selectionSmell.addItem("All");
        selectionSmell.addItem("Promiscuous Package");
        selectionSmell.addItem("Blob");
        selectionSmell.addItem("Misplaced Class");
        selectionSmell.addItem("Feature Envy");
        selectionSmell.setAlignmentY(Component.CENTER_ALIGNMENT);
        pannello.add(selectionSmell);
        //fine roba della combobox

        if (algorithm.equalsIgnoreCase("all")) {
            JComboBox<String> selectionAlgorithm = new ComboBox<>();
            pannello.add(selectionAlgorithm);
            selectionAlgorithm.addItem("All");
            selectionAlgorithm.addItem("Textual");
            selectionAlgorithm.addItem("Structural");

            selectionAlgorithm.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent a) {
                    JComboBox cb = (JComboBox) a.getSource();
                    setAlgorithm((String) cb.getSelectedItem());
                    ((DefaultTableModel) table.getModel()).setRowCount(0);
                    createTable();
                    table.repaint();
                }
            });
        }

        createTable();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() != false) {
                    setArea();
                }
            }
        });

        selectionSmell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                setSelection((String) cb.getSelectedItem());
                ((DefaultTableModel) table.getModel()).setRowCount(0);
                createTable();
                table.repaint();
            }
        });

        smellVisual.setBorder(new TitledBorder("List"));
        contentPanel.setLayout(new GridLayout(0, 2));//layout pannello principale

        JScrollPane scroll = new JScrollPane(table);
        smellVisual.add(scroll, BorderLayout.CENTER);// aggiunta tabella smell

        contentPanel.add(smellVisual);
        contentPanel.add(new JScrollPane(codeVisual));

        return contentPanel;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("INSPECT") {

            @Override
            protected void doAction(ActionEvent actionEvent) {
                try {
                    String whatToReturn; //fullqualified name dell'elemento selezionato nella tabella
                    String whereToSearch; //tipo di smell, indice della lista dove cercare il bean
                    whatToReturn = (String) table.getValueAt(table.getSelectedRow(), 0);
                    whereToSearch = (String) table.getValueAt(table.getSelectedRow(), 1);
                    if (whereToSearch.equalsIgnoreCase("blob")) {
                        for (ClassBean c : blobList) {
                            if (c.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                                DialogWrapper blob = new BlobPage(c, currentProject);
                                blob.show();
                            }
                        }
                    }

                    if (whereToSearch.equalsIgnoreCase("feature envy")) {
                        for (MethodBean m : featureEnvyList) {
                            if (m.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                                DialogWrapper feat = new FeatureEnvyPage(m, currentProject);
                                feat.show();
                            }
                        }
                    }

                    if (whereToSearch.equalsIgnoreCase("promiscuous package")) {
                        for (PackageBean p : promiscuousPackageList) {
                            if (p.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                                DialogWrapper prom = new PromiscuousPackagePage(p, currentProject);
                                prom.show();
                            }
                        }
                    }

                    if (whereToSearch.equalsIgnoreCase("misplaced class")) {
                        for (ClassBean c : misplacedClassList) {
                            if (c.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                                DialogWrapper mis = new MisplacedClassPage(c, currentProject);
                                mis.show();
                            }
                        }
                    }

                } catch (ArrayIndexOutOfBoundsException ex) {
                    String message = "Seleziona un elemento";
                    Messages.showMessageDialog(message, "Warning", Messages.getWarningIcon());
                }
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }

    private void createTable() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Member name");
        columnNames.add("Smell detected");
        columnNames.add("Algorithm used");
        columnNames.add("index");
        model = new DefaultTableModel(columnNames, 0);
        Vector<String> tableItem;
        String selection = getSelection();
        String algorithm = getAlgorithm();
        String used;

        if (selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("blob")) {
            if (blobList != null) {
                for (ClassBean c : blobList) {
                    for (CodeSmell smell : c.getAffectedSmell()) {
                        tableItem = new Vector<String>();
                        if (smell.getSmellName().equalsIgnoreCase("blob")) {
                            used = smell.getAlgoritmsUsed();
                            if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("textual")) && used.equalsIgnoreCase("textual")){
                                tableItem.add(c.getFullQualifiedName());
                                tableItem.add(smell.getSmellName());
                                tableItem.add(smell.getAlgoritmsUsed());
                                tableItem.add(df.format(smell.getIndex()));
                                model.addRow(tableItem);
                            }
                            if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("structural")) && used.equalsIgnoreCase("structural")){
                                tableItem.add(c.getFullQualifiedName());
                                tableItem.add(smell.getSmellName());
                                tableItem.add(smell.getAlgoritmsUsed());
                                tableItem.add("----");
                                //tableItem.add(df.format(smell.getIndex()));
                                model.addRow(tableItem);
                            }
                        }
                    }
                }
            }
        }


        if (selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("misplaced class")) {
            if (blobList != null) {
                for (ClassBean c : misplacedClassList) {
                    for (CodeSmell smell : c.getAffectedSmell()) {
                        tableItem = new Vector<String>();
                        used = smell.getAlgoritmsUsed();
                        if (smell.getSmellName().equalsIgnoreCase("misplaced class")) {
                            if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("textual")) && used.equalsIgnoreCase("textual")){
                                tableItem.add(c.getFullQualifiedName());
                                tableItem.add(smell.getSmellName());
                                tableItem.add(smell.getAlgoritmsUsed());
                                tableItem.add(df.format(smell.getIndex()));
                                model.addRow(tableItem);
                            }
                            if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("structural")) && used.equalsIgnoreCase("structural")){
                                tableItem.add(c.getFullQualifiedName());
                                tableItem.add(smell.getSmellName());
                                tableItem.add(smell.getAlgoritmsUsed());
                                tableItem.add("----");
                                //tableItem.add(df.format(smell.getIndex()));
                                model.addRow(tableItem);
                            }
                        }
                    }
                }
            }
        }

        if (selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("promiscuous package")) {
            if (promiscuousPackageList != null) {
                for (PackageBean pp : promiscuousPackageList) {
                    for (CodeSmell smell : pp.getAffectedSmell()) {
                        tableItem = new Vector<String>();
                        used = smell.getAlgoritmsUsed();
                        if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("textual")) && used.equalsIgnoreCase("textual")){
                            tableItem.add(pp.getFullQualifiedName());
                            tableItem.add(smell.getSmellName());
                            tableItem.add(smell.getAlgoritmsUsed());
                            tableItem.add(df.format(smell.getIndex()));
                            model.addRow(tableItem);
                        }
                        if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("structural")) && used.equalsIgnoreCase("structural")){
                            tableItem.add(pp.getFullQualifiedName());
                            tableItem.add(smell.getSmellName());
                            tableItem.add(smell.getAlgoritmsUsed());
                            tableItem.add("----");
                            //tableItem.add(df.format(smell.getIndex()));
                            model.addRow(tableItem);
                        }
                    }
                }
            }
        }

        if (selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("feature envy")) {
            if (featureEnvyList != null) {
                for (MethodBean m : featureEnvyList) {
                    for (CodeSmell smell : m.getAffectedSmell()) {
                        tableItem = new Vector<String>();
                        used = smell.getAlgoritmsUsed();
                        if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("textual")) && used.equalsIgnoreCase("textual")){
                            tableItem.add(m.getFullQualifiedName());
                            tableItem.add(smell.getSmellName());
                            tableItem.add(smell.getAlgoritmsUsed());
                            tableItem.add(df.format(smell.getIndex()));
                            model.addRow(tableItem);
                        }
                        if ((algorithm.equalsIgnoreCase("all") || algorithm.equalsIgnoreCase("structural")) && used.equalsIgnoreCase("structural")){
                            tableItem.add(m.getFullQualifiedName());
                            tableItem.add(smell.getSmellName());
                            tableItem.add(smell.getAlgoritmsUsed());
                            tableItem.add("----");
                            //tableItem.add(df.format(smell.getIndex()));
                            model.addRow(tableItem);
                        }
                    }
                }
            }
        }

        if (this.table == null) {
            JTable table = new JBTable();
            this.table = table;
        }
        this.table.setModel(model);
        table.setDefaultEditor(Object.class, null);
    }

    private void setArea() {
        String whatToReturn; //fullqualified name dell'elemento selezionato nella tabella
        String whereToSearch; //tipo di smell, indice della lista dove cercare il bean
        try {
            whatToReturn = (String) table.getValueAt(table.getSelectedRow(), 0);
            whereToSearch = (String) table.getValueAt(table.getSelectedRow(), 1);

            if (whereToSearch.equalsIgnoreCase("blob")) {
                for (ClassBean c : blobList) {
                    if (c.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                        codeVisual.append(c.getTextContent());
                    }
                }
            }

            if (whereToSearch.equalsIgnoreCase("feature envy")) {
                for (MethodBean m : featureEnvyList) {
                    if (m.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                        codeVisual.setText(m.getTextContent());
                    }
                }
            }

            if (whereToSearch.equalsIgnoreCase("promiscuous package")) {
                for (PackageBean p : promiscuousPackageList) {
                    if (p.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                        codeVisual.setText(p.getTextContent());
                    }
                }
            }

            if (whereToSearch.equalsIgnoreCase("misplaced class")) {
                for (ClassBean c : misplacedClassList) {
                    if (c.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                        codeVisual.setText(c.getTextContent());
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }
}