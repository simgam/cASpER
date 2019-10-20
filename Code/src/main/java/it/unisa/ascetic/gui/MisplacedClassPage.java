package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartPanel;
import src.main.java.it.unisa.ascetic.gui.StyleText;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class MisplacedClassPage extends DialogWrapper {

    private Project project;
    private ClassBean misplacedClassBean;
    private JPanel centerPanel;
    private JPanel radarmapContainer;
    private JPanel methodRadarmapContainer;
    private JPanel currentClassRadarmapContainer;
    private JPanel enviedClassRadarmapContainer;
    private JPanel tableContainer;
    private RadarMapUtils radarMapGenerator;
    private JTable table;
    private JPanel level2Panel;

    protected MisplacedClassPage(boolean canBeParent) {
        super(canBeParent);
    }

    public MisplacedClassPage(ClassBean misplacedClassBean, Project project) {
        super(true);
        this.misplacedClassBean = misplacedClassBean;
        this.project = project;
        this.radarMapGenerator = new RadarMapUtilsAdapter();
        setResizable(false);
        init();
        setTitle("MISPLACED CLASS ANALYSIS");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        //main panel init
        centerPanel = new JPanel();
        centerPanel.setBorder(JBUI.Borders.empty(5));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setPreferredSize(new Dimension(1050, 900));

        //radarmap containre init
        radarmapContainer = new JPanel();
        centerPanel.add(radarmapContainer);
        radarmapContainer.setLayout(new BoxLayout(radarmapContainer, BoxLayout.X_AXIS));


        Dimension radarmapPreferredSize = new Dimension(100, 300);
        JPanel methodRadarmap = radarMapGenerator.createRadarMapFromClassBean(misplacedClassBean, misplacedClassBean.getFullQualifiedName());
        methodRadarmap.setPreferredSize(radarmapPreferredSize);
        methodRadarmapContainer = new JPanel();
        methodRadarmapContainer.setLayout(new BorderLayout());
        methodRadarmapContainer.add(methodRadarmap);
        methodRadarmapContainer.setPreferredSize(radarmapPreferredSize);
        radarmapContainer.add(methodRadarmapContainer);

        radarmapContainer.add(Box.createHorizontalGlue());

        currentClassRadarmapContainer = new JPanel();
        currentClassRadarmapContainer.setLayout(new BorderLayout());
        ChartPanel currentClassRadarmap = radarMapGenerator.createRadarMapFromPackageBean(misplacedClassBean.getBelongingPackage(), misplacedClassBean.getBelongingPackage().getFullQualifiedName());
        currentClassRadarmap.setPreferredSize(radarmapPreferredSize);
        currentClassRadarmapContainer.add(currentClassRadarmap);
        currentClassRadarmap.setPreferredSize(radarmapPreferredSize);
        radarmapContainer.add(currentClassRadarmapContainer);

        radarmapContainer.add(Box.createHorizontalGlue());

        enviedClassRadarmapContainer = new JPanel();
        enviedClassRadarmapContainer.setLayout(new BorderLayout());
        ChartPanel enviedClassRadarmap = radarMapGenerator.createRadarMapFromPackageBean(misplacedClassBean.getEnviedPackage(), misplacedClassBean.getEnviedPackage().getFullQualifiedName());
        enviedClassRadarmap.setPreferredSize(radarmapPreferredSize);
        enviedClassRadarmapContainer.add(enviedClassRadarmap);
        enviedClassRadarmapContainer.setPreferredSize(radarmapPreferredSize);
        radarmapContainer.add(enviedClassRadarmapContainer);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));


        level2Panel = new JPanel();
        JPanel p = new JPanel();
        level2Panel.setLayout(new BoxLayout(level2Panel, BoxLayout.X_AXIS));
        centerPanel.add(level2Panel);

        JTextPane textContentArea = new JTextPane();
        textContentArea.setEditable(false);
        p.setBorder(new TitledBorder("Text Content"));
        JScrollPane scroll = new JScrollPane(textContentArea);
        p.setLayout(new BorderLayout(0, 0));
        p.add(scroll, BorderLayout.CENTER);
        StyleText generator = new StyleText();
        textContentArea.setStyledDocument(generator.createDocument(misplacedClassBean.getTextContent()));
        level2Panel.add(p);

        level2Panel.add(Box.createHorizontalGlue());

        tableContainer = new JPanel();
        tableContainer.setLayout(new BorderLayout());
        level2Panel.add(tableContainer);
        createTable();
        return centerPanel;
    }

    private void createTable() {
        table = new JBTable();

        String[] columnsNames = {"Method", "LOC", "WCM", "LCOM", "CBO", "NOM", "NOPA"};
        DefaultTableModel tableModel = new DefaultTableModel(columnsNames, 0);

        Vector<String> row = new Vector<>();
        row.add(misplacedClassBean.getFullQualifiedName());
        row.add(CKMetrics.getLOC(misplacedClassBean) + "");
        row.add(CKMetrics.getWMC(misplacedClassBean) + "");
        row.add(CKMetrics.getLCOM(misplacedClassBean) + "");
        row.add(CKMetrics.getCBO(misplacedClassBean) + "");
        row.add(CKMetrics.getNOA(misplacedClassBean) + "");
        row.add(CKMetrics.getNOM(misplacedClassBean) + "");
        row.add(CKMetrics.getNOPA(misplacedClassBean) + "");
        tableModel.addRow(row);

        table.setModel(tableModel);
        tableContainer.add(new JScrollPane(table));
        tableContainer.setBorder(new TitledBorder("Metrics"));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("FIND SOLUTION") {

            @Override
            protected void doAction(ActionEvent actionEvent) {
                MisplacedClassWizard misplacedClassWizard = new MisplacedClassWizard(misplacedClassBean, project);
                misplacedClassWizard.show();
                close(0);
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }
}
