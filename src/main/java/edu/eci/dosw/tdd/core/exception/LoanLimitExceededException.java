package edu.eci.dosw.tdd.core.exception;

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String userId, int maxLoans) {
        super("El usuario " + userId + " supero el limite de " + maxLoans + " prestamos activos.");
    }
}

