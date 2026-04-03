package com.bookstore.order.domain.port.in;

import java.util.UUID;

public interface MarkAwaitingPaymentUseCase {
    void execute(UUID orderId);
}
