package com.bookstore.catalog.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateBookPriceUseCase {
    void execute(UUID bookId, BigDecimal newPrice);
}
