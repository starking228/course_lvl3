package com.chychula;

import com.chychula.Message;

import java.util.List;

public class ValidationService {

    private final List<Validator> validators;

    public ValidationService(List<Validator> validators) {
        this.validators = validators;
    }

    public ValidationResult validate(Message message) {

        ValidationResult result = new ValidationResult();

        for (Validator validator : validators) {

            String error = validator.validate(message);

            if (error != null) {
                result.addError(error);
            }
        }

        return result;
    }
}