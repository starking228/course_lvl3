package com.chychula.consumer;

import com.chychula.PropertiesUtil;
import com.chychula.csv.CsvWriter;
import com.chychula.message.Message;
import com.chychula.validators.ValidationResult;
import com.chychula.validators.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumerRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ConsumerRunner.class);

    private final AtomicLong validCounter = new AtomicLong();
    private final AtomicLong invalidCounter = new AtomicLong();

    public void run() throws Exception {

        Properties properties =
                PropertiesUtil.getLoadedProperties("config.properties");

        int consumersCount =
                Integer.parseInt(
                        properties.getProperty("ConsumersCount", "16"));

        AtomicLong receivedCounter = new AtomicLong();

        /*
         * Час першого отриманого повідомлення.
         * compareAndSet гарантує, що час запише тільки перший consumer.
         */
        AtomicLong firstMessageTime = new AtomicLong();

        ExecutorService executor =
                Executors.newFixedThreadPool(consumersCount);

        CsvWriter csvWriter = new CsvWriter();

        ValidationService validationService =
                new ValidationService();

        logger.info("{} Consumers started", consumersCount);

        for (int i = 0; i < consumersCount; i++) {

            executor.submit(() -> {

                try (ActiveMqConsumer consumer =
                             new ActiveMqConsumer()) {

                    while (true) {

                        Message msg = consumer.receive();

                        if (msg == null) {
                            continue;
                        }

                        /*
                         * Фіксуємо час першого реального повідомлення.
                         */
                        firstMessageTime.compareAndSet(
                                0,
                                System.nanoTime()
                        );

                        if ("__POISON__".equals(msg.getName())) {
                            break;
                        }

                        ValidationResult result =
                                validationService.validate(msg);

                        long received =
                                receivedCounter.incrementAndGet();

                        if (received % 100_000 == 0) {
                            logger.info(
                                    "Received messages: {}",
                                    received
                            );
                        }

                        if (result.isValid()) {
                            validCounter.incrementAndGet();
                            csvWriter.writeValid(msg);
                        } else {
                            invalidCounter.incrementAndGet();
                            csvWriter.writeInvalid(msg, result);
                        }
                    }

                } catch (Exception e) {
                    logger.error("Consumer failed", e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long endTime = System.nanoTime();

        csvWriter.close();

        logger.info("Consumers finished");
        logger.info("All messages processed");
        logger.info("Received: {}", receivedCounter.get());
        logger.info("Valid: {}", validCounter.get());
        logger.info("Invalid: {}", invalidCounter.get());

        /*
         * Якщо повідомлень не було, час обробки не рахуємо.
         */
        if (firstMessageTime.get() != 0) {

            double processingSeconds =
                    (endTime - firstMessageTime.get())
                            / 1_000_000_000.0;

            long received = receivedCounter.get();

            double processingThroughput =
                    processingSeconds > 0
                            ? received / processingSeconds
                            : 0;

            logger.info(
                    "Consumer processing time: {} sec",
                    String.format("%.2f", processingSeconds)
            );

            logger.info(
                    "Consumer throughput: {} msg/sec",
                    String.format("%.2f", processingThroughput)
            );
        }
    }
}