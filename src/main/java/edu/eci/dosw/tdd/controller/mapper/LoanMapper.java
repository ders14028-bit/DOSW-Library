package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;

public final class LoanMapper {

    private LoanMapper() {
    }

    public static LoanDTO toDto(Loan loan) {
        return new LoanDTO(
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus().name()
        );
    }
}

