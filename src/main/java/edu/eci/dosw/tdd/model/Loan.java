package edu.eci.dosw.tdd.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class Loan {
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Status status;

}