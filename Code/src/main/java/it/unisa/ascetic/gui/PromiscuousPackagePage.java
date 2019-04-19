package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class PromiscuousPackagePage extends DialogWrapper {

    private RadarMapUtils radarMapUtils;        //roba che serve per le radar map
    private PackageBean packageBeanPP;            //PackageBean sul quale avviene l'analisi
    private Project project;
    private List<PackageBean> splittedPackages;    //lista di package splittate

    private JButton ignoreButton;               //button che permette di segnalare come falsi positivi gli smell selezionati
    private JButton remindButton;               //button che permette di inserire un reminder nella locazione degli smell selezionati

    private JTextArea area;                     //area di testo dove viene mostrato in dettaglio il codice del CodeSmell selezionato


    private JPanel contentPanel;                //panel che raggruppa tutti gli elementi
    private JPanel panelRadarMapMaster;         //panel che ingloba la radar map
    private JPanel panelRadarMap;
    private JPanel panelMetric;                 //panel per le metriche
    private JPanel panelButton;                 //panel che raggruppa i bottoni
    private JPanel panelWest;                   //panel che raggruppa gli elementi a sinistra
    private JPanel panelEast;                   //panel che raggruppa gli elementi a destra
    private JPanel panelGrid2;                  //panel inserito nella seconda cella del gridLayout

    private JBTable table;                      //tabella dove sono visualizzati i codeSmell

    private boolean errorOccured;               //serve per verificare se qualche cosa è andata storta

    public PromiscuousPackagePage(PackageBean packageBeanPP, Project project) {
        super(true);
        this.packageBeanPP = packageBeanPP;
        this.project = project;
        init();
        setTitle("PROMISCUOUS PACKAGE PAGE");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        radarMapUtils = new RadarMapUtilsAdapter();

        panelRadarMap = radarMapUtils.createRadarMapFromPackageBean(packageBeanPP, packageBeanPP.getFullQualifiedName());

        //INIZIALIZZO I PANEL
        contentPanel = new JPanel();            //pannello principale
        panelButton = new JPanel();             //pannello dei bottoni
        panelRadarMapMaster = new JPanel();     //pannello che ingloba le radarMap
        panelMetric = new JPanel();             //pannello che ingloba le metriche
        panelWest = new JPanel();               //pannello che ingloba gli elementi di sinistra
        panelEast = new JPanel();               //pannello che ingloba gli elementi di destra


        //INIZIALIZZO LA TABELLA E LA TEXT AREA
        area = new JTextArea();                 //text area dove viene visualizzato il codice in esame
        table = new JBTable();                  //tabella dove sono presenti gli smell da prendere in esame
        area.setEditable(false);

        //INIZIALIZZO I BUTTON
        ignoreButton = new JButton();           //bottone ignore
        remindButton = new JButton();           //bottone remind

        //SETTO IL TESTO NEI BUTTON
        ignoreButton.setText("Ignore");
        remindButton.setText("Remind");

        //SETTO A "NON UTILIZZABILI" I BUTTON A CUI NON CORRISPONDONO NESSUNA FUNZIONE IMPLEMENTATA
        ignoreButton.setEnabled(false);
        remindButton.setEnabled(false);

        //SETTO TESTO NELLA TEXT AREA
        area.append(packageBeanPP.getTextContent());


        //SETTO LA TABELLA PER LE METRICHE
        table = new JBTable();
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("Package Name");
        tableHeaders.add("MIC");
        Vector<String> tableElemet = new Vector<>();

        tableElemet.add(packageBeanPP.getFullQualifiedName());
        tableElemet.add(CKMetrics.computeMediumIntraConnectivity(packageBeanPP) + "");

        DefaultTableModel model = new DefaultTableModel(tableHeaders, 0);
        model.addRow(tableElemet);
        table.setModel(model);
        table.setDefaultEditor(Object.class, null);     //setta la table non editabile

        //SETTO I LAYOUT DEI PANEL
        panelButton.setLayout(new FlowLayout());
        panelRadarMapMaster.setLayout(new BorderLayout());
        panelWest.setLayout(new GridLayout(2, 1));
        panelEast.setLayout(new BorderLayout());
        panelMetric.setLayout((new BorderLayout()));
        contentPanel.setLayout(new BorderLayout());

        //AGGIUNGO COMPONENTI AI VARI PANEL

        panelButton.add(ignoreButton);
        panelButton.add(remindButton);

        panelGrid2 = new JPanel();
        panelGrid2.setLayout(new BorderLayout());


        panelRadarMapMaster.add(panelRadarMap, BorderLayout.CENTER);

        panelMetric.add(new JBScrollPane(table));
        table.setFillsViewportHeight(true);
        panelGrid2.add(panelMetric, BorderLayout.CENTER);
        panelGrid2.add(panelButton, BorderLayout.SOUTH);

        panelWest.add(panelRadarMapMaster);
        panelWest.add(panelGrid2);

        contentPanel.add(panelWest, BorderLayout.CENTER);
        contentPanel.add(new JBScrollPane(area), BorderLayout.EAST);

        contentPanel.setPreferredSize(new Dimension(900, 800));


        return contentPanel;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("REFACTOR") {

            @Override
            protected void doAction(ActionEvent actionEvent) {

                Messages.showMessageDialog("Promiscuous Package Reafctoring coming soon", "Attention !", Messages.getInformationIcon());
                /*String message;

                ProgressManager.getInstance().runProcessWithProgressSynchronously(()->{
                    try {
                        splittedPackages = (List<PackageBean>) new SplitPackages().split(packageBeanPP, 0.5);
                    } catch (Exception e) {
                        errorOccured = true;
                    }
                },"Blob",false,project);

                if(errorOccured){
                    message = "Something went wrong in computing solution";
                    Messages.showMessageDialog(message,"Oh!No!",Messages.getErrorIcon());
                } else {
                    message = "Now we should show Promiscuous Package Wizard that isn't ready yet";
                    Messages.showMessageDialog(message,"Hei !",Messages.getInformationIcon());
                }*/

                //PromiscuousWizard promiscuousWizard = new PromiscuousWizard(packageBeanPP, splittedPackages, project);
                //promiscuousWizard.show();
                close(1);
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }
}
