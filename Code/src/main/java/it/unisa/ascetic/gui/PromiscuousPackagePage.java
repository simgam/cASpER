package it.unisa.ascetic.gui;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import it.unisa.ascetic.analysis.splitting_algorithm.SplitPackages;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import src.main.java.it.unisa.ascetic.gui.StyleText;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

    private JTextPane area;                     //area di testo dove viene mostrato in dettaglio il codice del CodeSmell selezionato

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
        setResizable(false);
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
        area = new JTextPane();                 //text area dove viene visualizzato il codice in esame
        table = new JBTable();                  //tabella dove sono presenti gli smell da prendere in esame
        area.setEditable(false);

        //SETTO TESTO NELLA TEXT AREA
        StyleText generator = new StyleText();
        area.setStyledDocument(generator.createDocument(packageBeanPP.getTextContent()));

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
        contentPanel.setLayout(new GridLayout(0, 2, 0, 0));

        panelGrid2 = new JPanel();
        panelGrid2.setLayout(new BorderLayout());

        panelRadarMapMaster.add(panelRadarMap, BorderLayout.CENTER);

        panelMetric.add(new JBScrollPane(table));
        panelMetric.setBorder(new TitledBorder("Metrics"));
        table.setFillsViewportHeight(true);
        panelGrid2.add(panelMetric, BorderLayout.CENTER);
        panelGrid2.add(panelButton, BorderLayout.SOUTH);

        panelWest.add(panelRadarMapMaster);
        panelWest.add(panelGrid2);

        contentPanel.add(panelWest, BorderLayout.CENTER);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0, 0));
        textPanel.setBorder(new TitledBorder("Text Content"));
        textPanel.add(new JBScrollPane(area), BorderLayout.CENTER);
        contentPanel.add(textPanel, BorderLayout.EAST);

        contentPanel.setPreferredSize(new Dimension(1050, 900));

        return contentPanel;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("FIND SOLUTION") {

            String message;

            @Override
            protected void doAction(ActionEvent actionEvent) {

                //Messages.showMessageDialog("Promiscuous Package Refactoring coming soon", "Attention !", Messages.getInformationIcon());
                message = "Something went wrong in computing solution";
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    try {
                        splittedPackages = (List<PackageBean>) new SplitPackages().split(packageBeanPP, 0.5);
                    } catch (Exception e) {
                        errorOccured = true;
                    }
                }, "Blob", false, project);

                if (errorOccured) {
                    Messages.showMessageDialog(message, "Oh!No!", Messages.getErrorIcon());
                } else {
                    if (splittedPackages.size() < 2) {
                        message = "Error during creation of solution";
                        Messages.showMessageDialog(message, "Error", Messages.getErrorIcon());
                    } else {
                        PromiscuousPackageWizard promiscuousWizard = new PromiscuousPackageWizard(packageBeanPP, splittedPackages, project);
                        promiscuousWizard.show();
                    }
                    close(1);
                }
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }
}
