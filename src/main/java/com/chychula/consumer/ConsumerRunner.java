package com.chychula.consumer;

import com.chychula.*;
import com.chychula.csv.CsvWriter;
import com.chychula.message.Message;
import com.chychula.validators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumerRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ConsumerRunner.class);
    AtomicLong validCounter = new AtomicLong();
    AtomicLong invalidCounter = new AtomicLong();

    public void run() throws Exception {

        Properties properties =
                PropertiesUtil.getLoadedProperties("config.properties");

        int consumersCount =
                Integer.parseInt(properties.getProperty("ConsumersCount", "16"));

        AtomicLong receivedCounter = new AtomicLong();

        ExecutorService executor =
                Executors.newFixedThreadPool(consumersCount);

        CsvWriter csvWriter = new CsvWriter();

        ValidationService validationService =
                new ValidationService(List.of(
                        new NameLengthValidator(),
                        new NameContainsAValidator(),
                        new CountValidator(),
                        new EddrValidator()
                ));

        logger.info("Consumers started");
        for (int i = 0; i < consumersCount; i++) {

            executor.submit(() -> {

                try (ActiveMqConsumer consumer =
                             new ActiveMqConsumer()) {

                    while (true) {

                        Message msg = consumer.receive();

                        if ("__POISON__".equals(msg.getName())) {
                            break;
                        }

                        ValidationResult result =
                                validationService.validate(msg);
                        long received = receivedCounter.incrementAndGet();
                        if (received % 100_000 == 0) {
                            logger.info("Received messages: {}", received);
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
        logger.info("Consumers finished");
        csvWriter.close();

        logger.info("All messages processed");
        logger.info("Received: {}", receivedCounter.get());
        logger.info("Valid: {}", validCounter.get());
        logger.info("Invalid: {}", invalidCounter.get());
    }
}