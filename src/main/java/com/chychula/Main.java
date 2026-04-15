package com.chychula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

//    public static void main(String[] args) {
//
//        if (args.length == 0) {
//            throw new IllegalArgumentException("Number of messages must be provided");
//        }
//
//        int numberOfMessages;
//        try {
//            numberOfMessages = Integer.parseInt(args[0]);
//        } catch (NumberFormatException e) {
//            logger.error("Invalid number format for number of messages: '{}'", args[0]);
//            return;
//        }
//
//        if (numberOfMessages < 1_000_000) {
//            logger.error("Number of messages must be >= 1_000_000, but was {}", numberOfMessages);
//            return;
//        }
//
//        try (ActiveMqProducer producer = new ActiveMqProducer()) {
//
//            IntStream.range(0, numberOfMessages)
////                    .parallel()
//                    .forEach(i -> {
//                        try {
//                            Message msg = RandomMessageUtil.generateMessage();
//                            producer.send(msg);
//                        } catch (Exception e) {
//                            logger.error("Failed to send message #{}", i, e);
//                        }
//                    });
//
//            logger.info("Finished sending {} messages", numberOfMessages);
//
//        } catch (JMSException e) {
//            logger.error("Failed to connect or create producer for ActiveMQ");
//        }
//    }

//        public static void main(String[] args) {
//
//            try (ActiveMqProducer producer = new ActiveMqProducer()) {
//
//                IntStream.range(0, 100)
//                        .forEach(i -> producer.send(RandomMessageUtil.generateMessage()));
//
//                System.out.println("Stream sending finished");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
}
