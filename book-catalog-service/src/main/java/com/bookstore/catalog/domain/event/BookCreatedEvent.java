package com.bookstore.catalog.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookCreatedEvent(
        UUID eventId,
        String eventType,
        String eventVersion,
        Instant occurredAt,
        String aggregateId,
        UUID bookId,
        String title,
        String author,
        String isbn,
        BigDecimal price,
        int stockQuantity
) implements DomainEvent {

    public static BookCreatedEvent of(UUID bookId, String title, String author, String isbn,
                                       BigDecimal price, int stockQuantity) {
        return new BookCreatedEvent(
                UUID.randomUUID(), "BookCreated", "1.0", Instant.now(),
                bookId.toString(), bookId, title, author, isbn, price, stockQuantity
        );
    }
}
