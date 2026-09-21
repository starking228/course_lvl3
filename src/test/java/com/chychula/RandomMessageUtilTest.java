package com.chychula;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class RandomMessageUtilTest {

    @Test
    void shouldGenerateCountInValidRange() {

        for (int i = 0; i < 100; i++) {

            int count = RandomMessageUtil.generateCount();

            assertTrue(
                    count >= 1 && count <= 1500,
                    "Invalid count generated. " +
                            ", expected range: 1-1500"
            );
        }
    }

    @Test
    void shouldGenerateValidName() {

        String name = RandomMessageUtil.generateName();

        assertNotNull(
                name,
                "Generated name must not be null"
        );

        assertFalse(
                name.isBlank(),
                "Generated name must not be blank. "
        );
    }

    @Test
    void shouldGenerateValidEddrFormat() {

        String eddr = RandomMessageUtil.generateEddr();

        assertNotNull(
                eddr,
                "Generated EDDR must not be null"
        );

        assertTrue(
                eddr.matches("\\d{8}-\\d{5}"),
                "Invalid EDDR format. " +
                        "Generated EDDR: [" + eddr + "], " +
                        "expected format: YYYYMMDD-NNNNN"
        );
    }

    @Test
    void shouldGenerateValidDateInEddr() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMdd");

        for (int i = 0; i < 100; i++) {

            String eddr = RandomMessageUtil.generateEddr();
            String datePart = eddr.substring(0, 8);

            LocalDate date = LocalDate.parse(datePart, formatter);

            LocalDate startDate = LocalDate.of(1900, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            assertTrue(
                    !date.isBefore(startDate)
                            && !date.isAfter(endDate),
                    "Generated EDDR date is outside valid range. " +
                            ", EDDR: [" + eddr + "], " +
                            "date: [" + date + "], " +
                            "expected range: 1900-01-01 to 2025-12-31"
            );
        }
    }

    @Test
    void shouldGenerateValidNumberPartInEddr() {

        for (int i = 0; i < 100; i++) {

            String eddr = RandomMessageUtil.generateEddr();
            String numberPart = eddr.substring(9);

            int number = Integer.parseInt(numberPart);

            assertTrue(
                    number >= 1 && number <= 99999,
                    "Invalid EDDR number part. " +
                            "Iteration: " + i +
                            ", EDDR: [" + eddr + "], " +
                            "number part: [" + numberPart + "], " +
                            "expected range: 1-99999"
            );
        }
    }

    @Test
    void shouldGenerateCreatedAtInValidRange() {

        LocalDateTime start =
                LocalDateTime.of(1900, 1, 1, 0, 0);

        LocalDateTime end =
                LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        for (int i = 0; i < 100; i++) {

            LocalDateTime createdAt =
                    RandomMessageUtil.generateCreatedAt();

            assertNotNull(
                    createdAt,
                    "Generated createdAt must not be null"
            );

            assertTrue(
                    !createdAt.isBefore(start)
                            && !createdAt.isAfter(end),
                    "Generated createdAt is outside valid range. " +
                            ", generated value: [" + createdAt + "], " +
                            "expected range: [" + start + " - " + end + "]"
            );
        }
    }

    @Test
    void shouldGenerateValidLocalDateTime() {

        for (int i = 0; i < 10_000; i++) {

            LocalDateTime createdAt;

            try {
                createdAt = RandomMessageUtil.generateCreatedAt();
            } catch (RuntimeException e) {
                fail(
                        "Failed to generate valid LocalDateTime. " +
                                ", error: " + e.getMessage(),
                        e
                );
                return;
            }

            assertNotNull(
                    createdAt,
                    "Generated LocalDateTime is null. "
            );
        }
    }
}