package com.application.validator;

public interface Validator<T> {

    ValidationResult validate(T object);

}
