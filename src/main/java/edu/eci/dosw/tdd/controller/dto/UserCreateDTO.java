package edu.eci.dosw.tdd.controller.dto;

public record UserCreateDTO(
        String id,
        String name,
        String username,
        String password,
        String role
) {
}

