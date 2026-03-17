package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;

public final class BookMapper {

    private BookMapper() {
    }

    public static BookDTO toDto(Book book, Integer availableCopies) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), availableCopies);
    }
}

