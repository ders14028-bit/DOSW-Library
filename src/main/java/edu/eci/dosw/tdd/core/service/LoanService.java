package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.ForbiddenOperationException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.BookRepository;
import edu.eci.dosw.tdd.core.repository.LoanRepository;
import edu.eci.dosw.tdd.core.repository.UserRepository;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
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
        return loanRepository.findAllOrderByLoanDateDesc();
    }

    public List<Loan> getLoansByUser(String actorUsername, String userId) {
        validateAccess(actorUsername, userId);
        return loanRepository.findAllByUserIdOrderByLoanDateDesc(userId);
    }

    public Loan loanBook(String actorUsername, String userId, String bookId) {
        loanValidator.validateLoanRequest(userId, bookId);
        validateAccess(actorUsername, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario con ID: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + bookId));

        long activeLoansByUser = loanRepository.countByUserIdAndStatus(userId, Status.ACTIVE);
        if (activeLoansByUser >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(userId, MAX_ACTIVE_LOANS);
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(bookId);
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(DateUtil.today());
        loan.setStatus(Status.ACTIVE);

        Loan saved = loanRepository.save(loan);
        bookService.decrementInventory(bookId);
        return saved;
    }

    public Loan returnBook(String actorUsername, String userId, String bookId) {
        loanValidator.validateLoanRequest(userId, bookId);
        validateAccess(actorUsername, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario con ID: " + userId));
        bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + bookId));

        Loan loan = loanRepository.findFirstByUserIdAndBookIdAndStatus(userId, bookId, Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un prestamo activo para el usuario y libro indicados."));

        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(DateUtil.today());

        Loan saved = loanRepository.save(loan);
        bookService.incrementInventory(bookId);
        return saved;
    }

    private void validateAccess(String actorUsername, String userId) {
        User actor = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario: " + actorUsername));
        if (!actor.getId().equals(userId) && actor.getRole() != Role.LIBRARIAN) {
            throw new ForbiddenOperationException("Solo puede gestionar sus propios prestamos.");
        }
    }
}