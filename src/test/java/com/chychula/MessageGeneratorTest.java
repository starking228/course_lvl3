package com.chychula;

import com.chychula.message.MessageGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;


public class MessageGeneratorTest {
    private static final Logger logger =
            LoggerFactory.getLogger(MessageGeneratorTest.class);

    @ParameterizedTest
    @CsvSource({
            "0,     10000, true",
            "5000,  10000, true",
            "10000, 10000, true",
            "11000, 10000, false"
    })
    void shouldCheckTimeLimit(
            long elapsedTimeMs,
            long maxTimeMs,
            boolean expected
    ) {
        MessageGenerator messageGenerator = new MessageGenerator();

        long startTime =
                System.currentTimeMillis() - elapsedTimeMs;

        boolean actual =
                messageGenerator.isWithinTimeLimit(
                        startTime,
                        maxTimeMs
                );

        assertEquals(
                expected,
                actual,
                "Time limit test failed. " +
                        "elapsedTimeMs=" + elapsedTimeMs +
                        ", maxTimeMs=" + maxTimeMs +
                        ", expected=" + expected +
                        ", actual=" + actual
        );
    }
}
