package edu.eci.dosw.tdd.controller.dto;

public record BookCreateDTO(
        String id,
        String title,
        String author,
        Integer totalCopies,
        Integer availableCopies
) {
}

