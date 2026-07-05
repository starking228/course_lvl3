package com.chychula;

public class InvalidCsvRecord {

    private String name;
    private int count;
    private String errors;

    public InvalidCsvRecord(
            String name,
            int count,
            String errors) {

        this.name = name;
        this.count = count;
        this.errors = errors;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getErrors() {
        return errors;
    }
}