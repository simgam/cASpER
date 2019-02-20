package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class CheckProjectPage extends DialogWrapper {

    private DefaultTableModel model;

    private Project currentProject;
    private List<PackageBean> promiscuousPackageList;
    private List<MethodBean>  featureEnvyList;
    private List<ClassBean> misplacedClassList;
    private List<ClassBean> blobList;

    private JPanel contentPanel;
    private JPanel smellVisual;
    private JPanel bottoni;
    private JPanel panel;

    private JTextArea codeVisual;

    private JTable table;

    public CheckProjectPage(){
        super(true);
        init();
        setTitle("ANALYSIS CODE SMELL");
    }

    public CheckProjectPage(Project currentProj, List<PackageBean> promiscuous, List<ClassBean> blob, List<ClassBean> misplaced, List<MethodBean> feature) {
        super(true);
        this.currentProject=currentProj;
        this.promiscuousPackageList = promiscuous;
        this.featureEnvyList = feature;
        this.misplacedClassList = misplaced;
        this.blobList = blob;
        init();
        setTitle("CODE SMELL ANALYSIS");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        contentPanel = new JPanel(); //pannello principale
        smellVisual = new JPanel(); //pannello per visualizzare lista di smell
        codeVisual = new JTextArea(); //pannello per visualizzare il codice dello smell

        smellVisual.setPreferredSize(new Dimension(380,300));

        //roba della combobox
        panel = new JPanel(); // pannello per la combobox
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
        panel.setMaximumSize(maxSize);
        JLabel comboLabel = new JLabel("Search smells: ");
        comboLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(comboLabel);
        JComboBox<String> selectionCombo = new ComboBox<>();
        selectionCombo.addItem("All");
        selectionCombo.addItem("Promiscuous Package");
        selectionCombo.addItem("Blob");
        selectionCombo.addItem("Misplaced Class");
        selectionCombo.addItem("Feature Envy");
        selectionCombo.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(selectionCombo);
        //fine roba della combobox
        createTable("All");

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()!=false) {
                    setArea();
                }
            }
        });

        selectionCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String selection = (String) cb.getSelectedItem();
                ((DefaultTableModel)table.getModel()).setRowCount(0);
                createTable(selection);
                table.repaint();
            }
        });



        contentPanel.setLayout(new GridLayout(0,2));//layout pannello principale
        contentPanel.setPreferredSize(new Dimension(900,800));
        smellVisual.add(panel,BorderLayout.NORTH);//aggiunge la combobox al pannello laterale
        smellVisual.add(new JBScrollPane(table),BorderLayout.CENTER); // aggiunta tabella smell

        table.setFillsViewportHeight(true);
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
                String whatToReturn; //fullqualified name dell'elemento selezionato nella tabella
                String whereToSearch; //tipo di smell, indice della lista dove cercare il bean
                whatToReturn = (String) table.getValueAt(table.getSelectedRow(),0);
                whereToSearch = (String) table.getValueAt(table.getSelectedRow(),1);
                MethodBean met;
                ClassBean cla;
                ClassBean peppe;
                PackageBean pac;
                if(whereToSearch.equalsIgnoreCase("blob")){
                    for(ClassBean ueue : blobList){
                        if(ueue.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                            cla=ueue;
                            DialogWrapper blob = new BlobPage(currentProject,cla);
                            blob.show();
                        }
                    }
                }

                if(whereToSearch.equalsIgnoreCase("feature envy")){
                    for(MethodBean mariaritaaa : featureEnvyList){
                        if(mariaritaaa.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                            met=mariaritaaa;
                            DialogWrapper feat = new FeatureEnvyPage(met,currentProject);
                            feat.show();
                        }
                    }
                }

                if(whereToSearch.equalsIgnoreCase("promiscuous package")){
                    for(PackageBean gennaro : promiscuousPackageList){
                        if(gennaro.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                            pac=gennaro;
                            DialogWrapper pack = new PromiscuousPackagePage(currentProject,pac);
                            pack.show();
                        }
                    }
                }

                if(whereToSearch.equalsIgnoreCase("misplaced class")){
                    for(ClassBean enzuccio : misplacedClassList){
                        if(enzuccio.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                            peppe=enzuccio;
                            DialogWrapper mis = new MisplacedClassPage(peppe,currentProject);
                            mis.show();
                        }
                    }
                }

            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }

    private void createTable(String selection){
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Member name");
        columnNames.add("Smell detected");
        model = new DefaultTableModel(columnNames,0);


        if(selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("blob")){
            if(blobList!=null) {
                for (ClassBean b : blobList) {
                    Vector<String> tableItem = new Vector<>();
                    tableItem.add(b.getFullQualifiedName());
                    tableItem.add("Blob");
                    model.addRow(tableItem);
                }
            }

        }

        if(selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("promiscuous package")){
            if(promiscuousPackageList!=null) {
                for (PackageBean pp : promiscuousPackageList) {
                    Vector<String> tableItem = new Vector<>();
                    tableItem.add(pp.getFullQualifiedName());
                    tableItem.add("Promicuos Package");
                    model.addRow(tableItem);
                }
            }
        }

        if(selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("feature envy")){
            if(featureEnvyList!=null) {
                for (MethodBean pp : featureEnvyList) {
                    Vector<String> tableItem = new Vector<>();
                    tableItem.add(pp.getFullQualifiedName());
                    tableItem.add("Feature Envy");
                    model.addRow(tableItem);
                }
            }
        }

        if(selection.equalsIgnoreCase("all") || selection.equalsIgnoreCase("misplaced class")){
            if (misplacedClassList!=null) {
                for (ClassBean c : misplacedClassList) {
                    Vector<String> tableItem = new Vector<>();
                    tableItem.add(c.getFullQualifiedName());
                    tableItem.add("Misplaced Class");
                    model.addRow(tableItem);
                }
            }
        }

        if(this.table == null) {
            JTable table = new JBTable();
            this.table = table;
        }
        this.table.setModel(model);
        table.setDefaultEditor(Object.class,null);
    }

    private void setArea(){
        String whatToReturn; //fullqualified name dell'elemento selezionato nella tabella
        String whereToSearch; //tipo di smell, indice della lista dove cercare il bean
        whatToReturn = (String) table.getValueAt(table.getSelectedRow(),0);
        whereToSearch = (String) table.getValueAt(table.getSelectedRow(),1);
        MethodBean met;
        ClassBean cla;
        ClassBean peppe;
        PackageBean pac;
        if(whereToSearch.equalsIgnoreCase("blob")){
            for(ClassBean ueue : blobList){
                if(ueue.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                    cla=ueue;
                    codeVisual.setText(cla.getTextContent());
                }
            }
        }

        if(whereToSearch.equalsIgnoreCase("feature envy")){
            for(MethodBean mariaritaaa : featureEnvyList){
                if(mariaritaaa.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                    met=mariaritaaa;
                    codeVisual.setText(met.getTextContent());
                }
            }
        }

        if(whereToSearch.equalsIgnoreCase("promiscuous package")){
            for(PackageBean gennaro : promiscuousPackageList){
                if(gennaro.getFullQualifiedName().equalsIgnoreCase(whatToReturn)){
                    pac=gennaro;
                    codeVisual.setText(pac.getTextContent());
                }
            }
        }

        if(whereToSearch.equalsIgnoreCase("misplaced class")) {
            for (ClassBean enzuccio : misplacedClassList) {
                if (enzuccio.getFullQualifiedName().equalsIgnoreCase(whatToReturn)) {
                    peppe = enzuccio;
                    codeVisual.setText(peppe.getTextContent());
                }
            }
        }
    }
}
