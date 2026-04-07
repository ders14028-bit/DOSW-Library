package edu.eci.dosw.tdd.core.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(String id);

    List<Loan> findAll();

    void delete(String id);

    long countByUserIdAndStatus(String userId, Status status);

    long countByBookIdAndStatus(String bookId, Status status);

    Optional<Loan> findFirstByUserIdAndBookIdAndStatus(String userId, String bookId, Status status);

    List<Loan> findAllOrderByLoanDateDesc();

    List<Loan> findAllByUserIdOrderByLoanDateDesc(String userId);
}