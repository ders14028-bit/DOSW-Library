package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.util.ValidationUtil;

public class UserValidator {

    public String validateUserId(String userId) {
        return ValidationUtil.requireNotBlank(userId, "userId");
    }
}

