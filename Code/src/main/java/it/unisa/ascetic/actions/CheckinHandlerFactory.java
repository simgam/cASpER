package it.unisa.ascetic.actions;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.BaseCheckinHandlerFactory;
import com.intellij.openapi.vcs.checkin.BeforeCheckinDialogHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Factory which provides callbacks to run before and after checkin operations.
 * !! This factory is loaded on first commit
 * should NOT be used from VCS plugins
 * use {@link VcsCheckinHandlerFactory} implementations instead, define through EP "com.intellij.checkinHandlerFactory"
 * they would automatically would be registered in {@link com.intellij.openapi.vcs.AbstractVcs#activate()}
 * and unregistered in {@link com.intellij.openapi.vcs.AbstractVcs#deactivate()}
 *
 * @author lesya
 */
public abstract class CheckinHandlerFactory implements BaseCheckinHandlerFactory {

    public static final ExtensionPointName<CheckinHandlerFactory> EP_NAME = ExtensionPointName.create("com.intellij.checkinHandlerFactory");

    /**
     * Creates a handler for a single Checkin Project or Checkin File operation.
     *
     * @param panel the class which can be used to retrieve information about the files to be committed,
     *              and to get or set the commit message.
     * @return the handler instance.
     */
    @Override
    @NotNull
    public abstract CheckinHandler createHandler(@NotNull CheckinProjectPanel var1, @NotNull CommitContext var2);

    @Override
    public BeforeCheckinDialogHandler createSystemReadyHandler(@NotNull Project project) {
        return null;
    }
}