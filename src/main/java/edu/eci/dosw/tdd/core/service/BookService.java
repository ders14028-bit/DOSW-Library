package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class BookService {

    private final Map<String, Book> booksById = new LinkedHashMap<>();
    private final Map<String, Integer> inventoryByBookId = new LinkedHashMap<>();
    private final BookValidator bookValidator = new BookValidator();

    public BookService() {
        Book book1 = new Book();
        book1.setId("b1");
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert C. Martin");
        booksById.put(book1.getId(), book1);
        inventoryByBookId.put(book1.getId(), 2);

        Book book2 = new Book();
        book2.setId("b2");
        book2.setTitle("Domain-Driven Design");
        book2.setAuthor("Eric Evans");
        booksById.put(book2.getId(), book2);
        inventoryByBookId.put(book2.getId(), 1);
    }

    public Map<String, Integer> getInventory() {
        return Collections.unmodifiableMap(inventoryByBookId);
    }

    public Book getBookById(String bookId) {
        String validBookId = bookValidator.validateBookId(bookId);
        Book book = booksById.get(validBookId);
        if (book == null) {
            throw new IllegalArgumentException("No se encontro libro con ID: " + validBookId);
        }
        return book;
    }

    public int getAvailableCopies(String bookId) {
        return inventoryByBookId.get(bookId);
    }

    public void decrementInventory(String bookId) {
        inventoryByBookId.put(bookId, inventoryByBookId.get(bookId) - 1);
    }

    public void incrementInventory(String bookId) {
        inventoryByBookId.put(bookId, inventoryByBookId.get(bookId) + 1);
    }
}

