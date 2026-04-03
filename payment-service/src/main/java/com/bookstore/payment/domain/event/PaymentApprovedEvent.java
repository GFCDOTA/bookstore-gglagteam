package com.bookstore.payment.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentApprovedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        UUID paymentId,
        BigDecimal amount
) implements DomainEvent {

    public static PaymentApprovedEvent of(UUID orderId, UUID paymentId, BigDecimal amount) {
        return new PaymentApprovedEvent(
                UUID.randomUUID(), "PaymentApproved", "1.0", Instant.now(),
                orderId.toString(), orderId, paymentId, amount
        );
    }
}
