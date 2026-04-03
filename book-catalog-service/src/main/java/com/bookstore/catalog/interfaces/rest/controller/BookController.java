package com.bookstore.catalog.interfaces.rest.controller;

import com.bookstore.catalog.domain.port.in.CreateBookUseCase;
import com.bookstore.catalog.domain.port.in.GetBookUseCase;
import com.bookstore.catalog.domain.port.in.UpdateBookPriceUseCase;
import com.bookstore.catalog.domain.port.in.UpdateBookStockUseCase;
import com.bookstore.catalog.interfaces.exception.BookNotFoundException;
import com.bookstore.catalog.interfaces.rest.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final CreateBookUseCase createBookUseCase;
    private final GetBookUseCase getBookUseCase;
    private final UpdateBookPriceUseCase updateBookPriceUseCase;
    private final UpdateBookStockUseCase updateBookStockUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateBookResponse create(@Valid @RequestBody CreateBookRequest request) {
        var command = new CreateBookUseCase.CreateBookCommand(
                request.title(), request.author(), request.isbn(),
                request.price(), request.initialStock()
        );
        var result = createBookUseCase.execute(command);
        return new CreateBookResponse(result.bookId(), result.title(), result.status());
    }

    @GetMapping
    public List<BookResponse> findAll() {
        return getBookUseCase.findAll().stream()
                .map(BookResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable UUID id) {
        return getBookUseCase.findById(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + id));
    }

    @PutMapping("/{id}/price")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrice(@PathVariable UUID id, @Valid @RequestBody UpdatePriceRequest request) {
        updateBookPriceUseCase.execute(id, request.price());
    }

    @PutMapping("/{id}/stock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStock(@PathVariable UUID id, @Valid @RequestBody UpdateStockRequest request) {
        updateBookStockUseCase.execute(id, request.quantity());
    }

    public record CreateBookResponse(UUID id, String title, String status) {}
}
