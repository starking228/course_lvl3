package com.chychula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumerRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ConsumerRunner.class);

    public void run() throws Exception {

        Properties properties =
                PropertiesUtil.getLoadedProperties("config.properties");

        int workerCount =
                Integer.parseInt(properties.getProperty("WorkerCount", "16"));

        AtomicLong receivedCounter = new AtomicLong();

        ExecutorService executor =
                Executors.newFixedThreadPool(workerCount);

        for (int i = 0; i < workerCount; i++) {

            executor.submit(() -> {

                try (ActiveMqConsumer consumer =
                             new ActiveMqConsumer()) {

                    while (true) {

                        Message msg = consumer.receive(5000);

                        if (msg == null) {
                            break;
                        }

                        receivedCounter.incrementAndGet();
                    }

                } catch (Exception e) {
                    logger.error("Consumer failed", e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        logger.info("All messages processed");
        logger.info("Received messages: {}",
                receivedCounter.get());
    }
}