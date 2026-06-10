package com.chychula;

public class EddrValidator implements Validator {

    @Override
    public String validate(Message message) {

        String eddr = message.getEddr();

        if (eddr == null || !eddr.matches("\\d{8}-\\d{5}")) {
            return "invalid EDDR format";
        }

        return null;
    }
}