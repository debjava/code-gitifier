package com.ddlab.rnd.ui.dialog;

import com.ddlab.rnd.ui.CodePublishPanelComponent;
import com.ddlab.rnd.ui.util.CodeSharingUtil;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.ddlab.rnd.ui.util.UIUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class CodeSharingDialog extends DialogWrapper {

    private JPanel panel;
    private CodePublishPanelComponent codePublishPanelComponent;
    private Project project;

    public CodeSharingDialog(@Nullable Project project, File selectedRepo, boolean canBeParent) {
        super(project, canBeParent);
        this.project = project;
        setTitle("Share code in Github,Gitlab or Bitbucket ");
        setOKActionEnabled(false);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = createUIAndGetPanel();
//        showMessage();
        attachValidationListener();
        return panel;
    }

    @Override
    protected void doOKAction() {
        saveLastSession();
        close(1);
        shareYourCode();
    }


    // ~~~~~~~~ private methods ~~~~~~~~
    private JPanel createUIAndGetPanel() {
        codePublishPanelComponent = new CodePublishPanelComponent();
        return codePublishPanelComponent.getMainPanel();
    }

    private void saveLastSession() {
        JComboBox hostedGitTypeCombo = codePublishPanelComponent.getHostedGitTypeCombo();
        JComboBox slGitUserNameCombo = codePublishPanelComponent.getSlGitUserNameCombo();
        UIUtil.saveLastSessionSetting(hostedGitTypeCombo, slGitUserNameCombo);
    }

//    private void showMessage() {
//        PublisherSetting setting = PublisherSetting.getInstance();
//        Map<String, String> gitSettingMap = setting.getGitInfoTableMap();
//        Map<String, String> tableMap = setting.getGitInfoTableMap();
//        if (gitSettingMap.isEmpty()) {
//            CommonUIUtil.showError(project, "Fill up the information...");
//        }
//    }

    private void attachValidationListener() {
        JTextArea textArea = codePublishPanelComponent.getTextArea();
        textArea.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        JComboBox gitTypeCombo = codePublishPanelComponent.getHostedGitTypeCombo();
        JComboBox slGitUserNameCombo = codePublishPanelComponent.getSlGitUserNameCombo();

        boolean valid = !codePublishPanelComponent.getTextArea().getText().trim().isEmpty()
                && gitTypeCombo.getSelectedItem() != null
                && slGitUserNameCombo.getSelectedItem() != null;

        setOKActionEnabled(valid);
    }

    private void shareYourCode() {
        CompletableFuture<String> future = CodeSharingUtil.performSharing(project,codePublishPanelComponent);
        future.thenAccept(result -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                // Perform the logic
                if (result.equalsIgnoreCase("Success")) {
                    CommonUIUtil.notifyInfo(project, "Codebase hosted successfully");
                }
            });
        }).exceptionally(ex -> {
            log.error("Exception while sharing the code in Hosted Git: {}", ex);
            ApplicationManager.getApplication().invokeLater(() ->
                    Messages.showErrorDialog("Exception while hosting the codebase: " + ex.getMessage(), "Gitifier"));
            return null;
        });
    }

//    private CompletableFuture<String> performSharing(Project project) {
//        CompletableFuture<String> future = new CompletableFuture<>();
//        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Code Sharing", true) {
//            @Override
//            public void run(@NotNull ProgressIndicator indicator) {
//                try {
//                    indicator.setIndeterminate(true);
//                    indicator.setText("Generating files ...");
//                    String repoBasePath = project.getBasePath();
//                    File reposBaseDir = new File(repoBasePath);
//                    String projectName = project.getName();
//                    String briefRepoDesc = codePublishPanelComponent.getTextArea().getText();
//                    GeneratorUtil.createGitIgnoreFile(reposBaseDir);
//                    GeneratorUtil.createReadMeMdFile(reposBaseDir, projectName, briefRepoDesc);
//
//                    indicator.setText("Sharing code ...");
//                    shareCode();
//
//                    future.complete("Success");
//                } catch (Exception ex) {
//                    future.completeExceptionally(ex);
//                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
//                }
//                log.debug("\n************** END - TRACKING DATA FOR ANALYSIS **************\n");
//            }
//
//            @Override
//            public void onCancel() {
//                future.completeExceptionally(new CancellationException("Task cancelled"));
//            }
//        });
//
//        return future;
//    }
//
//    private void shareCode() throws Exception {
//        String selectedGitType = (String) codePublishPanelComponent.getHostedGitTypeCombo().getSelectedItem();
//        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
//                codePublishPanelComponent.getSlGitUserNameCombo());
//        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
//        String repoName = project.getName();
//        String repoBaseDirPath = project.getBasePath();
//        boolean repoExistFlag = gitHandler.repoExists(repoName);
//        boolean gitDirAvlFlag = GitUtil.gitDirExists(repoBaseDirPath);
//        String briefRepoDesc = codePublishPanelComponent.getTextArea().getText();
//        String branchName = gitHandler.getGitType().equalsIgnoreCase("bitbucket") ? "main" : "master";
//        if (!repoExistFlag && !gitDirAvlFlag) {
//            // It is a brand new repository to be hosted
//            log.debug("Brand new repository to be created ........");
//            GitUtil.createOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc, branchName);
//        } else if (repoExistFlag && gitDirAvlFlag) {
//            log.debug("Repo already exists and .dit dir already available........");
//            // Repo is available to a registed user and .git dir available
//            // update to the registered user
//            GitUtil.updateOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc);
//        } else if (repoExistFlag && !gitDirAvlFlag) {
//            log.error("Repo already exists and .git dir not available........");
//            // throw the exception for non-registered user.
//            throw new RepoExistException("A repository with the same name already exists, please clone and push the changes.");
//        } else if (!repoExistFlag && gitDirAvlFlag) {
//            // Repo is not available to a user, but .git dir available
//            // Read the user information and push code
//            log.debug("Repo does not exist and .git dir available........");
//            GitUtil.createOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc, branchName);
//        }
//    }
}
