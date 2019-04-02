package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
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

public class PromiscuousPackageWizard extends DialogWrapper {

    private PackageBean packageDestra, packageSinistra;
    private JComboBox<String> comboBox, comboBox2;
    private JTable metric;
    private JPanel main, vecchio;
    private JTable table1, table2;
    private RadarMapUtils radar;
    private List<JPanel> list;
    private PackageBean packageBean;
    private JPanel sinistra, destra;
    private List<PackageBean> splitting;
    private JPanel viste, panel_25, panel_26;

    public PromiscuousPackageWizard(PackageBean packageBean, List<PackageBean> splitting, Project project) {
        super(true);
        this.packageBean = packageBean;
        this.splitting = splitting;
        init();
        setTitle("PROMISCUOUS PACKAGE REFACTORING");
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperAction("Refactoring") {

            @Override
            protected void doAction(ActionEvent actionEvent) {

            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("EXIT", 0)};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        main = new JPanel();
        main.setPreferredSize(new Dimension(900, 800));
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

        JPanel radar_1 = new JPanel();
        panel_22.add(radar_1);
        radar_1.setLayout(new BorderLayout(0, 0));

        viste = new JPanel();
        viste.setAutoscrolls(true);
        viste.setLayout(new BoxLayout(viste, BoxLayout.X_AXIS));
        JScrollPane viewTableScroll = new JBScrollPane(viste);
        viste.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        radar_1.add(viewTableScroll, BorderLayout.CENTER);

        JPanel panel_58 = new JPanel();
        radar_1.add(panel_58, BorderLayout.SOUTH);
        panel_58.setLayout(new BoxLayout(panel_58, BoxLayout.X_AXIS));

        JPanel panel_27 = new JPanel();
        panel_58.add(panel_27);

        panel_25 = new JPanel();
        panel_58.add(panel_25);

        comboBox = new ComboBox<>();
        panel_25.add(comboBox);

        JPanel panel_28 = new JPanel();
        panel_58.add(panel_28);

        JPanel panel_30 = new JPanel();
        panel_58.add(panel_30);

        panel_26 = new JPanel();
        panel_58.add(panel_26);

        comboBox2 = new ComboBox<>();
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
        tableScrollPaneV.setPreferredSize(new Dimension(0, 0));

        JPanel panel_11 = new JPanel();
        panel_14.add(panel_11);

        JPanel panel_31 = new JPanel();
        panel_31.setLayout(new BorderLayout(0, 0));
        panel_15.add(panel_31);

        table1 = new JTable();
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

        JButton destra = new JButton("->");
        destra.setPreferredSize(new Dimension(50, 50));
        panel_7.add(destra);

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

        JButton sinistra = new JButton("<-");
        sinistra.setPreferredSize(new Dimension(50, 50));
        panel_13.add(sinistra);

        JPanel panel_32 = new JPanel();
        panel_32.setLayout(new BorderLayout(0, 0));
        panel_15.add(panel_32);

        table2 = new JTable();
        JScrollPane table2ScrollPane = new JScrollPane(table2);
        table2ScrollPane.setPreferredSize(new Dimension(300, 0));
        panel_32.add(table2);

        JPanel bottoni = new JPanel();
        bottoni.setLayout(new GridLayout(0, 2, 0, 0));
        main.setLayout(new BorderLayout(0, 0));
        main.add(panel_5);
        main.add(bottoni, BorderLayout.SOUTH);

        createView();
        if (!splitting.isEmpty()) {
            change1(packageBean);
            packageSinistra = packageBean;
            packageDestra = splitting.get(0);
            change2(splitting.get(0));
        }


        /*if (!splitting.isEmpty()&&splitting.size()>=2) {
            change1(splitting.get(0));
            change2(splitting.get(1));
        }*/
        ;
        metriche();

        destra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table1.getSelectedRow()!= 0) {
                    String full = (String) table1.getValueAt(table1.getSelectedRow(), 0);

                    if (!packageSinistra.getFullQualifiedName().equals(packageDestra.getFullQualifiedName())) {
                        int i = 0;
                        while (i < packageSinistra.getClassList().size() && !packageSinistra.getClassList().get(i).equals(full)) {
                            i++;
                        }
                        ClassBean bean = packageSinistra.getClassList().get(i);
                        //bean.setBelongingClass(packageDestra);
                        packageDestra.getClassList().add(bean);
                        packageSinistra.getClassList().remove(bean);

                    }
                }
                change1(packageSinistra);
                change2(packageDestra);
            }
        });

        sinistra.addActionListener(new

                                           ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {

                                               }
                                           });

        comboBox.addActionListener(new

                                           ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   JComboBox cb = (JComboBox) e.getSource();
                                                   String selection = (String) cb.getSelectedItem();
                                                   ((DefaultTableModel) table1.getModel()).setRowCount(0);
                                                   PackageBean ricerca;
                                                   if (selection.equalsIgnoreCase(packageBean.getFullQualifiedName())) {
                                                       ricerca = packageBean;
                                                   } else {
                                                       int i = 0;
                                                       while (i < splitting.size() && !selection.equalsIgnoreCase(splitting.get(i).getFullQualifiedName())) {
                                                           i++;
                                                       }
                                                       ricerca = splitting.get(i);
                                                   }
                                                   change1(ricerca);
                                               }
                                           });

        comboBox2.addActionListener(new

                                            ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    JComboBox cb2 = (JComboBox) e.getSource();
                                                    String selection = (String) cb2.getSelectedItem();
                                                    ((DefaultTableModel) table2.getModel()).setRowCount(0);
                                                    PackageBean ricerca;
                                                    if (selection.equalsIgnoreCase(packageBean.getFullQualifiedName())) {
                                                        ricerca = packageBean;
                                                    } else {
                                                        int i = 0;
                                                        while (i < splitting.size() && !selection.equalsIgnoreCase(splitting.get(i).getFullQualifiedName())) {
                                                            i++;
                                                        }
                                                        ricerca = splitting.get(i);
                                                    }
                                                    change2(ricerca);
                                                }
                                            });

        return main;
    }

    private void createView() {

        sinistra = new JPanel();
        sinistra = radar.createRadarMapFromPackageBean(packageBean, "OLD CLASS");
        vecchio.add(sinistra, BorderLayout.CENTER);

        JPanel aggiunta;

        if (splitting != null) {
            for (PackageBean packageBean : splitting) {
                aggiunta = new JPanel();
                aggiunta = radar.createRadarMapFromPackageBean(packageBean, packageBean.getFullQualifiedName());
                viste.add(aggiunta);
            }
        }
    }

    private void change1(PackageBean packageBean) {

        packageSinistra = packageBean;
        comboBox.removeAllItems();
        comboBox.addItem(packageBean.getFullQualifiedName());

        for (PackageBean cl : splitting) {
            if (!cl.getFullQualifiedName().equals(packageBean.getFullQualifiedName()))
                comboBox.addItem(cl.getFullQualifiedName());
        }

        comboBox.repaint();
        createTable1(packageBean);
    }

    private void change2(PackageBean c) {

        packageDestra = c;
        comboBox2.removeAllItems();
        comboBox2.addItem(c.getFullQualifiedName());

        for (PackageBean cl : splitting) {
            if (!cl.getFullQualifiedName().equals(c.getFullQualifiedName()))
                comboBox2.addItem(cl.getFullQualifiedName());
        }
        comboBox2.repaint();
        createTable2(c);
    }

    private void createTable1(PackageBean c) {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Method");
        columnNames.add("Class");
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table1.removeAll();
        if (c.getClassList() != null) {
            Vector<String> tableItem = null;
            for (ClassBean classe : c.getClassList()) {
                tableItem = new Vector<>();
                tableItem.add(classe.getFullQualifiedName());
                tableItem.add(classe.getBelongingPackage().getFullQualifiedName());
                model.addRow(tableItem);

            }
        }
        this.table1.setModel(model);
    }

    private void createTable2(PackageBean c) {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Method");
        columnNames.add("Class");
        DefaultTableModel model2 = new DefaultTableModel(columnNames, 0);
        table2.removeAll();
        if (c.getClassList() != null) {
            Vector<String> tableItem = null;
            for (ClassBean classBean : c.getClassList()) {
                tableItem = new Vector<>();
                tableItem.add(classBean.getFullQualifiedName());
                tableItem.add(classBean.getBelongingPackage().getFullQualifiedName());
                model2.addRow(tableItem);

            }
        }
        this.table2.setModel(model2);

    }

    private void metriche() {
        Vector<String> columnNames = new Vector<String>();
        /*columnNames.add("");
        columnNames.add("LOC");
        columnNames.add("WMC");
        columnNames.add("RFC");
        columnNames.add("CBO");
        columnNames.add("LCOM");
        columnNames.add("NOA");
        columnNames.add("NOM");
        columnNames.add("NOPA");*/

        columnNames.add("MIC");

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        Vector<String> tableItem = new Vector<String>();

        tableItem.add(packageBean.getFullQualifiedName());
        tableItem.add(String.valueOf(CKMetrics.computeMediumIntraConnectivity(packageBean)));
        /*tableItem.add(String.valueOf(CKMetrics.getWMC(packageBean)));
        tableItem.add(String.valueOf(CKMetrics.getRFC(packageBean)));
        tableItem.add(String.valueOf(CKMetrics.getCBO(packageBean)));
        tableItem.add(String.valueOf(CKMetrics.getLCOM(packageBean)));
        tableItem.add(String.valueOf(CKMetrics.getNOA(packageBean)));
        tableItem.add(String.valueOf(CKMetrics.getNOM(packageBean)));
        tableItem.add(String.valueOf(CKMetrics.getNOPA(packageBean)));*/

        model.addRow(tableItem);

        if (splitting != null) {
            for (PackageBean packageBean : splitting) {
                tableItem = new Vector<String>();
                tableItem.add(packageBean.getFullQualifiedName());
                tableItem.add(String.valueOf(CKMetrics.computeMediumIntraConnectivity(packageBean)));
                /*tableItem.add(String.valueOf(CKMetrics.getWMC(packageBean)));
                tableItem.add(String.valueOf(CKMetrics.getRFC(packageBean)));
                tableItem.add(String.valueOf(CKMetrics.getCBO(packageBean)));
                tableItem.add(String.valueOf(CKMetrics.getLCOM(packageBean)));
                tableItem.add(String.valueOf(CKMetrics.getNOA(packageBean)));
                tableItem.add(String.valueOf(CKMetrics.getNOM(packageBean)));
                tableItem.add(String.valueOf(CKMetrics.getNOPA(packageBean)));*/

                model.addRow(tableItem);
            }
        }
        this.metric.setModel(model);
    }

}
