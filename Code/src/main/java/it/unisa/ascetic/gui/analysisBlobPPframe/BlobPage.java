package it.unisa.ascetic.gui.analysisBlobPPframe;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import it.unisa.ascetic.gui.radarMap.RadarMapUtils;
import it.unisa.ascetic.gui.radarMap.RadarMapUtilsAdapter;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.structuralMetrics.CKMetrics;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlobPage extends DialogWrapper {

    private RadarMapUtils radarMapUtils;        //roba che serve per le radar map
    private ClassBean classBeanBlob;            //ClassBean sul quale avviene l'analisi

    private JButton refactorButton;             //button che permette il refactoring degli smell selezionati
    private JButton ignoreButton;               //button che permette di segnalare come falsi positivi gli smell selezionati
    private JButton remindButton;               //button che permette di inserire un reminder nella locazione degli smell selezionati

    private JTextArea area;                     //area di testo dove viene mostrato in dettaglio il codice del CodeSmell selezionato
    private JScrollPane scrollPaneArea;         //scroller per la text area


    private JPanel contentPanel;                //panel che raggruppa tutti gli elementi
    private JPanel panelRadarMapMaster;         //panel che ingloba la radar map
    private JPanel panelRadarMap;
    private JPanel panelMetric;                 //panel per le metriche
    private JPanel panelButton;                 //panel che raggruppa i bottoni
    private JPanel panelWest;                   //panel che raggruppa gli elementi a sinistra
    private JPanel panelEast;                   //panel che raggruppa gli elementi a destra
    private JPanel panelGrid1;                  //panel per il gridLayout
    private JPanel panelGrid2;                  //panel per il gridLayout
    private JPanel panelGrid3;                  //panel per il gridLayout

    private JBTable table;                      //tabella dove sono visualizzati i codeSmell

    public BlobPage(ClassBean classBeanBlob) {
        super(true);
        this.classBeanBlob = classBeanBlob;
        init();
        setTitle("ANALYSIS CODE SMELL BLOB");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        radarMapUtils = new RadarMapUtilsAdapter();

        panelRadarMap = radarMapUtils.createRadarMapFromClassBean(classBeanBlob, "BLOB");

        //INIZIALIZZO I PANEL
        contentPanel = new JPanel();            //pannello principale
        panelButton = new JPanel();             //pannello dei bottoni
        panelRadarMapMaster = new JPanel();     //pannello che ingloba le radarMap
        panelMetric = new JPanel();             //pannello che ingloba le metriche
        panelWest = new JPanel();               //pannello che ingloba gli elementi di sinistra
        panelEast = new JPanel();               //pannello che ingloba gli elementi di destra

        panelGrid1 = new JPanel();
        panelGrid2 = new JPanel();
        panelGrid3 = new JPanel();

        //INIZIALIZZO LA TABELLA E LA TEXT AREA
        area = new JTextArea();                 //text area dove viene visualizzato il codice in esame
        table = new JBTable();                  //tabella dove sono presenti gli smell da prendere in esame

        //INIZIALIZZO I BUTTON
        refactorButton = new JButton();         //bottone refactor
        ignoreButton = new JButton();           //bottone ignore
        remindButton = new JButton();           //bottone remind

        //SETTO IL TESTO NEI BUTTON
        refactorButton.setText("Refactor");
        ignoreButton.setText("Ignore");
        remindButton.setText("Remind");

        //SETTO A "NON UTILIZZABILI" I BUTTON A CUI NON CORRISPONDONO NESSUNA FUNZIONE IMPLEMENTATA
        ignoreButton.setEnabled(false);
        remindButton.setEnabled(false);

        //SETTO TESTO NELLA TEXT AREA
        area.append(classBeanBlob.getTextContent());

        area.append("    public static int getLOC(ClassBean cb) {\n" +
                "        return cb.getLOC();\n" +
                "    }\n" +
                "\n" +
                "    public static int getLOC(MethodBean mb) {\n" +
                "        return mb.getTextContent().split(\"\\n\").length;\n" +
                "    }\n" +
                "\n" +
                "    public static int getWMC(ClassBean cb) {\n" +
                "\n" +
                "        int WMC = 0;\n" +
                "\n" +
                "        Vector<MethodBean> methods = (Vector<MethodBean>) cb.getMethodList();\n" +
                "        for (MethodBean m : methods) {\n" +
                "            WMC += getMcCabeCycloComplexity(m);\n" +
                "        }\n" +
                "\n" +
                "        return WMC;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public static int getNumberOfClasses(PackageBean pb) {\n" +
                "        return pb.getClassList().size();\n" +
                "    }\n" +
                "\n" +
                "    public static int getDIT(ClassBean cb, Vector<ClassBean> System, int inizialization) {\n" +
                "\n" +
                "        int DIT = inizialization;\n" +
                "\n" +
                "        if (DIT == 3)\n" +
                "            return DIT;\n" +
                "        else {\n" +
                "            if (cb.getSuperclass() != null) {\n" +
                "                DIT++;\n" +
                "                for (ClassBean cb2 : System) {\n" +
                "                    if (cb2.getFullQualifiedName().equals(cb.getSuperclass())) {\n" +
                "                        getDIT(cb2, System, DIT);\n" +
                "                    }\n" +
                "                }\n" +
                "            } else {\n" +
                "                return DIT;\n" +
                "            }\n" +
                "        }\n" +
                "        return DIT;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public static int getNOC(ClassBean cb, Vector<ClassBean> System) {\n" +
                "\n" +
                "        int NOC = 0;\n" +
                "\n" +
                "        for (ClassBean c : System) {\n" +
                "            if (c.getSuperclass() != null && c.getSuperclass().equals(cb.getFullQualifiedName())) {\n" +
                "                NOC++;\n" +
                "            }\n" +
                "        }\n" +
                "        return NOC;\n" +
                "    }\n" +
                "\n" +
                "    public static int getRFC(ClassBean cb) {\n" +
                "\n" +
                "        int RFC = 0;\n" +
                "\n" +
                "        Vector<MethodBean> methods = (Vector<MethodBean>) cb.getMethodList();\n" +
                "        for (MethodBean m : methods) {\n" +
                "            RFC += m.getMethodsCalls().size();\n" +
                "        }\n" +
                "        return RFC;\n" +
                "    }\n" +
                "\n" +
                "    public static int getCBO(ClassBean cb) {\n" +
                "\n" +
                "        Vector<String> imports = (Vector<String>) cb.getImports();\n" +
                "\n" +
                "        return imports.size();\n" +
                "    }\n" +
                "\n" +
                "    public static int getLCOM(ClassBean cb) {\n" +
                "\n" +
                "        int share = 0;\n" +
                "        int notShare = 0;\n" +
                "\n" +
                "        Vector<MethodBean> methods = (Vector<MethodBean>) cb.getMethodList();\n" +
                "        for (int i = 0; i < methods.size(); i++) {\n" +
                "            for (int j = i + 1; j < methods.size(); j++) {\n" +
                "                if (shareAnInstanceVariable(methods.elementAt(i), methods.elementAt(j))) {\n" +
                "                    share++;\n" +
                "                } else {\n" +
                "                    notShare++;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        if (share > notShare) {\n" +
                "            return 0;\n" +
                "        } else {\n" +
                "            return (notShare - share);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static int getNOM(ClassBean cb) {\n" +
                "        return cb.getMethodList().size();\n" +
                "    }\n" +
                "\n" +
                "    public static int getNOA(ClassBean cb) {\n" +
                "        return cb.getInstanceVariablesList().size();\n" +
                "    }\n" +
                "\n" +
                "    public static int getNOPA(ClassBean cb) {\n" +
                "        int publicVariable = 0;\n" +
                "\n" +
                "        Collection<InstanceVariableBean> variables = cb.getInstanceVariablesList();\n" +
                "\n" +
                "        for (InstanceVariableBean variable : variables) {\n" +
                "            if (variable.getVisibility() != null && variable.getVisibility().equals(\"public\"))\n" +
                "                publicVariable++;\n" +
                "        }\n" +
                "\n" +
                "        return publicVariable;\n" +
                "    }\n" +
                "\n" +
                "    public static int getNOPrivateA(ClassBean cb) {\n" +
                "        int privateVariable = 0;\n" +
                "\n" +
                "        Collection<InstanceVariableBean> variables = cb.getInstanceVariablesList();\n" +
                "\n" +
                "        for (InstanceVariableBean variable : variables) {\n" +
                "            if (variable.getVisibility() == null || variable.getVisibility().equals(\"private\"))\n" +
                "                privateVariable++;\n" +
                "        }\n" +
                "        return privateVariable;\n" +
                "    }\n" +
                "\n" +
                "    //Number of operations added by a subclass\n" +
                "    public static int getNOA(ClassBean cb, Vector<ClassBean> System) {\n" +
                "\n" +
                "        int NOA = 0;\n" +
                "\n" +
                "        for (ClassBean c : System) {\n" +
                "            if (c.getFullQualifiedName().equals(cb.getSuperclass())) {\n" +
                "                Vector<MethodBean> subClassMethods = (Vector<MethodBean>) cb.getMethodList();\n" +
                "                Vector<MethodBean> superClassMethods = (Vector<MethodBean>) c.getMethodList();\n" +
                "                for (MethodBean m : subClassMethods) {\n" +
                "                    if (!superClassMethods.contains(m)) {\n" +
                "                        NOA++;\n" +
                "                    }\n" +
                "                }\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "        return NOA;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    //Number of operations overridden by a subclass\n" +
                "    public static int getNOO(ClassBean cb, Vector<ClassBean> System) {\n" +
                "\n" +
                "        int NOO = 0;\n" +
                "\n" +
                "        if (cb.getSuperclass() != null) {\n" +
                "            for (ClassBean c : System) {\n" +
                "                if (c.getFullQualifiedName().equals(cb.getSuperclass())) {\n" +
                "                    Vector<MethodBean> subClassMethods = (Vector<MethodBean>) cb.getMethodList();\n" +
                "                    Vector<MethodBean> superClassMethods = (Vector<MethodBean>) c.getMethodList();\n" +
                "                    for (MethodBean m : subClassMethods) {\n" +
                "                        if (superClassMethods.contains(m)) {\n" +
                "                            NOO++;\n" +
                "                        }\n" +
                "                    }\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return NOO;\n" +
                "    }\n" +
                "\n" +
                "    public static double computeMediumIntraConnectivity(PackageBean pPackage) {\n" +
                "        double packAllLinks = Math.pow(pPackage.getClassList().size(), 2);\n" +
                "        double packIntraConnectivity = 0.0;\n" +
                "\n" +
                "        for (ClassBean eClass : pPackage.getClassList()) {\n" +
                "            for (ClassBean current : pPackage.getClassList()) {\n" +
                "                if (eClass != current) {\n" +
                "                    if (existsDependence(eClass, current)) {\n" +
                "                        packIntraConnectivity++;\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return packIntraConnectivity / packAllLinks;\n" +
                "    }\n" +
                "\n" +
                "    public static double computeMediumInterConnectivity(PackageBean pPackage, Collection<PackageBean> pPackages) {\n" +
                "        double sumInterConnectivities = 0.0;\n" +
                "\n" +
                "        for (ClassBean eClass : pPackage.getClassList()) {\n" +
                "            for (PackageBean currentPack : pPackages) {\n" +
                "                double packsInterConnectivity = 0.0;\n" +
                "                double packsAllLinks = 2 * pPackage.getClassList().size() * currentPack.getClassList().size();\n" +
                "\n" +
                "                if (pPackage != currentPack) {\n" +
                "                    for (ClassBean currentClass : currentPack.getClassList()) {\n" +
                "                        if (existsDependence(eClass, currentClass)) {\n" +
                "                            packsInterConnectivity++;\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "                sumInterConnectivities += ((packsInterConnectivity) / packsAllLinks);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        return ((1.0 / (pPackages.size() * (pPackages.size() - 1))) * sumInterConnectivities);\n" +
                "    }\n" +
                "\n" +
                "    public static double computeMediumIntraConnectivity(Collection<PackageBean> pClusters) {\n" +
                "        double sumIntraConnectivities = 0.0;\n" +
                "        for (PackageBean pack : pClusters) {\n" +
                "            double packAllLinks = Math.pow(pack.getClassList().size(), 2);\n" +
                "            double packIntraConnectivity = 0.0;\n" +
                "            for (ClassBean eClass : pack.getClassList()) {\n" +
                "                for (ClassBean current : pack.getClassList()) {\n" +
                "                    if (eClass != current) {\n" +
                "                        if (existsDependence(eClass, current)) {\n" +
                "                            packIntraConnectivity++;\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "            sumIntraConnectivities += packIntraConnectivity / packAllLinks;\n" +
                "        }\n" +
                "\n" +
                "        return ((1.0 / pClusters.size()) * sumIntraConnectivities);\n" +
                "    }\n" +
                "\n" +
                "    public static double computeMediumInterConnectivity(Collection<PackageBean> pClusters) {\n" +
                "        double sumInterConnectivities = 0.0;\n" +
                "\n" +
                "        for (PackageBean pack : pClusters) {\n" +
                "            for (ClassBean eClass : pack.getClassList()) {\n" +
                "                for (PackageBean currentPack : pClusters) {\n" +
                "                    double packsInterConnectivity = 0.0;\n" +
                "                    double packsAllLinks = 2 * pack.getClassList().size() * currentPack.getClassList().size();\n" +
                "                    if (pack != currentPack) {\n" +
                "                        for (ClassBean currentClass : currentPack.getClassList()) {\n" +
                "                            if (existsDependence(eClass, currentClass)) {\n" +
                "                                packsInterConnectivity++;\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                    sumInterConnectivities += ((packsInterConnectivity) / packsAllLinks);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return ((1.0 / (pClusters.size() * (pClusters.size() - 1))) * sumInterConnectivities);\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public static int getMcCabeCycloComplexity(MethodBean mb) {\n" +
                "\n" +
                "        int mcCabe = 0;\n" +
                "        String code = mb.getTextContent();\n" +
                "\n" +
                "        String regex = \"return\";\n" +
                "        Pattern pattern = Pattern.compile(regex);\n" +
                "        Matcher matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"if\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"else\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"case\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"for\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"while\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"break\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"&&\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"||\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"catch\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "\n" +
                "        regex = \"throw\";\n" +
                "        pattern = Pattern.compile(regex);\n" +
                "        matcher = pattern.matcher(code);\n" +
                "\n" +
                "        if (matcher.find()) {\n" +
                "            mcCabe++;\n" +
                "        }\n" +
                "        return mcCabe;\n" +
                "    }\n" +
                "\n" +
                "    public static double getNumberOfDependencies(ClassBean pClass, PackageBean pPackage) {\n" +
                "        double dependencies = 0.0;\n" +
                "\n" +
                "        for (ClassBean classBean : pPackage.getClassList()) {\n" +
                "            if (existsDependence(pClass, classBean))\n" +
                "                dependencies++;\n" +
                "        }\n" +
                "        return dependencies;\n" +
                "    }\n" +
                "\n" +
                "    public static double getNumberOfDependencies(MethodBean pMethod, ClassBean pClass) {\n" +
                "        double dependencies = 0.0;\n" +
                "\n" +
                "        if (pClass != null&&pMethod.getMethodsCalls()!=null) {\n" +
                "            for (MethodBean call : pMethod.getMethodsCalls()) {\n" +
                "                for (MethodBean classMethod : pClass.getMethodList()) {\n" +
                "                    if (call.getFullQualifiedName().equals(classMethod.getFullQualifiedName()))\n" +
                "                        dependencies++;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return dependencies;\n" +
                "    }\n" +
                "\n" +
                "    private static boolean existsDependence(ClassBean pClass1, ClassBean pClass2) {\n" +
                "\n" +
                "\n" +
                "        for (MethodBean methodClass1 : pClass1.getMethodList()) {\n" +
                "            for (MethodBean call : methodClass1.getMethodsCalls()) {\n" +
                "                for (MethodBean methodClass2 : pClass2.getMethodList()) {\n" +
                "                    if (call.getFullQualifiedName().equals(methodClass2.getFullQualifiedName()))\n" +
                "                        return true;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        for (MethodBean methodClass2 : pClass2.getMethodList()) {\n" +
                "            for (MethodBean call : methodClass2.getMethodsCalls()) {\n" +
                "\n" +
                "                for (MethodBean methodClass1 : pClass1.getMethodList()) {\n" +
                "                    if (call.getFullQualifiedName().equals(methodClass1.getFullQualifiedName()))\n" +
                "                        return true;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    private static boolean shareAnInstanceVariable(MethodBean m1, MethodBean m2) {\n" +
                "\n" +
                "        Vector<InstanceVariableBean> m1Variables = (Vector<InstanceVariableBean>) m1.getInstanceVariableList();\n" +
                "        Vector<InstanceVariableBean> m2Variables = (Vector<InstanceVariableBean>) m2.getInstanceVariableList();\n" +
                "\n" +
                "        for (InstanceVariableBean i : m1Variables) {\n" +
                "            if (m2Variables.contains(i)) {\n" +
                "                return true;\n" +
                "            }\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n");


        //SETTO LA TABELLA PER LE METRICHE
        Object[] label = {"LOC", "WMC", "RFC", "CBO", "LCOM"};
        //Object[] contenuto = {CKMetrics.getLOC(classBeanBlob), CKMetrics.getWMC(classBeanBlob), CKMetrics.getRFC(classBeanBlob),CKMetrics.getCBO(classBeanBlob), CKMetrics.getLCOM(classBeanBlob)};
        Object[] contenuto = {"pippo", "pluto", "paperino","alta","cise"};
        DefaultTableModel model = new DefaultTableModel(label, 0);
        model.addRow(contenuto);
        table.setModel(model);

        //SETTO I LAYOUT DEI PANEL
        panelButton.setLayout(new FlowLayout());
        panelRadarMapMaster.setLayout(new BorderLayout());
        panelWest.setLayout(new GridLayout(3, 1));
        panelEast.setLayout(new BorderLayout());
        panelMetric.setLayout((new BorderLayout()));
        contentPanel.setLayout(new BorderLayout());
        panelGrid1.setLayout(new BorderLayout());
        panelGrid2.setLayout(new BorderLayout());
        panelGrid3.setLayout(new BorderLayout());

        //AGGIUNGO COMPONENTI AI VARI PANEL

        panelButton.add(refactorButton);
        panelButton.add(ignoreButton);
        panelButton.add(remindButton);
        panelGrid3.add(panelButton, BorderLayout.SOUTH);

        panelRadarMapMaster.add(panelRadarMap, BorderLayout.CENTER);
        panelGrid1.add(panelRadarMapMaster);

        panelMetric.add(table);
        panelGrid2.add(panelMetric, BorderLayout.SOUTH);

        panelWest.add(panelGrid1);
        panelWest.add(panelGrid2);
        panelWest.add(panelGrid3);

        panelEast.add(area, BorderLayout.CENTER);
       // panelEast.add(scrollPaneArea);

        contentPanel.add(panelWest, BorderLayout.WEST);
        contentPanel.add(panelEast, BorderLayout.EAST);

        class RefactorListener implements ActionListener {  //Listener per il button refactor
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 31/01/2019 aggiungerer vera logica del listener
                //RefactorBlobDialog refactorDialog = new RefactorBlobDialog();
                //refactorDialog.show();
            }
        }
        refactorButton.addActionListener(new RefactorListener());   //Linko il button al listener


        return contentPanel;
    }
}
