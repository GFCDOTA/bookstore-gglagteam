package com.bookstore.order.domain.port.in;

import java.util.UUID;

public interface ConfirmOrderUseCase {
    void execute(UUID orderId);
}
