package it.unisa.ascetic.gui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ConfigureThreshold extends DialogWrapper {

    private JComboBox<String> algorith;

    private static ArrayList<String> smell;
    private JPanel contentPane;
    private JPanel centerPanel;
    private JCheckBox standard;
    private HashMap<String, JSlider> sogliaT;
    private HashMap<String, JTextField> coseno;
    private HashMap<String, JTextField> dipendenze;
    private JPanel scelta;
    private JPanel app;

    private static HashMap<String, Double> cos;
    private static HashMap<String, Integer> dipendence;
    private static String algoritmi;

    public ConfigureThreshold() {
        super(true);
        setResizable(false);
        smell = new ArrayList<String>();
        smell.add("Feature envy");
        smell.add("Misplaced class");
        smell.add("Blob");
        smell.add("Promiscuous package");
        init();
        setTitle("CONFIGURE THRESHOLD");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        cos = new HashMap<String, Double>();
        dipendence = new HashMap<String, Integer>();

        JLabel textual, structural;
        boolean set = true;
        try {
            FileReader f = new FileReader(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
            BufferedReader b = new BufferedReader(f);
            String[] list;

            for (String s : smell) {
                list = b.readLine().split(",");
                cos.put("coseno" + s, Double.parseDouble(list[0]));
                if (!s.equalsIgnoreCase("promiscuous package")) {
                    dipendence.put("dip" + s, Integer.parseInt(list[1]));
                    if (!s.equalsIgnoreCase("blob")) {
                        algoritmi = list[2];
                    } else {
                        dipendence.put("dip" + s + "2", Integer.parseInt(list[2]));
                        dipendence.put("dip" + s + "3", Integer.parseInt(list[3]));
                        algoritmi = list[4];
                    }
                }
                ;
                if (!(Double.parseDouble(list[0]) == 0.5 || (s.equalsIgnoreCase("misplaced class") && Double.parseDouble(list[0]) == 0.0)) || !((!s.equalsIgnoreCase("blob") && Integer.parseInt(list[1]) == 0) || (s.equalsIgnoreCase("blob") && (Integer.parseInt(list[1]) == 350) && (Integer.parseInt(list[2]) == 20) && (Integer.parseInt(list[3]) == 500))) || !algoritmi.equalsIgnoreCase("all")) {
                    set = false;
                }
                ;
            }
        } catch (Exception e) {
            reset();
        }
        contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(1200, 500));
        contentPane.setLayout(new BorderLayout(0, 0));

        centerPanel = new JPanel();

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        contentPane.add(centerPanel, BorderLayout.CENTER);

        JPanel livelli;
        JPanel primo, secondo;
        sogliaT = new HashMap<String, JSlider>();
        coseno = new HashMap<String, JTextField>();
        dipendenze = new HashMap<String, JTextField>();
        JPanel slider, app2;

        for (String s : smell) {
            livelli = new JPanel();
            livelli.setLayout(new BoxLayout(livelli, BoxLayout.Y_AXIS));
            primo = new JPanel();
            secondo = new JPanel();

            livelli.add(primo);
            primo.setLayout(new GridLayout(0, 2, 0, 0));

            textual = new JLabel("Soglia algoritmi testuale:");
            textual.setHorizontalAlignment(SwingConstants.CENTER);
            primo.add(textual);

            app = new JPanel();
            slider = new JPanel();
            slider.setLayout(new BoxLayout(slider, BoxLayout.X_AXIS));
            app2 = new JPanel();
            app.setLayout(new GridLayout(0, 2, 0, 0));
            slider.add(new JLabel("Coseno ="));
            sogliaT.put("soglia" + s, new JSlider());
            sogliaT.get("soglia" + s).setForeground(Color.WHITE);
            sogliaT.get("soglia" + s).setFont(new Font("Arial", Font.PLAIN, 12));
            sogliaT.get("soglia" + s).setToolTipText("");
            sogliaT.get("soglia" + s).setPaintTicks(true);
            sogliaT.get("soglia" + s).setValue((int) (cos.get("coseno" + s) * 100));
            sogliaT.get("soglia" + s).setMinorTickSpacing(10);
            sogliaT.get("soglia" + s).setMinimum(0);
            slider.add(sogliaT.get("soglia" + s));
            app.add(slider);

            coseno.put("coseno" + s, new JTextField());
            coseno.get("coseno" + s).setText(cos.get("coseno" + s) + "");
            if (s.equalsIgnoreCase("misplaced class")) {
                coseno.get("coseno" + s).setToolTipText("Tale soglia verra' successivamente riportata nell'intervallo [0-1]");
            }
            coseno.get("coseno" + s).setColumns(10);
            app2.add(coseno.get("coseno" + s));
            app.add(app2);
            primo.add(app);
            livelli.add(primo);

            sogliaT.get("soglia" + s).addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    JSlider t = (JSlider) event.getSource();
                    String corrente = null;
                    Iterator<String> list = sogliaT.keySet().iterator();
                    while (list.hasNext() && !sogliaT.get(corrente = list.next()).equals(t)) {
                    }
                    corrente = corrente.replace("soglia", "coseno");
                    coseno.get(corrente).setText(String.valueOf(((double) t.getValue()) / 100));
                    standard.setSelected(false);
                    cos.put(corrente, (double) t.getValue() / 100);
                }
            });
            coseno.get("coseno" + s).setToolTipText("Inserire valore tra [0-1]");
            coseno.get("coseno" + s).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent c) {
                    try {
                        JTextField t = (JTextField) c.getSource();
                        double valore = Double.parseDouble(t.getText());
                        Iterator<String> list = coseno.keySet().iterator();
                        String corrente = null;
                        while (list.hasNext() && !coseno.get(corrente = list.next()).equals(t)) {
                        }
                        corrente = corrente.replace("coseno", "");

                        if (valore >= 0.0 && valore <= 1.0) {
                            sogliaT.get("soglia" + corrente).setValue((int) (valore * 100));
                        } else {
                            if (valore < 0.0) {
                                sogliaT.get("soglia" + corrente).setValue(0);
                                t.setText(0.0 + "");
                            } else {
                                if (valore > 1.0) {
                                    sogliaT.get("soglia" + corrente).setValue(100);
                                    t.setText(1.0 + "");
                                } else {
                                    sogliaT.get("soglia" + corrente).setValue((int) valore);
                                    t.setText(valore + "");
                                }
                            }
                            ;
                        }
                        if (valore != 0.5 || (corrente.equalsIgnoreCase("misplaced class") && valore != 0.0)) {
                            standard.setSelected(false);
                        }
                        ;
                        cos.put(corrente, Double.parseDouble(coseno.get("coseno" + corrente).getText()));
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Inserire valori decimali con \".\" [0-1]", "", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            if (!s.equalsIgnoreCase("promiscuous package")) {
                livelli.add(secondo);

                secondo.setLayout(new GridLayout(0, 2, 0, 0));

                structural = new JLabel("Soglia algoritmi strutturali:");
                structural.setHorizontalAlignment(SwingConstants.CENTER);
                secondo.add(structural);

                app = new JPanel();
                if (!s.equalsIgnoreCase("Blob")) {
                    app.add(new JLabel("Dipendenze ="));
                } else {
                    app.add(new JLabel("LCOM ="));
                }
                addFieldStructural(s);

                if (s.equalsIgnoreCase("Blob")) {
                    app.add(new JLabel("FeatureSum ="));
                    addFieldStructural(s + "2");
                    app.add(new JLabel("ELOC ="));
                    addFieldStructural(s + "3");
                }
                secondo.add(app);
            }

            livelli.setBorder(new TitledBorder(s));
            centerPanel.add(livelli);
        }

        scelta = new JPanel();
        app = new JPanel();
        algorith = new JComboBox<String>();
        algorith.addItem("All");
        algorith.addItem("Textual");
        algorith.addItem("Structural");
        algorith.setSelectedItem(algoritmi);
        standard = new JCheckBox("Soglie di default");
        standard.setHorizontalAlignment(SwingConstants.CENTER);
        filtro(algoritmi);
        app.add(new JLabel("Tipo di algoritmo da usare:"));
        app.add(algorith);
        scelta.add(app);
        JPanel sotto = new JPanel();
        sotto.setLayout(new GridLayout(0, 2, 0, 0));

        sotto.add(scelta);

        algorith.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                JComboBox cb = (JComboBox) action.getSource();
                algoritmi = (String) cb.getSelectedItem();
                filtro(algoritmi);
            }
        });

        standard.setSelected(set);
        standard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                for (JSlider slider : sogliaT.values()) {
                    slider.setEnabled(true);
                    slider.setValue(50);
                }
                for (JTextField c : coseno.values()) {
                    c.setEnabled(true);
                    c.setText("0.5");
                }
                for (JTextField d : dipendenze.values()) {
                    d.setEnabled(true);
                    d.setText("0");
                }
                sogliaT.get("sogliaMisplaced class").setValue(0);
                coseno.get("cosenoMisplaced class").setText("0");
                dipendenze.get("dipendenzaBlob").setText("350");
                dipendenze.get("dipendenzaBlob2").setText("20");
                dipendenze.get("dipendenzaBlob3").setText("500");
                algorith.setSelectedIndex(0);
                standard.setSelected(true);
                reset();
            }
        });

        sotto.add(standard);
        contentPane.add(sotto, BorderLayout.SOUTH);

        return contentPane;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperExitAction("OK", 1) {
            @Override
            protected void doAction(ActionEvent actionEvent) {
                try {
                    scrivi();
                } finally {
                    super.doAction(actionEvent);
                }
            }
        };
        Action exitAction = new DialogWrapperExitAction("Cancel", 0) {
            @Override
            protected void doAction(ActionEvent actionEvent) {
                try {
                    FileReader f = new FileReader(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
                } catch (Exception e) {
                    scrivi();
                } finally {
                    super.doAction(actionEvent);
                }
            }
        };
        return new Action[]{okAction, exitAction};
    }

    private void reset() {
        for (String s : smell) {
            if (!s.equalsIgnoreCase("misplaced class")) {
                cos.put("coseno" + s, 0.5);
            } else {
                cos.put("coseno" + s, 0.0);
            }
            if (!s.equalsIgnoreCase("blob")) {
                dipendence.put("dip" + s, 0);
            } else {
                dipendence.put("dip" + s, 350);
            }
        }
        dipendence.put("dipBlob2", 20);
        dipendence.put("dipBlob3", 500);
        algoritmi = "All";
    }

    private void addFieldStructural(String name) {
        dipendenze.put("dipendenza" + name, new JTextField());
        dipendenze.get("dipendenza" + name).setText(dipendence.get("dip" + name) + "");
        app.add(dipendenze.get("dipendenza" + name));
        dipendenze.get("dipendenza" + name).setColumns(10);
        dipendenze.get("dipendenza" + name).setToolTipText("Inserire valore maggiore o uguale di 0");
        dipendenze.get("dipendenza" + name).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent d) {
                String corrente = null;
                try {
                    JTextField t = (JTextField) d.getSource();
                    Iterator<String> list = dipendenze.keySet().iterator();
                    while (list.hasNext() && !dipendenze.get(corrente = list.next()).equals(t)) {
                    }
                    if (Integer.parseInt(t.getText()) >= 0) {
                        dipendence.put(corrente, Integer.parseInt(t.getText()));
                    } else {
                        JOptionPane.showMessageDialog(null, "Inserire valori intero >=0", "Errore", JOptionPane.WARNING_MESSAGE);
                        dipendenze.get(corrente).setText("0");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Inserire valori intero >=0", "Errore", JOptionPane.WARNING_MESSAGE);
                    dipendenze.get(corrente).setText("0");
                }
                standard.setSelected(false);
            }
        });
    }

    private void scrivi() {
        try {
            FileWriter f = new FileWriter(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
            BufferedWriter out = new BufferedWriter(f);
            for (String a : smell) {
                if (a.equalsIgnoreCase("misplaced class") && standard.isSelected()) {
                    out.write("0.0,");
                } else {
                    out.write("0" + coseno.get("coseno" + a).getText() + ",");
                }
                ;
                if (!a.equalsIgnoreCase("promiscuous package")) {
                    out.write("0" + dipendenze.get("dipendenza" + a).getText() + ",");
                } else {
                    out.write("0,");
                }
                ;
                if (a.equalsIgnoreCase("blob")) {
                    out.write("0" + dipendenze.get("dipendenza" + a + "2").getText() + ",");
                    out.write("0" + dipendenze.get("dipendenza" + a + "3").getText() + ",");
                }
                out.write(String.valueOf(algorith.getSelectedItem()));
                out.flush();
                out.newLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void filtro(String algoritmi) {
        switch (algoritmi) {
            case "All":
                for (JSlider slider : sogliaT.values()) {
                    slider.setEnabled(true);
                }
                for (JTextField c : coseno.values()) {
                    c.setEnabled(true);
                }
                for (JTextField d : dipendenze.values()) {
                    d.setEnabled(true);
                }
                break;
            case "Structural":
                for (JSlider slider : sogliaT.values()) {
                    slider.setEnabled(false);
                }
                for (JTextField c : coseno.values()) {
                    c.setEnabled(false);
                }
                for (JTextField d : dipendenze.values()) {
                    d.setEnabled(true);
                }
                break;
            case "Textual":
                for (JSlider slider : sogliaT.values()) {
                    slider.setEnabled(true);
                }
                for (JTextField c : coseno.values()) {
                    c.setEnabled(true);
                }
                for (JTextField d : dipendenze.values()) {
                    d.setEnabled(false);
                }
        }
        standard.setSelected(false);
    }


}
