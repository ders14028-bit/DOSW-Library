package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;

public final class BookEntityMapper {

    private BookEntityMapper() {}

    public static Book toDomain(BookEntity entity) {
        Book book = new Book();
        book.setId(entity.getId());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setTotalCopies(entity.getTotalCopies());
        book.setAvailableCopies(entity.getAvailableCopies());
        return book;
    }

    public static BookEntity toEntity(Book book) {
        BookEntity entity = new BookEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setTotalCopies(book.getTotalCopies());
        entity.setAvailableCopies(book.getAvailableCopies());
        return entity;
    }
}