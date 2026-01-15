package com.ddlab.rnd.action;

import com.ddlab.rnd.ui.dialog.CodePublishDialog;
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

        // Perform logic later
        CodePublishDialog gitPushDialog = new CodePublishDialog(project, selectedFile, true);
        gitPushDialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        VirtualFile selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        // Show ONLY when:
        // 1. Project exists
        // 2. Selected node is project root
        boolean visible = project != null
                && selectedFile != null
                && selectedFile.equals(project.getBaseDir());

        presentation.setEnabledAndVisible(visible);
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // UI-safe
    }
}
