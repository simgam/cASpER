package it.unisa.ascetic.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import it.unisa.ascetic.gui.CheckProjectPage;
import it.unisa.ascetic.parser.ParsingException;
import it.unisa.ascetic.parser.PsiParser;
import it.unisa.ascetic.storage.beans.ClassBean;
import it.unisa.ascetic.storage.beans.MethodBean;
import it.unisa.ascetic.storage.beans.PackageBean;
import it.unisa.ascetic.storage.repository.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import it.unisa.ascetic.storage.repository.*;

public class SystemStart {

    boolean errorHappened;
    private String algoritmo;
    private HashMap<String, Double> coseno;
    private HashMap<String, Integer> dipendence;
    private static ArrayList<String> smell;
    private double minC = 0.5;
    private int minD = 0;

    public SystemStart() {

        coseno = new HashMap<String, Double>();
        dipendence = new HashMap<String, Integer>();

        smell = new ArrayList<String>();
        smell.add("Feature");
        smell.add("Misplaced");
        smell.add("Blob");
        smell.add("Promiscuous");
        try {
            FileReader f = new FileReader(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
            BufferedReader b = new BufferedReader(f);

            String[] list = null;
            double sogliaCoseno;
            int sogliaDip;
            for(String s : smell){
                list = b.readLine().split(",");

                sogliaCoseno = Double.parseDouble(list[0]);
                if (sogliaCoseno < minC) {
                    minC = sogliaCoseno;
                }
                coseno.put("coseno" + s, sogliaCoseno);

                sogliaDip = Integer.parseInt(list[1]);
                if (sogliaDip < minD) {
                    minD = sogliaDip;
                }
                dipendence.put("dip" + s, sogliaDip);
            }
            ;
            this.algoritmo = list[2];
        } catch (Exception e) {
            for(String s : smell){
                coseno.put("coseno" + s, 0.5);
                dipendence.put("dip" + s, 0);
            }
            this.algoritmo = "All";
        }
    }

    public void form(Project currentProject) {

        PackageBeanRepository packRepo = new PackageRepository();
        ClassBeanRepository classRepo = new ClassRepository();
        MethodBeanRepository methodRepo = new MethodRepository();
        InstanceVariableBeanRepository instanceRepo = new InstanceVariableRepository();

        PsiParser parser = new PsiParser(currentProject);
        errorHappened = false;

        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {

            ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
            indicator.setIndeterminate(true);
            indicator.setText("Analyzing project ...");

            ApplicationManager.getApplication().runReadAction(() -> {
                try {
                    parser.parse();
                } catch (ParsingException e) {
                    errorHappened = true;
                    e.printStackTrace();
                } catch (RepositoryException e) {
                    errorHappened = true;
                    e.printStackTrace();
                }
            });

        }, "Ascetic - Code Smell Detection", false, currentProject);

        if (!errorHappened) {
            List<PackageBean> promiscuousList = new ArrayList<>();
            List<ClassBean> blobList = new ArrayList<>();
            List<ClassBean> misplacedList = new ArrayList<>();
            List<MethodBean> featureList = new ArrayList<>();

            try {
                promiscuousList = packRepo.select(new SQLPromiscuousSelection());
                blobList = classRepo.select(new SQLBlobSelection());
                misplacedList = classRepo.select(new SQLMisplacedSelection());
                featureList = methodRepo.select(new SQLFeatureSelection());

            } catch (RepositoryException e) {
                e.printStackTrace();
            }
            CheckProjectPage frame = new CheckProjectPage(currentProject, promiscuousList, blobList, misplacedList, featureList, minC, minD, algoritmo);
            frame.show();
        } else {
            Messages.showMessageDialog(currentProject, "Sorry, an error has occurred. Prease try again or contact support", "OH ! No! ", Messages.getErrorIcon());
        }

    }
}
