package com.ddlab.rnd.ui.util;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;

public class CommonUIUtil {

    private static final String GROUP_ID = "ddlabs-gitifier.notifications";
    private static final String TITLE = "DDLABS Gitifier";

    public static void showAppSuccessfulMessage(String message) {
        ApplicationManager.getApplication().invokeLater(() ->
                Messages.showInfoMessage(message, "Gitifier"));
    }

    public static void showAppErrorMessage(String message) {
        ApplicationManager.getApplication().invokeLater(() ->
                Messages.showErrorDialog(message, "Gitifier"));
    }

    public static void info(Project project, String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(TITLE, message, NotificationType.INFORMATION)
                .notify(project);
    }

    public static void showError(Project project, String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(TITLE, message, NotificationType.ERROR)
                .notify(project);
    }

//    public static void showErrorNotifiation(String msg) {
//        Project[] projects = ProjectManager.getInstance().getOpenProjects();
//        NotificationGroupManager.getInstance()
//                .getNotificationGroup("Snykomycin Notification Group")
//                .createNotification(msg, NotificationType.ERROR)
//                .notify(projects[0]);
//    }
}
