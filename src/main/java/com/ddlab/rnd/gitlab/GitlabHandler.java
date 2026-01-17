package com.ddlab.rnd.gitlab;

import com.ddlab.rnd.exception.BadCredentialsException;
import com.ddlab.rnd.git.model.GitOnlineErrorResponse;
import com.ddlab.rnd.git.model.GitOnlineResponse;
import com.ddlab.rnd.git.model.UserAccount;
import com.ddlab.rnd.gitlab.model.GitLabRepo;
import com.ddlab.rnd.handler.IGitHandler;
import com.ddlab.rnd.util.ConfigReaderUtil;
import com.ddlab.rnd.util.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@ToString
@AllArgsConstructor
public class GitlabHandler implements IGitHandler {

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
        String gitlabRepoCreateApi = ConfigReaderUtil.getMessage("gitlab.repo.create.api");
        MessageFormat formatter = new MessageFormat(gitlabRepoCreateApi);
        String uri = formatter
                .format(new String[]{userAccount.getToken(), userAccount.getUserName(), repoName, repoDescription});
        try {
            HttpPost httpPost = new HttpPost(uri);
            GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpPost);
            if (gitResponse.getStatusCode() == 200 || gitResponse.getStatusCode() == 201) {
                ObjectMapper mapper = new ObjectMapper();
                gitRepo = mapper.readValue(gitResponse.getResponseText(), GitLabRepo.class);
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
        String userName = getUserName();
        String gitlabGetRepoApi = ConfigReaderUtil.getMessage("gitlab.get.repos.api");
        MessageFormat formatter = new MessageFormat(gitlabGetRepoApi);
        String uri = formatter.format(new String[]{token, userName});
        HttpGet httpGet = new HttpGet(uri);
        try {
            GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
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
        GitLabRepo gitRepo = null;
        String gitlabUserApi = ConfigReaderUtil.getMessage("gitlab.user.api");
        MessageFormat formatter = new MessageFormat(gitlabUserApi);
        String uri = formatter.format(new String[]{userAccount.getUserName(), userAccount.getToken()});
        HttpGet httpGet = new HttpGet(uri);
        GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
        if (gitResponse.getStatusCode() != 200) {
            GitOnlineErrorResponse errResponse = getError(gitResponse);
            throw new BadCredentialsException("Error code: " + errResponse.getStatus() + " - " + errResponse.getMessage());
        }
        gitRepo = getGitLabUser(gitResponse);

        return gitRepo.getUserName();
    }

    @Override
    public boolean repoExists(String repoName) throws Exception {
        boolean existsFlag = false;
        String userName = userAccount.getUserName();
        String token = userAccount.getToken();
        String gitlabRepoExistApi = ConfigReaderUtil.getMessage("gitlab.repo.exist.api");
        MessageFormat formatter = new MessageFormat(gitlabRepoExistApi);
        String uri = formatter.format(new String[]{token, userName, repoName});
        HttpGet httpGet = new HttpGet(uri);
        GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
        if (gitResponse.getStatusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            GitLabRepo[] gitLabRepo = objectMapper.readValue(gitResponse.getResponseText(), GitLabRepo[].class);
            existsFlag = gitLabRepo.length != 0 && gitLabRepo[0].getName().equals(repoName);
        }
        return existsFlag;
    }

    @Override
    public String getUrlToClone(String repositoryName) throws Exception {
        String urlToClone = null;
        String gitUserName = getUserName();
        String gitlabRepoExistApi = ConfigReaderUtil.getMessage("gitlab.repo.exist.api");
        MessageFormat formatter = new MessageFormat(gitlabRepoExistApi);
        String uri = formatter.format(new String[]{userAccount.getToken(), gitUserName, repositoryName});
        HttpGet httpGet = new HttpGet(uri);
        GitOnlineResponse gitResponse = HttpUtil.getHttpGetOrPostResponse(httpGet);
        ObjectMapper objectMapper = new ObjectMapper();
        GitLabRepo[] gitLabRepo = objectMapper.readValue(gitResponse.getResponseText(), GitLabRepo[].class);
        urlToClone = gitLabRepo[0].getCloneUrl();
        return urlToClone;
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
