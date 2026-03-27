package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.BookCreateDTO;
import edu.eci.dosw.tdd.controller.dto.BookStockUpdateDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.service.BookService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @GetMapping("/inventory")
    public List<BookDTO> getInventory() {
        return bookService.getInventory().stream().map(BookMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public BookDTO createBook(@RequestBody BookCreateDTO request) {
        return BookMapper.toDto(bookService.createBook(
                request.id(), request.title(), request.author(),
                request.totalCopies(), request.availableCopies()
        ));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{bookId}/stock")
    public BookDTO updateStock(@PathVariable String bookId, @RequestBody BookStockUpdateDTO request) {
        return BookMapper.toDto(bookService.updateBookStock(
                bookId, request.totalCopies(), request.availableCopies()
        ));
    }
}