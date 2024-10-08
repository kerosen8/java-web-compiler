package com.application.compiler;

import lombok.Value;

import java.util.Date;

@Value(staticConstructor = "of")
public class CompilationResult {

    String code;
    String result;
    Date compilationTime;

}
