package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.tdd.core.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoLoanRepository extends MongoRepository<LoanDocument, String> {

    long countByUserIdAndStatus(String userId, Status status);

    long countByBookIdAndStatus(String bookId, Status status);

    Optional<LoanDocument> findFirstByUserIdAndBookIdAndStatusOrderByLoanDateAsc(
            String userId,
            String bookId,
            Status status
    );

    List<LoanDocument> findAllByOrderByLoanDateDesc();

    List<LoanDocument> findAllByUserIdOrderByLoanDateDesc(String userId);
}
