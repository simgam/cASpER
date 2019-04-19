package it.unisa.ascetic.gui;

import com.intellij.openapi.ui.DialogWrapper;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.MethodList;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static java.lang.System.exit;

public class FeatureEnvyRefactorDialog extends DialogWrapper {

    private JPanel refactoringPanel;
    private JPanel buttons;
    private JPanel radarmaps;

    private JTextArea codice;
    private ChartPanel oldcurrentclass;
    private ChartPanel oldenviedclass;
    private ChartPanel newenviedclass;
    private ChartPanel newcurrentclass;
    private MethodList featureEnvyList;
    private JButton applyButton;
    private JButton cancelButton;
    private ButtonGroup group;
    private List<MethodBean> listMethod;
    public RadarMapUtilsAdapter radars;

    public FeatureEnvyRefactorDialog() {
        super(true);
        init();
        setTitle("REFACTORING CODE SMELL");
    }

    /*public FeatureEnvyRefactorDialog(List<MethodBean> methodBeans){
        super(true);
        this.listMethod = methodBeans;
    }*/

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        refactoringPanel = new JPanel();
        refactoringPanel.setLayout(new BorderLayout());
        refactoringPanel.setPreferredSize(new Dimension(900, 800));

        // radarmaps
       /* radarmaps = new JPanel();
        radarmaps.setLayout(new GridLayout(2,2));
        for(MethodBean aBean : listMethod) {
            oldcurrentclass = radars.createRadarMapFromClassBean(aBean.getBelongingClass(), "Old Current Class");
            oldenviedclass = radars.createRadarMapFromClassBean(aBean.getEnviedClass(), "Old Envied Class");
*/
        // applicare soluzione refactor
        //      newcurrentclass = radars.createRadarMapFromClassBean(, "New Current Class");
        //    newenviedclass = radars.createRadarMapFromClassBean(, "New Envied Class");
        //}

      /*  radarmaps.add(newcurrentclass);
        radarmaps.add(newenviedclass);
        radarmaps.add(oldcurrentclass);
        radarmaps.add(oldenviedclass);
        radarmaps.setPreferredSize(new Dimension(400,400)); //prova
*/

        // buttons
        class ApplyRefactorListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit(0); // messaggio di effettuato refactoring
            }
        }

        applyButton = new JButton("Apply");

        //       cancelButton = new JButton("Cancel");

        group = new ButtonGroup();
        group.add(applyButton);
        group.add(cancelButton);

        buttons.add(applyButton);
        buttons.add(cancelButton);
        applyButton.addActionListener(new ApplyRefactorListener());

        //AGGIUNGERE MISURE

        // area testo per codice
        codice = new JTextArea();
        codice.setEditable(false);

        refactoringPanel.add(radarmaps, BorderLayout.WEST);
        refactoringPanel.add(codice, BorderLayout.EAST);
        refactoringPanel.add(buttons, BorderLayout.PAGE_END);


        return refactoringPanel;
    }
}
