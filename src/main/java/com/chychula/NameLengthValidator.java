package com.chychula;

public class NameLengthValidator implements Validator {

    @Override
    public String validate(Message message) {
        if (message.getName() == null || message.getName().length() < 7) {
            return "name length < 7";
        }
        return null;
    }
}
