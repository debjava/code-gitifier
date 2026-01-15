package com.ddlab.rnd.util;

import com.ddlab.rnd.generator.GitIgnoreGenerator;
import com.ddlab.rnd.generator.IGenerator;
import com.ddlab.rnd.generator.ReadMeGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneratorUtil {

    public static void createGitIgnoreFile(File file) {
        IGenerator generator = new GitIgnoreGenerator();
        String contents = generator.generate();
        Path gitIgnorePath = Paths.get(file.getAbsolutePath() + File.separator + ".gitignore");
        try {
            if (Files.exists(gitIgnorePath))
                return;
            Files.write(gitIgnorePath, contents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createReadMeMdFile(File file, String projectName, String description) {
        IGenerator generator = new ReadMeGenerator(projectName, description);
        description = (description == null || description.trim().isEmpty()) ? "To be updated later" : description;
        String readMeContents =  generator.generate();
        Path readMePath = Paths.get(file.getAbsolutePath() + File.separator + "README.md");
        try {
            if (Files.exists(readMePath))
                return;
            Files.write(readMePath, readMeContents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
