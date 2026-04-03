package com.bookstore.catalog.domain.port.in;

import java.util.List;
import java.util.UUID;

public interface ReserveStockUseCase {
    void execute(UUID orderId, List<StockReservationItem> items);

    record StockReservationItem(UUID bookId, int quantity) {}
}
