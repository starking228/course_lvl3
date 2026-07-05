package com.chychula;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.io.FileWriter;
import java.util.Map;
import java.util.Properties;

public class CsvWriter implements AutoCloseable {

    private final CSVWriter validCsv;
    private final CSVWriter invalidCsv;

    private final StatefulBeanToCsv<ValidCsvRecord> validWriter;
    private final StatefulBeanToCsv<InvalidCsvRecord> invalidWriter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    Properties properties =
            PropertiesUtil.getLoadedProperties("config.properties");

    String validFile =
            properties.getProperty(
                    "ValidCsvFile",
                    "valid.csv");

    String invalidFile =
            properties.getProperty(
                    "InvalidCsvFile",
                    "invalid.csv");

    public CsvWriter() throws Exception {

        validCsv = new CSVWriter(new FileWriter(validFile));
        invalidCsv = new CSVWriter(new FileWriter(invalidFile));

        validWriter = new StatefulBeanToCsvBuilder<ValidCsvRecord>(validCsv)
                .build();

        invalidWriter = new StatefulBeanToCsvBuilder<InvalidCsvRecord>(invalidCsv)
                .build();
    }

    public synchronized void writeValid(Message message) {

        try {

            ValidCsvRecord record =
                    new ValidCsvRecord(
                            message.getName(),
                            message.getCount()
                    );

            validWriter.write(record);

        } catch (Exception e) {
            throw new RuntimeException("Failed to write valid record", e);
        }
    }

    public synchronized void writeInvalid(
            Message message,
            ValidationResult validationResult) {

        try {

            String errorsJson = objectMapper.writeValueAsString(
                    Map.of("errors", validationResult.getErrors())
            );

            InvalidCsvRecord record =
                    new InvalidCsvRecord(
                            message.getName(),
                            message.getCount(),
                            errorsJson
                    );

            invalidWriter.write(record);

        } catch (Exception e) {
            throw new RuntimeException("Failed to write invalid record", e);
        }
    }

    @Override
    public void close() throws Exception {
        validCsv.close();
        invalidCsv.close();
    }
}