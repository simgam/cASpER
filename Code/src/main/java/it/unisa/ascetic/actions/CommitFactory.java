package src.main.java.it.unisa.ascetic.actions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import org.jetbrains.annotations.NotNull;

public class CommitFactory extends CheckinHandlerFactory {

    boolean errorHappened;

    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {

        final CheckinHandler checkinHandler = new CheckinHandler() {
            @Override
            public ReturnResult beforeCheckin() {

                ActionManager am = ActionManager.getInstance();
                am.getAction("it.unisa.ascetic.actions.BeginAction").actionPerformed(new AnActionEvent(null, DataManager.getInstance().getDataContext(),
                        ActionPlaces.UNKNOWN, new Presentation(),
                        ActionManager.getInstance(), 0));

                return super.beforeCheckin();
            }

        };
        return checkinHandler;

    }
}
