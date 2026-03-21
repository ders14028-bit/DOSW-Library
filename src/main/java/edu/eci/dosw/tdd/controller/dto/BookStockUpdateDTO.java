package edu.eci.dosw.tdd.controller.dto;

public record BookStockUpdateDTO(
        Integer totalCopies,
        Integer availableCopies
) {
}

