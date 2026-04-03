package com.bookstore.payment.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProcessPaymentUseCase {
    void execute(UUID orderId, BigDecimal amount);
}
