package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.ForbiddenOperationException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.LoanEntityMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService {

    private static final int MAX_ACTIVE_LOANS = 3;

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final LoanValidator loanValidator;

    public LoanService(
            LoanRepository loanRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            BookService bookService
    ) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.loanValidator = new LoanValidator();
    }

    public List<Loan> getLoans() {
        return loanRepository.findAllByOrderByLoanDateDesc().stream().map(LoanEntityMapper::toDomain).toList();
    }

    public List<Loan> getLoansByUser(String actorUsername, String userId) {
        validateAccess(actorUsername, userId);
        return loanRepository.findAllByUser_IdOrderByLoanDateDesc(userId).stream().map(LoanEntityMapper::toDomain).toList();
    }

    public Loan loanBook(String actorUsername, String userId, String bookId) {
        loanValidator.validateLoanRequest(userId, bookId);
        validateAccess(actorUsername, userId);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario con ID: " + userId));
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + bookId));

        long activeLoansByUser = loanRepository.countByUser_IdAndStatus(userId, Status.ACTIVE);
        if (activeLoansByUser >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(userId, MAX_ACTIVE_LOANS);
        }

        if (bookEntity.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(bookId);
        }

        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setUser(userEntity);
        loanEntity.setBook(bookEntity);
        loanEntity.setLoanDate(DateUtil.today());
        loanEntity.setStatus(Status.ACTIVE);

        LoanEntity saved = loanRepository.save(loanEntity);
        bookService.decrementInventory(bookId);
        return LoanEntityMapper.toDomain(saved);
    }

    public Loan returnBook(String actorUsername, String userId, String bookId) {
        loanValidator.validateLoanRequest(userId, bookId);
        validateAccess(actorUsername, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario con ID: " + userId));
        bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + bookId));

        LoanEntity loanEntity = loanRepository
                .findFirstByUser_IdAndBook_IdAndStatusOrderByLoanDateAsc(userId, bookId, Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un prestamo activo para el usuario y libro indicados."));

        loanEntity.setStatus(Status.RETURNED);
        loanEntity.setReturnDate(DateUtil.today());

        LoanEntity saved = loanRepository.save(loanEntity);
        bookService.incrementInventory(bookId);
        return LoanEntityMapper.toDomain(saved);
    }

    private void validateAccess(String actorUsername, String userId) {
        UserEntity actor = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario: " + actorUsername));
        if (!actor.getId().equals(userId) && actor.getRole() != Role.LIBRARIAN) {
            throw new ForbiddenOperationException("Solo puede gestionar sus propios prestamos.");
        }
    }

}
