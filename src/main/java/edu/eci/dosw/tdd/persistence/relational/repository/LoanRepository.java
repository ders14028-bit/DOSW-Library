package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.relational.dao.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    long countByUser_IdAndStatus(String userId, Status status);

    long countByBook_IdAndStatus(String bookId, Status status);

    Optional<LoanEntity> findFirstByUser_IdAndBook_IdAndStatusOrderByLoanDateAsc(
            String userId,
            String bookId,
            Status status
    );

    List<LoanEntity> findAllByOrderByLoanDateDesc();

    List<LoanEntity> findAllByUser_IdOrderByLoanDateDesc(String userId);
}
