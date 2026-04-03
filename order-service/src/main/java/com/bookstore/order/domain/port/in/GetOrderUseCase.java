package com.bookstore.order.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase {
    Optional<OrderView> execute(UUID orderId);

    record OrderView(UUID id, UUID customerId, String status, BigDecimal totalAmount, List<OrderItemView> items) {}
    record OrderItemView(UUID bookId, int quantity, BigDecimal unitPrice) {}
}
