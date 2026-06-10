package com.chychula;

public class NameContainsAValidator implements Validator {

    @Override
    public String validate(Message message) {
        if (message.getName() == null || !message.getName().toLowerCase().contains("a")) {
            return "name has no 'a'";
        }
        return null;
    }
}