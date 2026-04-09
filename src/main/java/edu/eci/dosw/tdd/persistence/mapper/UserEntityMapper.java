package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.dao.UserEntity;

public final class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setRole(entity.getRole());
        return user;
    }
}

