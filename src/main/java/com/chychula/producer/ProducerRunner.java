package com.chychula.producer;

import com.chychula.PropertiesUtil;
import com.chychula.message.Message;
import com.chychula.message.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
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

    public void run(int numberOfMessages, int maxTime) throws Exception {

        Properties properties =
                PropertiesUtil.getLoadedProperties("config.properties");

        int producersCount =
                Integer.parseInt(
                        properties.getProperty("ProducersCount", "16"));

        int consumersCount =
                Integer.parseInt(
                        properties.getProperty("ConsumersCount", "16"));

        AtomicLong generatedCounter = new AtomicLong();
        AtomicLong sentCounter = new AtomicLong();

        List<ActiveMqProducer> producers = new ArrayList<>();

        for (int i = 0; i < producersCount; i++) {
            producers.add(createProducer());
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(producersCount);

        MessageGenerator generator = new MessageGenerator();

        long startTime = System.currentTimeMillis();
        long maxTimeMs = TimeUnit.SECONDS.toMillis(maxTime);

        logger.info("{} Producers started", producersCount);

        List<Future<Long>> futures = new ArrayList<>();

        int baseMessages = numberOfMessages / producersCount;
        int remainder = numberOfMessages % producersCount;

        int start = 1;

        for (int i = 0; i < producersCount; i++) {

            int messagesForProducer =
                    baseMessages + (i < remainder ? 1 : 0);

            int end = start + messagesForProducer;

            ActiveMqProducer producer = producers.get(i);

            int producerStart = start;
            int producerEnd = end;

            Future<Long> future = executor.submit(() ->
                    generator.generateMessages(
                            producer,
                            producerStart,
                            producerEnd,
                            startTime,
                            maxTimeMs,
                            generatedCounter,
                            sentCounter
                    )
            );

            futures.add(future);

            start = end;
        }

        for (Future<Long> future : futures) {
            future.get();
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        /*
         * Відправляємо poison pills після завершення
         * всіх producer-потоків.
         */
        for (int i = 0; i < consumersCount; i++) {
            ActiveMqProducer producer =
                    producers.get(i % producers.size());

            producer.send(POISON);
        }

        for (ActiveMqProducer producer : producers) {
            producer.close();
        }

        long endTime = System.currentTimeMillis();

        long sentMessages = sentCounter.get();

        double seconds =
                (endTime - startTime) / 1000.0;

        double msgPerSec =
                sentMessages / seconds;

        logger.info("Generated messages: {}",
                generatedCounter.get());

        logger.info("Sent messages: {}",
                sentMessages);

        logger.info("Execution time: {} sec",
                String.format("%.2f", seconds));

        logger.info("Throughput: {} msg/sec",
                String.format("%.2f", msgPerSec));
    }

    protected ActiveMqProducer createProducer()
            throws JMSException {

        return new ActiveMqProducer();
    }
}