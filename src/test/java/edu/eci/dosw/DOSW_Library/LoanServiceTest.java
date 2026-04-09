package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.tdd.DoswLibraryApplication;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reto #6 — Required functional tests for LoanService
 *
 * These tests cover the 5 scenarios required by the exercise:
 *   1. Given 1 loan exists → query by service → success (validate id)
 *   2. Given no loans      → query by service → returns empty
 *   3. Given no loans      → create via service → creation successful
 *   4. Given 1 loan exists → delete via service → deletion successful
 *   5. Given 1 loan exists → delete then query → returns empty
 */
@SpringBootTest(classes = DoswLibraryApplication.class)
@ActiveProfiles({"relational", "test"})
class LoanServiceTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // Shared test fixtures
    private static final String USER_ID       = "test-user-01";
    private static final String USERNAME      = "testuser";
    private static final String BOOK_ID       = "test-book-01";

    @BeforeEach
    void setUp() {
        // Clean state before every test
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create a standard user
        UserEntity user = new UserEntity();
        user.setId(USER_ID);
        user.setName("Test User");
        user.setUsername(USERNAME);
        user.setPassword("pass");
        user.setRole(Role.USER);
        userRepository.save(user);

        // Create a book with 3 available copies
        BookEntity book = new BookEntity();
        book.setId(BOOK_ID);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setTotalCopies(3);
        book.setAvailableCopies(3);
        bookRepository.save(book);
    }

    /**
     * Test 1
     * Dado que tengo 1 reserva registrada,
     * Cuando lo consulto a nivel de servicio,
     * entonces la consulta será exitosa validando el campo id.
     */
    @Test
    void givenOneLoanRegistered_whenQueryByService_thenSuccessValidatingId() {
        // Given — create a loan
        Loan created = loanService.loanBook(USERNAME, USER_ID, BOOK_ID);
        assertNotNull(created.getId(), "Loan must have an id after creation");

        // When — fetch all loans for the user
        List<Loan> loans = loanService.getLoansByUser(USERNAME, USER_ID);

        // Then — exactly one loan and the id matches
        assertEquals(1, loans.size());
        assertEquals(created.getId(), loans.get(0).getId());
    }

    /**
     * Test 2
     * Dado que no hay ninguna reserva registrada,
     * Cuando la consultó a nivel de servicio,
     * Entonces la consulta no retorna ningún resultado.
     */
    @Test
    void givenNoLoansRegistered_whenQueryByService_thenReturnsEmpty() {
        // Given — no loans (setUp already cleared them)

        // When
        List<Loan> loans = loanService.getLoansByUser(USERNAME, USER_ID);

        // Then
        assertTrue(loans.isEmpty(), "Expected no loans but got: " + loans.size());
    }

    /**
     * Test 3
     * Dado que no hay ninguna reserva registrada,
     * Cuando lo creo a nivel de servicio,
     * entonces la creación será exitosa.
     */
    @Test
    void givenNoLoansRegistered_whenCreateByService_thenCreationSuccessful() {
        // Given — no pre-existing loans

        // When
        Loan loan = loanService.loanBook(USERNAME, USER_ID, BOOK_ID);

        // Then — loan was persisted with a valid id
        assertNotNull(loan, "Returned loan must not be null");
        assertNotNull(loan.getId(), "Loan id must be set after creation");
        assertEquals(USER_ID, loan.getUser().getId());
        assertEquals(BOOK_ID, loan.getBook().getId());
    }

    /**
     * Test 4
     * Dado que tengo 1 reserva registrada,
     * Cuando la elimino a nivel de servicio,
     * entonces la eliminación será exitosa.
     *
     * NOTE: The domain uses returnBook (Status.RETURNED) as the "delete" operation.
     * After a return the loan is marked RETURNED and no longer shows as active.
     */
    @Test
    void givenOneLoanRegistered_whenDeleteByService_thenDeletionSuccessful() {
        // Given
        loanService.loanBook(USERNAME, USER_ID, BOOK_ID);

        // When — "delete" = return the book
        Loan returned = loanService.returnBook(USERNAME, USER_ID, BOOK_ID);

        // Then — operation completed without exception and loan has RETURNED status
        assertNotNull(returned, "Returned loan object must not be null");
        assertEquals(
                edu.eci.dosw.tdd.core.model.Status.RETURNED,
                returned.getStatus(),
                "Loan status should be RETURNED after deletion"
        );
    }

    /**
     * Test 5
     * Dado que tengo 1 reserva registrada,
     * Cuando la elimino y consulto a nivel de servicio,
     * entonces el resultado de la consulta no retorna ningún resultado.
     *
     * NOTE: After returning a book, getLoansByUser returns only ACTIVE loans for the user.
     * We verify by querying all loans and confirming none are ACTIVE.
     */
    @Test
    void givenOneLoanRegistered_whenDeleteThenQuery_thenResultReturnsNoActiveLoans() {
        // Given
        loanService.loanBook(USERNAME, USER_ID, BOOK_ID);

        // When
        loanService.returnBook(USERNAME, USER_ID, BOOK_ID);
        List<Loan> activeLoans = loanService.getLoansByUser(USERNAME, USER_ID)
                .stream()
                .filter(l -> l.getStatus() == edu.eci.dosw.tdd.core.model.Status.ACTIVE)
                .toList();

        // Then — no active loans remain
        assertTrue(activeLoans.isEmpty(),
                "After returning the book, there should be no active loans");
    }
}
