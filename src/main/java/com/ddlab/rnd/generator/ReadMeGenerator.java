///*
// * Copyright 2018 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
// */
//package com.ddlab.rnd.generator;
//
//import com.ddlab.generator.IReadMeGen;
//import org.apache.commons.io.IOUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//import java.text.MessageFormat;
//
//import static com.ddlab.generator.GeneratorConstants.README_MD_TEMPLATE;
//
///**
// * The Class ReadMeGenerator.
// *
// * @author Debadatta Mishra
// */
//public class ReadMeGenerator implements IReadMeGen {
//
//  /* (non-Javadoc)
//   * @see com.ddlab.generator.IReadMeGen#generateReadMeMdContents(java.lang.String, java.lang.String, java.lang.String)
//   */
//  @Override
//  public String generateReadMeMdContents(
//      String projectName, String description, String contributorName) {
//    String readMeContents = "";
//    InputStream inputStream = getClass().getResourceAsStream(README_MD_TEMPLATE);
//    try {
//      String username = System.getProperty("user.name");
//      contributorName = contributorName == null ? username : contributorName;
//      readMeContents = IOUtils.toString(inputStream, Charset.defaultCharset());
//      MessageFormat formatter = new MessageFormat(readMeContents);
//      readMeContents =
//          formatter.format(new String[] {projectName, description, contributorName, username});
//    } catch (IOException e) {
//      // Handle it
//      e.printStackTrace();
//    }
//    return readMeContents;
//  }
//}
