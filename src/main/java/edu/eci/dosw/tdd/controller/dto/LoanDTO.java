package edu.eci.dosw.tdd.controller.dto;

import java.time.LocalDate;

public record LoanDTO(
        String userId,
        String bookId,
        LocalDate loanDate,
        LocalDate returnDate,
        String status
) {
}

