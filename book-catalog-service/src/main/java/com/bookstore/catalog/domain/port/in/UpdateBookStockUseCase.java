package com.bookstore.catalog.domain.port.in;

import java.util.UUID;

public interface UpdateBookStockUseCase {
    void execute(UUID bookId, int newQuantity);
}
