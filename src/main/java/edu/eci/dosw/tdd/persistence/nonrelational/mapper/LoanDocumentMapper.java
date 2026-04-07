package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanHistory;

import java.util.ArrayList;

public final class LoanDocumentMapper {

    private LoanDocumentMapper() {}

    public static Loan toDomain(LoanDocument doc) {
        Loan loan = new Loan();

        Book book = new Book();
        book.setId(doc.getBookId());
        loan.setBook(book);

        User user = new User();
        user.setId(doc.getUserId());
        loan.setUser(user);

        loan.setLoanDate(doc.getLoanDate());
        loan.setReturnDate(doc.getReturnDate());
        loan.setStatus(doc.getStatus());
        return loan;
    }

    public static LoanDocument toDocument(Loan loan) {
        LoanDocument doc = new LoanDocument();
        doc.setId(loan.getId());
        doc.setUserId(loan.getUser().getId());
        doc.setBookId(loan.getBook().getId());
        doc.setLoanDate(loan.getLoanDate());
        doc.setReturnDate(loan.getReturnDate());
        doc.setStatus(loan.getStatus());

        LoanHistory history = new LoanHistory();
        history.setStatus(loan.getStatus());
        history.setExecutedAt(loan.getLoanDate());

        doc.setHistory(new ArrayList<>());
        doc.getHistory().add(history);

        return doc;
    }
}
