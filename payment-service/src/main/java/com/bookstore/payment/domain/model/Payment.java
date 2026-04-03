package com.bookstore.payment.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final String currency;
    private PaymentStatus status;

    public static Payment create(UUID orderId, BigDecimal amount) {
        return new Payment(UUID.randomUUID(), orderId, amount, "BRL", PaymentStatus.PENDING);
    }

    public Payment(UUID id, UUID orderId, BigDecimal amount, String currency, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public void approve() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment " + id + " cannot be approved from status " + status);
        }
        this.status = PaymentStatus.APPROVED;
    }

    public void reject() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment " + id + " cannot be rejected from status " + status);
        }
        this.status = PaymentStatus.REJECTED;
    }

    // Getters
    public UUID id() { return id; }
    public UUID orderId() { return orderId; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
    public PaymentStatus status() { return status; }
}
