package it.unisa.casper.gui.radarMap;


import it.unisa.casper.storage.beans.ClassBean;
import it.unisa.casper.storage.beans.MethodBean;
import it.unisa.casper.storage.beans.PackageBean;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.util.TreeMap;

public interface RadarMapUtils {
    ChartPanel createRadarMapFromPackageBean(PackageBean aPackage, String mapTitle);

    ChartPanel createRadarMapFromClassBean(ClassBean aClass, String mapTitle);

    ChartPanel createRadarMapFromMethodBean(MethodBean aMethod, String mapTitle);

    JPanel getRadarMapPanel(TreeMap<String, Integer> belongingClassTopicsFinali, String newCurrentClassTopic);
}
