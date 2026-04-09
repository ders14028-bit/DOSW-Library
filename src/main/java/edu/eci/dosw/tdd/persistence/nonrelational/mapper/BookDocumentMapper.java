package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookAvailability;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;

public final class BookDocumentMapper {

    private BookDocumentMapper() {}

    public static Book toDomain(BookDocument doc) {
        Book book = new Book();
        book.setId(doc.getId());
        book.setTitle(doc.getTitle());
        book.setAuthor(doc.getAuthor());
        if (doc.getAvailability() != null) {
            book.setTotalCopies(doc.getAvailability().getTotalCopies());
            book.setAvailableCopies(doc.getAvailability().getAvailableCopies());
        }
        return book;
    }

    public static BookDocument toDocument(Book book) {
        BookDocument doc = new BookDocument();
        doc.setId(book.getId());
        doc.setTitle(book.getTitle());
        doc.setAuthor(book.getAuthor());

        BookAvailability availability = new BookAvailability();
        availability.setTotalCopies(book.getTotalCopies());
        availability.setAvailableCopies(book.getAvailableCopies());
        availability.setLoanedCopies(book.getTotalCopies() - book.getAvailableCopies());
        availability.setStatus(book.getAvailableCopies() > 0 ? "AVAILABLE" : "UNAVAILABLE");
        doc.setAvailability(availability);

        return doc;
    }
}
