package it.unisa.ascetic.actions;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import it.unisa.ascetic.actions.BeginAction;
import org.jetbrains.annotations.NotNull;

public class CommitFactory extends CheckinHandlerFactory {

    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {

        final CheckinHandler checkinHandler = new CheckinHandler() {
            @Override
            public ReturnResult beforeCheckin() {

                SystemStart start = new SystemStart();
                start.form(panel.getProject());

                return super.beforeCheckin();

            }
        };
        return checkinHandler;
    }

}

