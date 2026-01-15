package com.ddlab.rnd.git.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class GitOnlineResponse {
	
	private int statusCode;

	private String responseText;

}
