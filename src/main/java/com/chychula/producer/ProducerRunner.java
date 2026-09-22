package com.chychula.producer;

import com.chychula.message.Message;
import com.chychula.message.MessageGenerator;
import com.chychula.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ProducerRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ProducerRunner.class);

    private static final Message POISON =
            new Message("__POISON__", "", -1, null);

    public void run(int numberOfMessages) throws Exception {

        Properties properties =
                PropertiesUtil.getLoadedProperties("config.properties");

        int producersCount =
                Integer.parseInt(properties.getProperty("ProducersCount", "16"));

        int consumersCount =
                Integer.parseInt(properties.getProperty("ConsumersCount", "16"));


        BlockingQueue<Message> queue =
                new LinkedBlockingQueue<>(50_000);

        List<ActiveMqProducer> producers = new ArrayList<>();

        AtomicLong sentCounter = new AtomicLong();

        for (int i = 0; i < producersCount; i++) {
            producers.add(new ActiveMqProducer());
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(producersCount);
        MessageGenerator generator = new MessageGenerator();


        // Producers
        long startTime = System.currentTimeMillis();
        logger.info("{} Producers started", producers.size());

        for (int i = 0; i < producers.size(); i++) {

            ActiveMqProducer producer = producers.get(i);

            executor.submit(() -> {

                try {

                    while (true) {

                        Message msg = queue.take();

                        if ("__POISON__".equals(msg.getName())) {
                            break;
                        }

                        producer.send(msg);
                        long sent = sentCounter.incrementAndGet();
                        if (sent % 100_000 == 0) {
                            logger.info("Sent messages: {}", sent);
                        }
                    }

                } catch (Exception e) {
                    logger.error("Send failed", e);
                }
            });
        }

        // generator
        generator.generateMessages(
                queue,
                numberOfMessages,
                producersCount
        );

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        for (int i = 0; i < consumersCount; i++) {
            producers.getFirst().send(POISON);
        }
        for (ActiveMqProducer producer : producers) {
            producer.close();
        }

        long endTime = System.currentTimeMillis();

        long sentMessages = sentCounter.get();

        double seconds = (endTime - startTime) / 1000.0;
        double msgPerSec = sentMessages / seconds;

        logger.info("Sent messages: {}", sentMessages);
        logger.info("Execution time: {} sec",
                String.format("%.2f", seconds));
        logger.info("Throughput: {} msg/sec",
                String.format("%.2f", msgPerSec));
    }
}