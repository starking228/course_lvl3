package com.chychula.message;

import com.chychula.PropertiesUtil;
import com.chychula.RandomMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class MessageGenerator {

    private static final Logger logger =
            LoggerFactory.getLogger(MessageGenerator.class);

    private static final Message POISON =
            new Message("__POISON__", "", -1, null);

    public long generateMessages(
            BlockingQueue<Message> queue,
            int numberOfMessages,
            int producersCount,
            int maxTimeSec) throws InterruptedException {

        AtomicLong generatedMessages = new AtomicLong();
        long startTime = System.currentTimeMillis();
        long maxTimeMs = TimeUnit.SECONDS.toMillis(maxTimeSec);

        IntStream.range(1, numberOfMessages+1)
                .takeWhile(i ->
                        isWithinTimeLimit(startTime, maxTimeMs))
                .forEach(i -> {
                    try {
                        queue.put(RandomMessageUtil.generateMessage());
                        generatedMessages.incrementAndGet();
                        if (i % 100_000 == 0) {
                            logger.info("Generated messages: {}", i);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

        if (generatedMessages.get() == numberOfMessages) {
            logger.info("All messages generated");
        } else if (!isWithinTimeLimit(startTime, maxTimeMs)) {
            logger.info("Time limit reached");
        } else {
            logger.warn("Generator finished, but All messages were generated and time limit was not reached");
        }

        for (int i = 0; i < producersCount; i++) {
            queue.put(POISON);
        }

        logger.info("Generator finished");
        logger.info("Generated messages: {}", generatedMessages.get());

        return generatedMessages.get();

    }

    public boolean isWithinTimeLimit(
            long startTime,
            long maxTimeMs
    ) {
        return System.currentTimeMillis() - startTime <= maxTimeMs;
    }
}