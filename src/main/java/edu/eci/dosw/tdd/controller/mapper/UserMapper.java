package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.UserDTO;
import edu.eci.dosw.tdd.core.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDTO toDto(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getUsername(), user.getRole().name());
    }
}

