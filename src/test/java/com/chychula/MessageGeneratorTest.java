package com.chychula;

import com.chychula.message.Message;
import com.chychula.message.MessageGenerator;
import com.chychula.producer.ActiveMqProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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
    void shouldGenerateAllMessagesWithinTimeLimit() throws Exception {
        MessageGenerator generator = new MessageGenerator();

        ActiveMqProducer producer = mock(ActiveMqProducer.class);

        int numberOfMessages = 1000;
        int maxTimeSec = 10;

        AtomicLong generatedCounter = new AtomicLong();
        AtomicLong sentCounter = new AtomicLong();

        long startTime = System.currentTimeMillis();
        long maxTimeMs = TimeUnit.SECONDS.toMillis(maxTimeSec);

        long generatedMessages = generator.generateMessages(
                producer,
                1,
                numberOfMessages + 1,
                startTime,
                maxTimeMs,
                generatedCounter,
                sentCounter
        );

        assertEquals(
                numberOfMessages,
                generatedMessages,
                "Generated messages count does not match expected count"
        );

        verify(producer, times(numberOfMessages))
                .send(any(Message.class));
    }
}
