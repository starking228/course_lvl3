package com.chychula.validators;

import com.chychula.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EddrValidatorTest {

    private final EddrValidator validator = new EddrValidator();

    @ParameterizedTest(name = "Invalid EDDR test {index}: eddr={0}")
    @ValueSource(strings = {
            "1234567-12345",
            "123456789-12345",
            "12345678-1234",
            "12345678-123456",
            "abcdefgh-12345",
            "12345678_12345"
    })
    void shouldReturnErrorWhenEddrFormatIsInvalid(String eddr) {
        Message message = new Message();
        message.setEddr(eddr);

        assertEquals("invalid EDDR format", validator.validate(message));
    }

    @ParameterizedTest(name = "Valid EDDR test {index}: eddr={0}")
    @ValueSource(strings = {
            "12345678-12345",
            "00000000-00000",
            "98765432-54321"
    })
    void shouldReturnNullWhenEddrFormatIsValid(String eddr) {
        Message message = new Message();
        message.setEddr(eddr);

        assertNull(validator.validate(message));
    }
}
