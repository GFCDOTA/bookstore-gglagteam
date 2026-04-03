package com.bookstore.order.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {
    CreateOrderResult execute(CreateOrderCommand command);

    record CreateOrderCommand(UUID customerId, List<OrderItemCommand> items) {}
    record OrderItemCommand(UUID bookId, int quantity, BigDecimal unitPrice) {}
    record CreateOrderResult(UUID orderId, String status) {}
}
