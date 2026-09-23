package com.chychula.validators;

import com.chychula.message.Message;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationServiceTest {

    @Test
    void shouldDistributeMessagesByValidationResult() {

        ValidationService validationService = new ValidationService(List.of(
                new NameLengthValidator(),
                new NameContainsAValidator(),
                new CountValidator(),
                new EddrValidator()
        ));

        List<Message> messages = List.of(

                // 15 valid
                new Message("Waltraud", "20010523-12345", 1344, LocalDateTime.now()),
                new Message("Margarete", "20010523-12345", 1059, LocalDateTime.now()),
                new Message("Thurman", "20010523-12345", 391, LocalDateTime.now()),
                new Message("Raleigh", "20010523-12345", 1344, LocalDateTime.now()),
                new Message("Patrice", "20010523-12345", 743, LocalDateTime.now()),
                new Message("Adriane", "20010523-12345", 969, LocalDateTime.now()),
                new Message("Madison", "20010523-12345", 402, LocalDateTime.now()),
                new Message("Katheryn", "20010523-12345", 677, LocalDateTime.now()),
                new Message("Janette", "20010523-12345", 540, LocalDateTime.now()),
                new Message("Rosamond", "20010523-12345", 217, LocalDateTime.now()),
                new Message("Patricia", "20010523-12345", 500, LocalDateTime.now()),
                new Message("Barbara", "20010523-12345", 300, LocalDateTime.now()),
                new Message("Anakonda", "20010523-12345", 250, LocalDateTime.now()),
                new Message("Natasha", "20010523-12345", 800, LocalDateTime.now()),
                new Message("Miranda", "20010523-12345", 120, LocalDateTime.now()),

                // 5 invalid
                new Message("Robert", "12345678-12345", 100, LocalDateTime.now()),
                new Message("Alexander", "INVALID", 100, LocalDateTime.now()),
                new Message("Amanda", "12345678-12345", 5, LocalDateTime.now()),
                new Message("Robert", "INVALID", 5, LocalDateTime.now()),
                new Message("Thomas", "12345678-12345", 100, LocalDateTime.now())
        );

        int validCount = 0;
        int invalidCount = 0;

        for (Message msg : messages) {

            ValidationResult result = validationService.validate(msg);

            if (result.isValid()) {
                validCount++;
            } else {
                invalidCount++;
            }
        }

        assertEquals(15, validCount);
        assertEquals(5, invalidCount);
    }
}
