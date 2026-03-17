package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> usersById = new LinkedHashMap<>();
    private final UserValidator userValidator = new UserValidator();

    public UserService() {
        User user1 = new User();
        user1.setId("u1");
        user1.setName("Ana");
        usersById.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId("u2");
        user2.setName("Luis");
        usersById.put(user2.getId(), user2);
    }

    public List<User> getUsers() {
        return new ArrayList<>(usersById.values());
    }

    public User getUserById(String userId) {
        String validUserId = userValidator.validateUserId(userId);
        User user = usersById.get(validUserId);
        if (user == null) {
            throw new UserNotFoundException("No se encontro usuario con ID: " + validUserId);
        }
        return user;
    }
}

