package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.dto.BookCreateDTO;
import edu.eci.dosw.tdd.controller.dto.BookStockUpdateDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/inventory")
    public List<BookDTO> getInventory() {
        return bookService.getInventory().stream().map(BookMapper::toDto).toList();
    }

    @PostMapping
    public BookDTO createBook(@RequestParam String actorUserId, @RequestBody BookCreateDTO request) {
        return BookMapper.toDto(bookService.createBook(
                actorUserId,
                request.id(),
                request.title(),
                request.author(),
                request.totalCopies(),
                request.availableCopies()
        ));
    }

    @PutMapping("/{bookId}/stock")
    public BookDTO updateStock(
            @PathVariable String bookId,
            @RequestParam String actorUserId,
            @RequestBody BookStockUpdateDTO request
    ) {
        return BookMapper.toDto(bookService.updateBookStock(
                actorUserId,
                bookId,
                request.totalCopies(),
                request.availableCopies()
        ));
    }
}

