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
import edu.eci.dosw.tdd.core.exception.ForbiddenOperationException;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.persistence.dao.BookEntity;
import edu.eci.dosw.tdd.persistence.dao.LoanEntity;
import edu.eci.dosw.tdd.persistence.dao.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = DoswLibraryApplication.class)
@ActiveProfiles("test")
class ManagementRulesTest {

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
    void librarianCanCreateAndUpdateBook() {
        BookCreateDTO create = new BookCreateDTO("b3", "Patterns", "GoF", 4, 4);
        BookDTO created = bookController.createBook("u2", create);

        Assertions.assertEquals("b3", created.id());
        Assertions.assertEquals(4, created.totalCopies());
        Assertions.assertEquals(4, created.availableCopies());

        BookStockUpdateDTO update = new BookStockUpdateDTO(6, 5);
        BookDTO updated = bookController.updateStock("b3", "u2", update);
        Assertions.assertEquals(6, updated.totalCopies());
        Assertions.assertEquals(5, updated.availableCopies());
    }

    @Test
    void nonLibrarianCannotManageBooks() {
        BookCreateDTO create = new BookCreateDTO("b4", "Refactoring", "Fowler", 3, 3);
        Assertions.assertThrows(ForbiddenOperationException.class, () -> bookController.createBook("u1", create));
    }

    @Test
    void invalidBookStockShouldFail() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookController.createBook("u2", new BookCreateDTO("b4", "T", "A", 0, 0)));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookController.createBook("u2", new BookCreateDTO("b5", "T", "A", 2, 3)));
    }

    @Test
    void userRegistrationAndLibrarianCreationShouldWork() {
        UserDTO registered = userController.registerUser(new UserCreateDTO("u3", "Cam", "cam", "cam123", null));
        Assertions.assertEquals("USER", registered.role());

        UserDTO created = userController.createUserByLibrarian(
                "u2",
                new UserCreateDTO("u4", "Mia", "mia", "mia123", "LIBRARIAN")
        );
        Assertions.assertEquals("LIBRARIAN", created.role());

        Assertions.assertThrows(ForbiddenOperationException.class, () ->
                userController.createUserByLibrarian("u1", new UserCreateDTO("u5", "No", "no", "no123", "USER")));
    }

    @Test
    void userQueriesShouldRespectPermissions() {
        UserDTO own = userController.getUserById("u1", "u1");
        Assertions.assertEquals("u1", own.id());

        Assertions.assertThrows(ForbiddenOperationException.class, () -> userController.getUsers("u1"));
        Assertions.assertThrows(ForbiddenOperationException.class, () -> userController.getUserById("u2", "u1"));

        List<UserDTO> asLibrarian = userController.getUsers("u2");
        Assertions.assertEquals(2, asLibrarian.size());
    }

    @Test
    void loanAccessRulesShouldBeApplied() {
        Assertions.assertThrows(ForbiddenOperationException.class,
                () -> loanController.borrowBook(new LoanDTO("u2", "b1", null, null, null), "u1"));

        LoanDTO active = loanController.borrowBook(new LoanDTO("u1", "b1", null, null, null), null);
        Assertions.assertEquals("ACTIVE", active.status());

        Assertions.assertThrows(ForbiddenOperationException.class, () -> loanController.getLoans("u1"));

        List<LoanDTO> ownLoans = loanController.getLoansByUser("u1", "u1");
        Assertions.assertEquals(1, ownLoans.size());

        LoanDTO returned = loanController.returnBook(new LoanDTO("u1", "b1", null, null, null), null);
        Assertions.assertEquals("RETURNED", returned.status());
        Assertions.assertEquals(2, bookRepository.findById("b1").orElseThrow().getAvailableCopies());
    }

    @Test
    void invalidRoleAndDuplicateUsernameShouldFail() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userController.createUserByLibrarian("u2", new UserCreateDTO("u6", "Bad", "bad", "bad123", "ADMIN")));

        userController.registerUser(new UserCreateDTO("u7", "Al", "alex", "123", null));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userController.registerUser(new UserCreateDTO("u8", "Al2", "alex", "456", null)));
    }

    @Test
    void returningWithoutActiveLoanShouldFail() {
        LoanEntity returnedLoan = new LoanEntity();
        returnedLoan.setUser(userRepository.findById("u1").orElseThrow());
        returnedLoan.setBook(bookRepository.findById("b1").orElseThrow());
        returnedLoan.setLoanDate(LocalDate.now().minusDays(3));
        returnedLoan.setReturnDate(LocalDate.now().minusDays(1));
        returnedLoan.setStatus(Status.RETURNED);
        loanRepository.save(returnedLoan);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> loanController.returnBook(new LoanDTO("u1", "b1", null, null, null), null));
    }
}

