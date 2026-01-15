package com.ddlab.rnd.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The Class GitLabRepo.
 * 
 * @author Debadatta Mishra
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabRepo {

	/** The id. */
	@JsonProperty("id")
	private Long id;

	/** The name. */
	@JsonProperty("name")
	private String name;

	/** The user name. */
	@JsonProperty("username")
	private String userName;

	/** The repo git. */
//	@JsonProperty("web_url")
//	private String repoGit;
	
	@JsonProperty("http_url_to_repo")
	private String cloneUrl;

}
