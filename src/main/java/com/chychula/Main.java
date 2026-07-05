package com.chychula;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException(
                    "Number of messages must be provided");
        }

        int numberOfMessages = Integer.parseInt(args[0]);

        if (numberOfMessages < 1_000_000) {
            throw new IllegalArgumentException(
                    "N must be >= 1_000_000");
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try {
                new ConsumerRunner().run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        executor.submit(() -> {
            try {
                new ProducerRunner().run(numberOfMessages);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }
}