///*
// * Copyright 2018 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
// */
//package com.ddlab.rnd.generator;
//
//import com.ddlab.generator.IGitIgnoreGen;
//import org.apache.commons.io.IOUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//
//import static com.ddlab.generator.GeneratorConstants.GIT_IGNORE_TEMPLATE;
//
///**
// * The Class GitIgnoreGenerator.
// *
// * @author Debadatta Mishra
// */
//public class GitIgnoreGenerator implements IGitIgnoreGen {
//
//  /* (non-Javadoc)
//   * @see com.ddlab.generator.IGitIgnoreGen#generateGitIgnoreContents()
//   */
//  @Override
//  public String generateGitIgnoreContents() {
//    String gitIgnoreContents = "";
//    InputStream inputStream = getClass().getResourceAsStream(GIT_IGNORE_TEMPLATE);
//    try {
//      gitIgnoreContents = IOUtils.toString(inputStream, Charset.defaultCharset());
//    } catch (IOException e) {
//      // Handle it
//      e.printStackTrace();
//    }
//    return gitIgnoreContents;
//  }
//}
