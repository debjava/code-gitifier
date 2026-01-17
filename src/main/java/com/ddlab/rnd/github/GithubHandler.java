package com.ddlab.rnd.github;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

//import com.ddlab.rnd.constants.CommonConstants;
import com.ddlab.rnd.util.ConfigReaderUtil;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.ddlab.rnd.exception.BadCredentialsException;
import com.ddlab.rnd.git.model.GitOnlineErrorResponse;
import com.ddlab.rnd.git.model.GitOnlineResponse;
import com.ddlab.rnd.git.model.Repo;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.github.model.GitHubRepo;
import com.ddlab.rnd.github.model.HostedRepo;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.util.HttpUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Data
@ToString
@AllArgsConstructor
public class GithubHandler implements IGitHandler {

//	private static final String GITHUB_REPO_API = "https://api.github.com/user/repos";
//	private static final String GITHUB_USER_API = "https://api.github.com/user";
//	private static final String GITHUB_REPO_CHECK_API = "https://api.github.com/repos/{0}/{1}";

	private UserAccount userAccount;
	
	@Override
	public String getGitType() {
		return "Github";
	}

	@Override
	public String getUrlToClone(String repositoryName) throws Exception {
		String urlToClone = null;
		String gitUserName = getUserName();
		String uri = ConfigReaderUtil.getMessage("github.repo.check.api");
		MessageFormat formatter = new MessageFormat(uri);
//		String loginUser = userAccount.getUserName();
		uri = formatter.format(new String[] { gitUserName, repositoryName});
		HttpGet httpGet = new HttpGet(uri);
		String encodedUser = HttpUtil.getEncodedUser(userAccount.getUserName(), userAccount.getToken());
		httpGet.setHeader("Authorization", "Basic " + encodedUser);

		GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
		GitHubRepo gitHubRepo = getNewlyCreatedHostedRepo(gitResponse.getResponseText());
		urlToClone = gitHubRepo.getCloneUrl();
//		log.debug("Clone URL: {}" , urlToClone);

		return urlToClone;
	}

	@Override
	public String getCloneUrlAfterRepoCreation(String repoName, String repoDescription) throws Exception {
		String cloneUrl = null;
		GitHubRepo gitRepo = null;
		String jsonRepo = new HostedRepo(repoName, repoDescription).toJson();
//		String uri = "https://api.github.com/user/repos";
//		String uri = GIT_API_URI + REPO_API;
		String githubRepoApi = ConfigReaderUtil.getMessage("github.repo.api");
		HttpPost httpPost = new HttpPost(githubRepoApi);
		String encodedUser = HttpUtil.getEncodedUser(userAccount.getUserName(), userAccount.getToken());
		httpPost.setHeader("Authorization", "Basic " + encodedUser);
		StringEntity jsonBodyRequest = new StringEntity(jsonRepo);
		httpPost.setEntity(jsonBodyRequest);
		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpPost);
			if (gitResponse.getStatusCode() == 401) // it should be not 201 or 200
				throw new RuntimeException("Unable to create a repo...");
			gitRepo = getNewlyCreatedHostedRepo(gitResponse.getResponseText());
			cloneUrl = gitRepo.getCloneUrl();
//			log.debug("Git Repo Clone/Remote: " + cloneUrl);

		} catch (RuntimeException e) {
			throw e;
		}
		return cloneUrl;
	}

	@Override
	public String[] getAllRepositories() throws Exception {
		String[] repoNames = new String[0];
		List<String> repoList = new ArrayList<String>();
		GitHubRepo gitRepo = null;
//		String uri = "https://api.github.com/user/repos";
		String githubRepoApi = ConfigReaderUtil.getMessage("github.repo.api");
		HttpGet httpGet = new HttpGet(githubRepoApi);
		String githubUserName = getUserName();
//		log.debug("Github user name: {}", githubUserName);
//		String encodedUser = HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
		String encodedUser = HttpUtil.getEncodedUser(githubUserName, userAccount.getToken());
//		log.debug("Github encoded user: {}", encodedUser);
		httpGet.setHeader("Authorization", "Basic " + encodedUser);
		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
//			log.debug("Git Response: {}", gitResponse);
			gitRepo = getAllGitHubRepos(gitResponse);
			Repo[] repos = gitRepo.getRepos();
			for (Repo repo : repos)
				repoList.add(repo.getName());
			repoNames = repoList.toArray(new String[0]);
		}
		catch (Exception e) {
			throw e;
		}
		
		return repoNames;
	}

	@Override
	public String getUserName() throws Exception {
		GitHubRepo gitRepo = null;
//		String uri = "https://api.github.com/user";
		String githubUserApi = ConfigReaderUtil.getMessage("github.user.api");
		HttpGet httpGet = new HttpGet(githubUserApi);
		String encodedUser = HttpUtil.getEncodedUser(userAccount.getUserName(), userAccount.getToken());
//		log.debug("Github user api encoded user: {}", encodedUser);
		httpGet.setHeader("Authorization", "Basic " + encodedUser);
//		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
//			log.debug("Response: "+gitResponse);
			if (gitResponse.getStatusCode() == 400 || gitResponse.getStatusCode() == 401) {
//				log.debug("----------- Coming inside ----------------");
				GitOnlineErrorResponse errResponse = getError(gitResponse.getResponseText());
//				log.debug("Error Response: {}", errResponse);
//				throw new RuntimeException("Exception while getting user information - Error Code: "
//						+ errResponse.getStatus() + " , Error Message: " + errResponse.getMessage());
				throw new BadCredentialsException("Error code: "+errResponse.getStatus()+" - "+errResponse.getMessage());
			}
			gitRepo = getUser(gitResponse.getResponseText());
//		} 
//		catch (RuntimeException e) {
////			throw e;
//			throw new BadCredentialsException("Bad Credentials - " + e.getMessage());
//		}
//		log.debug("gitRepo.getLoginUser()---->{}", gitRepo.getLoginUser());
		return gitRepo.getLoginUser();
	}

	@Override
	public boolean repoExists(String repoName) throws Exception {
		boolean existsFlag = false;
		String uri = ConfigReaderUtil.getMessage("github.repo.check.api");
//		String githubRepoCheckApi = ConfigReaderUtil.getMessage("github.repo.check.api");
		MessageFormat formatter = new MessageFormat(uri);
		String loginUser = getUserName();
		uri = formatter.format(new String[] { loginUser, repoName });
//		log.debug("Complete URI: {}", uri);
		HttpGet httpGet = new HttpGet(uri);
		String encodedUser = HttpUtil.getEncodedUser(userAccount.getUserName(), userAccount.getToken());
//		log.debug("Encoded user: {}", encodedUser);
		httpGet.setHeader("Authorization", "Basic " + encodedUser);
		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
			existsFlag = gitResponse.getStatusCode() == 200 ? true : false;
		} catch (RuntimeException e) {
			throw e;
		}
		return existsFlag;
	}

//	public String getCloneUrl(String repoName) throws Exception {
//		String urlToClone = null;
//
//		return urlToClone;
//	}

	// ~~~~~~~~~~~~~~~~~~~~~~~ Private methods ~~~~~~~~~~~~~~~~~~~~~~~

	private GitHubRepo getNewlyCreatedHostedRepo(String jsonResponse) throws RuntimeException {
		GitHubRepo gitRepo = new GitHubRepo();
		ObjectMapper mapper = new ObjectMapper();
		try {
			gitRepo = mapper.readValue(jsonResponse, GitHubRepo.class);
			if (gitRepo.getCloneUrl() == null) {
//				String errorMessage = errorParser.parseError(jsonResponse);
				throw new RuntimeException("Unable to create repo ...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return gitRepo;
	}

	private GitHubRepo getAllGitHubRepos(GitOnlineResponse gitResponse) throws Exception {
//		log.debug("Git Response: \n{}", gitResponse.getResponseText());
		GitHubRepo gitRepo = null;
		if (gitResponse.getStatusCode() == 200) {
			ObjectMapper mapper = new ObjectMapper();
			Repo[] repos = mapper.readValue(gitResponse.getResponseText(), Repo[].class);
			gitRepo = new GitHubRepo();
			gitRepo.setRepos(repos);

		}
		return gitRepo;
	}

	private GitHubRepo getUser(String jsonResponse) throws RuntimeException {
		GitHubRepo gitRepo = new GitHubRepo();
		ObjectMapper mapper = new ObjectMapper();
		try {
			gitRepo = mapper.readValue(jsonResponse, GitHubRepo.class);
		} catch (Exception e) {
			log.error("Exception while getting user infor: {}", e);
			e.printStackTrace();
			throw e;
		}
		return gitRepo;
	}

	private GitOnlineErrorResponse getError(String jsonResponse) {
		ObjectMapper mapper = new ObjectMapper();
		GitOnlineErrorResponse errResponse = mapper.readValue(jsonResponse, GitOnlineErrorResponse.class);
		return errResponse;
	}

}
