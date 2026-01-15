package com.ddlab.rnd.git.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Repo {

	/** The name. */
	@JsonProperty("name")
	private String name;

	/** The clone url. */
	@JsonProperty("clone_url")
	private String cloneUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Repo repo = (Repo) o;
		return Objects.equals(name, repo.name) && Objects.equals(cloneUrl, repo.cloneUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name, cloneUrl);
	}

}