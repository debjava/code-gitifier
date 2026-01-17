package com.ddlab.rnd.ui.dialog;

import com.ddlab.rnd.exception.RepoExistException;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.handler.HostedGitType;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.setting.PublisherSetting;
import com.ddlab.rnd.ui.CodePublishPanelComponent;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.ddlab.rnd.ui.util.UIUtil;
import com.ddlab.rnd.util.GeneratorUtil;
import com.ddlab.rnd.util.GitUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
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
        setTitle("Share codebase ");
        setOKActionEnabled(false);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = createUIAndGetPanel();
        showMessage();
        attachValidationListener();
        return panel;
    }

    @Override
    protected void doOKAction() {
        saveLastSession();
//        getGitInfo();
        close(1);
//        new RepoExecutor(this).createRepo();
//        validate(project);

        shareYourCode();
    }



    // ~~~~~~~~ private methods ~~~~~~~~

    private void showMessage() {
        PublisherSetting setting = PublisherSetting.getInstance();
        Map<String,String> gitSettingMap = setting.getGitInfoTableMap();
        Map<String, String> tableMap = setting.getGitInfoTableMap();
//        log.debug("Git setting map: {}", gitSettingMap);
        if(gitSettingMap.isEmpty()) {
            CommonUIUtil.showError(project,"Fill up the information...");
        }
    }

    private void shareYourCode() {
        CompletableFuture<String> future = perform(project);
        future.thenAccept(result -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                // Perform the logic
                if(result.equalsIgnoreCase("Success")) {
//                    CommonUIUtil.showAppSuccessfulMessage("Codebase hosted successfully");
                    CommonUIUtil.info(project, "Codebase hosted successfully");
                }
            });
        }).exceptionally(ex -> {
            log.error("Exception while sharing the code in Hosted Git: {}", ex);
            ApplicationManager.getApplication().invokeLater(() ->
                    Messages.showErrorDialog("Exception while hosting the codebase: " + ex.getMessage(), "Gitifier"));
            return null;
        });
    }

    private void shareCode() throws Exception {
        String selectedGitType = (String) codePublishPanelComponent.getHostedGitTypeCombo().getSelectedItem();
        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
                codePublishPanelComponent.getSlGitUserNameCombo());
        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
        String repoName = project.getName();
        String repoBaseDirPath = project.getBasePath();
//        try {
            boolean repoExistFlag = gitHandler.repoExists(repoName);
            boolean gitDirAvlFlag = GitUtil.gitDirExists(repoBaseDirPath);
            String briefRepoDesc = codePublishPanelComponent.getTextArea().getText();
            String branchName = gitHandler.getGitType().equalsIgnoreCase("bitbucket") ? "main" : "master";
            if(!repoExistFlag && !gitDirAvlFlag) {
                // It is a brand new repository to be hosted
                log.debug("Brand new repository to be created ........");
                GitUtil.createOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc, branchName);
            } else if(repoExistFlag && gitDirAvlFlag){
                log.debug("Repo already exists and .dit dir already available........");
                // Repo is available to a registed user and .git dir available
                // update to the registered user
                GitUtil.updateOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc);
            } else if(repoExistFlag && !gitDirAvlFlag) {
                log.error("Repo already exists and .git dir not available........");
                // throw the exception for non-registered user.
                throw new RepoExistException("A repository with the same name already exists, please clone and push the changes.");
            } else if(!repoExistFlag && gitDirAvlFlag) {
                // Repo is not available to a user, but .git dir available
                // Read the user information and push code
                log.debug("Repo does not exist and .git dir available........");
                GitUtil.createOnlineRepo(repoBaseDirPath, gitHandler, briefRepoDesc, branchName);
            }
//        }
//        catch(Exception ex) {
//            log.error("Error while sharing code: \n{}", ex);
//            throw ex;
//        }
    }

    private void attachValidationListener() {
        JTextArea  textArea = codePublishPanelComponent.getTextArea();
        textArea.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                validateInput();
            }
        });
    }

    private void validateInput() {
//        boolean valid = !codePublishPanelComponent.getTextArea().getText().trim().isEmpty();

        JComboBox gitTypeCombo = codePublishPanelComponent.getHostedGitTypeCombo();
        JComboBox slGitUserNameCombo = codePublishPanelComponent.getSlGitUserNameCombo();

        boolean valid = !codePublishPanelComponent.getTextArea().getText().trim().isEmpty()
                && gitTypeCombo.getSelectedItem() != null
                && slGitUserNameCombo.getSelectedItem() != null;

        setOKActionEnabled(valid);
    }

//    private void validate(Project project) {
//        String selectedGitType = (String) codePublishPanelComponent.getHostedGitTypeCombo().getSelectedItem();
////        log.debug("selectedGitType: {}", selectedGitType);
////        String selectedGitUserName = (String) codePublishPanelComponent.getSlGitUserNameCombo().getSelectedItem();
////        log.debug("selectedGitUserName: {}", selectedGitUserName);
////        String gitNUser = selectedGitType+"~"+selectedGitUserName;
//        PublisherSetting setting = PublisherSetting.getInstance();
////        Map<String, String> tableInfoMap = setting.getGitInfoTableMap();
////        String gitToken = tableInfoMap.get(gitNUser);
////        UserAccount userAccount = new UserAccount(selectedGitUserName, gitToken);
////        log.debug("userAccount: {}", userAccount);
//
//        String repoName = project.getName();
//        log.debug("repoName: {}", repoName);
//
//        String repoBasePath = project.getBasePath();
//        log.debug("projectBasePath: {}", repoBasePath);
//
//        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
//                codePublishPanelComponent.getSlGitUserNameCombo());
//        log.debug("userAccount: {}", userAccount);
////
//        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
//        System.out.println("Git Handler : " + gitHandler);
//
//        try {
//            boolean checkFlag = gitHandler.repoExists(repoName);
//            log.debug("checkFlag: {}", checkFlag);
//            if(checkFlag) {
//                // simply update the project to the registed user
//                // Check whether repository has valid user name and token registered to the plugin
//
//                //		if(GitUtil.gitDirExists(projDirPath)) {
////			Map<String,String> userAndUrlMap = GitUtil.getUserAndCloneUrlMap(projDirPath);
////			System.out.println("User Url Map: "+userAndUrlMap);
////		}
//
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//
////        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
////
////        boolean checkFlag = gitHandler.repoExists(repositoryName);
////        System.out.println("Check Flag: " + checkFlag);
//
//
//        // Done
////        UserAccount userAccount = UIUtil.getSelectedUserAccount(codePublishPanelComponent.getHostedGitTypeCombo(),
////                codePublishPanelComponent.getSlGitUserNameCombo());
////        log.debug("userAccount: {}", userAccount);
////
////        IGitHandler gitHandler = HostedGitType.fromString(selectedGitType).getGitHandler(userAccount);
////        System.out.println("Git Handler : " + gitHandler);
////
////        String gitUserName = null;
////        try {
////            gitUserName = gitHandler.getUserName();
////            log.debug("User Name: {}", gitUserName);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
//
//
//
////        CompletableFuture<String> future = perform(project);
////        future.thenAccept(result -> {
////            ApplicationManager.getApplication().invokeLater(() -> {
////                // Perform the logic
////            });
////        }).exceptionally(ex -> {
////            log.error("Exception while getting the list of repos: {}", ex);
////            ApplicationManager.getApplication().invokeLater(() ->
////                    Messages.showErrorDialog("Exception while getting the list of repos: "+ex,"Publisher"));
////            return null;
////        });
//    }

    private CompletableFuture<String> perform(Project project) {
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
                    GeneratorUtil.createReadMeMdFile(reposBaseDir,projectName,briefRepoDesc);

                    indicator.setText("Sharing code ...");
                    shareCode();

                    future.complete("Success");
                }
                catch (Exception ex) {
                    future.completeExceptionally(ex);
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
//            log.debug("project file path: {}", projectFilePath);
            repository = new FileRepositoryBuilder()
                    .setGitDir(new File(projectFilePath+"/"+".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
            Git git = new Git(repository);
            Repository repo = git.getRepository();
            StoredConfig config = repository.getConfig();
            String gitUrl = config.getString("remote", "origin", "url");
//            log.debug("Git URL: " + gitUrl);
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
