package common.service;

import common.validation.ValidationError;

import java.util.List;

public interface ValidationService {
    <T> void validate(T var1, Class<?> var2);

    <T> void validate(List<T> var1, Class<?> var2);

    <T> List<ValidationError> validateAndGet(T var1, Class<?> var2);
}

