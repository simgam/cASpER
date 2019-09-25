package it.unisa.ascetic.gui.radarMap;

import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.topic.TopicExtracter;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Crea radar map per ogni tipo di Bean utilizzando i topic correlati
 * @version 2.0
 * @author Sara Patierno
 */

public class RadarMapUtilsAdapter implements RadarMapUtils {

    private JFreeChart chart;

    /**
     * Genera la radar map di un PackageBean
     * @param aPackage PackageBean di cui mostrare la radar map
     * @param mapTitle String titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    public ChartPanel createRadarMapFromPackageBean(PackageBean aPackage, String mapTitle) {
        TopicExtracter extracter = new TopicExtracter();
        TreeMap<String, Integer> terms = extracter.extractTopicFromPackageBean(aPackage);
        return getRadarMapPanel(terms, mapTitle);
    }

    /**
     * Genera la radar map di un ClassBean
     * @param aClass ClassBean di cui mostrare la radar map
     * @param mapTitle String titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    public ChartPanel createRadarMapFromClassBean(ClassBean aClass, String mapTitle) {
        TopicExtracter extracter = new TopicExtracter();
        TreeMap<String, Integer> terms = extracter.extractTopicFromClassBean(aClass);
        return getRadarMapPanel(terms, mapTitle);
    }

    /**
     * Genera la radar map di un MethodBean
     * @param aMethod MethodBean di cui mostrare la radar map
     * @param mapTitle String titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    public ChartPanel createRadarMapFromMethodBean(MethodBean aMethod, String mapTitle) {
        TopicExtracter extracter = new TopicExtracter();
        TreeMap<String, Integer> terms = extracter.extractTopicFromMethodBean(aMethod);
        return getRadarMapPanel(terms, mapTitle);
    }

    /**
     * Crea la radar map di un qualsiasi Bean
     * @param terms TreeMap<String,Integer> contenente i topic di un qualsiasi Bean
     * @param mapTitle String titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    @NotNull
    public ChartPanel getRadarMapPanel(TreeMap<String, Integer> terms, String mapTitle) {
        Set<Map.Entry<String,Integer>> topics = terms.entrySet();
        DefaultCategoryDataset defaultCategoryDataSet = new DefaultCategoryDataset();
        for(Map.Entry<String, Integer> topic : topics) {
            defaultCategoryDataSet.addValue(topic.getValue(), "Responsabilities", topic.getKey());
        }

        SpiderWebPlot plot = new SpiderWebPlot(defaultCategoryDataSet);
        plot.setNoDataMessage("No data available");
        plot.setStartAngle(0);
        plot.setWebFilled(true);
        plot.setBackgroundPaint(Color.white);

        chart = new JFreeChart(mapTitle, TextTitle.DEFAULT_FONT, plot, false);
        chart.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(50,50);
            }
        };
        chartPanel.setMaximumSize(new Dimension(50,50));
        return chartPanel;
    }

    /**
     * Aggiorna la radar map di un PackageBean
     * @param radarMap ChartPanel radar map preesistente di cui aggiornare i valori
     * @param newPackageBean PackageBean con cui modificare la radar map
     * @param newMapTitle String nuovo titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    public void updatePackageBeanRadarmap(ChartPanel radarMap, PackageBean newPackageBean, String newMapTitle){
        TopicExtracter extracter = new TopicExtracter();
        TreeMap<String, Integer> terms = extracter.extractTopicFromPackageBean(newPackageBean);

        updateRadarMap(radarMap, terms, newMapTitle);

    }

    /**
     * Aggiorna la radar map di un ClassBean
     * @param radarMap ChartPanel radar map preesistente di cui aggiornare i valori
     * @param newClassBean ClassBean con cui modificare la radar map
     * @param newMapTitle String nuovo titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    public void updateClassBeanRadarmap(ChartPanel radarMap, ClassBean newClassBean, String newMapTitle){
        TopicExtracter extracter = new TopicExtracter();
        TreeMap<String, Integer> terms = extracter.extractTopicFromClassBean(newClassBean);

        updateRadarMap(radarMap, terms, newMapTitle);

    }

    /**
     * Aggiorna la radar map di un MethodBean
     * @param radarMap ChartPanel radar map preesistente di cui aggiornare i valori
     * @param newMethodBean MethodBean con cui modificare la radar map
     * @param newMapTitle String nuovo titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    public void updateMethodBeanRadarmap(ChartPanel radarMap, MethodBean newMethodBean, String newMapTitle){
        TopicExtracter extracter = new TopicExtracter();
        TreeMap<String, Integer> terms = extracter.extractTopicFromMethodBean(newMethodBean);

        updateRadarMap(radarMap, terms, newMapTitle);

    }
    /**
     * Modifica la radar map di un qualsiasi Bean
     * @param radarMap ChartPanel radar map preesistente di cui aggiornare i valori
     * @param terms TreeMap<String,Integer> contenente i topic di un qualsiasi Bean
     * @param newMapTitle String nuovo titolo del panel contenente la radar map
     * @return ChartPanel panel contenente la radar map
     */
    private void updateRadarMap(ChartPanel radarMap, TreeMap<String, Integer> terms, String newMapTitle) {
        Set<Map.Entry<String,Integer>> topics = terms.entrySet();
        DefaultCategoryDataset defaultCategoryDataSet = new DefaultCategoryDataset();
        for(Map.Entry<String, Integer> topic : topics) {
            defaultCategoryDataSet.addValue(topic.getValue(), "Responsabilities", topic.getKey());
        }

        radarMap.getChart().setTitle(newMapTitle);
        SpiderWebPlot plot = (SpiderWebPlot) radarMap.getChart().getPlot();
        plot.setDataset(defaultCategoryDataSet);

        radarMap.repaint();
    }
}
