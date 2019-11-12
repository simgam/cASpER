package it.unisa.ascetic.gui;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.refactor.splitting_algorithm.SplitClasses;
import it.unisa.ascetic.storage.beans.ClassBean;
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

public class BlobPage extends DialogWrapper {

    private RadarMapUtils radarMapUtils;        //roba che serve per le radar map
    private ClassBean classBeanBlob;            //ClassBean sul quale avviene l'analisi
    private List<ClassBean> splittedClasses;    //lista di classi splittate

    private Project project;
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

    private boolean errorOccured;               //serve per determinare se qualcosa è andato storto

    public BlobPage(ClassBean classBeanBlob, Project project) {
        super(true);
        this.classBeanBlob = classBeanBlob;
        this.project = project;
        this.errorOccured = false;
        setResizable(false);
        init();
        setTitle("BLOB PAGE");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        radarMapUtils = new RadarMapUtilsAdapter();

        panelRadarMap = radarMapUtils.createRadarMapFromClassBean(classBeanBlob, classBeanBlob.getFullQualifiedName());

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

        //SETTO TESTO NELLA TEXT AREA
        JPanel app = new JPanel();
        app.setLayout(new BorderLayout(0, 0));
        app.setBorder(new TitledBorder("Text content"));
        StyleText generator = new StyleText();
        area.setStyledDocument(generator.createDocument(classBeanBlob.getTextContent()));

        //SETTO LA TABELLA PER LE METRICHE
        table = new JBTable();
        Vector<String> tableHeaders = new Vector<>();
        tableHeaders.add("LOC");
        tableHeaders.add("WMC");
        tableHeaders.add("RFC");
        tableHeaders.add("CBO");
        tableHeaders.add("LCOM");

        tableHeaders.add("NOA");
        tableHeaders.add("NOM");
        tableHeaders.add("NOPA");

        Vector<String> tableElemet = new Vector<>();
        tableElemet.add(CKMetrics.getLOC(classBeanBlob) + "");
        tableElemet.add(CKMetrics.getWMC(classBeanBlob) + "");
        tableElemet.add(CKMetrics.getRFC(classBeanBlob) + "");
        tableElemet.add(CKMetrics.getCBO(classBeanBlob) + "");
        tableElemet.add(CKMetrics.getLCOM(classBeanBlob) + "");

        tableElemet.add(String.valueOf(CKMetrics.getNOA(classBeanBlob)));
        tableElemet.add(String.valueOf(CKMetrics.getNOM(classBeanBlob)));
        tableElemet.add(String.valueOf(CKMetrics.getNOPA(classBeanBlob)));

        DefaultTableModel model = new DefaultTableModel(tableHeaders, 0);
        model.addRow(tableElemet);

        table.setModel(model);

        //SETTO I LAYOUT DEI PANEL
        panelButton.setLayout(new FlowLayout());
        panelRadarMapMaster.setLayout(new BorderLayout());
        panelWest.setLayout(new GridLayout(2, 1));
        panelEast.setLayout(new BorderLayout());
        panelMetric.setLayout((new BorderLayout()));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

        panelGrid2 = new JPanel();
        panelGrid2.setLayout(new BorderLayout());


        panelRadarMapMaster.add(panelRadarMap, BorderLayout.CENTER);

        panelMetric.setBorder(new TitledBorder("Metrics"));
        panelMetric.add(new JBScrollPane(table));
        table.setFillsViewportHeight(true);
        panelGrid2.add(panelMetric, BorderLayout.CENTER);
        panelGrid2.add(panelButton, BorderLayout.SOUTH);

        panelWest.add(panelRadarMapMaster);
        panelWest.add(panelGrid2);

        contentPanel.add(panelWest);
        JScrollPane scroll = new JScrollPane(area);
        app.add(scroll, BorderLayout.CENTER);
        contentPanel.add(app);

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

                message = "Something went wrong in computing solution";
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    try {
                        splittedClasses = (List<ClassBean>) new SplitClasses().split(classBeanBlob, 0.09);

                        if (splittedClasses.size() == 1) {
                            message += "\nIt is not possible to split the class without introducing new smell";
                            errorOccured = true;
                        }
                    } catch (Exception e) {
                        errorOccured = true;
                    }
                }, "Blob", false, project);

                if (errorOccured) {
                    Messages.showMessageDialog(message, "Oh!No!", Messages.getErrorIcon());
                } else {
                    if (splittedClasses.size() < 2) {
                        message = "Error during creation of solution";
                        Messages.showMessageDialog(message, "Error", Messages.getErrorIcon());
                    } else {
                        BlobWizard blobWizardMock = new BlobWizard(classBeanBlob, splittedClasses, project);
                        blobWizardMock.show();
                    }
                    close(0);
                }
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("CANCEL", 0)};
    }
}