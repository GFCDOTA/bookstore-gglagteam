package com.bookstore.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        UUID customerId
) implements DomainEvent {

    public static OrderConfirmedEvent of(UUID orderId, UUID customerId) {
        return new OrderConfirmedEvent(
                UUID.randomUUID(), "OrderConfirmed", "1.0", Instant.now(),
                orderId.toString(), orderId, customerId
        );
    }
}
