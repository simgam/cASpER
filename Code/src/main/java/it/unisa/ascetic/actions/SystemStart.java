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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//import it.unisa.ascetic.storage.repository.*;

public class SystemStart {

    boolean errorHappened;
    private String algoritmo;
    private static ArrayList<String> smell;
    private double minC = 0.5;
    private ArrayList<Integer> sogliaStructural;

    public SystemStart() {

        smell = new ArrayList<String>();
        smell.add("Feature");
        smell.add("Misplaced");
        smell.add("Blob");
        smell.add("Promiscuous");
        sogliaStructural = new ArrayList<Integer>();

        try {
            FileReader f = new FileReader(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
            BufferedReader b = new BufferedReader(f);

            String[] list = null;
            double sogliaCoseno;
            sogliaStructural.add(0);
            int app = 0;

            for (String s : smell) {
                list = b.readLine().split(",");
                sogliaCoseno = Double.parseDouble(list[0]);
                if (sogliaCoseno < minC) {
                    minC = sogliaCoseno;
                }

                if (!s.equalsIgnoreCase("promiscuous package")) {
                    app = Integer.parseInt(list[1]);
                    if (!s.equalsIgnoreCase("blob")) {
                        if (app < sogliaStructural.get(0)) {
                            sogliaStructural.add(0, app);
                        }
                        algoritmo = list[2];
                    } else {
                        sogliaStructural.add(app);
                        sogliaStructural.add(Integer.parseInt(list[2]));
                        sogliaStructural.add(Integer.parseInt(list[3]));
                        algoritmo = list[4];
                    }
                }
                ;
            }
            ;
        } catch (Exception e) {
            try {
                FileWriter f = new FileWriter(System.getProperty("user.home") + File.separator + ".ascetic" + File.separator + "threshold.txt");
                BufferedWriter out = new BufferedWriter(f);
                out.write("00.5,00,All\n" +
                        "0.0,00,All\n" +
                        "00.5,0350,020,0500,All\n" +
                        "00.5,0,All");
                out.flush();
                out.newLine();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ;
            minC = 0.5;
            sogliaStructural.add(0);
            sogliaStructural.add(350);
            sogliaStructural.add(20);
            sogliaStructural.add(500);
            algoritmo = "All";
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
            CheckProjectPage frame = new CheckProjectPage(currentProject, promiscuousList, blobList, misplacedList, featureList, minC, sogliaStructural, algoritmo);
            frame.show();
        } else {
            Messages.showMessageDialog(currentProject, "Sorry, an error has occurred. Prease try again or contact support", "OH ! No! ", Messages.getErrorIcon());
        }

    }
}
