package com.ddlab.rnd.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.handler.IGitHandler;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GitUtil {

    public static void createOnlineRepo(String projDirPath, IGitHandler gitHandler, String repoDescription, String branchName) throws Exception {
        try {
            UserAccount userAct = gitHandler.getUserAccount();
//            String userEmail = userAct.getUserName();
//            String userToken = userAct.getToken();
//            System.out.println("User Email: " + userEmail);
//            System.out.println("User Token: " + userToken);

            File projDirFile = new File(projDirPath);

            String repoName = projDirFile.getName();
            System.out.println("Repo Name: " + repoName);

//			// Create a Hosted Repository
            String remoteCloneUrl = gitHandler.getCloneUrlAfterRepoCreation(repoName, repoDescription);

            boolean repoExistFlag = GitUtil.gitDirExists(projDirPath);

            // throw exception here repo already exists

//			Map<String,String> userAndUrlMap = GitUtil.getUserAndCloneUrlMap(projDirPath);
            if (!repoExistFlag) {
                GitUtil.initializeRepo(projDirFile);
            }
            // Initialize repo
//			if(GitUtil.gitDirExists(projDirPath)) {
//				GitUtil.initializeRepo(projDirFile);
//				Map<String,String> userAndUrlMap = GitUtil.getUserAndCloneUrlMap(projDirPath);
//			}


            String gitUserName = gitHandler.getUserName();
            System.out.println("User Name: " + gitUserName);

            String gitTypeUserName = gitHandler.getGitType() + "_" + gitUserName;
            System.out.println("gitTypeUserName: " + gitTypeUserName);

            // Add Multi remote origin
            GitUtil.addMultiRemoteOrigin(projDirPath, gitTypeUserName, remoteCloneUrl);
//
//			// Add all files
            GitUtil.addAllFiles(projDirPath);

//			// Commit
            PersonIdent author = new PersonIdent(gitUserName, userAct.getUserName());
            GitUtil.commit(projDirPath, author, "first commit");

            // Add a branch details
//			GitUtil.addBranch(projDirPath);

            GitUtil.addBranch(projDirPath, branchName);

            // Checkout main branch safely
            GitUtil.checkoutRepo(projDirPath);

            // Push
            UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(gitUserName,
					userAct.getToken());
            GitUtil.multiPushRepo(projDirPath, gitTypeUserName, credentialsProvider);

            //Finally push the code
//			GitUtil.pushRepo(projDirPath, credentialsProvider);
//			GitUtil.multiPushRepo(projDirPath, gitTypeUserName, credentialsProvider);


        } catch (Exception ex) {
            log.error("Exception while sharing the code in online repository: {\n}", ex);
			throw ex;
        }
    }

    public static void createNewRepo() {

        // First check wheather a repo with similar name already exists

        // create a repo in Github, get the response with remote url.
//		GithubUtil.createHostedRepo("spring-sample1", repoDesc, userAccount);
//		System.out.println("Github repo creation ............");

        // Initialize repo
//		GitUtil.initializeRepo(new File(projectDirPath));
//		// Add remote origin
//		GitUtil.addRemoteOrigin(projectDirPath, remoteUrl);
//		// Add all files
//		GitUtil.addAllFiles(projectDirPath);
//		// Commit
//		PersonIdent author = new PersonIdent(userName, userEmail);
//		GitUtil.commit(projectDirPath, author, "first commit");
//		// Checkout main branch safely
//		GitUtil.checkoutRepo(projectDirPath);
//		// Push
//		UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(userEmail,
//				token);
//		GitUtil.pushRepo(projectDirPath, credentialsProvider);

    }

    public static void initializeRepo(File dir) throws GitAPIException {
        Git.init().setDirectory(dir).call();
        System.out.println("Repository initialized!");
    }

    public static void addRemoteOrigin(String repoDir, String remoteUrl) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(repoDir, ".git")).readEnvironment().findGitDir().build();
        StoredConfig config = repository.getConfig();
        config.setBoolean("http", null, "sslVerify", false);

        config.setString("remote", "origin", "url", remoteUrl);
        config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.save();
        repository.close();
        System.out.println("Remote origin added!");
    }

    public static void addMultiRemoteOrigin(String repoDir, String gitTypeUserName, String remoteUrl)
            throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(repoDir, ".git")).readEnvironment().findGitDir().build();
        StoredConfig config = repository.getConfig();
        config.setBoolean("http", null, "sslVerify", false);

        config.setString("remote", "origin", "url", remoteUrl);

        config.setString("remote", gitTypeUserName, "url", remoteUrl);
        config.setString("remote", gitTypeUserName, "fetch", "+refs/heads/*:refs/remotes/github/*");

//		config.setString("remote", "origin", "url", remoteUrl);
//		config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");

        config.save();
        repository.close();
        System.out.println("Remote origin added!");
    }

    public static void addAllFiles(String repoDir) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(repoDir))) {
            git.add().addFilepattern(".").call();
            System.out.println("All files staged!");
        }
    }

    public static void commit(String repoDir, PersonIdent author, String commitMessage)
            throws IOException, GitAPIException {
        try (Git git = Git.open(new File(repoDir))) {
            git.commit().setMessage(commitMessage).setAuthor(author).setCommitter(author).call();
            System.out.println("Committed successfully!");
        }
    }

    public static void checkoutRepo(String repoDir) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(repoDir))) {
            boolean mainExists = git.branchList().call().stream()
                    .anyMatch(ref -> ref.getName().equals("refs/heads/master"));

            if (mainExists) {
                git.checkout().setName("master").call();
            } else {
                git.checkout().setCreateBranch(true).setName("master")
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).call();
            }
            System.out.println("Checked out 'main' branch!");
        }
    }

    public static void pushRepo(String repoDir, UsernamePasswordCredentialsProvider credentialsProvider)
            throws IOException, GitAPIException {
        try (Git git = Git.open(new File(repoDir))) {
            git.push().setRemote("origin").add("main") // push local main branch
                    .setCredentialsProvider(credentialsProvider).call();
            System.out.println("Pushed successfully!");
        }
    }

    public static void multiPushRepo(String repoDir, String gitTypeUserName,
                                     UsernamePasswordCredentialsProvider credentialsProvider) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(repoDir))) {

            PushCommand pushCommand = git.push();
            pushCommand.setRemote(gitTypeUserName);// remote name
//            pushCommand.add("refs/heads/main");
            pushCommand.add("main");

            pushCommand.setCredentialsProvider(credentialsProvider);

            pushCommand.call();

//			git.push().setRemote("origin").add("main") // push local main branch
//					.setCredentialsProvider(credentialsProvider).call();
            System.out.println("Pushed successfully!");
        }
    }

    @Deprecated
    public static void readGitConfig(String projDirPath) throws Exception {
        Repository repository = null;
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            repository = builder.setGitDir(new File(projDirPath + File.separator + ".git")).readEnvironment()
                    .findGitDir().build();
            StoredConfig config = repository.getConfig();

            // ---- User info ----
            String userName = config.getString("user", null, "name");
            String userEmail = config.getString("user", null, "email");

            System.out.println("User Name : " + userName);
            System.out.println("User Email: " + userEmail);

            // ---- Remote URLs ----
            Set<String> remotes = config.getSubsections("remote");
            for (String remote : remotes) {
                String url = config.getString("remote", remote, "url");
                System.out.println("Remote [" + remote + "] URL: " + url);
            }

        } catch (Exception ex) {
            log.error("Unable to read .git directory ...{}", ex);

        } finally {
            if (repository != null)
                repository.close();
        }
//        repository.close();
    }

    public static Map<String, String> getUserAndCloneUrlMap(String projDirPath) {
        Map<String, String> userCloneUrlMap = new HashMap<>();
        Repository repository = null;
        try {
            File gitDir = new FileRepositoryBuilder().findGitDir(new File(projDirPath)).getGitDir();
            if (gitDir != null) {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                repository = builder.setGitDir(new File(projDirPath + File.separator + ".git")).readEnvironment()
                        .findGitDir().build();

                StoredConfig config = repository.getConfig();

                Set<String> remotes = config.getSubsections("remote");
                for (String remote : remotes) {
                    String url = config.getString("remote", remote, "url");
//					System.out.println("Remote [" + remote + "] URL: " + url);

                    userCloneUrlMap.put(remote, url);
                }
            }

        } catch (Exception ex) {
            log.error("Unable to read .git directory ...{}", ex);

        } finally {
            if (repository != null)
                repository.close();
        }
        return userCloneUrlMap;
    }

    public static boolean gitDirExists(String projDirPath) {
        File gitDir = new FileRepositoryBuilder().findGitDir(new File(projDirPath)).getGitDir();
        return gitDir != null;
    }

    @Deprecated
    public static void addBranch(String projDirPath) throws IOException {
        Repository repository = new FileRepositoryBuilder()
                .findGitDir(new File(projDirPath + File.separator + ".git"))
                .build();

        StoredConfig config = repository.getConfig();

        // branch.master.remote = origin
        config.setString("branch", "master", "remote", "origin");

        // branch.master.merge = refs/heads/master
        config.setString("branch", "master", "merge", "refs/heads/master");

        // 🔴 IMPORTANT: persist changes
        config.save();

        repository.close();
    }

    public static void addBranch(String projDirPath, String branchName) throws IOException {
        Repository repository = new FileRepositoryBuilder()
                .findGitDir(new File(projDirPath + File.separator + ".git"))
                .build();

        StoredConfig config = repository.getConfig();

        // branch.master.remote = origin
        config.setString("branch", branchName, "remote", "origin");

        // branch.master.merge = refs/heads/master
        config.setString("branch", branchName, "merge", "refs/heads/" + branchName);

        config.save();

        repository.close();
    }
}
