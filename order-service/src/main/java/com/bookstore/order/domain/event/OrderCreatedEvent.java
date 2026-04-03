package com.bookstore.order.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID orderId,
        UUID customerId,
        List<OrderItemPayload> items,
        BigDecimal totalAmount
) implements DomainEvent {

    public record OrderItemPayload(UUID bookId, int quantity, BigDecimal unitPrice) {}

    public static OrderCreatedEvent of(UUID orderId, UUID customerId,
                                        List<OrderItemPayload> items, BigDecimal totalAmount) {
        return new OrderCreatedEvent(
                UUID.randomUUID(), "OrderCreated", "1.0", Instant.now(),
                orderId.toString(), orderId, customerId, items, totalAmount
        );
    }
}
