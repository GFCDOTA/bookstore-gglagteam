package com.bookstore.order.domain.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    String eventType();
    String aggregateId();
    Instant occurredAt();
    String eventVersion();
    UUID eventId();
}
