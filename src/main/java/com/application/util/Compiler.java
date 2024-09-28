package com.application.util;

import lombok.experimental.UtilityClass;

import javax.tools.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@UtilityClass
public class Compiler {

    public static CompilationResult compile(String code, String input) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        Date compilationTime;
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
            if (!input.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(input);
                    writer.newLine();
                    writer.flush();
                }
            } else {
                process.getOutputStream().close();
            }
            process.waitFor();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.lineSeparator());
                }
            }
            process.waitFor();
        } else {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                result.append(diagnostic.getMessage(null)).append("\n");
            }
        }
        LocalDateTime currentTime = LocalDateTime.now();
        compilationTime = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        return CompilationResult.of(code, result.toString(), compilationTime);
    }

}
