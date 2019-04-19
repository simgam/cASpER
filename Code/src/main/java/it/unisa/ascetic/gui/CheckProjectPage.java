package it.unisa.ascetic.gui;

import com.intellij.openapi.project.Project;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class CheckProjectPage extends DialogWrapper {

    private DefaultTableModel model;

    private Project currentProject;
    private List<PackageBean> promiscuousPackageList;
    private List<MethodBean> featureEnvyList;
    private List<ClassBean> misplacedClassList;
    private List<ClassBean> blobList;
    private JPanel pannello;
    private JTextArea codeVisual;
    private JTable table;
    private DecimalFormat df = new DecimalFormat("0.000");
    private DecimalFormat df2 = new DecimalFormat("0");
    private JPanel contentPanel;
    private JPanel smellVisual;
    private JPanel panel;
    private JPanel textual;
    private JPanel structural;
    private JTextField valore1;
    private JTextField valore2;
    private JLabel soglia2;

    private JPanel centerPanel;
    private JPanel nuovo;
    private JPanel slider;
    private JSlider dipendenze;
    private JSlider coseno;
    private JPanel smell;
    private JPanel featurePanel;
    private JPanel missPanel;
    private JPanel blobPanel;
    private JPanel promiscuousPanel;
    private HashMap<String, JCheckBox> codeSmell = new HashMap<String, JCheckBox>();
    private JPanel algorithm1;
    private JPanel algorithm2;
    private JPanel algorithm3;
    private JPanel algorithm4;
    private HashMap<String, JCheckBox> algoritmi = new HashMap<String, JCheckBox>();

    private double maxS = 0.0;
    private String algorithm;
    private double sogliaCoseno;
    private int sogliaDipendenze;

    public CheckProjectPage() {
        super(true);
        setResizable(false);
        init();
        setTitle("CODE SMELL ANALYSIS");
    }

    public CheckProjectPage(Project currentProj, List<PackageBean> promiscuous, List<ClassBean> blob, List<ClassBean> misplaced, List<MethodBean> feature, double sogliaCoseno, int sogliaDipendenze, String algorithm) {
        super(true);
        this.currentProject = currentProj;
        this.promiscuousPackageList = promiscuous;
        this.featureEnvyList = feature;
        this.misplacedClassList = misplaced;
        this.blobList = blob;
        this.algorithm = algorithm;
        this.sogliaCoseno = sogliaCoseno;
        this.sogliaDipendenze = sogliaDipendenze;
        init();
        setTitle("CODE SMELL ANALYSIS");
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

        contentPanel.setLayout(new GridLayout(0, 2));//layout pannello principale
        contentPanel.setPreferredSize(new Dimension(1200, 800));
        smellVisual.setLayout(new BorderLayout(0, 0));
        smellVisual.add(panel, BorderLayout.NORTH);

        pannello = new JPanel();
        panel.add(pannello, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        nuovo = new JPanel();
        centerPanel.add(nuovo);
        nuovo.setLayout(new BoxLayout(nuovo, BoxLayout.X_AXIS));
        smell = new JPanel();
        nuovo.add(smell);
        smell.setLayout(new BoxLayout(smell, BoxLayout.Y_AXIS));

        //feature envy
        featurePanel = new JPanel();
        featurePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        smell.add(featurePanel);
        featurePanel.setLayout(new GridLayout(0, 2, 0, 0));
        codeSmell.put("Feature Envy", new JCheckBox("Feature Envy"));
        featurePanel.add(codeSmell.get("Feature Envy"));
        algorithm1 = new JPanel();
        featurePanel.add(algorithm1);
        algorithm1.setLayout(new GridLayout(0, 1, 0, 0));
        algoritmi.put("textualF", new JCheckBox("Textual"));
        algorithm1.add(algoritmi.get("textualF"));
        algoritmi.put("structuralF", new JCheckBox("Structural"));
        algorithm1.add(algoritmi.get("structuralF"));

        //Misplaced class
        missPanel = new JPanel();
        missPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        smell.add(missPanel);
        missPanel.setLayout(new GridLayout(0, 2, 0, 0));

        codeSmell.put("Misplaced Class", new JCheckBox("Misplaced Class"));
        missPanel.add(codeSmell.get("Misplaced Class"));
        algorithm2 = new JPanel();
        missPanel.add(algorithm2);
        algorithm2.setLayout(new GridLayout(0, 1, 0, 0));
        algoritmi.put("textualM", new JCheckBox("Textual"));
        algorithm2.add(algoritmi.get("textualM"));
        algoritmi.put("structuralM", new JCheckBox("Structural"));
        algorithm2.add(algoritmi.get("structuralM"));

        //Blob
        blobPanel = new JPanel();
        blobPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        smell.add(blobPanel);
        blobPanel.setLayout(new GridLayout(0, 2, 0, 0));

        codeSmell.put("Blob", new JCheckBox("Blob"));
        blobPanel.add(codeSmell.get("Blob"));
        algorithm3 = new JPanel();
        blobPanel.add(algorithm3);
        algorithm3.setLayout(new GridLayout(0, 1, 0, 0));
        algoritmi.put("textualB", new JCheckBox("Textual"));
        algorithm3.add(algoritmi.get("textualB"));
        algoritmi.put("structuralB", new JCheckBox("Structural"));
        algorithm3.add(algoritmi.get("structuralB"));

        //Promiscuous pakcage
        promiscuousPanel = new JPanel();
        promiscuousPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        smell.add(promiscuousPanel);
        promiscuousPanel.setLayout(new GridLayout(0, 2, 0, 0));

        codeSmell.put("Promiscuous Package", new JCheckBox("Promiscuous Package"));
        promiscuousPanel.add(codeSmell.get("Promiscuous Package"));
        algorithm4 = new JPanel();
        promiscuousPanel.add(algorithm4);
        algorithm4.setLayout(new GridLayout(0, 1, 0, 0));
        algoritmi.put("textualP", new JCheckBox("Textual"));
        algorithm4.add(algoritmi.get("textualP"));
        algoritmi.put("structuralP", new JCheckBox("Structural"));
        algorithm4.add(algoritmi.get("structuralP"));

        for (JCheckBox c : codeSmell.values()) {
            c.setSelected(true);
            c.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent a) {
                    JCheckBox cb = (JCheckBox) a.getSource();
                    createTable();
                    table.repaint();
                }
            });
        }

        JCheckBox c;
        for (String s : algoritmi.keySet()) {

            c = algoritmi.get(s);
            c.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent a) {
                    createTable();
                    table.repaint();
                }
            });
            if (algorithm.contains("All")) {
                c.setSelected(true);
            } else {
                if ((s.contains("textual") && algorithm.contains("Textual")) || (s.contains("structural") && algorithm.contains("Structural"))) {
                    c.setSelected(true);
                }
            }
        }

        slider = new JPanel();
        nuovo.add(slider);
        slider.setLayout(new GridLayout(2, 1, 0, 0));

        textual = new JPanel();
        textual.setBorder(new TitledBorder("Textual"));
        slider.add(textual);
        textual.setLayout(new BoxLayout(textual, BoxLayout.Y_AXIS));

        JPanel bar1 = new JPanel();
        textual.add(bar1);
        JPanel s = new JPanel();
        JLabel soglia1 = new JLabel();
        soglia1.setText("similarity >= [" + sogliaCoseno + "-1]");
        JPanel val1 = new JPanel();
        textual.add(s);
        s.add(soglia1);
        textual.add(val1);

        coseno = new JSlider();
        coseno.setForeground(Color.WHITE);
        coseno.setFont(new Font("Arial", Font.PLAIN, 12));
        coseno.setToolTipText("");
        coseno.setPaintTicks(true);
        coseno.setMinorTickSpacing(10);
        if (sogliaCoseno <= 0.5) {
            coseno.setMinimum((int) (sogliaCoseno * 100));
        } else {
            coseno.setMinimum(50);
        }
        ;
        coseno.setValue((int) (sogliaCoseno * 100));
        bar1.add(coseno);

        coseno.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                valore1.setText(String.valueOf(((double) coseno.getValue()) / 100));
                createTable();
                table.repaint();
            }
        });

        valore1 = new JTextField();
        valore1.setText(String.valueOf(((double) coseno.getValue()) / 100));
        val1.add(valore1);
        valore1.setToolTipText("Non viene usata per il Misplaced class");
        valore1.setColumns(10);

        valore1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent c) {

                try {
                    JTextField t = (JTextField) c.getSource();
                    double valore = Double.parseDouble(t.getText());
                    if (valore >= 0.0 && valore <= 1.0) {
                        coseno.setValue((int) (valore * 100));
                    } else {
                        if (valore < 0.0) {
                            coseno.setValue(0);
                            valore1.setText(0.0 + "");
                        } else {
                            if (valore > 1.0) {
                                coseno.setValue(100);
                                valore1.setText(1.0 + "");
                            } else {
                                coseno.setValue((int) valore);
                                valore1.setText(valore + "");
                            }
                        }
                        ;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Inserire valori decimali con \".\" [" + sogliaCoseno + "-1]", "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        structural = new JPanel();
        structural.setBorder(new TitledBorder("Structural"));
        slider.add(structural);
        structural.setLayout(new BoxLayout(structural, BoxLayout.Y_AXIS));

        JPanel bar2 = new JPanel();
        structural.add(bar2);
        JPanel s2 = new JPanel();
        soglia2 = new JLabel();
        JPanel val2 = new JPanel();
        structural.add(s2);
        s2.add(soglia2);
        structural.add(val2);

        dipendenze = new JSlider();
        dipendenze.setForeground(Color.WHITE);
        dipendenze.setFont(new Font("Arial", Font.PLAIN, 12));
        dipendenze.setToolTipText("");
        dipendenze.setValue(sogliaDipendenze);
        dipendenze.setPaintTicks(true);
        dipendenze.setMinorTickSpacing(1);
        dipendenze.setMinimum(0);
        bar2.add(dipendenze);

        dipendenze.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                try {
                    valore2.setText(String.valueOf(dipendenze.getValue()));
                    createTable();
                    table.repaint();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Inserire valori interi", "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        valore2 = new JTextField();
        valore2.setText(String.valueOf(dipendenze.getValue()));
        val2.add(valore2);
        valore2.setColumns(10);

        valore2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent c) {
                JTextField t = (JTextField) c.getSource();
                dipendenze.setValue(Integer.parseInt(t.getText()));
            }
        });

        pannello.add(centerPanel);

        createTable();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() != false) {
                    setArea();
                }
            }
        });

        JPanel text = new JPanel();
        text.setLayout(new BorderLayout(0, 0));
        text.setBorder(new TitledBorder("Text content"));
        JPanel tab = new JPanel();
        tab.setLayout(new BorderLayout(0, 0));
        tab.setBorder(new TitledBorder("List"));
        contentPanel.setLayout(new GridLayout(0, 2));//layout pannello principale

        JScrollPane scroll = new JScrollPane(table);
        tab.add(scroll, BorderLayout.CENTER);// aggiunta tabella smell
        smellVisual.add(tab);

        contentPanel.add(smellVisual);

        codeVisual.setEditable(false);
        JScrollPane scroll2 = new JScrollPane(codeVisual);
        text.add(scroll2, BorderLayout.CENTER);
        contentPanel.add(text);

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
        columnNames.add("Textual algorithm");
        columnNames.add("Structural algorithm");
        columnNames.add("priority");
        model = new DefaultTableModel(columnNames, 0);

        if (blobList != null) {
            if (codeSmell.get("Blob").isSelected()) {
                for (ClassBean c : blobList) {
                    gestione(c.getAffectedSmell(), "Blob", c.getFullQualifiedName());
                }
            }
        }

        if (misplacedClassList != null) {
            if (codeSmell.get("Misplaced Class").isSelected()) {
                for (ClassBean c : misplacedClassList) {
                    gestione(c.getAffectedSmell(), "Misplaced Class", c.getFullQualifiedName());
                }
            }
        }

        if (promiscuousPackageList != null) {
            if (codeSmell.get("Promiscuous Package").isSelected()) {
                for (PackageBean pp : promiscuousPackageList) {
                    gestione(pp.getAffectedSmell(), "Promiscuous Package", pp.getFullQualifiedName());
                }
            }
        }

        if (featureEnvyList != null) {
            if (codeSmell.get("Feature Envy").isSelected()) {
                for (MethodBean m : featureEnvyList) {
                    gestione(m.getAffectedSmell(), "Feature Envy", m.getFullQualifiedName());
                }
            }
        }

        if (this.table == null) {
            JTable table = new JBTable();
            this.table = table;
        }

        soglia2.setText("dipendency >= [" + "0" + "-" + (int) maxS + "]");
        dipendenze.setMaximum((int) maxS);
        this.table.setModel(model);
        table.setDefaultEditor(Object.class, null);
    }

    private void gestione(List<CodeSmell> list, String codeSmell, String bean) {

        boolean basso = false;
        int alto = 0;
        int complessita = 0;
        Vector<String> tableItem;
        String used;

        double cos = 0.0;
        int dip = 0;
        boolean controllo = false;
        int i = 0;

        for (CodeSmell smell : list) {
            if (smell.getSmellName().equalsIgnoreCase(codeSmell)) {
                used = smell.getAlgoritmsUsed();
                tableItem = new Vector<String>();

                if (used.equalsIgnoreCase("textual") && Double.parseDouble(valore1.getText()) <= smell.getIndex()) {
                    if (algoritmi.get("textual" + codeSmell.substring(0, 1)).isSelected()) {
                        complessita++;
                        cos = smell.getIndex();
                        if (cos <= sogliaCoseno) {
                            basso = true;
                        };
                        if (cos >= 0.75) {
                            alto++;
                        };
                    } else {
                        controllo = true;
                    }
                } else {
                    if (used.equalsIgnoreCase("structural") && Double.parseDouble(valore2.getText()) <= smell.getIndex()) {
                        if (algoritmi.get("structural" + codeSmell.substring(0, 1)).isSelected()) {
                            complessita += 2;
                            dip = (int) smell.getIndex();
                            if (dip <= sogliaDipendenze) {
                                basso = true;
                            };
                            if (dip >= (int) (maxS - (maxS * 0.25))) {
                                alto++;
                            };
                            if (dip >= maxS) {
                                maxS = dip;
                            };
                        } else {
                            controllo = true;
                        }
                    }
                }

                if (i + 1 >= list.size() || !list.get(i + 1).getSmellName().equalsIgnoreCase(smell.getSmellName())) {

                    if ((cos != 0.0 || dip != 0) && ((Double.parseDouble(valore1.getText()) <= smell.getIndex() || algoritmi.get("textual" + codeSmell.substring(0, 1)).isSelected()) ||
                            (Double.parseDouble(valore2.getText()) <= smell.getIndex() || algoritmi.get("structural" + codeSmell.substring(0, 1)).isSelected()))) {
                        tableItem.add(bean);
                        tableItem.add(smell.getSmellName());
                        if (cos == 0.0) {
                            tableItem.add("---");
                        } else {
                            tableItem.add(df.format(cos));
                        };
                        if (dip == 0) {
                            tableItem.add("---");
                        } else {
                            tableItem.add(df2.format(dip));
                        };
                        tableItem.add(prioritySmell(controllo, complessita, basso, alto));
                        model.addRow(tableItem);
                        cos = 0.0;
                        dip = 0;
                        complessita = 0;
                        alto = 0;
                        basso = false;
                        controllo = false;
                    }
                }
            }
            i++;
        }
    }

    private String prioritySmell(boolean controllo, int complessita, boolean basso, int alto) {

        if (complessita <= 2 && !controllo && alto<1) {
            return "bassa";
        } else {
            if (!basso) {
                switch (alto) {
                    case 1:
                        return "alto";
                    case 2:
                        return "urgente";
                    default:
                        return "media";
                }
            }
            return "media";
        }
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