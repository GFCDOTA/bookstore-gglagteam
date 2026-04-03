package com.bookstore.catalog.interfaces.rest.dto;

import com.bookstore.catalog.domain.port.in.GetBookUseCase;

import java.math.BigDecimal;
import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        String author,
        String isbn,
        BigDecimal price,
        int stockQuantity,
        String status
) {
    public static BookResponse from(GetBookUseCase.BookView view) {
        return new BookResponse(
                view.id(), view.title(), view.author(), view.isbn(),
                view.price(), view.stockQuantity(), view.status()
        );
    }
}
