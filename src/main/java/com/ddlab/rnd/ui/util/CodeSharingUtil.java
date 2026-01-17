package com.ddlab.rnd.ui.util;

import com.ddlab.rnd.exception.RepoExistException;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.handler.HostedGitType;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.ui.CodePublishPanelComponent;
import com.ddlab.rnd.util.GeneratorUtil;
import com.ddlab.rnd.util.GitUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class CodeSharingUtil {

    public static CompletableFuture<String> performSharing(Project project, CodePublishPanelComponent codePublishPanelComponent) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Code Sharing", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText("Generating files ...");
                    String repoBasePath = project.getBasePath();
                    File reposBaseDir = new File(repoBasePath);
                    String projectName = project.getName();
                    String briefRepoDesc = codePublishPanelComponent.getTextArea().getText();
                    GeneratorUtil.createGitIgnoreFile(reposBaseDir);
                    GeneratorUtil.createReadMeMdFile(reposBaseDir, projectName, briefRepoDesc);

                    indicator.setText("Sharing code ...");
                    shareCode(project,codePublishPanelComponent);

                    future.complete("Success");
                } catch (Exception ex) {
                    future.completeExceptionally(ex);
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                }
                log.debug("\n************** END - TRACKING DATA FOR ANALYSIS **************\n");
            }

            @Override
            public void onCancel() {
                future.completeExceptionally(new CancellationException("Task cancelled"));
            }
        });

        return future;
    }

    private static void shareCode(Project project, CodePublishPanelComponent codePublishPanelComponent) throws Exception {
        String selectedGitType = (String) codePublishPanelComponent.getHostedGitTypeCombo().getSelectedItem();
        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
                codePublishPanelComponent.getSlGitUserNameCombo());
        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
        String repoName = project.getName();
        String repoBaseDirPath = project.getBasePath();
        boolean repoExistFlag = gitHandler.repoExists(repoName);
        boolean gitDirAvlFlag = GitUtil.gitDirExists(repoBaseDirPath);
        String briefRepoDesc = codePublishPanelComponent.getTextArea().getText();
        String branchName = gitHandler.getGitType().equalsIgnoreCase("bitbucket") ? "main" : "master";
        if (!repoExistFlag && !gitDirAvlFlag) {
            // It is a brand new repository to be hosted
            log.debug("Brand new repository to be created ........");
            GitUtil.createOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc, branchName);
        } else if (repoExistFlag && gitDirAvlFlag) {
            log.debug("Repo already exists and .dit dir already available........");
            // Repo is available to a registed user and .git dir available
            // update to the registered user
            GitUtil.updateOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc);
        } else if (repoExistFlag && !gitDirAvlFlag) {
            log.error("Repo already exists and .git dir not available........");
            // throw the exception for non-registered user.
            throw new RepoExistException("A repository with the same name already exists, please clone and push the changes.");
        } else if (!repoExistFlag && gitDirAvlFlag) {
            // Repo is not available to a user, but .git dir available
            // Read the user information and push code
            log.debug("Repo does not exist and .git dir available........");
            GitUtil.createOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc, branchName);
        }
    }
}
