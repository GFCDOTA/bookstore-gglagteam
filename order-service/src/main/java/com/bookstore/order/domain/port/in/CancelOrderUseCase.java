package com.bookstore.order.domain.port.in;

import java.util.UUID;

public interface CancelOrderUseCase {
    void execute(UUID orderId, String reason);
}
