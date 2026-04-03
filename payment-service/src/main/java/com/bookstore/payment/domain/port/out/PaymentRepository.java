package com.bookstore.payment.domain.port.out;

import com.bookstore.payment.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}
