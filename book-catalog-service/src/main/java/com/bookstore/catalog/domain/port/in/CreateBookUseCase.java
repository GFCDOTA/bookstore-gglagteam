package com.bookstore.catalog.domain.port.in;

import java.math.BigDecimal;

public interface CreateBookUseCase {
    CreateBookResult execute(CreateBookCommand command);

    record CreateBookCommand(String title, String author, String isbn, BigDecimal price, int initialStock) {}
    record CreateBookResult(java.util.UUID bookId, String title, String status) {}
}
