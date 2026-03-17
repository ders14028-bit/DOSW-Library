package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.util.ValidationUtil;

public class BookValidator {

    public String validateBookId(String bookId) {
        return ValidationUtil.requireNotBlank(bookId, "bookId");
    }
}

