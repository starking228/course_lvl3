package com.chychula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class ProducerRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(ProducerRunner.class);

    private static final Message POISON =
            new Message("__POISON__", "", -1, null);

    public void run(int numberOfMessages) throws Exception {

        Properties properties =
                PropertiesUtil.getLoadedProperties("config.properties");

        int maxTimeSec =
                Integer.parseInt(properties.getProperty("MaxTime", "90"));

        int workerCount =
                Integer.parseInt(properties.getProperty("WorkerCount", "16"));

        BlockingQueue<Message> queue =
                new LinkedBlockingQueue<>(50_000);

        List<ActiveMqProducer> producers = new ArrayList<>();

        AtomicLong sentCounter = new AtomicLong();

        for (int i = 0; i < workerCount; i++) {
            producers.add(new ActiveMqProducer());
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(workerCount);

        long startTime = System.currentTimeMillis();
        long maxTimeMs = TimeUnit.SECONDS.toMillis(maxTimeSec);

        // workers
        for (int i = 0; i < workerCount; i++) {

            ActiveMqProducer producer = producers.get(i);

            executor.submit(() -> {

                try {

                    while (true) {

                        Message msg = queue.take();

                        if ("__POISON__".equals(msg.getName())) {
                            break;
                        }

                        producer.send(msg);
                        sentCounter.incrementAndGet();
                    }

                } catch (Exception e) {
                    logger.error("Send failed", e);
                }
            });
        }

        // generator
        IntStream.range(0, numberOfMessages)
                .takeWhile(i ->
                        System.currentTimeMillis() - startTime <= maxTimeMs)
                .forEach(i -> {
                    try {
                        queue.put(RandomMessageUtil.generateMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

        boolean timeExceeded =
                System.currentTimeMillis() - startTime > maxTimeMs;

        if (timeExceeded) {
            logger.info("Time limit reached");
        } else {
            logger.info("All messages generated");
        }

        for (int i = 0; i < workerCount; i++) {
            queue.put(POISON);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

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