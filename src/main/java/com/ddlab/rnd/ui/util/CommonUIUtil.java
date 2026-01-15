package com.ddlab.rnd.ui.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;

public class CommonUIUtil {

    public static void showAppSuccessfulMessage(String message) {
        ApplicationManager.getApplication().invokeLater(() ->
                Messages.showInfoMessage(message, "Gitifier"));
    }

    public static void showAppErrorMessage(String message) {
        ApplicationManager.getApplication().invokeLater(() ->
                Messages.showErrorDialog(message, "Gitifier"));
    }
}
