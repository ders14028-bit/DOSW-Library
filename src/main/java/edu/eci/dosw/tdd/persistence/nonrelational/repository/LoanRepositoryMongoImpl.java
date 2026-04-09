package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.LoanDocumentMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class LoanRepositoryMongoImpl implements LoanRepository {

    private final MongoLoanRepository repository;

    public LoanRepositoryMongoImpl(MongoLoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return LoanDocumentMapper.toDomain(repository.save(LoanDocumentMapper.toDocument(loan)));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return repository.findById(id).map(LoanDocumentMapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAllByOrderByLoanDateDesc().stream().map(LoanDocumentMapper::toDomain).toList();
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public long countByUserIdAndStatus(String userId, Status status) {
        return repository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public long countByBookIdAndStatus(String bookId, Status status) {
        return repository.countByBookIdAndStatus(bookId, status);
    }

    @Override
    public Optional<Loan> findFirstByUserIdAndBookIdAndStatus(String userId, String bookId, Status status) {
        return repository.findFirstByUserIdAndBookIdAndStatusOrderByLoanDateAsc(userId, bookId, status)
                .map(LoanDocumentMapper::toDomain);
    }

    @Override
    public List<Loan> findAllOrderByLoanDateDesc() {
        return repository.findAllByOrderByLoanDateDesc().stream().map(LoanDocumentMapper::toDomain).toList();
    }

    @Override
    public List<Loan> findAllByUserIdOrderByLoanDateDesc(String userId) {
        return repository.findAllByUserIdOrderByLoanDateDesc(userId).stream().map(LoanDocumentMapper::toDomain).toList();
    }
}
