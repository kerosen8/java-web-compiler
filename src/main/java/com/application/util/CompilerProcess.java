package com.application.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class CompilerProcess {

    public static void compile(String sessionId, int compilationNumber) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c",
                    "javac Main.java && java Main");
            processBuilder.directory(new File("resources/" + sessionId + "/" + compilationNumber));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(new File("resources/" + sessionId + "/" + compilationNumber + "/output.txt"));
            Process process = processBuilder.start();
            process.waitFor();
            process.destroy();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
