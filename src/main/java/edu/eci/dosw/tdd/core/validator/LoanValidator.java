package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.util.ValidationUtil;

public class LoanValidator {

    public void validateLoanRequest(String userId, String bookId) {
        ValidationUtil.requireNotBlank(userId, "userId");
        ValidationUtil.requireNotBlank(bookId, "bookId");
    }
}

