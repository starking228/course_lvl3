package com.chychula.validators;

import com.chychula.message.Message;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class ValidationService {

    private final Validator validator;

    public ValidationService() {
        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        this.validator = factory.getValidator();
    }

    public ValidationResult validate(Message message) {

        Set<jakarta.validation.ConstraintViolation<Message>> violations =
                validator.validate(message);

        ValidationResult result = new ValidationResult();

        violations.forEach(violation ->
                result.addError(violation.getMessage())
        );

        return result;
    }
}