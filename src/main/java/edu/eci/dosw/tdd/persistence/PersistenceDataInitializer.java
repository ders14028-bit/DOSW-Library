package edu.eci.dosw.tdd.persistence;

import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PersistenceDataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PersistenceDataInitializer(
            BookRepository bookRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            BookEntity book1 = new BookEntity();
            book1.setId("b1");
            book1.setTitle("Clean Code");
            book1.setAuthor("Robert C. Martin");
            book1.setTotalCopies(2);
            book1.setAvailableCopies(2);

            BookEntity book2 = new BookEntity();
            book2.setId("b2");
            book2.setTitle("Domain-Driven Design");
            book2.setAuthor("Eric Evans");
            book2.setTotalCopies(1);
            book2.setAvailableCopies(1);

            bookRepository.save(book1);
            bookRepository.save(book2);
        }

        if (userRepository.count() == 0) {
            UserEntity user1 = new UserEntity();
            user1.setId("u1");
            user1.setName("Ana");
            user1.setUsername("ana");
            user1.setPassword(passwordEncoder.encode("ana123"));
            user1.setRole(Role.USER);

            UserEntity user2 = new UserEntity();
            user2.setId("u2");
            user2.setName("Luis");
            user2.setUsername("luis");
            user2.setPassword(passwordEncoder.encode("luis123"));
            user2.setRole(Role.LIBRARIAN);

            userRepository.save(user1);
            userRepository.save(user2);
        }

        // Backward compatibility for existing environments where users were saved with plain text password.
        userRepository.findAll().forEach(user -> {
            String currentPassword = user.getPassword();
            if (currentPassword != null && !isBcryptHash(currentPassword)) {
                user.setPassword(passwordEncoder.encode(currentPassword));
                userRepository.save(user);
            }
        });
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}

