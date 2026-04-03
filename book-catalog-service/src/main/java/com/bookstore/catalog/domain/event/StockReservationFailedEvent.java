package com.bookstore.catalog.domain.event;

import java.time.Instant;
import java.util.UUID;

public record StockReservationFailedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        String reason
) implements DomainEvent {

    public static StockReservationFailedEvent of(UUID orderId, String reason) {
        return new StockReservationFailedEvent(
                UUID.randomUUID(), "StockReservationFailed", "1.0", Instant.now(),
                orderId.toString(), orderId, reason
        );
    }
}
