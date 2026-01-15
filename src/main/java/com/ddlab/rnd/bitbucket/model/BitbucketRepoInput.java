package com.ddlab.rnd.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Data
@NoArgsConstructor
public class BitbucketRepoInput {

	@JsonProperty("scm")
	private String scm = "git";

	@JsonProperty("description")
	private String shortDescription;

	public BitbucketRepoInput(String shortDesciption) {
		this.shortDescription = shortDesciption;
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		String toJson = null;
		try {
			toJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toJson;
	}

}
