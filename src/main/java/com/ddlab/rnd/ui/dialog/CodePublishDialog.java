package com.ddlab.rnd.ui.dialog;

import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.handler.HostedGitType;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.setting.PublisherSetting;
import com.ddlab.rnd.ui.CodePublishPanelComponent;
import com.ddlab.rnd.ui.util.UIUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CodePublishDialog extends DialogWrapper {

    private JPanel panel;
    private CodePublishPanelComponent  codePublishPanelComponent;
    private Project project;

    public CodePublishDialog(@Nullable Project project, File selectedRepo, boolean canBeParent) {
        super(project, canBeParent);
        this.project = project;
        setTitle("Temporary Title ...");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = createUIAndGetPanel();
        return panel;
    }

    @Override
    protected void doOKAction() {
        saveLastSession();
//        getGitInfo();
        close(1);
//        new RepoExecutor(this).createRepo();
        validate(project);
    }



    // ~~~~~~~~ private methods ~~~~~~~~

    private void validate(Project project) {
        String selectedGitType = (String) codePublishPanelComponent.getHostedGitTypeCombo().getSelectedItem();
//        log.debug("selectedGitType: {}", selectedGitType);
//        String selectedGitUserName = (String) codePublishPanelComponent.getSlGitUserNameCombo().getSelectedItem();
//        log.debug("selectedGitUserName: {}", selectedGitUserName);
//        String gitNUser = selectedGitType+"~"+selectedGitUserName;
        PublisherSetting setting = PublisherSetting.getInstance();
//        Map<String, String> tableInfoMap = setting.getGitInfoTableMap();
//        String gitToken = tableInfoMap.get(gitNUser);
//        UserAccount userAccount = new UserAccount(selectedGitUserName, gitToken);
//        log.debug("userAccount: {}", userAccount);

        String repoName = project.getName();
        log.debug("repoName: {}", repoName);

        String repoBasePath = project.getBasePath();
        log.debug("projectBasePath: {}", repoBasePath);

        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
                codePublishPanelComponent.getSlGitUserNameCombo());
        log.debug("userAccount: {}", userAccount);
//
        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
        System.out.println("Git Handler : " + gitHandler);

        try {
            boolean checkFlag = gitHandler.repoExists(repoName);
            log.debug("checkFlag: {}", checkFlag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


//        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
//
//        boolean checkFlag = gitHandler.repoExists(repositoryName);
//        System.out.println("Check Flag: " + checkFlag);


        // Done
//        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
//                codePublishPanelComponent.getSlGitUserNameCombo());
//        log.debug("userAccount: {}", userAccount);
//
//        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
//        System.out.println("Git Handler : " + gitHandler);
//
//        String gitUserName = null;
//        try {
//            gitUserName = gitHandler.getUserName();
//            log.debug("User Name: {}", gitUserName);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }



//        CompletableFuture<String> future = perform(project);
//        future.thenAccept(result -> {
//            ApplicationManager.getApplication().invokeLater(() -> {
//                // Perform the logic
//            });
//        }).exceptionally(ex -> {
//            log.error("Exception while getting the list of repos: {}", ex);
//            ApplicationManager.getApplication().invokeLater(() ->
//                    Messages.showErrorDialog("Exception while getting the list of repos: "+ex,"Publisher"));
//            return null;
//        });
    }

    private CompletableFuture<String> perform(Project project) {

        CompletableFuture<String> future = new CompletableFuture<>();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Some Titile", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText("Constants.SNYK_ISSUES_PROGRESS_MSG");
                    TimeUnit.SECONDS.sleep(10);
//                    String snykProjectIssuesJsonTxt = SnykApi.fetchSnykProjectIssuesAsJsonText(project, buildTypeName);
                    indicator.setText("Analyzing and Consolidating Issues ...");

//                    SnykProjectIssues allProjectIssue = getAIManipulatedSnykIssues(snykProjectIssuesJsonTxt);
//                    JTable table = SnykUiUtil.getUpdatedSnykIssueTable(allProjectIssue);
//                    indicator.setText("Finishing all ...");
                    TimeUnit.SECONDS.sleep(5);
                    future.complete("Completed");
                }
                catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
//                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
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

    @Deprecated
    private void getGitInfo() {
        Repository repository = null;
        try {
//            String projectFilePath = this.project.getProjectFilePath();
            String projectFilePath = this.project.getBasePath();
            log.debug("project file path: {}", projectFilePath);
            repository = new FileRepositoryBuilder()
                    .setGitDir(new File(projectFilePath+"/"+".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
            Git git = new Git(repository);
            Repository repo = git.getRepository();
            StoredConfig config = repository.getConfig();
            String gitUrl = config.getString("remote", "origin", "url");
            log.debug("Git URL: " + gitUrl);
//            String gitUrl = getPrimaryGitUrl(repo);
//            log.debug("Git URL: " + gitUrl);


        } catch (Exception e) {
            log.error("Exception while reading git info: {}", e);
        }



//        GitRepository gitRepo = GitUtil.getRepositoryManager(project).getRepositories().get(0);
//        String branch = gitRepo.getCurrentBranchName();
    }

    @Deprecated
    public static String getPrimaryGitUrl(Repository repository) {
        StoredConfig config = repository.getConfig();

        // try origin first
        String url = config.getString("remote", "origin", "url");
        if (url != null) {
            return url;
        }

        // fallback to first available remote
        for (String remoteName : config.getSubsections("remote")) {
            url = config.getString("remote", remoteName, "url");
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    private void saveLastSession() {
//        PublisherSetting setting = PublisherSetting.getInstance();
        JComboBox hostedGitTypeCombo = codePublishPanelComponent.getHostedGitTypeCombo();
        JComboBox slGitUserNameCombo = codePublishPanelComponent.getSlGitUserNameCombo();
        // Save the last session, save when window is closed, not here
//        setting.setLastSavedHostedGitTypeSelection(hostedGitTypeCombo.getSelectedItem().toString());
//        setting.setLastSavedGitUserNameSelection(slGitUserNameCombo.getSelectedItem().toString());

        UIUtil.saveLastSessionSetting(hostedGitTypeCombo,slGitUserNameCombo);

    }
    private JPanel createUIAndGetPanel() {
        codePublishPanelComponent = new CodePublishPanelComponent();
        return codePublishPanelComponent.getMainPanel();
    }

//    public GridBagLayout getPanelLayout() {
//        GridBagLayout gridBagLayout = new GridBagLayout();
//        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
//        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
//        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
//        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
//        return gridBagLayout;
//    }
}
