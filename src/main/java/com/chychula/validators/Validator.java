package com.chychula.validators;

import com.chychula.message.Message;

public interface Validator {
    String validate(Message message);
}
