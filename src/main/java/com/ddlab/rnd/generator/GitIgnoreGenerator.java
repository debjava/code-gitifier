/*
 * Copyright 2018 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.rnd.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


/**
 * The Class GitIgnoreGenerator.
 *
 * @author Debadatta Mishra
 */
@Slf4j
public class GitIgnoreGenerator implements IGenerator {

    @Override
    public String generate() {
        return getGitIgnoreContents();
    }

    public String getGitIgnoreContents() {
        try (InputStream inputStream = GitIgnoreGenerator.class
                             .getClassLoader()
                             .getResourceAsStream("config/projgitignore.txt")) {
            if (inputStream == null) {
                throw new IllegalStateException("projgitignore.txt not found in classpath");
            }
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (Exception ex) {
            log.error("Exception while generating .gitIgnore Contents: \n{}", ex);
            return "";
        }
    }

//    public String getGitIgnoreContents() {
//        String gitIgnoreContents = "";
//        InputStream inputStream = GitIgnoreGenerator.class.getClassLoader().getResourceAsStream("config/projgitignore.txt");
//        try {
//            gitIgnoreContents = IOUtils.toString(inputStream, Charset.defaultCharset());
//        } catch (IOException e) {
//            // Handle it
//            e.printStackTrace();
//        }
//        return gitIgnoreContents;
//    }




}
