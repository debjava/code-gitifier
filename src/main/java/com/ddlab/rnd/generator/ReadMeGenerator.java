package com.ddlab.rnd.generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;

@NoArgsConstructor
@AllArgsConstructor
@Data @Slf4j
public class ReadMeGenerator implements IGenerator {

    private String projectName;
    private String description;

    @Override
    public String generate() {
        try (InputStream inputStream = ReadMeGenerator.class
                             .getClassLoader()
                             .getResourceAsStream("config/projreadmemd.txt")) {
            if (inputStream == null) {
                throw new IllegalStateException("projreadmemd.txt not found in classpath");
            }
            String readMeContents = IOUtils.toString(inputStream, Charset.defaultCharset());
            MessageFormat formatter = new MessageFormat(readMeContents);
            return formatter.format(new Object[]{projectName, description});
        } catch (Exception ex) {
            log.error("Exception while generating ReadMe Contents: \n{}", ex);
            return "";
        }
    }

}
