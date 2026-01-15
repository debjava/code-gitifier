package com.ddlab.rnd.git.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data @AllArgsConstructor @NoArgsConstructor @ToString
public class UserAccount {
	private String userName;
	private String token;
}