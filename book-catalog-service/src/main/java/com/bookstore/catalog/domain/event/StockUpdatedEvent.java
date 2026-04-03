package com.bookstore.catalog.domain.event;

import java.time.Instant;
import java.util.UUID;

public record StockUpdatedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID bookId,
        int newStock
) implements DomainEvent {

    public static StockUpdatedEvent of(UUID bookId, int newStock) {
        return new StockUpdatedEvent(
                UUID.randomUUID(), "StockUpdated", "1.0", Instant.now(),
                bookId.toString(), bookId, newStock
        );
    }
}
