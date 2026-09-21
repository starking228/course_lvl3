package com.chychula.validators;

import com.chychula.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NameContainsAValidatorTest {

    private final NameContainsAValidator validator = new NameContainsAValidator();

    @ParameterizedTest(name = "Invalid name test {index}: name={0}")
    @ValueSource(strings = {
            "John",
            "Miki",
            "Robert",
            "Ihor"
    })
    void shouldReturnErrorWhenNameHasNoA(String name) {
        Message message = new Message();
        message.setName(name);

        assertEquals("name has no 'a'", validator.validate(message));
    }

    @ParameterizedTest(name = "Valid name test {index}: name={0}")
    @ValueSource(strings = {
            "Anna",
            "Andrew",
            "Alexander",
            "SARAH",
            "Maria"
    })
    void shouldReturnNullWhenNameContainsA(String name) {
        Message message = new Message();
        message.setName(name);

        assertNull(validator.validate(message));
    }

}
