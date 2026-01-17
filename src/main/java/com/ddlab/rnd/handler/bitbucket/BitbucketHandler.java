package com.ddlab.rnd.handler.bitbucket;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

//import com.ddlab.rnd.constants.CommonConstants;
import com.ddlab.rnd.util.ConfigReaderUtil;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.ddlab.rnd.bitbucket.model.BitbucketOutputRepo;
import com.ddlab.rnd.bitbucket.model.BitbucketOutputRepo.BitbucketClone;
import com.ddlab.rnd.bitbucket.model.BitbucketRepoInput;
import com.ddlab.rnd.exception.BadCredentialsException;
import com.ddlab.rnd.git.model.GitOnlineErrorResponse;
import com.ddlab.rnd.git.model.GitOnlineResponse;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.util.HttpUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Data @ToString @AllArgsConstructor
public class BitbucketHandler implements IGitHandler {
	
//	public static final String BITBUCKET_USER_API_URI = "https://api.bitbucket.org/2.0/user";
//	private static final String BITBUCKET_CREATE_REPO_API = "https://api.bitbucket.org/2.0/repositories/{0}/{1}";
//	private static final String BITBUCKET_REPO_API = "https://api.bitbucket.org/2.0/repositories/{0}";
//	private static final String BITBUCKET_REPO_CHECK_API = "https://api.bitbucket.org/2.0/repositories/{0}/{1}";
	
	
	private UserAccount userAccount;
	
	
	@Override
	public String getGitType() {
		return "Bitbucket";
	}

	@Override
	public String getUrlToClone(String repositoryName) throws Exception {
		String urlToClone = null;
		String gitUserName = getUserName();
		String uri = ConfigReaderUtil.getMessage("bitbucket.repo.check.api");
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { gitUserName, repositoryName });

		HttpGet httpGet = HttpUtil.getHttpGet(uri, userAccount);
		GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
		urlToClone = getCloneUrl(gitResponse.getResponseText());
//		log.debug("Bitbucket clone URL: {}", urlToClone);

		return urlToClone;
	}


	@Override
	public String getCloneUrlAfterRepoCreation(String repoName, String repoDescription) throws Exception {
		String cloneUrl = null;
		String loginUser = getUserName();
		String jsonRepo = new BitbucketRepoInput(repoDescription).toJson();
//		log.debug("Json input: {}", jsonRepo);
		String uri = ConfigReaderUtil.getMessage("bitbucket.create.repo.api");
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { loginUser, repoName.toLowerCase() });
//		log.debug("Complete UrI: {}", uri);
		HttpPost httpPost = HttpUtil.getHttpPost(uri, userAccount);
		StringEntity jsonBodyRequest = new StringEntity(jsonRepo, ContentType.APPLICATION_JSON);
		httpPost.setEntity(jsonBodyRequest);
		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpPost);
//			log.debug("Response Code: {}", gitResponse.getStatusCode());
//			log.debug("Response Text: \n{}",gitResponse.getResponseText());
			cloneUrl = getCloneUrl(gitResponse.getResponseText());
		} catch (Exception e) {
			throw e;
		}
		return cloneUrl;
	}

	@Override
	public String[] getAllRepositories() throws Exception {
		String[] repoNames = new String[0];
		String uri = ConfigReaderUtil.getMessage("bitbucket.repo.api");
		String userName = getUserName();
		MessageFormat formatter = new MessageFormat(uri);
		uri = formatter.format(new String[] { userName });
		HttpGet httpGet = HttpUtil.getHttpGet(uri, userAccount);
		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
//			log.debug("Git Response: {}", gitResponse);
			repoNames = getAllRepos(gitResponse.getResponseText());

		} catch (Exception e) {
			throw e;
		}
		return repoNames;
	}

	@Override
	public String getUserName() throws Exception {
		String userName = null;
		String uri = ConfigReaderUtil.getMessage("bitbucket.user.api.uri");
		HttpGet httpGet = HttpUtil.getHttpGet(uri, userAccount);
//		try {
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
//			log.debug("Bitbucket Response Text: {}", gitResponse.getResponseText());
			if(gitResponse.getStatusCode() != 200) {
				GitOnlineErrorResponse errResponse = getError(gitResponse);
				throw new BadCredentialsException("Error code: "+errResponse.getStatus()+" - "+errResponse.getMessage());
			}
			String[] users = getUser(gitResponse.getResponseText());
			userName = users[0];
//		} 
//		catch (Exception e) {
//			throw e;
//		}
		return userName;
	}

	@Override
	public boolean repoExists(String repoName) throws Exception {
		boolean existsFlag = false;
		String uri = ConfigReaderUtil.getMessage("bitbucket.repo.check.api");
		MessageFormat formatter = new MessageFormat(uri);
		String loginUser = getUserName();
		uri = formatter.format(new String[] { loginUser, repoName });
		try {
			HttpGet httpGet = HttpUtil.getHttpGet(uri, userAccount);
			GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
			existsFlag = gitResponse.getStatusCode()== 200 ? true : false;
		} catch (Exception e) {
			throw e;
		}
		return existsFlag;
	}
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ private Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~
	private String getCloneUrl(String jsonResponse) {
		String cloneUrl = null;
		ObjectMapper mapper = new ObjectMapper();
		BitbucketOutputRepo bitbucketRepo = mapper.readValue(jsonResponse, BitbucketOutputRepo.class);
		
		BitbucketClone[] clones = bitbucketRepo.getLink().getBitBucketClones();
		for(BitbucketClone clone : clones) {
			if(clone.getType().equalsIgnoreCase("https")) {
				cloneUrl = clone.getCloneUrl();
				break;
			}
		}
		return cloneUrl;
	}
	
	private GitOnlineErrorResponse getError(GitOnlineResponse gitResponse) {
		ObjectMapper mapper = new ObjectMapper();
		GitOnlineErrorResponse errResponse = null;
		if(gitResponse.getResponseText() != null && ! gitResponse.getResponseText().isEmpty() ) {
			errResponse = mapper.readValue(gitResponse.getResponseText(), GitOnlineErrorResponse.class);
		} else {
			errResponse = new GitOnlineErrorResponse(String.valueOf(gitResponse.getStatusCode()), "Unable to find the user, check the credentials.");
		}
//		GitOnlineErrorResponse errResponse = mapper.readValue(gitResponse.getResponseText(), GitOnlineErrorResponse.class);
//		errResponse.setStatus(String.valueOf(gitResponse.getStatusCode()));
		return errResponse;
	}

	
	private  String[] getAllRepos(String jsonResponse) throws Exception {
		List<String> repoList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = mapper.readTree(jsonResponse);
			JsonNode valuesNode = rootNode.get("values");
			if (valuesNode.isArray()) {
				for (final JsonNode objNode : valuesNode)
					repoList.add(objNode.get("name").asString());
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return repoList.toArray(new String[0]);
	}
	
	private String[] getUser(String jsonResponse) throws Exception {
		String userName = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
//			GitHubRepo gitRepo = mapper.readValue(jsonResponse, GitHubRepo.class);
			BitbucketOutputRepo gitRepo = mapper.readValue(jsonResponse, BitbucketOutputRepo.class);
			userName = gitRepo.getUserName();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return new String[] { userName };
	}

}
