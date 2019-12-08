package it.unisa.casper.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import it.unisa.casper.gui.radarMap.RadarMapUtils;
import it.unisa.casper.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.casper.storage.beans.MethodBean;
import it.unisa.casper.structuralMetrics.CKMetrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartPanel;
import src.main.java.it.unisa.casper.gui.StyleText;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class FeatureEnvyPage extends DialogWrapper {

    private Project project;
    private MethodBean featureEnvyBean;
    private JPanel centerPanel;
    private JPanel radarmapContainer;
    private JPanel methodRadarmapContainer;
    private JPanel currentClassRadarmapContainer;
    private JPanel enviedClassRadarmapContainer;
    private JPanel tableContainer;
    private RadarMapUtils radarMapGenerator;
    private JTable table;
    private JPanel level2Panel;

    protected FeatureEnvyPage(boolean canBeParent) {
        super(canBeParent);
    }

    public FeatureEnvyPage(MethodBean featureEnvyBean, Project project) {
        super(true);
        this.featureEnvyBean = featureEnvyBean;
        this.project = project;
        this.radarMapGenerator = new RadarMapUtilsAdapter();
        setResizable(false);
        init();
        setTitle("FEATURE ENVY ANALYSIS");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        //main panel init
        centerPanel = new JPanel();
        centerPanel.setBorder(JBUI.Borders.empty(5));
        centerPanel.setLayout(new GridLayout(2, 1, 0, 0));
        centerPanel.setPreferredSize(new Dimension(1050, 900));

        //radarmap containre init
        radarmapContainer = new JPanel();
        centerPanel.add(radarmapContainer);
        radarmapContainer.setLayout(new BoxLayout(radarmapContainer, BoxLayout.X_AXIS));

        Dimension radarmapPreferredSize = new Dimension(100, 300);
        JPanel methodRadarmap = radarMapGenerator.createRadarMapFromMethodBean(featureEnvyBean, featureEnvyBean.getFullQualifiedName());
        methodRadarmap.setPreferredSize(radarmapPreferredSize);
        methodRadarmapContainer = new JPanel();
        methodRadarmapContainer.setLayout(new BorderLayout());
        methodRadarmapContainer.add(methodRadarmap);
        methodRadarmapContainer.setPreferredSize(radarmapPreferredSize);
        radarmapContainer.add(methodRadarmapContainer);

        radarmapContainer.add(Box.createHorizontalGlue());

        currentClassRadarmapContainer = new JPanel();
        currentClassRadarmapContainer.setLayout(new BorderLayout());
        ChartPanel currentClassRadarmap = radarMapGenerator.createRadarMapFromClassBean(featureEnvyBean.getBelongingClass(), featureEnvyBean.getBelongingClass().getFullQualifiedName());
        currentClassRadarmap.setPreferredSize(radarmapPreferredSize);
        currentClassRadarmapContainer.add(currentClassRadarmap);
        currentClassRadarmap.setPreferredSize(radarmapPreferredSize);
        radarmapContainer.add(currentClassRadarmapContainer);

        radarmapContainer.add(Box.createHorizontalGlue());

        enviedClassRadarmapContainer = new JPanel();
        enviedClassRadarmapContainer.setLayout(new BorderLayout());
        ChartPanel enviedClassRadarmap = radarMapGenerator.createRadarMapFromClassBean(featureEnvyBean.getEnviedClass(), featureEnvyBean.getEnviedClass().getFullQualifiedName());
        enviedClassRadarmap.setPreferredSize(radarmapPreferredSize);
        enviedClassRadarmapContainer.add(enviedClassRadarmap);
        enviedClassRadarmapContainer.setPreferredSize(radarmapPreferredSize);
        radarmapContainer.add(enviedClassRadarmapContainer);

        level2Panel = new JPanel();
        JPanel p = new JPanel();
        level2Panel.setLayout(new GridLayout(0, 2, 0, 0));
        centerPanel.add(level2Panel);

        JTextPane textContentArea = new JTextPane();
        textContentArea.setEditable(false);
        p.setBorder(new TitledBorder("Text Content"));
        JScrollPane scroll = new JScrollPane(textContentArea);
        p.setLayout(new BorderLayout(0, 0));
        p.add(scroll, BorderLayout.CENTER);
        StyleText generator = new StyleText();
        textContentArea.setStyledDocument(generator.createDocument(featureEnvyBean.getTextContent()));
        level2Panel.add(p);

        tableContainer = new JPanel();
        tableContainer.setLayout(new BorderLayout());
        level2Panel.add(tableContainer);
        createTable();
        return centerPanel;
    }

    private void createTable() {
        table = new JBTable();

        String[] columnsNames = {"Method", "LOC", "McCabeComplexity"};
        DefaultTableModel tableModel = new DefaultTableModel(columnsNames, 0);

        Vector<String> row = new Vector<>();
        row.add(featureEnvyBean.getFullQualifiedName());
        row.add(CKMetrics.getLOC(featureEnvyBean) + "");
        row.add(CKMetrics.getMcCabeCycloComplexity(featureEnvyBean) + "");
        tableModel.addRow(row);

        table.setModel(tableModel);
        tableContainer.add(new JScrollPane(table));
        tableContainer.setBorder(new TitledBorder("Metrics"));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //table.setFillsViewportHeight(true);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("FIND SOLUTION") {

            @Override
            protected void doAction(ActionEvent actionEvent) {
                FeatureEnvyWizard featureEnvyWizard = new FeatureEnvyWizard(featureEnvyBean, project);
                featureEnvyWizard.show();
                close(0);
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("CANCEL", 0)};
    }
}
