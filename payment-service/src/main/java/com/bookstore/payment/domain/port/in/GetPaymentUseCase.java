package com.bookstore.payment.domain.port.in;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface GetPaymentUseCase {
    Optional<PaymentView> execute(UUID orderId);

    record PaymentView(UUID id, UUID orderId, BigDecimal amount, String currency, String status) {}
}
