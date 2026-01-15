package com.ddlab.rnd.handler;

import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.github.GithubHandler;
import com.ddlab.rnd.gitlab.GitlabHandler;
import com.ddlab.rnd.handler.bitbucket.BitbucketHandler;

public enum HostedGitType {

	GITHUB("Github") {
		public IGitHandler getGitHandler(UserAccount userAccount) {
			return new GithubHandler(userAccount);
		}
	},

	GITLAB("Gitlab") {
		public IGitHandler getGitHandler(UserAccount userAccount) {
			return new GitlabHandler(userAccount);
		}
	},
	
	BITBUCKET("Bitbucket") {
		public IGitHandler getGitHandler(UserAccount userAccount) {
			return new BitbucketHandler(userAccount);
		}
	};

	private String gitType;

	private HostedGitType(String gitType) {
		this.gitType = gitType;
	}

	public String getGitType() {
		return gitType;
	}

	public static HostedGitType fromString(String text) {
		for (HostedGitType type : HostedGitType.values()) {
			if (text.equalsIgnoreCase(type.gitType))
				return type;
		}
		return null;
	}

	public abstract IGitHandler getGitHandler(UserAccount userAccount);

}
