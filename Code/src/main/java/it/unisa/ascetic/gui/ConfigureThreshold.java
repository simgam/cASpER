package it.unisa.ascetic.gui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

public class ConfigureThreshold extends DialogWrapper {

    private JComboBox<String> algorith;

    private JPanel contentPane;
    private JPanel centerPanel;
    private JLabel textual;
    private JLabel structural;
    private JCheckBox standard;
    private JSlider sogliaT;
    private JTextField coseno;
    private JTextField dipendenze;
    private JPanel livello1;
    private JPanel livello2;
    private JPanel livello3;

    private static double cos;
    private static int dipendence;
    private static String algoritmi;

    public ConfigureThreshold() {
        super(true);
        setResizable(false);
        init();
        setTitle("CONFIGURE THRESHOLD");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        try {
            FileReader f = new FileReader(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
            BufferedReader b = new BufferedReader(f);
            String[] list = b.readLine().split(",");

            cos = Double.parseDouble(list[0]);
            dipendence = Integer.parseInt(list[1]);
            algoritmi = list[2];
        } catch (Exception e) {
            cos = 0.5;
            dipendence = 0;
            algoritmi = "All";
        }
        contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(800, 500));
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel sfondo = new JPanel();
        centerPanel = new JPanel();
        sfondo.setLayout(new GridLayout(2,2));
        sfondo.add(centerPanel);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        contentPane.add(sfondo, BorderLayout.CENTER);

        livello1 = new JPanel();
        centerPanel.add(livello1);
        livello1.setLayout(new GridLayout(0, 2, 0, 0));
        JPanel primo = new JPanel();
        JPanel secondo = new JPanel();
        livello1.add(primo);
        livello1.add(secondo);

        textual = new JLabel("Soglia algoritmi testuali:");
        textual.setHorizontalAlignment(SwingConstants.CENTER);
        primo.add(textual);

        sogliaT = new JSlider();
        sogliaT.setForeground(Color.WHITE);
        sogliaT.setFont(new Font("Arial", Font.PLAIN, 12));
        sogliaT.setToolTipText("");
        sogliaT.setPaintTicks(true);
        sogliaT.setValue((int) (cos * 100));
        sogliaT.setMinorTickSpacing(10);
        sogliaT.setMinimum(0);
        secondo.add(sogliaT);

        coseno = new JTextField();
        coseno.setText(cos + "");
        secondo.add(coseno);
        coseno.setColumns(10);

        sogliaT.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                coseno.setText(String.valueOf(((double) sogliaT.getValue()) / 100));
                standard.setSelected(false);
                cos = Double.parseDouble(coseno.getText());
            }
        });

        coseno.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent c) {

                try {
                    JTextField t = (JTextField) c.getSource();
                    double valore = Double.parseDouble(t.getText());
                    if (valore >= 0.0 && valore <= 1.0) {
                        sogliaT.setValue((int) (valore * 100));
                    } else {
                        if (valore < 0.0) {
                            sogliaT.setValue(0);
                            coseno.setText(0.0 + "");
                        } else {
                            if (valore > 1.0) {
                                sogliaT.setValue(100);
                                coseno.setText(1.0 + "");
                            } else {
                                sogliaT.setValue((int) valore);
                                coseno.setText(valore + "");
                            }
                        }
                        ;
                    }
                    if (valore != 0.5) {
                        standard.setSelected(false);
                    }
                    ;
                    cos = Double.parseDouble(coseno.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Inserire valori decimali con \".\" [0-1]", "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        livello2 = new JPanel();
        livello2.setLayout(new GridLayout(0, 2, 0, 0));
        centerPanel.add(livello2);

        structural = new JLabel("Soglia algoritmi strutturali:");
        structural.setVerticalAlignment(SwingConstants.TOP);
        structural.setHorizontalAlignment(SwingConstants.CENTER);
        livello2.add(structural);

        JPanel app = new JPanel();
        dipendenze = new JTextField();
        dipendenze.setText(dipendence + "");
        app.add(dipendenze);
        livello2.add(app);
        dipendenze.setColumns(10);

        dipendenze.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent c) {
                dipendence = Integer.parseInt(dipendenze.getText());
                if (dipendence != 0) {
                    standard.setSelected(false);
                }
                ;
            }
        });

        livello3 = new JPanel();
        JPanel app2 = new JPanel();
        algorith = new JComboBox<String>();
        algorith.addItem("All");
        algorith.addItem("Textual");
        algorith.addItem("Structural");
        algorith.setSelectedItem(algoritmi);
        switch (algoritmi) {
            case "All":
                sogliaT.setEnabled(true);
                coseno.setEnabled(true);
                dipendenze.setEnabled(true);
                break;
            case "Structural":
                sogliaT.setEnabled(false);
                coseno.setEnabled(false);
                dipendenze.setEnabled(true);
                break;
            case "Textual":
                sogliaT.setEnabled(true);
                coseno.setEnabled(true);
                dipendenze.setEnabled(false);
        }
        app2.add(algorith);
        livello3.add(app2);
        centerPanel.add(livello3);

        algorith.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent action) {
                JComboBox cb = (JComboBox) action.getSource();
                algoritmi = (String) cb.getSelectedItem();
                switch (algoritmi) {
                    case "All":
                        sogliaT.setEnabled(true);
                        coseno.setEnabled(true);
                        dipendenze.setEnabled(true);
                        break;
                    case "Structural":
                        sogliaT.setEnabled(false);
                        coseno.setEnabled(false);
                        dipendenze.setEnabled(true);
                        break;
                    case "Textual":
                        sogliaT.setEnabled(true);
                        coseno.setEnabled(true);
                        dipendenze.setEnabled(false);
                }
                standard.setSelected(false);
            }
        });
        standard = new JCheckBox("Soglie di default");
        standard.setHorizontalAlignment(SwingConstants.CENTER);
        if (cos == 0.5 && dipendence == 0 && algorith.getSelectedItem().equals("All")) standard.setSelected(true);
        standard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                JCheckBox cb = (JCheckBox) a.getSource();
                coseno.setText("0.5");
                dipendenze.setText("0");
                algorith.setSelectedIndex(0);
                sogliaT.setValue(50);
                standard.setSelected(true);
                sogliaT.setEnabled(true);
                coseno.setEnabled(true);
                dipendenze.setEnabled(true);
                algoritmi = "All";
                cos = 0.5;
                dipendence = 0;
            }
        });

        contentPane.add(standard, BorderLayout.SOUTH);

        return contentPane;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        Action okAction = new DialogWrapperExitAction("OK", 1) {

            @Override
            protected void doAction(ActionEvent actionEvent) {
                try {
                    FileWriter f = new FileWriter(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
                    BufferedWriter out = new BufferedWriter(f);
                    out.write(coseno.getText() + ",");
                    out.write(dipendenze.getText() + ",");
                    out.write(String.valueOf(algorith.getSelectedItem()));
                    out.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    super.doAction(actionEvent);
                }

            }
        };

        return new Action[]{okAction, new DialogWrapperExitAction("Cancel", 0)};
    }

}
