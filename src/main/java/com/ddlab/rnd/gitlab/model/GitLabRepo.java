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

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("username")
	private String userName;

	@JsonProperty("http_url_to_repo")
	private String cloneUrl;

}
