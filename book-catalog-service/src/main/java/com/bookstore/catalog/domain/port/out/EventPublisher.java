package com.bookstore.catalog.domain.port.out;

import com.bookstore.catalog.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
