package com.application.util;

import lombok.experimental.UtilityClass;

import javax.tools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@UtilityClass
public class Compiler {

    public static CompilationResult compile(String code) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        Date compilationTime = null;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject javaFile = new JavaSourceFromString("Main", code);

        Iterable<? extends JavaFileObject> compilationUnits = List.of(javaFile);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        if (success) {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "Main");
            processBuilder.directory(new File("."));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            LocalDateTime currentTime = LocalDateTime.now();
            compilationTime = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
            process.waitFor();
        } else {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                result.append(diagnostic.getMessage(null)).append("\n");
            }
        }
        return CompilationResult.of(code, result.toString(), compilationTime);
    }
}

