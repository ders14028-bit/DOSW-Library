package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanDTO> getLoans(@RequestParam String actorUserId) {
        return loanService.getLoans(actorUserId).stream().map(LoanMapper::toDto).toList();
    }

    @GetMapping("/user/{userId}")
    public List<LoanDTO> getLoansByUser(@PathVariable String userId, @RequestParam String actorUserId) {
        return loanService.getLoansByUser(actorUserId, userId).stream().map(LoanMapper::toDto).toList();
    }

    @PostMapping("/borrow")
    public LoanDTO borrowBook(@RequestBody LoanDTO request, @RequestParam(required = false) String actorUserId) {
        String effectiveActor = actorUserId == null || actorUserId.isBlank() ? request.userId() : actorUserId;
        return LoanMapper.toDto(loanService.loanBook(effectiveActor, request.userId(), request.bookId()));
    }

    @PostMapping("/return")
    public LoanDTO returnBook(@RequestBody LoanDTO request, @RequestParam(required = false) String actorUserId) {
        String effectiveActor = actorUserId == null || actorUserId.isBlank() ? request.userId() : actorUserId;
        return LoanMapper.toDto(loanService.returnBook(effectiveActor, request.userId(), request.bookId()));
    }
}

