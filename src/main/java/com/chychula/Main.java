package com.chychula;

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

        ProducerRunner producerRunner = new ProducerRunner();
        producerRunner.run(numberOfMessages);

        ConsumerRunner consumerRunner = new ConsumerRunner();
        consumerRunner.run();
    }
}