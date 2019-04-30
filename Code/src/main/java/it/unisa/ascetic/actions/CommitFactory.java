package it.unisa.ascetic.actions;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CommitFactory extends CheckinHandlerFactory {

    @NotNull
    @Override
    public CheckinHandler createHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {

        final CheckinHandler checkinHandler = new CheckinHandler() {
            @Override
            public ReturnResult beforeCheckin() {

                SystemStart start = new SystemStart();
                start.form(panel.getProject());

                int valore = JOptionPane.showConfirmDialog(null, "Vuoi effettuare il commit?", "Commit", JOptionPane.YES_NO_OPTION);
                if (valore == 0)return super.beforeCheckin();

                return ReturnResult.CANCEL;
            }
        };
        return checkinHandler;
    }

}

