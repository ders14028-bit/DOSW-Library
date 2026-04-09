package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.persistence.dao.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, String> {
}

