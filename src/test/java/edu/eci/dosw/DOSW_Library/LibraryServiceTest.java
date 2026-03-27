package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.tdd.DoswLibraryApplication;
import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.persistence.dao.BookEntity;
import edu.eci.dosw.tdd.persistence.dao.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = DoswLibraryApplication.class)
@ActiveProfiles("test")
class LibraryServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user1 = new UserEntity();
        user1.setId("u1");
        user1.setName("Ana");
        user1.setUsername("ana");
        user1.setPassword("ana123");
        user1.setRole(Role.USER);
        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setId("u2");
        user2.setName("Luis");
        user2.setUsername("luis");
        user2.setPassword("luis123");
        user2.setRole(Role.LIBRARIAN);
        userRepository.save(user2);

        BookEntity book1 = new BookEntity();
        book1.setId("b1");
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert C. Martin");
        book1.setTotalCopies(2);
        book1.setAvailableCopies(2);
        bookRepository.save(book1);

        BookEntity book2 = new BookEntity();
        book2.setId("b2");
        book2.setTitle("Domain-Driven Design");
        book2.setAuthor("Eric Evans");
        book2.setTotalCopies(1);
        book2.setAvailableCopies(1);
        bookRepository.save(book2);
    }

    @Test
    void shouldReturnSeededUsersAndInventory() {
        assertEquals(2, userService.getUsers().size());
        assertEquals(2, bookService.getBookById("b1").getAvailableCopies());
        assertEquals(1, bookService.getBookById("b2").getAvailableCopies());
    }

    @Test
    void shouldLoanBookSuccessfully() {
        Loan loan = loanService.loanBook("ana", "u1", "b1");

        assertEquals(Status.ACTIVE, loan.getStatus());
        assertEquals("u1", loan.getUser().getId());
        assertEquals("b1", loan.getBook().getId());
        assertEquals(1, bookService.getBookById("b1").getAvailableCopies());
    }

    @Test
    void shouldFailWhenBookIsNotAvailable() {
        loanService.loanBook("ana", "u1", "b2");

        assertThrows(BookNotAvailableException.class, () -> loanService.loanBook("luis", "u2", "b2"));
    }

    @Test
    void shouldReturnEmptyLoansAtStart() {
        assertTrue(loanService.getLoansByUser("ana", "u1").isEmpty());
    }

    @Test
    void shouldReturnBookSuccessfully() {
        loanService.loanBook("ana", "u1", "b1");

        Loan returnedLoan = loanService.returnBook("ana", "u1", "b1");

        assertEquals(Status.RETURNED, returnedLoan.getStatus());
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(2, bookService.getBookById("b1").getAvailableCopies());
    }

    @Test
    void shouldFailWhenUserExceedsActiveLoanLimit() {
        loanService.loanBook("ana", "u1", "b1");
        loanService.returnBook("ana", "u1", "b1");
        loanService.loanBook("ana", "u1", "b1");
        loanService.loanBook("ana", "u1", "b2");
        loanService.loanBook("ana", "u1", "b1");

        assertThrows(LoanLimitExceededException.class, () -> loanService.loanBook("ana", "u1", "b1"));
    }

    @Test
    void shouldFailWhenReturningWithoutActiveLoan() {
        assertThrows(IllegalArgumentException.class, () -> loanService.returnBook("ana", "u1", "b1"));
    }
}
