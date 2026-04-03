package com.bookstore.payment.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentRejectedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        UUID paymentId,
        String reason
) implements DomainEvent {

    public static PaymentRejectedEvent of(UUID orderId, UUID paymentId, String reason) {
        return new PaymentRejectedEvent(
                UUID.randomUUID(), "PaymentRejected", "1.0", Instant.now(),
                orderId.toString(), orderId, paymentId, reason
        );
    }
}
