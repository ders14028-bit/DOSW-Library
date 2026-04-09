package edu.eci.dosw.tdd.persistence.relational.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.mapper.LoanEntityMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("relational")
public class LoanRepositoryJpaImpl implements LoanRepository {

    private final JpaLoanRepository repository;
    private final JpaBookRepository bookRepository;
    private final JpaUserRepository userRepository;

    public LoanRepositoryJpaImpl(JpaLoanRepository repository,
                                 JpaBookRepository bookRepository,
                                 JpaUserRepository userRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Loan save(Loan loan) {
        return LoanEntityMapper.toDomain(
                repository.save(LoanEntityMapper.toEntity(loan, userRepository, bookRepository))
        );
    }

    @Override
    public Optional<Loan> findById(String id) {
        return repository.findById(Long.valueOf(id)).map(LoanEntityMapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAllByOrderByLoanDateDesc().stream().map(LoanEntityMapper::toDomain).toList();
    }

    @Override
    public void delete(String id) {
        repository.deleteById(Long.valueOf(id));
    }

    @Override
    public long countByUserIdAndStatus(String userId, Status status) {
        return repository.countByUser_IdAndStatus(userId, status);
    }

    @Override
    public long countByBookIdAndStatus(String bookId, Status status) {
        return repository.countByBook_IdAndStatus(bookId, status);
    }

    @Override
    public Optional<Loan> findFirstByUserIdAndBookIdAndStatus(String userId, String bookId, Status status) {
        return repository.findFirstByUser_IdAndBook_IdAndStatusOrderByLoanDateAsc(userId, bookId, status)
                .map(LoanEntityMapper::toDomain);
    }

    @Override
    public List<Loan> findAllOrderByLoanDateDesc() {
        return repository.findAllByOrderByLoanDateDesc().stream().map(LoanEntityMapper::toDomain).toList();
    }

    @Override
    public List<Loan> findAllByUserIdOrderByLoanDateDesc(String userId) {
        return repository.findAllByUser_IdOrderByLoanDateDesc(userId).stream().map(LoanEntityMapper::toDomain).toList();
    }
}
