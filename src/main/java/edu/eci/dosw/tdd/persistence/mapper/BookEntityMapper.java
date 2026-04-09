package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.dao.BookEntity;

public final class BookEntityMapper {

    private BookEntityMapper() {
    }

    public static Book toDomain(BookEntity entity) {
        Book book = new Book();
        book.setId(entity.getId());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setTotalCopies(entity.getTotalCopies());
        book.setAvailableCopies(entity.getAvailableCopies());
        return book;
    }
}

