package edu.eci.dosw.tdd.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class Loan {
    private Book book;
    private User user;
    private LocalDateTime loanDate;
    private Status status;


}