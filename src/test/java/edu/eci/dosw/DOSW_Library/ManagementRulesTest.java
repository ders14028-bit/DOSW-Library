package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.tdd.DoswLibraryApplication;
import edu.eci.dosw.tdd.controller.BookController;
import edu.eci.dosw.tdd.controller.LoanController;
import edu.eci.dosw.tdd.controller.UserController;
import edu.eci.dosw.tdd.controller.dto.BookCreateDTO;
import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.BookStockUpdateDTO;
import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.dto.UserCreateDTO;
import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
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
@ActiveProfiles({"relational", "test"})
class ManagementRulesTest {

    private static final UserDetails ANA = User.withUsername("ana").password("n/a").roles("USER").build();
    private static final UserDetails LUIS = User.withUsername("luis").password("n/a").roles("LIBRARIAN").build();

    @Autowired
    private UserController userController;

    @Autowired
    private BookController bookController;

    @Autowired
    private LoanController loanController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @BeforeEach
    void setup() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setId("u1");
        user.setName("Ana");
        user.setUsername("ana");
        user.setPassword("ana123");
        user.setRole(Role.USER);
        userRepository.save(user);

        UserEntity librarian = new UserEntity();
        librarian.setId("u2");
        librarian.setName("Luis");
        librarian.setUsername("luis");
        librarian.setPassword("luis123");
        librarian.setRole(Role.LIBRARIAN);
        userRepository.save(librarian);

        BookEntity book = new BookEntity();
        book.setId("b1");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setTotalCopies(2);
        book.setAvailableCopies(2);
        bookRepository.save(book);
    }

    @Test
    @WithMockUser(username = "luis", roles = {"LIBRARIAN"})
    void librarianCanCreateAndUpdateBook() {
        BookCreateDTO create = new BookCreateDTO("b3", "Patterns", "GoF", 4, 4);
        BookDTO created = bookController.createBook(create);

        Assertions.assertEquals("b3", created.id());
        Assertions.assertEquals(4, created.totalCopies());
        Assertions.assertEquals(4, created.availableCopies());

        BookStockUpdateDTO update = new BookStockUpdateDTO(6, 5);
        BookDTO updated = bookController.updateStock("b3", update);
        Assertions.assertEquals(6, updated.totalCopies());
        Assertions.assertEquals(5, updated.availableCopies());
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void nonLibrarianCannotManageBooks() {
        BookCreateDTO create = new BookCreateDTO("b4", "Refactoring", "Fowler", 3, 3);
        Assertions.assertThrows(Exception.class, () -> bookController.createBook(create));
    }

    @Test
    @WithMockUser(username = "luis", roles = {"LIBRARIAN"})
    void invalidBookStockShouldFail() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookController.createBook(new BookCreateDTO("b4", "T", "A", 0, 0)));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookController.createBook(new BookCreateDTO("b5", "T", "A", 2, 3)));
    }

    @Test
    @WithMockUser(username = "luis", roles = {"LIBRARIAN"})
    void userRegistrationAndLibrarianCreationShouldWork() {
        UserDTO registered = userController.registerUser(new UserCreateDTO("u3", "Cam", "cam", "cam123", null));
        Assertions.assertEquals("USER", registered.role());

        UserDTO created = userController.createUserByLibrarian(
                new UserCreateDTO("u4", "Mia", "mia", "mia123", "LIBRARIAN")
        );
        Assertions.assertEquals("LIBRARIAN", created.role());
    }

    @Test
    @WithMockUser(username = "luis", roles = {"LIBRARIAN"})
    void userQueriesShouldRespectPermissions() {
        UserDTO own = userController.getUserById("u1", LUIS);
        Assertions.assertEquals("u1", own.id());

        List<UserDTO> asLibrarian = userController.getUsers();
        Assertions.assertEquals(2, asLibrarian.size());
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void loanAccessRulesShouldBeApplied() {
        LoanDTO active = loanController.borrowBook(new LoanDTO("u1", "b1", null, null, null), ANA);
        Assertions.assertEquals("ACTIVE", active.status());

        List<LoanDTO> ownLoans = loanController.getLoansByUser("u1", ANA);
        Assertions.assertEquals(1, ownLoans.size());

        LoanDTO returned = loanController.returnBook(new LoanDTO("u1", "b1", null, null, null), ANA);
        Assertions.assertEquals("RETURNED", returned.status());
        Assertions.assertEquals(2, bookRepository.findById("b1").orElseThrow().getAvailableCopies());
    }

    @Test
    @WithMockUser(username = "luis", roles = {"LIBRARIAN"})
    void invalidRoleAndDuplicateUsernameShouldFail() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userController.createUserByLibrarian(new UserCreateDTO("u6", "Bad", "bad", "bad123", "ADMIN")));

        userController.registerUser(new UserCreateDTO("u7", "Al", "alex", "123", null));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userController.registerUser(new UserCreateDTO("u8", "Al2", "alex", "456", null)));
    }

    @Test
    @WithMockUser(username = "ana", roles = {"USER"})
    void returningWithoutActiveLoanShouldFail() {
        LoanEntity returnedLoan = new LoanEntity();
        returnedLoan.setUser(userRepository.findById("u1").orElseThrow());
        returnedLoan.setBook(bookRepository.findById("b1").orElseThrow());
        returnedLoan.setLoanDate(LocalDate.now().minusDays(3));
        returnedLoan.setReturnDate(LocalDate.now().minusDays(1));
        returnedLoan.setStatus(Status.RETURNED);
        loanRepository.save(returnedLoan);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> loanController.returnBook(new LoanDTO("u1", "b1", null, null, null), ANA));
    }
}