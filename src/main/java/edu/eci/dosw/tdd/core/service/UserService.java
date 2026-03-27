package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.ForbiddenOperationException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.persistence.relational.dao.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.UserEntityMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator = new UserValidator();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll().stream().map(UserEntityMapper::toDomain).toList();
    }

    public User getUserById(String actorUsername, String userId) {
        String validUserId = userValidator.validateUserId(userId);
        UserEntity actor = userRepository.findByUsername(actorUsername)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario: " + actorUsername));
        if (!actor.getId().equals(validUserId) && actor.getRole() != Role.LIBRARIAN) {
            throw new ForbiddenOperationException("Solo puede consultar su propio perfil.");
        }
        return userRepository.findById(validUserId)
                .map(UserEntityMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException("No se encontro usuario con ID: " + validUserId));
    }

    public User registerUser(String id, String name, String username, String password) {
        return createUserInternal(id, name, username, password, Role.USER);
    }

    public User createUserByLibrarian(String id, String name, String username, String password, String role) {
        Role parsedRole = parseRole(role);
        return createUserInternal(id, name, username, password, parsedRole);
    }

    private User createUserInternal(String id, String name, String username, String password, Role role) {
        String validUserId = userValidator.validateUserId(id);
        if (userRepository.existsById(validUserId)) {
            throw new IllegalArgumentException("Ya existe un usuario con ID: " + validUserId);
        }

        String validUsername = requireText(username, "username");
        if (userRepository.findByUsername(validUsername).isPresent()) {
            throw new IllegalArgumentException("El username ya existe: " + validUsername);
        }

        UserEntity entity = new UserEntity();
        entity.setId(validUserId);
        entity.setName(requireText(name, "name"));
        entity.setUsername(validUsername);
        entity.setPassword(passwordEncoder.encode(requireText(password, "password")));
        entity.setRole(role);

        return UserEntityMapper.toDomain(userRepository.save(entity));
    }

    public boolean isLibrarian(String username) {
        return userRepository.findByUsername(username)
                .map(u -> u.getRole() == Role.LIBRARIAN)
                .orElse(false);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol invalido: " + role);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio.");
        }
        return value;
    }
}
