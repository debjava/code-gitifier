package com.ddlab.rnd.handler;

import com.ddlab.rnd.git.model.UserAccount;

public interface IGitHandler {
	
	String getCloneUrlAfterRepoCreation(String repoName, String repoDescription) throws Exception;
	
	String[] getAllRepositories() throws Exception;
	
	String getUserName() throws Exception;
	
	boolean repoExists(String repoName) throws Exception;
	
	String getGitType();
	
	UserAccount getUserAccount();

}
