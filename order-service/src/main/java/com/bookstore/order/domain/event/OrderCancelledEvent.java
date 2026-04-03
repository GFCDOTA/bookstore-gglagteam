package com.bookstore.order.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        UUID customerId,
        List<OrderCancelledItem> items
) implements DomainEvent {

    public record OrderCancelledItem(UUID bookId, int quantity) {}

    public static OrderCancelledEvent of(UUID orderId, UUID customerId, List<OrderCancelledItem> items) {
        return new OrderCancelledEvent(
                UUID.randomUUID(), "OrderCancelled", "1.0", Instant.now(),
                orderId.toString(), orderId, customerId, items
        );
    }
}
