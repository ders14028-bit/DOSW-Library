package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.tdd.DoswLibraryApplication;
import edu.eci.dosw.tdd.controller.BookController;
import edu.eci.dosw.tdd.controller.LoanController;
import edu.eci.dosw.tdd.controller.UserController;
import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.relational.dao.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.dao.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.dao.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = DoswLibraryApplication.class)
@ActiveProfiles("test")
class LibraryApiFunctionalTest {

    private static final UserDetails ANA = User.withUsername("ana").password("n/a").roles("USER").build();
    private static final UserDetails LUIS = User.withUsername("luis").password("n/a").roles("LIBRARIAN").build();

    @Autowired
    private UserController userController;

    @Autowired
    private BookController bookController;

    @Autowired
    private LoanController loanController;

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
    @WithMockUser(username = "luis", roles = {"LIBRARIAN"})
    void shouldGetUsersFromDatabase() {
        List<UserDTO> users = userController.getUsers();

        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals("u1", users.get(0).id());
        Assertions.assertEquals("USER", users.get(0).role());
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void shouldGetInventoryFromDatabase() {
        List<BookDTO> inventory = bookController.getInventory();

        Assertions.assertEquals(2, inventory.size());
        Assertions.assertEquals("b1", inventory.get(0).id());
        Assertions.assertEquals(2, inventory.get(0).totalCopies());
        Assertions.assertEquals(2, inventory.get(0).availableCopies());
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void shouldBorrowBookAndPersistLoan() {
        LoanDTO response = loanController.borrowBook(new LoanDTO("u1", "b1", null, null, null), ANA);

        Assertions.assertEquals("ACTIVE", response.status());
        Assertions.assertEquals("u1", response.userId());
        Assertions.assertEquals("b1", response.bookId());

        LoanEntity saved = loanRepository.findAll().getFirst();
        Assertions.assertEquals(Status.ACTIVE, saved.getStatus());
        Assertions.assertEquals("u1", saved.getUser().getId());
        Assertions.assertEquals("b1", saved.getBook().getId());
        Assertions.assertEquals(1, bookRepository.findById("b1").orElseThrow().getAvailableCopies());
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void shouldGetLoansFromDatabase() {
        UserEntity user = userRepository.findById("u1").orElseThrow();
        BookEntity book = bookRepository.findById("b1").orElseThrow();

        LoanEntity loan = new LoanEntity();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(Status.ACTIVE);
        loanRepository.save(loan);

        List<LoanDTO> loans = loanController.getLoansByUser("u1", ANA);

        Assertions.assertEquals(1, loans.size());
        Assertions.assertEquals("u1", loans.get(0).userId());
        Assertions.assertEquals("b1", loans.get(0).bookId());
        Assertions.assertEquals("ACTIVE", loans.get(0).status());
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void shouldReturnBookAndUpdateLoanInDatabase() {
        UserEntity user = userRepository.findById("u1").orElseThrow();
        BookEntity book = bookRepository.findById("b1").orElseThrow();
        book.setAvailableCopies(1);
        bookRepository.save(book);

        LoanEntity loan = new LoanEntity();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now().minusDays(2));
        loan.setStatus(Status.ACTIVE);
        LoanEntity stored = loanRepository.save(loan);

        LoanDTO response = loanController.returnBook(new LoanDTO("u1", "b1", null, null, null), ANA);

        Assertions.assertEquals("RETURNED", response.status());
        Assertions.assertNotNull(response.returnDate());

        LoanEntity updated = loanRepository.findById(stored.getId()).orElseThrow();
        Assertions.assertEquals(Status.RETURNED, updated.getStatus());
        Assertions.assertNotNull(updated.getReturnDate());
        Assertions.assertEquals(2, bookRepository.findById("b1").orElseThrow().getAvailableCopies());
    }
}
