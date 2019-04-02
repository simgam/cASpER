package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.refactor.manipulator.BlobRefatoringStrategy;
import it.unisa.ascetic.refactor.strategy.RefactoringManager;
import it.unisa.ascetic.refactor.strategy.RefactoringStrategy;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.InstanceVariableBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class BlobWizard extends DialogWrapper {

    private ClassBean classeDestra, classeSinistra;
    private JComboBox<String> comboBox, comboBox2;
    private JTable metric;
    private JPanel main, vecchio, sinistra, destra, radar_1, viste = null, panel_25, panel_26;
    private JTable table1, table2;
    private RadarMapUtils radar;
    private List<JPanel> list;
    private ClassBean blobClassBean;
    private List<ClassBean> splitting;
    private Project project;
    private boolean errorOccured;

    public BlobWizard(ClassBean c, List<ClassBean> splitting, Project project) {
        super(true);
        this.blobClassBean = c;
        this.splitting = splitting;
        classeSinistra = splitting.get(0);
        classeDestra = splitting.get(1);
        this.project = project;
        this.errorOccured = false;
        init();
        setTitle("BLOB REFACTORING");
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("Refactoring") {

            String message;

            @Override
            protected void doAction(ActionEvent actionEvent) {
                try {
                    //splittedClasses = (List<ClassBean>) new SplitClasses().split(classBeanBlob,0.1);

                    RefactoringStrategy refactoringStrategy = new BlobRefatoringStrategy(blobClassBean, splitting, project);
                    RefactoringManager refactoringManager = new RefactoringManager(refactoringStrategy);
                    refactoringManager.executeRefactor();
                } catch (Exception e) {
                    errorOccured = true;
                    message = e.getMessage();
                    e.printStackTrace();
                }

                if (errorOccured) {
                    Messages.showMessageDialog(message, "Oh!No!", Messages.getErrorIcon());
                } else {
                    close(0);
                    message = "Blob Corrected, check new classes generated name";
                    Messages.showMessageDialog(message, "Success !", Messages.getInformationIcon());
                }
            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        main = new JPanel();
        main.setPreferredSize(new Dimension(1100, 800));
        radar = new RadarMapUtilsAdapter();

        JPanel panel_5 = new JPanel();
        panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));

        JPanel panel_60 = new JPanel();
        panel_5.add(panel_60);

        JPanel panel_23 = new JPanel();
        panel_5.add(panel_23);
        panel_23.setLayout(new BoxLayout(panel_23, BoxLayout.X_AXIS));

        JPanel metriche = new JPanel();
        panel_23.add(metriche);
        metriche.setLayout(new BorderLayout(0, 0));

        JPanel panel_14 = new JPanel();
        metriche.add(panel_14, BorderLayout.CENTER);
        panel_14.setLayout(new BoxLayout(panel_14, BoxLayout.X_AXIS));

        JPanel panel_22 = new JPanel();
        panel_5.add(panel_22);
        panel_22.setLayout(new BorderLayout(0, 0));

        radar_1 = new JPanel();
        panel_22.add(radar_1);
        radar_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_58 = new JPanel();
        radar_1.add(panel_58, BorderLayout.SOUTH);
        panel_58.setLayout(new BoxLayout(panel_58, BoxLayout.X_AXIS));

        JPanel panel_27 = new JPanel();
        panel_58.add(panel_27);

        panel_25 = new JPanel();
        panel_58.add(panel_25);

        comboBox = new ComboBox<>();
        comboBox.setSize(100, comboBox.getPreferredSize().height);
        panel_25.add(comboBox);

        JPanel panel_28 = new JPanel();
        panel_58.add(panel_28);

        JPanel panel_30 = new JPanel();
        panel_58.add(panel_30);

        panel_26 = new JPanel();
        panel_58.add(panel_26);

        comboBox2 = new ComboBox<>();
        comboBox2.setSize(100, comboBox.getPreferredSize().height);
        panel_26.add(comboBox2);

        JPanel panel_29 = new JPanel();
        panel_58.add(panel_29);

        JPanel panel_59 = new JPanel();
        radar_1.add(panel_59, BorderLayout.NORTH);

        JPanel tabelle = new JPanel();
        panel_5.add(tabelle);
        tabelle.setLayout(new BorderLayout(0, 0));

        JPanel panel_15 = new JPanel();
        tabelle.add(panel_15, BorderLayout.CENTER);
        panel_15.setLayout(new BoxLayout(panel_15, BoxLayout.X_AXIS));

        JPanel panel_24 = new JPanel();
        panel_14.add(panel_24);
        panel_24.setLayout(new BorderLayout(0, 0));

        JPanel panel_16 = new JPanel();
        panel_24.add(panel_16, BorderLayout.EAST);

        JPanel panel_18 = new JPanel();
        panel_16.add(panel_18);

        JPanel panel_21 = new JPanel();
        panel_16.add(panel_21);

        JPanel panel_20 = new JPanel();
        panel_16.add(panel_20);

        JPanel panel_19 = new JPanel();
        panel_16.add(panel_19);

        vecchio = new JPanel();
        panel_24.add(vecchio, BorderLayout.CENTER);
        vecchio.setLayout(new BorderLayout(0, 0));

        JPanel panel_17 = new JPanel();
        panel_14.add(panel_17);
        panel_17.setLayout(new BorderLayout(0, 0));

        metric = new JTable();
        metric.setDefaultEditor(Object.class, null);
        JScrollPane tableScrollPaneV = new JScrollPane(metric);
        tableScrollPaneV.setPreferredSize(new Dimension(300, 0));
        panel_17.add(tableScrollPaneV);

        JPanel panel_11 = new JPanel();
        panel_14.add(panel_11);

        JPanel panel_31 = new JPanel();
        panel_31.setLayout(new BorderLayout(0, 0));
        panel_15.add(panel_31);

        table1 = new JTable();
        table1.setDefaultEditor(Object.class, null);
        JScrollPane table1ScrollPane = new JScrollPane(table1);
        table1ScrollPane.setPreferredSize(new Dimension(300, 0));
        panel_31.add(table1ScrollPane);

        JPanel frecce = new JPanel();
        panel_15.add(frecce);
        frecce.setMaximumSize(new Dimension(3000, 32767));
        frecce.setLayout(new BoxLayout(frecce, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        frecce.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel panel_2 = new JPanel();
        panel.add(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        JPanel panel_4 = new JPanel();
        panel.add(panel_4);
        panel_4.setLayout(new BorderLayout(0, 0));

        JPanel panel_7 = new JPanel();
        panel_4.add(panel_7);

        JButton destro = new JButton("->");
        destro.setPreferredSize(new Dimension(50, 50));
        panel_7.add(destro);

        JPanel panel_1 = new JPanel();
        frecce.add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

        JPanel panel_3 = new JPanel();
        panel_1.add(panel_3);
        panel_3.setLayout(new BorderLayout(0, 0));

        JPanel panel_6 = new JPanel();
        panel_1.add(panel_6);
        panel_6.setLayout(new BorderLayout(0, 0));

        JPanel panel_13 = new JPanel();
        panel_6.add(panel_13, BorderLayout.NORTH);

        JButton sinistro = new JButton("<-");
        sinistro.setPreferredSize(new Dimension(50, 50));
        panel_13.add(sinistro);

        JPanel panel_32 = new JPanel();
        panel_32.setLayout(new BorderLayout(0, 0));
        panel_15.add(panel_32);

        table2 = new JTable();
        table2.setDefaultEditor(Object.class, null);
        JScrollPane table2ScrollPane = new JScrollPane(table2);
        table2ScrollPane.setPreferredSize(new Dimension(300, 0));
        panel_32.add(table2ScrollPane);

        JPanel bottoni = new JPanel();
        bottoni.setLayout(new GridLayout(0, 2, 0, 0));
        main.setLayout(new BorderLayout(0, 0));
        main.add(panel_5);
        main.add(bottoni, BorderLayout.SOUTH);

        createView();
        if (!splitting.isEmpty()) {
            classeSinistra = splitting.get(0);
            change1(classeSinistra, 1);
            createTable1(classeSinistra);
            classeDestra = splitting.get(1);
            change2(classeDestra, 1);
            createTable2(classeDestra);
        }

        metriche();

        sinistra = new JPanel();
        sinistra = radar.createRadarMapFromClassBean(blobClassBean, "OLD CLASS");
        vecchio.add(sinistra, BorderLayout.CENTER);

        destro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                if (!classeSinistra.getMethodList().isEmpty()) {
                    if (!classeSinistra.getFullQualifiedName().equals(classeDestra.getFullQualifiedName())) {
                        try {
                            String full = (String) table1.getValueAt(table1.getSelectedRow(), 0);

                            int i = 0;
                            while (i < classeSinistra.getMethodList().size() && !classeSinistra.getMethodList().get(i).getFullQualifiedName().equals(full)) {
                                i++;
                            }

                            if (i < classeSinistra.getMethodList().size()) {

                                MethodBean bean = classeSinistra.getMethodList().get(i);
                                bean.setBelongingClass(classeDestra);
                                classeDestra.getMethodList().add(bean);
                                classeSinistra.getMethodList().remove(bean);
                                createTable1(classeSinistra);
                                createTable2(classeDestra);
                                radar_1.remove(viste);
                                radar_1.repaint();
                                createView();
                                metriche();
                                scelta1((String) comboBox.getSelectedItem(), 0);

                            } else {
                                String message = "Error";
                                Messages.showMessageDialog(message, "Oh no", Messages.getErrorIcon());
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            String message = "Seleziona un elemento";
                            Messages.showMessageDialog(message, "Warning", Messages.getWarningIcon());
                        }
                        ;
                    } else {
                        String message = "Non puoi spostare un metodo nella classe in cui \u00E8 gi\u00E0 presente";
                        Messages.showMessageDialog(message, "Error", Messages.getErrorIcon());
                    }
                }
            }
        });

        sinistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!classeDestra.getMethodList().isEmpty()) {
                    if (!classeDestra.getFullQualifiedName().equals(classeSinistra.getFullQualifiedName())) {
                        try {
                            String full = (String) table2.getValueAt(table2.getSelectedRow(), 0);

                            int i = 0;
                            while (i < classeDestra.getMethodList().size() && !classeDestra.getMethodList().get(i).getFullQualifiedName().equals(full)) {
                                i++;
                            }

                            if (i < classeDestra.getMethodList().size()) {

                                MethodBean bean = classeDestra.getMethodList().get(i);
                                bean.setBelongingClass(classeSinistra);
                                classeSinistra.getMethodList().add(bean);
                                classeDestra.getMethodList().remove(bean);
                                createTable1(classeSinistra);
                                createTable2(classeDestra);
                                radar_1.remove(viste);
                                radar_1.repaint();
                                createView();
                                metriche();
                                scelta2((String) comboBox2.getSelectedItem(), 0);

                            } else {
                                String message = "Error";
                                Messages.showMessageDialog(message, "Oh no", Messages.getErrorIcon());
                            }

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            String message = "Seleziona un elemento";
                            Messages.showMessageDialog(message, "Warning", Messages.getWarningIcon());
                        }
                        ;
                    } else {
                        String message = "Non puoi spostare un metodo nella classe in cui \u00E8 gi\u00E0 presente";
                        Messages.showMessageDialog(message, "Error", Messages.getErrorIcon());
                    }
                }
            }
        });

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                JComboBox cb = (JComboBox) action.getSource();
                String selection = (String) cb.getSelectedItem();
                scelta1(selection, 1);
            }
        });

        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action2) {
                JComboBox cb2 = (JComboBox) action2.getSource();
                String selection = (String) cb2.getSelectedItem();
                scelta2(selection, 1);
            }
        });

        return main;
    }

    public void scelta1(String selection, int val) {
        if (selection != null) {
            ((DefaultTableModel) table1.getModel()).setRowCount(0);
            ClassBean ricerca;
            if (selection.equalsIgnoreCase(blobClassBean.getFullQualifiedName())) {
                ricerca = blobClassBean;
            } else {
                int i = 0;
                while (i < splitting.size() && !selection.equalsIgnoreCase(splitting.get(i).getFullQualifiedName())) {
                    i++;
                }
                ricerca = splitting.get(i);
            }
            change1(ricerca, val);
        }
    }

    public void scelta2(String selection, int val) {
        if (selection != null) {
            ((DefaultTableModel) table2.getModel()).setRowCount(0);
            ClassBean ricerca2;
            if (selection.equalsIgnoreCase(blobClassBean.getFullQualifiedName())) {
                ricerca2 = blobClassBean;
            } else {
                int i = 0;
                while (i < splitting.size() && !selection.equalsIgnoreCase(splitting.get(i).getFullQualifiedName())) {
                    i++;
                }
                ricerca2 = splitting.get(i);
            }
            change2(ricerca2, val);
        }
    }

    private void createView() {

        viste = new JPanel();
        viste.setLayout(new GridLayout(0, 4, 0, 0));
        JPanel aggiunta;
        if (splitting != null) {
            int index = 1;
            for (ClassBean classe : splitting) {
                String classShortName = "Class_" + (index);
                StringBuilder classTextContent = new StringBuilder();
                classTextContent.append("public class ");
                classTextContent.append(classShortName);
                classTextContent.append(" {");

                for (InstanceVariableBean instanceVariableBean : classe.getInstanceVariablesList()) {
                    classTextContent.append(instanceVariableBean.getFullQualifiedName());
                    classTextContent.append("\n");
                }

                for (MethodBean methodBean : classe.getMethodList()) {
                    classTextContent.append(methodBean.getTextContent());
                    classTextContent.append("\n");
                }

                classTextContent.append("}");
                classe.setTextContent(classTextContent.toString());

                aggiunta = new JPanel();
                aggiunta = radar.createRadarMapFromClassBean(classe, classe.getFullQualifiedName());
                viste.add(aggiunta);
                index++;
            }
        }
        viste.repaint();
        radar_1.add(viste, BorderLayout.CENTER);
        radar_1.repaint();
    }

    private void change1(ClassBean cs, int val) {

        classeSinistra = cs;
        comboBox.removeAllItems();
        comboBox.addItem(cs.getFullQualifiedName());
        if (val == 1) {
            for (ClassBean cl1 : splitting) {
                if (!cs.getFullQualifiedName().equals(cl1.getFullQualifiedName()))
                    comboBox.addItem(cl1.getFullQualifiedName());
            }
            comboBox.repaint();
            createTable1(cs);
        }
    }

    private void change2(ClassBean cd, int val) {

        classeDestra = cd;
        comboBox2.removeAllItems();
        comboBox2.addItem(cd.getFullQualifiedName());
        if (val == 1) {
            for (ClassBean cl2 : splitting) {
                if (!cl2.getFullQualifiedName().equals(cd.getFullQualifiedName()))
                    comboBox2.addItem(cl2.getFullQualifiedName());
            }
            comboBox2.repaint();
            createTable2(cd);
        }
    }

    private void createTable1(ClassBean c) {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Method");
        columnNames.add("Class");
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table1.removeAll();
        if (c.getMethodList() != null) {
            Vector<String> tableItem = null;
            for (MethodBean method : c.getMethodList()) {
                tableItem = new Vector<>();
                tableItem.add(method.getFullQualifiedName());
                tableItem.add(method.getBelongingClass().getFullQualifiedName());
                model.addRow(tableItem);

            }
        }
        this.table1.setModel(model);

    }

    private void createTable2(ClassBean c) {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Method");
        columnNames.add("Class");
        DefaultTableModel model2 = new DefaultTableModel(columnNames, 0);
        table2.removeAll();
        if (c.getMethodList() != null) {
            Vector<String> tableItem = null;
            for (MethodBean method : c.getMethodList()) {
                tableItem = new Vector<>();
                tableItem.add(method.getFullQualifiedName());
                tableItem.add(method.getBelongingClass().getFullQualifiedName());
                model2.addRow(tableItem);

            }
        }
        this.table2.setModel(model2);

    }

    private void metriche() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("");
        columnNames.add("LOC");
        columnNames.add("WMC");
        columnNames.add("RFC");
        columnNames.add("LCOM");
        columnNames.add("NOA");
        columnNames.add("NOM");
        columnNames.add("NOPA");

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        Vector<String> tableItem = new Vector<String>();

        tableItem.add(blobClassBean.getFullQualifiedName());
        tableItem.add(String.valueOf(CKMetrics.getLOC(blobClassBean)));
        tableItem.add(String.valueOf(CKMetrics.getWMC(blobClassBean)));
        tableItem.add(String.valueOf(CKMetrics.getRFC(blobClassBean)));
        tableItem.add(String.valueOf(CKMetrics.getLCOM(blobClassBean)));
        tableItem.add(String.valueOf(CKMetrics.getNOA(blobClassBean)));
        tableItem.add(String.valueOf(CKMetrics.getNOM(blobClassBean)));
        tableItem.add(String.valueOf(CKMetrics.getNOPA(blobClassBean)));

        model.addRow(tableItem);

        if (splitting != null) {
            for (ClassBean classe : splitting) {
                tableItem = new Vector<String>();
                tableItem.add(classe.getFullQualifiedName());
                tableItem.add(String.valueOf(CKMetrics.getLOC(classe)));
                tableItem.add(String.valueOf(CKMetrics.getWMC(classe)));
                tableItem.add(String.valueOf(CKMetrics.getRFC(classe)));
                tableItem.add(String.valueOf(CKMetrics.getLCOM(classe)));
                tableItem.add(String.valueOf(CKMetrics.getNOA(classe)));
                tableItem.add(String.valueOf(CKMetrics.getNOM(classe)));
                tableItem.add(String.valueOf(CKMetrics.getNOPA(classe)));

                model.addRow(tableItem);
            }
        }
        this.metric.setModel(model);
    }

}
