package com.ddlab.rnd.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitbucketOutputRepo {

	@JsonProperty("repository")
	private String repoType;

	@JsonProperty("name")
	private String slug;

	@JsonProperty("links")
	private BitbucketRepoLink link;

	@JsonProperty("type")
	private String userType;

	@JsonProperty("username")
	private String userName;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BitbucketRepoLink {
		@JsonProperty("clone")
		private BitbucketClone[] bitBucketClones;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BitbucketClone {
		@JsonProperty("name")
		private String type;

		@JsonProperty("href")
		private String cloneUrl;
	}

}
