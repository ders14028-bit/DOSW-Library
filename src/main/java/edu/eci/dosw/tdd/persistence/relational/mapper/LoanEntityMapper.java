package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;

public final class LoanEntityMapper {

    private LoanEntityMapper() {
    }

    public static Loan toDomain(LoanEntity entity) {
        Loan loan = new Loan();
        loan.setBook(BookEntityMapper.toDomain(entity.getBook()));
        loan.setUser(UserEntityMapper.toDomain(entity.getUser()));
        loan.setLoanDate(entity.getLoanDate());
        loan.setReturnDate(entity.getReturnDate());
        loan.setStatus(entity.getStatus());
        return loan;
    }
}

