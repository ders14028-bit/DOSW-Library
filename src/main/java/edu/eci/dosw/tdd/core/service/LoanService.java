package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private static final int MAX_ACTIVE_LOANS = 3;

    private final List<Loan> loans = new ArrayList<>();
    private final UserService userService;
    private final BookService bookService;
    private final LoanValidator loanValidator;

    public LoanService(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanValidator = new LoanValidator();
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public Loan loanBook(String userId, String bookId) {
        loanValidator.validateLoanRequest(userId, bookId);

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        long activeLoansByUser = loans.stream()
                .filter(loan -> loan.getStatus() == Status.ACTIVE)
                .filter(loan -> loan.getUser().getId().equals(userId))
                .count();
        if (activeLoansByUser >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(userId, MAX_ACTIVE_LOANS);
        }

        int availableCopies = bookService.getAvailableCopies(bookId);
        if (availableCopies <= 0) {
            throw new BookNotAvailableException(bookId);
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(DateUtil.today());
        loan.setStatus(Status.ACTIVE);

        loans.add(loan);
        bookService.decrementInventory(bookId);
        return loan;
    }

    public Loan returnBook(String userId, String bookId) {
        loanValidator.validateLoanRequest(userId, bookId);
        userService.getUserById(userId);
        bookService.getBookById(bookId);

        Optional<Loan> activeLoan = loans.stream()
                .filter(loan -> loan.getStatus() == Status.ACTIVE)
                .filter(loan -> loan.getUser().getId().equals(userId))
                .filter(loan -> loan.getBook().getId().equals(bookId))
                .findFirst();

        Loan loan = activeLoan.orElseThrow(() ->
                new IllegalArgumentException("No existe un prestamo activo para el usuario y libro indicados."));

        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(DateUtil.today());
        bookService.incrementInventory(bookId);
        return loan;
    }
}


