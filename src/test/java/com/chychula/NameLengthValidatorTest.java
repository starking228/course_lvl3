package com.chychula;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NameLengthValidatorTest {

    private final NameLengthValidator validator = new NameLengthValidator();

    @ParameterizedTest(name = "Invalid name test {index}: name={0}")
    @ValueSource(strings = {
            "A",
            "Ihor",
            "Robert",
            "Alexan"
    })
    void shouldReturnErrorWhenNameLengthIsLessThan7(String name) {
        Message message = new Message();
        message.setName(name);

        assertEquals("name length < 7", validator.validate(message));
    }

    @ParameterizedTest(name = "Valid name test {index}: name={0}")
    @ValueSource(strings = {
            "Michael",
            "Alexander",
            "Jonathan",
            "Elizabeth"
    })
    void shouldReturnNullWhenNameLengthIsAtLeast7(String name) {
        Message message = new Message();
        message.setName(name);

        assertNull(validator.validate(message));
    }
}