package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping
    public List<LoanDTO> getLoans() {
        return loanService.getLoans().stream().map(LoanMapper::toDto).toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @GetMapping("/user/{userId}")
    public List<LoanDTO> getLoansByUser(@PathVariable String userId,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        return loanService.getLoansByUser(currentUser.getUsername(), userId)
                .stream().map(LoanMapper::toDto).toList();
    }

    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @PostMapping("/borrow")
    public LoanDTO borrowBook(@RequestBody LoanDTO request,
                              @AuthenticationPrincipal UserDetails currentUser) {
        return LoanMapper.toDto(loanService.loanBook(
                currentUser.getUsername(), request.userId(), request.bookId()
        ));
    }

    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @PostMapping("/return")
    public LoanDTO returnBook(@RequestBody LoanDTO request,
                              @AuthenticationPrincipal UserDetails currentUser) {
        return LoanMapper.toDto(loanService.returnBook(
                currentUser.getUsername(), request.userId(), request.bookId()
        ));
    }
}