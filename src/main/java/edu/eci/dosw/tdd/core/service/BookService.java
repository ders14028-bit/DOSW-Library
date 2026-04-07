package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.persistence.relational.dao.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.BookEntityMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookValidator bookValidator = new BookValidator();

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> getInventory() {
        return bookRepository.findAll().stream().map(BookEntityMapper::toDomain).toList();
    }

    public Book getBookById(String bookId) {
        String validBookId = bookValidator.validateBookId(bookId);
        return bookRepository.findById(validBookId)
                .map(BookEntityMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + validBookId));
    }

    public Book createBook(String id, String title, String author, Integer totalCopies, Integer availableCopies) {
        validateBookStock(totalCopies, availableCopies);

        String validBookId = bookValidator.validateBookId(id);
        if (bookRepository.existsById(validBookId)) {
            throw new IllegalArgumentException("Ya existe un libro con ID: " + validBookId);
        }

        BookEntity entity = new BookEntity();
        entity.setId(validBookId);
        entity.setTitle(requireText(title, "title"));
        entity.setAuthor(requireText(author, "author"));
        entity.setTotalCopies(totalCopies);
        entity.setAvailableCopies(availableCopies);

        return BookEntityMapper.toDomain(bookRepository.save(entity));
    }

    public Book updateBookStock(String bookId, Integer totalCopies, Integer availableCopies) {
        validateBookStock(totalCopies, availableCopies);

        String validBookId = bookValidator.validateBookId(bookId);
        BookEntity entity = bookRepository.findById(validBookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + validBookId));

        entity.setTotalCopies(totalCopies);
        entity.setAvailableCopies(availableCopies);
        return BookEntityMapper.toDomain(bookRepository.save(entity));
    }

    public void decrementInventory(String bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + bookId));
        if (entity.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(bookId);
        }
        entity.setAvailableCopies(entity.getAvailableCopies() - 1);
        bookRepository.save(entity);
    }

    public void incrementInventory(String bookId) {
        BookEntity entity = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro libro con ID: " + bookId));
        if (entity.getAvailableCopies() >= entity.getTotalCopies()) {
            throw new IllegalArgumentException("La disponibilidad no puede superar el stock total del libro.");
        }
        entity.setAvailableCopies(entity.getAvailableCopies() + 1);
        bookRepository.save(entity);
    }

    private void validateBookStock(Integer totalCopies, Integer availableCopies) {
        if (totalCopies == null || totalCopies <= 0) {
            throw new IllegalArgumentException("La cantidad total de ejemplares debe ser mayor a 0.");
        }
        if (availableCopies == null || availableCopies < 0) {
            throw new IllegalArgumentException("La cantidad de ejemplares disponibles no puede ser menor a 0.");
        }
        if (availableCopies > totalCopies) {
            throw new IllegalArgumentException("La disponibilidad no puede ser mayor al stock total.");
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio.");
        }
        return value;
    }
}
