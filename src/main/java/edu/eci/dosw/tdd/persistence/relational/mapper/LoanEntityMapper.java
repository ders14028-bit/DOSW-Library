package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaBookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.JpaUserRepository;

public final class LoanEntityMapper {

    private LoanEntityMapper() {}

    public static Loan toDomain(LoanEntity entity) {
        Loan loan = new Loan();
        loan.setId(String.valueOf(entity.getId()));
        loan.setBook(BookEntityMapper.toDomain(entity.getBook()));
        loan.setUser(UserEntityMapper.toDomain(entity.getUser()));
        loan.setLoanDate(entity.getLoanDate());
        loan.setReturnDate(entity.getReturnDate());
        loan.setStatus(entity.getStatus());
        return loan;
    }

    public static LoanEntity toEntity(Loan loan, JpaUserRepository userRepo, JpaBookRepository bookRepo) {
        LoanEntity entity = new LoanEntity();
        if (loan.getId() != null) {
            entity.setId(Long.valueOf(loan.getId()));
        }
        entity.setUser(userRepo.findById(loan.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + loan.getUser().getId())));
        entity.setBook(bookRepo.findById(loan.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + loan.getBook().getId())));
        entity.setLoanDate(loan.getLoanDate());
        entity.setReturnDate(loan.getReturnDate());
        entity.setStatus(loan.getStatus());
        return entity;
    }
}