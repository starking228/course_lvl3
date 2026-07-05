package com.chychula;

public class ValidCsvRecord {

    private String name;
    private int count;

    public ValidCsvRecord(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}