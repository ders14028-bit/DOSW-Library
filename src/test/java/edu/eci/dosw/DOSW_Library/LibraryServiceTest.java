package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryServiceTest {

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        bookService = new BookService();
        loanService = new LoanService(userService, bookService);
    }

    @Test
    void shouldReturnSeededUsersAndInventory() {
        assertEquals(2, userService.getUsers().size());
        assertEquals(2, bookService.getInventory().get("b1"));
        assertEquals(1, bookService.getInventory().get("b2"));
    }

    @Test
    void shouldLoanBookSuccessfully() {
        Loan loan = loanService.loanBook("u1", "b1");

        assertEquals(Status.ACTIVE, loan.getStatus());
        assertEquals("u1", loan.getUser().getId());
        assertEquals("b1", loan.getBook().getId());
        assertEquals(1, bookService.getInventory().get("b1"));
    }

    @Test
    void shouldFailWhenBookIsNotAvailable() {
        loanService.loanBook("u1", "b2");

        assertThrows(BookNotAvailableException.class, () -> loanService.loanBook("u2", "b2"));
    }

    @Test
    void shouldReturnEmptyLoansAtStart() {
        assertTrue(loanService.getLoans().isEmpty());
    }

    @Test
    void shouldReturnBookSuccessfully() {
        loanService.loanBook("u1", "b1");

        Loan returnedLoan = loanService.returnBook("u1", "b1");

        assertEquals(Status.RETURNED, returnedLoan.getStatus());
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(2, bookService.getInventory().get("b1"));
    }

    @Test
    void shouldFailWhenUserExceedsActiveLoanLimit() {
        loanService.loanBook("u1", "b1");
        loanService.returnBook("u1", "b1");
        loanService.loanBook("u1", "b1");
        loanService.loanBook("u1", "b2");
        loanService.loanBook("u1", "b1");

        assertThrows(LoanLimitExceededException.class, () -> loanService.loanBook("u1", "b1"));
    }

    @Test
    void shouldFailWhenReturningWithoutActiveLoan() {
        assertThrows(IllegalArgumentException.class, () -> loanService.returnBook("u1", "b1"));
    }
}
