package com.chychula;

public class CountValidator implements Validator {

    @Override
    public String validate(Message message) {
        if (message.getCount() < 10) {
            return "count < 10";
        }
        return null;
    }
}