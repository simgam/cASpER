package it.unisa.ascetic.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BeginAction extends AnAction {

    boolean errorHappened;
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        PackageBeanRepository packRepo = new PackageRepository();
        ClassBeanRepository classRepo = new ClassRepository();
        MethodBeanRepository methodRepo = new MethodRepository();
        InstanceVariableBeanRepository instanceRepo = new InstanceVariableRepository();
        

        Project currentProject=anActionEvent.getProject();
        PsiParser parser = new PsiParser(currentProject);
        parser.setRepository(packRepo,classRepo,methodRepo,instanceRepo);
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

        }, "Ascetic - Code Smell Detection", false, anActionEvent.getProject());

        if(!errorHappened) {
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
            CheckProjectPage frame = new CheckProjectPage(currentProject, promiscuousList, blobList, misplacedList, featureList, "all");
            frame.show();
        } else {
            Messages.showMessageDialog(anActionEvent.getProject(),"Sorry, an error has occurred. Prease try again or contact support","OH ! No! ",Messages.getErrorIcon());
        }
    }
}
