package edu.eci.dosw.tdd.persistence.nonrelational.repository;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.UserRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.UserDocumentMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class UserRepositoryMongoImpl implements UserRepository {

    private final MongoUserRepository repository;

    public UserRepositoryMongoImpl(MongoUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return UserDocumentMapper.toDomain(repository.save(UserDocumentMapper.toDocument(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(UserDocumentMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(UserDocumentMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(UserDocumentMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
