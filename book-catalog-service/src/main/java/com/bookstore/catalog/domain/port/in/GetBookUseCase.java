package com.bookstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetBookUseCase {
    Optional<BookView> findById(UUID id);
    List<BookView> findAll();

    record BookView(UUID id, String title, String author, String isbn,
                    BigDecimal price, int stockQuantity, String status) {}
}
