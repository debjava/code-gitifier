package com.ddlab.rnd.github.model;

import com.ddlab.rnd.git.model.Repo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @ToString
public class GitHubRepo {

	/** The repos. */
	private Repo[] repos;

	/** The clone url. */
	@JsonProperty("clone_url")
	private String cloneUrl;

	/** The login user. */
	@JsonProperty("login")
	private String loginUser;
	
	@JsonProperty("username")
	private String userName;


}