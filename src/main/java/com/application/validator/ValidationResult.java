package com.application.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationResult {

    private final List<String> errors = new ArrayList<>();

    public void add(String error) {
        errors.add(error);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

}
