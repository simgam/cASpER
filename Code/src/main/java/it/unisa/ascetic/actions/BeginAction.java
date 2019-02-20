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

public class BeginAction extends AnAction {

    boolean errorHappened;
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        System.out.println("BEGIN");
        PackageBeanRepository pacrepo = new PackageRepository();
        ClassBeanRepository classrepo = new ClassRepository();
        MethodBeanRepository metrepo = new MethodRepository();
        InstanceVariableBeanRepository insrepo = new InstanceVariableRepository();
        

        Project currentProject=anActionEvent.getProject();
        PsiParser parser = new PsiParser(currentProject);
        parser.setRepository(pacrepo,classrepo,metrepo,insrepo);
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
                } catch (RepositoryException e) {
                    errorHappened = true;
                }
            });

        }, "Ascetic - Code Smell Detection", false, anActionEvent.getProject());

        if(!errorHappened) {
            List<PackageBean> smellList = new ArrayList<>();
            List<ClassBean> classList = new ArrayList<>();
            List<ClassBean> classList2 = new ArrayList<>();
            List<MethodBean> methodList = new ArrayList<>();

            try {
                smellList = pacrepo.select(new SQLPromiscuousSelection());
                classList = classrepo.select(new SQLBlobSelection());
                classList2 = classrepo.select(new SQLMisplacedSelection());
                methodList = metrepo.select(new SQLFeatureSelection());

            } catch (RepositoryException e) {
                e.printStackTrace();
            }
            CheckProjectPage frank = new CheckProjectPage(currentProject, smellList, classList, classList2, methodList);
            frank.show();
        } else {
            Messages.showMessageDialog(anActionEvent.getProject(),"Sorry, an error has occurred. Prease try again or contact support","OH ! No! ",Messages.getErrorIcon());
        }
    }
}
