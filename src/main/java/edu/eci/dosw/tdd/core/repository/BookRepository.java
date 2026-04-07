package edu.eci.dosw.tdd.core.repository;

import edu.eci.dosw.tdd.core.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(String id);

    List<Book> findAll();

    boolean existsById(String id);

    void deleteById(String id);
}
