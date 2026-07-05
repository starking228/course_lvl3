package com.chychula;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

public class RandomMessageUtil {
    private static final Faker faker = new Faker();
    private static final double INVALID_RATE = 0.30;

    public static String generateName() {
        return faker.name().firstName();
    }

    public static String generateEddr() {
        int year = ThreadLocalRandom.current().nextInt(1900, 2026); // 1900-2025
        int month = ThreadLocalRandom.current().nextInt(1, 13);

        int day;
        switch (month) {
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    day = ThreadLocalRandom.current().nextInt(1, 30);
                } else {
                    day = ThreadLocalRandom.current().nextInt(1, 29);
                }
                break;
            case 4, 6, 9, 11:
                day = ThreadLocalRandom.current().nextInt(1, 31);
                break;
            default:
                day = ThreadLocalRandom.current().nextInt(1, 32);
        }

        String datePart = String.format("%04d%02d%02d", year, month, day);
        int numberPart = ThreadLocalRandom.current().nextInt(1, 100000);
        String numberPartStr = String.format("%05d", numberPart);

        return datePart + "-" + numberPartStr;
    }

    // Генерація count від 1 до 1500
    public static int generateCount() {
        return ThreadLocalRandom.current().nextInt(1, 1501);
    }

    // Генерація дати та часу від 1900-01-01 до 2025-12-31
    public static LocalDateTime generateCreatedAt() {
        LocalDate startDate = LocalDate.of(1900, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();

        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);

        LocalTime randomTime = LocalTime.of(ThreadLocalRandom.current().nextInt(24),
                ThreadLocalRandom.current().nextInt(60),
                ThreadLocalRandom.current().nextInt(60));

        return LocalDateTime.of(randomDate, randomTime);
    }

    public static Message generateMessage() {

        Message message = new Message(
                generateName(),
                generateEddr(),
                generateCount(),
                generateCreatedAt()
        );

        if (ThreadLocalRandom.current().nextDouble() < INVALID_RATE) {
            corruptMessage(message);
        }

        return message;
    }

    private static void corruptMessage(Message message) {

        switch (ThreadLocalRandom.current().nextInt(2)) {

            case 0 -> message.setCount(
                    ThreadLocalRandom.current().nextInt(0, 10)
            );
            case 1 -> message.setEddr("INVALID");
        }
    }
}
