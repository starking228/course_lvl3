package com.chychula.validators;

import com.chychula.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CountValidatorTest {

    private final CountValidator validator = new CountValidator();

    @ParameterizedTest(name = "Invalid count test {index}: count={0}")
    @ValueSource(ints = {0, 1, 9})
    void shouldReturnErrorWhenCountIsLessThan10(int count) {
        Message message = new Message();
        message.setCount(count);

        assertEquals("count < 10", validator.validate(message));
    }

    @ParameterizedTest(name = "Invalid count test {index}: count={0}")
    @ValueSource(ints = {-1, -100})
    void shouldReturnErrorWhenCountIsNegative(int count) {
        Message message = new Message();
        message.setCount(count);

        assertEquals("count < 10", validator.validate(message));
    }

    @ParameterizedTest(name = "Valid count test {index}: count={0}")
    @ValueSource(ints = {10, 11, 100})
    void shouldReturnNullWhenCountIsValid(int count) {
        Message message = new Message();
        message.setCount(count);

        assertNull(validator.validate(message));
    }


}
