package it.unisa.ascetic.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import it.unisa.ascetic.actions.SystemStart;

public class BeginAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        SystemStart start = new SystemStart();
        start.form(anActionEvent.getProject());

    }

}
