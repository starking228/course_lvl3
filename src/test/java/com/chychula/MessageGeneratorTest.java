package com.chychula;

import com.chychula.message.Message;
import com.chychula.message.MessageGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    @Test
    void shouldGenerateAllMessagesWithinTimeLimit() throws InterruptedException {
        MessageGenerator generator = new MessageGenerator();

        BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

        int numberOfMessages = 1000;
        int producersCount = 2;
        int maxTimeSec = 10;

        long generatedMessages = generator.generateMessages(
                queue,
                numberOfMessages,
                producersCount,
                maxTimeSec
        );

        assertEquals(
                numberOfMessages,
                generatedMessages,
                "Generated messages count does not match expected count"
        );
    }
}
