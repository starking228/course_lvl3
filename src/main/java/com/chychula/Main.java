package com.chychula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Message POISON = new Message("__POISON__", "", -1, null);

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("Number of messages must be provided");
        }

        int numberOfMessages = Integer.parseInt(args[0]);

        // 🔹 load config
        Properties properties = PropertiesUtil.getLoadedProperties("config.properties");

        int maxTimeSec = Integer.parseInt(properties.getProperty("MaxTime", "90"));
        int workerCount = Integer.parseInt(properties.getProperty("WorkerCount", "16"));

        BlockingQueue<Message> queue = new LinkedBlockingQueue<>(50_000);
        List<ActiveMqProducer> producers = new ArrayList<>();

        for (int i = 0; i < workerCount; i++) {
            producers.add(new ActiveMqProducer());
        }

        ExecutorService executor = Executors.newFixedThreadPool(workerCount);

        long startTime = System.currentTimeMillis();
        long maxTimeMs = TimeUnit.SECONDS.toMillis(maxTimeSec);

        // 🔹 workers
        for (int i = 0; i < workerCount; i++) {
            ActiveMqProducer producer = producers.get(i);

            executor.submit(() -> {
                try {
                    while (true) {

                        Message msg = queue.take();

                        if ("__POISON__".equals(msg.getName())) {
                            break;
                        }

                        try {
                            producer.send(msg);
                        } catch (Exception e) {
                            logger.error("Send failed", e);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 🔹 generator (Stream як в ТЗ)
        IntStream.range(0, numberOfMessages)
                .forEach(i -> {

                    // ⏱ TIME STOP
                    if (System.currentTimeMillis() - startTime > maxTimeMs) {
                        logger.info("Time limit reached, stopping generation at {}", i);
                        throw new RuntimeException("STOP"); // зупинка stream
                    }

                    try {
                        queue.put(RandomMessageUtil.generateMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

        // 🔹 poison pills
        for (int i = 0; i < workerCount; i++) {
            queue.put(POISON);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        for (ActiveMqProducer producer : producers) {
            producer.close();
        }

        long endTime = System.currentTimeMillis();

        logger.info("Time: {} ms", (endTime - startTime));
        logger.info("Done");
    }
}
