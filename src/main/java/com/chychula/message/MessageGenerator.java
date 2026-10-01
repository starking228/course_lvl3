package com.chychula.message;

import com.chychula.RandomMessageUtil;
import com.chychula.producer.ActiveMqProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class MessageGenerator {

    private static final Logger logger =
            LoggerFactory.getLogger(MessageGenerator.class);

    public long generateMessages(
            ActiveMqProducer producer,
            int start,
            int end,
            long startTime,
            long maxTimeMs,
            AtomicLong generatedCounter,
            AtomicLong sentCounter) {

        AtomicLong generated = new AtomicLong();

        IntStream.range(start, end)
                .takeWhile(i ->
                        isWithinTimeLimit(startTime, maxTimeMs))
                .forEach(i -> {

                    Message message =
                            RandomMessageUtil.generateMessage();

                    try {
                        producer.send(message);

                        generated.incrementAndGet();
                        long generatedTotal =
                                generatedCounter.incrementAndGet();

                        sentCounter.incrementAndGet();

                        if (generatedTotal % 100_000 == 0) {
                            logger.info(
                                    "Generated and sent messages: {}",
                                    generatedTotal
                            );
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Failed to send message", e);
                    }
                });

        return generated.get();
    }

    public boolean isWithinTimeLimit(
            long startTime,
            long maxTimeMs) {

        return System.currentTimeMillis() - startTime <= maxTimeMs;
    }
}