package com.ddlab.rnd.action;

import com.ddlab.rnd.ui.dialog.CodeSharingDialog;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CodePublishAction extends AnAction  {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        VirtualFile virtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        File selectedFile = new File(virtualFile.getPath());

        CodeSharingDialog gitPushDialog = new CodeSharingDialog(project, selectedFile, true);
        gitPushDialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        VirtualFile selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean visible = project != null
                && selectedFile != null
                && selectedFile.equals(project.getBaseDir());

        presentation.setEnabledAndVisible(visible);
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
