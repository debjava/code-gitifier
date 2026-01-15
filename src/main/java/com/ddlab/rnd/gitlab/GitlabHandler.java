package com.ddlab.rnd.gitlab;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

//import com.ddlab.rnd.constants.CommonConstants;
import com.ddlab.rnd.util.ConfigReaderUtil;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;

import com.ddlab.rnd.exception.BadCredentialsException;
import com.ddlab.rnd.git.model.GitOnlineErrorResponse;
import com.ddlab.rnd.git.model.GitOnlineResponse;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.gitlab.model.GitLabRepo;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.util.HttpUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Data
@ToString
@AllArgsConstructor
public class GitlabHandler implements IGitHandler {

//	public static final String GITLAB_REPO_EXIST_API = "https://gitlab.com/api/v4/projects?"
//			+ "private_token={0}&username={1}&owned=true&simple=true&per_page=10&search={2}";
//
//	public static final String GITLAB_REPO_CREATE_API = "https://gitlab.com/api/v4/projects?"
//			+ "private_token={0}&username={1}&name={2}&description={3}&visibility=public";
//
//	public static final String GITLAB_GET_REPOS_API = "https://gitlab.com/api/v4/projects?"
//			+ "private_token={0}&username={1}&owned=true&simple=true&per_page=10";
//	
//	private static final String GITLAB_USER_API = "https://gitlab.com/api/v4/user?public_email={0}&private_token={1}";

	private UserAccount userAccount;
	
	
	@Override
	public String getGitType() {
		return "Gitlab";
	}

	@Override
	public String getCloneUrlAfterRepoCreation(String repoName, String repoDescription) throws Exception {
		String cloneUrl = null;
		GitLabRepo gitRepo = null;
		repoDescription = URLEncoder.encode(repoDescription, "UTF-8");
		repoName = URLEncoder.encode(repoName, "UTF-8");
		String gitlabRepoCreateApi = ConfigReaderUtil.getMessage("gitlab.repo.create.api ");
		MessageFormat formatter = new MessageFormat(gitlabRepoCreateApi);
		String uri = formatter
				.format(new String[] { userAccount.getToken(), userAccount.getUserName(), repoName, repoDescription });
		try {
			HttpPost httpPost = new HttpPost(uri);
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpPost);
			log.debug("Git Response: {}", gitResponse);
			if (gitResponse.getStatusCode() == 200 || gitResponse.getStatusCode() == 201) {
				ObjectMapper mapper = new ObjectMapper();
				gitRepo = mapper.readValue(gitResponse.getResponseText(), GitLabRepo.class);
				log.debug("Git Repo: {}", gitRepo);
				cloneUrl = gitRepo.getCloneUrl();
			} else if (gitResponse.getStatusCode() == 400) {
				throw new RuntimeException("Project with the same name already exists");
			}
		} catch (Exception e) {
			throw e;
		}
		return cloneUrl;
	}

	@Override
	public String[] getAllRepositories() throws Exception {
		GitLabRepo[] gitRepos = null;
		String token = userAccount.getToken();
//		log.debug("Gitlab Token: " + token);
		String userName = getUserName();
		log.debug("Gitlab User Name: " + userName);
//		String userName = userAccount.getUserName();
//		log.debug("Gitlab User Name: " + userName);
		String gitlabGetRepoApi = ConfigReaderUtil.getMessage("gitlab.get.repos.api");
		MessageFormat formatter = new MessageFormat(gitlabGetRepoApi);
		String uri = formatter.format(new String[] { token, userName });
		log.debug("What is the Gitlab URI: {}", uri);
		HttpGet httpGet = new HttpGet(uri);
		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
			log.debug("Gitlab Response : " + gitResponse);
			gitRepos = getAllGitLabRepos(gitResponse);
		} catch (Exception e) {
			log.error("Exception while getting the list of repos: \n{}", e);
			throw e;
		}
		List<String> repoList = new ArrayList<String>();
		for (GitLabRepo repo : gitRepos)
			repoList.add(repo.getName());
		return repoList.toArray(new String[0]);
	}

	@Override
	public String getUserName() throws Exception {
//		log.debug("What is the user account: {}", userAccount);
		GitLabRepo gitRepo = null;
//		String gitlabUserSearchApi = "https://gitlab.com/api/v4/user?public_email={0}&private_token={1}";
		String gitlabUserApi = ConfigReaderUtil.getMessage("gitlab.user.api");
		MessageFormat formatter = new MessageFormat(gitlabUserApi);
		String uri = formatter.format(new String[] { userAccount.getUserName(), userAccount.getToken() });
		HttpGet httpGet = new HttpGet(uri);
//		try {
		
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
//			log.debug("Git response: {}", gitResponse);
			if(gitResponse.getStatusCode() != 200) {
				GitOnlineErrorResponse errResponse = getError(gitResponse);
//				GitOnlineErrorResponse errResponse = getError(gitResponse.getResponseText());
//				log.debug("errResponse----->"+errResponse);
				throw new BadCredentialsException("Error code: "+errResponse.getStatus()+" - "+errResponse.getMessage());
			}
			gitRepo = getGitLabUser(gitResponse);
//		} 
//		catch (RuntimeException e) {
//			log.error("Unable to find the user in Gitlab: \n{}", e);
//			throw e;
//		}
		return gitRepo.getUserName();
	}

	@Override
	public boolean repoExists(String repoName) throws Exception {
		boolean existsFlag = false;
		String userName = userAccount.getUserName();
		String token = userAccount.getToken();
		String gitlabRepoExistApi = ConfigReaderUtil.getMessage("gitlab.repo.exist.api");
		MessageFormat formatter = new MessageFormat(gitlabRepoExistApi);
		String uri = formatter.format(new String[] { token, userName, repoName });
		HttpGet httpGet = new HttpGet(uri);
		GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
		log.debug("Git response: {}", gitResponse.getResponseText());
		if (gitResponse.getStatusCode() == 200) {
			ObjectMapper objectMapper = new ObjectMapper();
			GitLabRepo[] gitLabRepo = objectMapper.readValue(gitResponse.getResponseText(), GitLabRepo[].class);
//			log.debug("GitLab repo: {}", gitLabRepo);
			existsFlag = gitLabRepo.length != 0 && gitLabRepo[0].getName().equals(repoName);
		}

		return existsFlag;
	}

	// ~~~~~~~~~~~~~~~~~~~ Private Methods ~~~~~~~~~~~~~~~~~~~

	private GitLabRepo getGitLabUser(GitOnlineResponse gitResponse) throws RuntimeException {
		GitLabRepo gitRepo = null;
		if (gitResponse.getStatusCode() == 400 || gitResponse.getStatusCode() == 401) {
			GitOnlineErrorResponse errResponse = getError(gitResponse.getResponseText());
			throw new RuntimeException("Exception while getting user information - Error Code: "
					+ errResponse.getStatus() + " , Error Message: " + errResponse.getMessage());
		}
		gitRepo = getUser(gitResponse.getResponseText());

		return gitRepo;
	}
	
	private GitOnlineErrorResponse getError(GitOnlineResponse gitResponse) {
		ObjectMapper mapper = new ObjectMapper();
		GitOnlineErrorResponse errResponse = mapper.readValue(gitResponse.getResponseText(), GitOnlineErrorResponse.class);
		errResponse.setStatus(String.valueOf(gitResponse.getStatusCode()));
		return errResponse;
	}

	@Deprecated
	private GitOnlineErrorResponse getError(String jsonResponse) {
		ObjectMapper mapper = new ObjectMapper();
		GitOnlineErrorResponse errResponse = mapper.readValue(jsonResponse, GitOnlineErrorResponse.class);
		return errResponse;
	}

	private GitLabRepo getUser(String jsonResponse) throws RuntimeException {
		GitLabRepo gitRepo = new GitLabRepo();
		ObjectMapper mapper = new ObjectMapper();
		try {
			gitRepo = mapper.readValue(jsonResponse, GitLabRepo.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return gitRepo;
	}

	private GitLabRepo[] getAllGitLabRepos(GitOnlineResponse gitResponse) throws Exception {
		GitLabRepo[] gitRepos = null;
		if (gitResponse.getStatusCode() == 200) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				gitRepos = mapper.readValue(gitResponse.getResponseText(), GitLabRepo[].class);
			} catch (Exception e) {
				throw e;
			}
		}
		return gitRepos;
	}

}
