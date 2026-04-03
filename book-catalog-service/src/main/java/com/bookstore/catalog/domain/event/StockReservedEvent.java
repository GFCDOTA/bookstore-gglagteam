package com.bookstore.catalog.domain.event;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        UUID bookId,
        int quantity
) implements DomainEvent {

    public static StockReservedEvent of(UUID orderId, UUID bookId, int quantity) {
        return new StockReservedEvent(
                UUID.randomUUID(), "StockReserved", "1.0", Instant.now(),
                orderId.toString(), orderId, bookId, quantity
        );
    }
}
