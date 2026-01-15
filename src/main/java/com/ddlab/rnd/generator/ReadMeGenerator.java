/*
 * Copyright 2018 Tornado Project from DDLAB Inc. or its subsidiaries. All Rights Reserved.
 */
package com.ddlab.rnd.generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;

/**
 * The Class ReadMeGenerator.
 *
 * @author Debadatta Mishra
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReadMeGenerator implements IGenerator {

    private String projectName;
    private String description;


    //    @Override
//    public String generate() {
//        String readMeContents = "";
//        InputStream inputStream = ReadMeGenerator.class.getClassLoader().getResourceAsStream("config/projreadmemd.txt");
//        try {
//            readMeContents = IOUtils.toString(inputStream, Charset.defaultCharset());
//            MessageFormat formatter = new MessageFormat(readMeContents);
//            readMeContents =
//                    formatter.format(new String[]{projectName, description});
//        } catch (IOException e) {
//            // Handle it
//            e.printStackTrace();
//        }
//        return readMeContents;
//    }
    @Override
    public String generate() {
        try (InputStream inputStream =
                     ReadMeGenerator.class
                             .getClassLoader()
                             .getResourceAsStream("config/projreadmemd.txt")) {

            if (inputStream == null) {
                throw new IllegalStateException("projreadmemd.txt not found in classpath");
            }

            String readMeContents = IOUtils.toString(inputStream, Charset.defaultCharset());

            MessageFormat formatter = new MessageFormat(readMeContents);
            return formatter.format(new Object[]{projectName, description});

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
