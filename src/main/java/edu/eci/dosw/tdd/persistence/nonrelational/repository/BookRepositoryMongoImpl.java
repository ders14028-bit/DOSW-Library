package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.BookDocumentMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class BookRepositoryMongoImpl implements BookRepository {

    private final MongoBookRepository repository;

    public BookRepositoryMongoImpl(MongoBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return BookDocumentMapper.toDomain(repository.save(BookDocumentMapper.toDocument(book)));
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id).map(BookDocumentMapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream().map(BookDocumentMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}